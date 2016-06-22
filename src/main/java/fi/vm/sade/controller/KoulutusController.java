package fi.vm.sade.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.MediaType;

import fi.vm.sade.wrapper.KoulutusWrapper;
import org.apache.cxf.helpers.IOUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import fi.vm.sade.koodisto.service.types.*;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodistoRyhmaCollectionType;
import fi.vm.sade.koodisto.service.types.common.KoodistoType;
import fi.vm.sade.koodisto.service.types.common.KoodistoVersioListType;
import fi.vm.sade.koodisto.util.CachingKoodistoClient;
import fi.vm.sade.koodisto.util.KoodistoClient;
import fi.vm.sade.model.Koodi;
import fi.vm.sade.model.KoulutusAsteTyyppi;
import fi.vm.sade.model.StatusObject;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.parser.JAXBParser;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.TarjoajaHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.AmmattitutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.ErikoisammattitutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusAmmatillinenPerustutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.ValmistavaKoulutusV1RDTO;

@RestController
public class KoulutusController {
    private static final String tarjontaURI = "https://testi.virkailija.opintopolku.fi/tarjonta-service/rest/";
    private static final String organisaatioURI = "https://testi.virkailija.opintopolku.fi/organisaatio-service/rest/";
    private static final String opitopolkuURI = "https://testi.opintopolku.fi/lo/";
    private static final String koodisto = "https://testi.virkailija.opintopolku.fi/koodisto-service";
    private static final String JSON_UTF8 = MediaType.APPLICATION_JSON + ";charset=UTF-8";
    private static final String FILE_PATH = "generated/lo_full_sample.zip";

    private static final Logger log = LoggerFactory.getLogger(KoulutusController.class);
    
    private List<KoulutusHakutulosV1RDTO> haetutKoulutukset;
    private List<OrganisaatioRDTO> haetutOrganisaatiot;
    private Map<String, Koodi> haetutKoodit;
    private WebResource v1KoulutusResource;
    private WebResource v1OrganisaatioResource;
    private KoodistoClient koodistoClient = new CachingKoodistoClient(koodisto);

    private double status;
    private StatusObject statusObject;

    private double numberOfOrganisations;
    private double numberOfCurrentOrganisation;

    @RequestMapping("koulutus/status")
    public String getStatus() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(statusObject);
    }

    @GET
    @RequestMapping("/download")
    public void download(HttpServletResponse response) throws IOException {
        File file = new File(FILE_PATH);
        InputStream myStream = new FileInputStream(file);
        response.addHeader("Content-disposition", "attachment;filename=" + file.getName());
        response.setContentType("txt/plain");
        IOUtils.copy(myStream, response.getOutputStream());
        response.flushBuffer();
        myStream.close();
    }

    @RequestMapping("/koulutus/")
    public String getKoulutukset() throws Exception {
        status = 0.01;
        haetutKoulutukset = new ArrayList<>();
        haetutOrganisaatiot = new ArrayList<>();
        haetutKoodit = new HashMap<>();


        createInitialStatusObject();
        Client clientWithJacksonSerializer = createClient();
        v1KoulutusResource = clientWithJacksonSerializer.resource(tarjontaURI + "v1/koulutus");
        v1OrganisaatioResource = clientWithJacksonSerializer.resource(organisaatioURI + "organisaatio");

        statusObject.setStatusText("Haetaan Koodisto dataa...");

        // Aalto yliopisto 1.2.246.562.10.72985435253
        // 1.2.246.562.10.53642770753
        // tai tyhja kaikille tuloksille
        HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> hakutulokset = searchOrganisationsEducations("1.2.246.562.10.72985435253").getResult();
        numberOfOrganisations = hakutulokset.getTulokset().size();
        numberOfCurrentOrganisation = 0.0;

        fetchOrganisaatiotAndKoulutuksetAndKoodit(hakutulokset);
        // noin 1200 koulutusta minuutissa
        statusObject.setDurationEstimate(haetutKoulutukset.size() / 1200);
        statusObject.setStatusText("Haetaan ja parsitaan Koulutus dataa...");

        final Map<String, OrganisaatioRDTO> organisaatioMap = haetutOrganisaatiot.stream()
                .collect(Collectors.toMap(OrganisaatioRDTO::getOid, s -> s));
        KoulutusWrapper kw = new KoulutusWrapper();
        int skipCount = fetchKoulutukset(kw, organisaatioMap);
        kw.forwardLOtoJaxBParser();

        status = 1.0;
        statusObject.setStatus(status);
        statusObject.setStatusText("Valmis");

        if(skipCount != 0){
            log.warn("Amount of skipped koulutus: " + skipCount);
        }
        log.info("Request ready");
        return "";
    }

    private int fetchKoulutukset(KoulutusWrapper kw, Map<String, OrganisaatioRDTO> organisaatioMap) throws Exception {
        double i = 0.0;
        int skip = 0;
        for (KoulutusHakutulosV1RDTO kh : haetutKoulutukset) {
            switch (kh.getKoulutusasteTyyppi().name()) {
                case KoulutusAsteTyyppi.AMMATILLINEN_PERUSKOULUTUS:
                    ResultV1RDTO<KoulutusAmmatillinenPerustutkintoV1RDTO> ammatillinenPerustutkintoResult = searchAmmatillinenPerustutkinto(kh.getOid());
                    KoulutusAmmatillinenPerustutkintoV1RDTO ammatillinenPerustutkintoKoulutus = ammatillinenPerustutkintoResult.getResult();
                    kw.fetchAmmatillinenPerustutkintoInfo(ammatillinenPerustutkintoKoulutus, organisaatioMap, kh, haetutKoodit);
                    break;

                case KoulutusAsteTyyppi.AMMATTITUTKINTO:
                    ResultV1RDTO<AmmattitutkintoV1RDTO> ammattiResult = searchAmmattitutkinto(kh.getOid());
                    AmmattitutkintoV1RDTO ammattiKoulutus = ammattiResult.getResult();
                    kw.fetchAmmattiInfo(ammattiKoulutus, organisaatioMap, kh, haetutKoodit);
                    break;

                case KoulutusAsteTyyppi.ERIKOISAMMATTITUTKINTO:
                    ResultV1RDTO<ErikoisammattitutkintoV1RDTO> erikoisResult = searchErikoisammattitutkinto(kh.getOid());
                    ErikoisammattitutkintoV1RDTO erikoisKoulutus = erikoisResult.getResult();
                    kw.fetchErikoisInfo(erikoisKoulutus, organisaatioMap, kh, haetutKoodit);
                    break;

                case KoulutusAsteTyyppi.KORKEAKOULUTUS:
                    ResultV1RDTO<KoulutusKorkeakouluV1RDTO> koulutusResult = searchKoulutusKorkeakoulu(kh.getOid());
                    KoulutusKorkeakouluV1RDTO korkeaKoulutus = koulutusResult.getResult();
                    kw.fetchKorkeaInfo(korkeaKoulutus, organisaatioMap, kh, haetutKoodit);
                    break;

                case KoulutusAsteTyyppi.AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS:
                    ResultV1RDTO<ValmistavaKoulutusV1RDTO> ammValmistavaResult = searchValmistavaKoulutus(kh.getOid());
                    ValmistavaKoulutusV1RDTO ammValmistavaKoulutus = ammValmistavaResult.getResult();
                    kw.fetchValmistavaInfo(ammValmistavaKoulutus, organisaatioMap, kh, haetutKoodit);
                    break;

                case KoulutusAsteTyyppi.LUKIOKOULUTUS:
                    ResultV1RDTO<KoulutusLukioV1RDTO> lukioResult = searchKoulutusLukio(kh.getOid());
                    KoulutusLukioV1RDTO lukioKoulutus = lukioResult.getResult();
                    kw.fetchLukioInfo(lukioKoulutus, organisaatioMap, kh, haetutKoodit);
                    break;
                default:
                    log.info("Skipping on data parsing Koulutus: " + kh.getKomoOid() +
                            ", Type: " + kh.getKoulutusasteTyyppi());
                    skip++;
            }
            i++;
            statusObject.setStatusText("Haetaan ja parsitaan Koulutusta " + (int) i + "/" + haetutKoulutukset.size());
            status = 0.3 + (i / (double) haetutKoulutukset.size() * 0.66);
            status = (Math.ceil(status * 100.0) / 100.0);
            statusObject.setStatus(status);
            // noin 1200 koulutusta minuutissa
            statusObject.setDurationEstimate((haetutKoulutukset.size() - i) / 1200);
        }
        return skip;
    }

    private void fetchOrganisaatiotAndKoulutuksetAndKoodit(HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> hakutulokset) throws Exception {
        for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> organisaatioData: hakutulokset.getTulokset()) {
            OrganisaatioRDTO organisaatio = searchOrganisation(organisaatioData.getOid());
            haetutOrganisaatiot.add(organisaatio);
            numberOfCurrentOrganisation++;

            for (KoulutusHakutulosV1RDTO koulutusData : organisaatioData.getTulokset()) {
                if (koulutusData != null) {
                    switch (koulutusData.getKoulutusasteTyyppi().value().toUpperCase()) {
                        case KoulutusAsteTyyppi.AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS:
                        case KoulutusAsteTyyppi.LUKIOKOULUTUS:
                        case KoulutusAsteTyyppi.AMMATILLINENPERUSKOULUTUS:
                            if (checkKoulutusValidnessFromOpintopolku("koulutus/", koulutusData.getOid())) {
                                addKoulutusToArray(koulutusData);
                                fetchKoodi(koulutusData);
                            }
                            break;
                        case KoulutusAsteTyyppi.AMMATTITUTKINTO:
                        case KoulutusAsteTyyppi.ERIKOISAMMATTITUTKINTO:
                            if (checkKoulutusValidnessFromOpintopolku("adultvocational/", koulutusData.getOid())) {
                                addKoulutusToArray(koulutusData);
                                fetchKoodi(koulutusData);
                            }
                            break;
                        case KoulutusAsteTyyppi.KORKEAKOULUTUS:
                            if (checkKoulutusValidnessFromOpintopolku("highered/", koulutusData.getOid())) {
                                addKoulutusToArray(koulutusData);
                                fetchKoodi(koulutusData);
                            }
                            break;
                        default:
                            log.info("Skipping on data fetch Koulutus: " + koulutusData.getKomoOid() +
                                    ", Type: " + koulutusData.getKoulutusasteTyyppi());
                            break;
                    }
                }
            }
        }
    }

    private void createInitialStatusObject() {
        statusObject = new StatusObject();
        statusObject.setDurationEstimate(0.0);
        statusObject.setStatus(status);
        statusObject.setStatusText("Alustetaan...");
    }

    private Client createClient() {
        ObjectMapper mapper = new ObjectMapper();
        JacksonJsonProvider jacksProv = new JacksonJsonProvider(mapper);
        ClientConfig cc = new DefaultClientConfig();
        cc.getSingletons().add(jacksProv);
        return Client.create(cc);
    }

    private void fetchKoodi(KoulutusHakutulosV1RDTO koulutusData) {
        List<KoodiType> kt = koodistoClient.getAlakoodis(koulutusData.getKoulutuskoodi());
        Koodi code = new Koodi();
        for(KoodiType k : kt ){
            if(k.getKoodisto().getKoodistoUri().equals("isced2011koulutusaste")){
                code.setIsced2011koulutusaste(k.getKoodiArvo());
            }
            if(k.getKoodisto().getKoodistoUri().equals("isced2011koulutusalataso3")){
                code.setIsced2011koulutusalataso3(k.getKoodiArvo());
            }
            haetutKoodit.put(koulutusData.getKoulutuskoodi(), code);
        }        
    }

    @SuppressWarnings("unchecked")
    public ResultV1RDTO<KoulutusAmmatillinenPerustutkintoV1RDTO> searchAmmatillinenPerustutkinto(String oid) throws Exception {
        return (ResultV1RDTO<KoulutusAmmatillinenPerustutkintoV1RDTO>) getWithRetries(v1KoulutusResource.path(oid),
                new GenericType<ResultV1RDTO<KoulutusAmmatillinenPerustutkintoV1RDTO>>() {
                });
    }

    @SuppressWarnings("unchecked")
    public ResultV1RDTO<AmmattitutkintoV1RDTO> searchAmmattitutkinto(String oid) throws Exception {
        return (ResultV1RDTO<AmmattitutkintoV1RDTO>) getWithRetries(v1KoulutusResource.path(oid),
                new GenericType<ResultV1RDTO<AmmattitutkintoV1RDTO>>() {
                });
    }

    @SuppressWarnings("unchecked")
    public ResultV1RDTO<ErikoisammattitutkintoV1RDTO> searchErikoisammattitutkinto(String oid) throws Exception {
        return (ResultV1RDTO<ErikoisammattitutkintoV1RDTO>) getWithRetries(v1KoulutusResource.path(oid),
                new GenericType<ResultV1RDTO<ErikoisammattitutkintoV1RDTO>>() {
                });
    }

    @SuppressWarnings("unchecked")
    public ResultV1RDTO<KoulutusKorkeakouluV1RDTO> searchKoulutusKorkeakoulu(String oid) throws Exception {
        return (ResultV1RDTO<KoulutusKorkeakouluV1RDTO>) getWithRetries(v1KoulutusResource.path(oid),
                new GenericType<ResultV1RDTO<KoulutusKorkeakouluV1RDTO>>() {
                });
    }

    @SuppressWarnings("unchecked")
    public ResultV1RDTO<ValmistavaKoulutusV1RDTO> searchValmistavaKoulutus(String oid) throws Exception {
        return (ResultV1RDTO<ValmistavaKoulutusV1RDTO>) getWithRetries(v1KoulutusResource.path(oid),
                new GenericType<ResultV1RDTO<ValmistavaKoulutusV1RDTO>>() {
                });
    }

    @SuppressWarnings("unchecked")
    public ResultV1RDTO<KoulutusLukioV1RDTO> searchKoulutusLukio(String oid) throws Exception {
        return (ResultV1RDTO<KoulutusLukioV1RDTO>) getWithRetries(v1KoulutusResource.path(oid), new GenericType<ResultV1RDTO<KoulutusLukioV1RDTO>>() {
        });
    }

    // Hakee yhden organisaation tiedot organisaatio-rajapinnasta
    public OrganisaatioRDTO searchOrganisation(String oid) throws Exception {
        return (OrganisaatioRDTO) getWithRetries(v1OrganisaatioResource.path(oid), new GenericType<OrganisaatioRDTO>() {
        });
    }

    // Hakee yhden organisaation tiedot organisaatio-rajapinnasta
    public boolean checkKoulutusValidnessFromOpintopolku(String type, String oid) throws Exception {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(opitopolkuURI + type + oid).openConnection();
            // wrap the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = null;
            StringBuilder content = new StringBuilder();
            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
            if (200 <= connection.getResponseCode() && connection.getResponseCode() <= 399) {
                log.debug("checkKoulutusValidnessFromOpintopolku found: " + opitopolkuURI + type + oid);
                return true;
            } else {
                log.debug("checkKoulutusValidnessFromOpintopolku didn't find: " + opitopolkuURI + type + oid);
                return false;
            }
        } catch (IOException exception) {
            log.debug("checkKoulutusValidnessFromOpintopolku didn't find: " + opitopolkuURI + type + oid);
            return false;
        }
    }

    // Hakee yhden organisaation julkaistu-tilassa olevat koulutukset
    // tarjonta-rajapinnasta.
    // Jos OrganisationOid on tyhja, metodi palauttaa kaikkien organisaatioiden
    // julkaistut koulutukset
    @SuppressWarnings("unchecked")
    public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> searchOrganisationsEducations(String OrganisationOid) throws Exception {
        if (OrganisationOid.equals("")) {
            return (ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>) getWithRetries(
                    v1KoulutusResource.path("search").queryParam("tila", "JULKAISTU").queryParam("meta", "true").queryParam("img", "false"),
                    new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
                    });
        } else {
            return (ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>) getWithRetries(
                    v1KoulutusResource.path("search").queryParam("organisationOid", OrganisationOid).queryParam("tila", "JULKAISTU")
                            .queryParam("meta", "true").queryParam("img", "false"),
                    new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
                    });
        }

    }

    @SuppressWarnings("unchecked")
    private Object getWithRetries(WebResource resource, GenericType type) throws Exception {
        int retries = 2;
        log.debug("getWithRetries: " + resource.getURI().toString());
        while (--retries > 0) {
            try {
                return resource.accept(JSON_UTF8).get(type);
            } catch (Exception e) {
                log.warn("Calling resource failed: " + resource);
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e1) {
                    log.error("Interrupted", e1);
                }
            }
        }
        log.warn("Calling resource failed, last retry: " + resource);
        try {
            return resource.accept(JSON_UTF8).get(type);
        } catch (Exception e) {
            log.error("Calling resource failed: " + resource, e);
            throw e;
        }
    }

    private void addKoulutusToArray(KoulutusHakutulosV1RDTO koulutusData) {
        log.debug("Adding : " + koulutusData.getOid());
        haetutKoulutukset.add(koulutusData); // lisataan koulutus
        status = numberOfCurrentOrganisation / numberOfOrganisations * 0.30;
        status = (Math.ceil(status * 100.0) / 100.0);
        statusObject.setStatus(status);
    }
}
