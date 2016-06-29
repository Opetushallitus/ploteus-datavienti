package fi.vm.sade.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;

import fi.vm.sade.wrapper.KoulutusWrapper;
import org.apache.cxf.helpers.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import fi.vm.sade.javautils.httpclient.OphHttpClient;
import fi.vm.sade.javautils.httpclient.OphHttpRequest;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.CachingKoodistoClient;
import fi.vm.sade.koodisto.util.KoodistoClient;
import fi.vm.sade.model.Koodi;
import fi.vm.sade.model.KoulutusAsteTyyppi;
import fi.vm.sade.model.StatusObject;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.ploteus.UrlConfiguration;
import fi.vm.sade.properties.OphProperties;
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
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

import static fi.vm.sade.javautils.httpclient.OphHttpClient.JSON;


import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.xml.xsom.parser.JAXPParser;
import com.sun.jersey.api.client.Client;
import javax.ws.rs.core.MediaType;

@RestController
public class KoulutusController {
    @Value("${xml.output.dir}")
    private String FILE_PATH;
    private static final String JSON_UTF8 = MediaType.APPLICATION_JSON + ";charset=UTF-8";
    private static String tarjontaURI;
    private static String organisaatioURI;

    private static final Logger log = LoggerFactory.getLogger(KoulutusController.class);

    private List<KoulutusHakutulosV1RDTO> haetutKoulutukset;
    private List<OrganisaatioRDTO> haetutOrganisaatiot;
    private Map<String, Koodi> haetutKoodit;
    private KoodistoClient koodistoClient;

    private double status;
    private final StatusObject statusObject = new StatusObject();

    private ObjectMapper mapper = new ObjectMapper();
    private final OphHttpClient httpclient;

    private WebResource v1KoulutusResource;
    private WebResource v1OrganisaatioResource;
    
    private KoulutusWrapper kw;
    
    @Autowired
    public KoulutusController(HttpClient httpclient, UrlConfiguration urlConfiguration, OphProperties urlProperties, KoulutusWrapper koulutusWrapper) {
        this.httpclient = httpclient.getClient();
        koodistoClient = new CachingKoodistoClient(urlConfiguration.url("koodisto-service.base"));
        tarjontaURI = urlProperties.require("tarjonta-service.koulutus", "");
        organisaatioURI = urlProperties.require("organisaatio-service.byOid", "");
        kw = koulutusWrapper;
    }

    @RequestMapping("koulutus/status")
    public String getStatus() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(statusObject);
    }

    @RequestMapping("download")
    public void download(HttpServletResponse response) throws IOException {
        File file = new File(FILE_PATH + "lo_full_sample.zip");
        InputStream myStream = new FileInputStream(file);
        response.addHeader("Content-disposition", "attachment;filename=" + file.getName());
        response.setContentType("txt/plain");
        IOUtils.copy(myStream, response.getOutputStream());
        response.flushBuffer();
        myStream.close();
        resetStatusObject();
    }

    private void resetStatusObject() {
        statusObject.setStatus(0.0);
        statusObject.setStatusText(null);
        statusObject.setDurationEstimate(0.0);
    }
    
    private Client createClient() {
        ObjectMapper mapper = new ObjectMapper();
        JacksonJsonProvider jacksProv = new JacksonJsonProvider(mapper);
        ClientConfig cc = new DefaultClientConfig();
        cc.getSingletons().add(jacksProv);
        return Client.create(cc);
}

    @RequestMapping("/koulutus/")
    public String getKoulutukset() throws Exception {
        status = 0.01;
        haetutKoulutukset = new ArrayList<>();
        haetutOrganisaatiot = new ArrayList<>();
        haetutKoodit = new HashMap<>();

        createInitialStatusObject();

        statusObject.setStatusText("Haetaan alustavat Koulutukset ja Koodisto data...");
        
        Client clientWithJacksonSerializer = createClient();
        v1KoulutusResource = clientWithJacksonSerializer.resource(tarjontaURI);
        v1OrganisaatioResource = clientWithJacksonSerializer.resource(organisaatioURI);

        // Aalto yliopisto 1.2.246.562.10.72985435253
        // 1.2.246.562.10.53642770753
        // tai tyhja kaikille tuloksille
        ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> hakutulokset2 = searchOrganisationsEducations("1.2.246.562.10.76144863909");
        //HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> hakutulokset = hakutulokset2.getResult();
        HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> hakutulokset = hakutulokset2.getResult();
        
        
        int count = 0;
        for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> organisaatioData : hakutulokset.getTulokset()) {
            count += organisaatioData.getTulokset().size();
        }
        fetchOrganisaatiotAndKoulutuksetAndKoodit(hakutulokset, count);
        // noin 1200 koulutusta minuutissa
        statusObject.setDurationEstimate(haetutKoulutukset.size() / 1200);
        statusObject.setStatusText("Haetaan ja parsitaan Koulutus dataa...");

        final Map<String, OrganisaatioRDTO> organisaatioMap = haetutOrganisaatiot.stream()
                .collect(Collectors.toMap(OrganisaatioRDTO::getOid, s -> s));
        int skipCount = fetchKoulutukset(kw, organisaatioMap);
        kw.forwardLOtoJaxBParser();

        status = 1.0;
        statusObject.setStatus(status);
        statusObject.setStatusText("Valmis");

        if (skipCount != 0) {
            log.warn("Amount of skipped koulutus: " + skipCount);
        }
        log.info("Request ready");
        return "";
    }

    private int fetchKoulutukset(KoulutusWrapper kw, Map<String, OrganisaatioRDTO> organisaatioMap) throws Exception {
        double i = 0.0;
        int skip = 0;
        for (KoulutusHakutulosV1RDTO kh : haetutKoulutukset) {
            switch (kh.getKoulutusasteTyyppi().name().toUpperCase()) {
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
                // FIXME: use
                // KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO.java
                if (kh.getToteutustyyppiEnum().equals(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA)) {
                    System.out.println("löytyi: " + kh.getOid());
                } else {
                    ResultV1RDTO<ErikoisammattitutkintoV1RDTO> erikoisResult = searchErikoisammattitutkinto(kh.getOid());
                    ErikoisammattitutkintoV1RDTO erikoisKoulutus = erikoisResult.getResult();
                    kw.fetchErikoisInfo(erikoisKoulutus, organisaatioMap, kh, haetutKoodit);
                }
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
                log.info("Skipping on data parsing Koulutus: " + kh.getKomoOid() + ", Type: " + kh.getKoulutusasteTyyppi() + " : "
                        + kh.getKoulutusmoduuliTyyppi().name() + " : " + kh.getToteutustyyppiEnum().name());
                skip++;

            }
            i++;
            String text = "Haetaan ja parsitaan Koulutusta " + (int) i + "/" + haetutKoulutukset.size();
            status = 0.5 + (i / (double) haetutKoulutukset.size() * 0.50);
            // noin 1200 koulutusta minuutissa
            double estimate = (haetutKoulutukset.size() - i) / 1200;
            setStatusObject(estimate, status, text);
        }
        return skip;
    }

    private void fetchOrganisaatiotAndKoulutuksetAndKoodit(HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> hakutulokset, int count) throws Exception {
        int current = 0;
        for (TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> organisaatioData : hakutulokset.getTulokset()) {
            OrganisaatioRDTO organisaatio = searchOrganisation(organisaatioData.getOid());
            haetutOrganisaatiot.add(organisaatio);
            for (KoulutusHakutulosV1RDTO koulutusData : organisaatioData.getTulokset()) {
                if (koulutusData != null) {
                    switch (koulutusData.getKoulutusasteTyyppi().value().toUpperCase()) {
                    case KoulutusAsteTyyppi.AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS:
                    case KoulutusAsteTyyppi.LUKIOKOULUTUS:
                    case KoulutusAsteTyyppi.AMMATILLINENPERUSKOULUTUS:
                        if (checkKoulutusValidnessFromOpintopolku("koulutus", koulutusData.getOid())) {
                            addKoulutusToArray(koulutusData, count, current);
                            fetchKoodi(koulutusData);
                        }
                        break;
                    case KoulutusAsteTyyppi.AMMATTITUTKINTO:
                    case KoulutusAsteTyyppi.ERIKOISAMMATTITUTKINTO:
                        if (checkKoulutusValidnessFromOpintopolku("adultvocational", koulutusData.getOid())) {
                            addKoulutusToArray(koulutusData, count, current);
                            fetchKoodi(koulutusData);
                        }
                        break;
                    case KoulutusAsteTyyppi.KORKEAKOULUTUS:
                        if (checkKoulutusValidnessFromOpintopolku("highered", koulutusData.getOid())) {
                            addKoulutusToArray(koulutusData, count, current);
                            fetchKoodi(koulutusData);
                        }
                        break;
                    default:
                        log.info("Skipping on data fetch Koulutus: " + koulutusData.getKomoOid() + ", Type: " + koulutusData.getKoulutusasteTyyppi());
                        break;
                    }
                    current++;
                    status = (double) current / (double) count * 0.50;
                    status = (Math.ceil(status * 100.0) / 100.0);
                    double estimate = (double) (count - current) / (double) 1200;
                    String text = "Haetaan alustavat Koulutukset ja Koodisto data " + current + "/" + count;
                    setStatusObject(estimate, status, text);
                }
            }
        }
    }

    private void setStatusObject(double estimate, double status, String text) {
        status = (Math.ceil(status * 100.0) / 100.0);
        estimate = Math.ceil(estimate);
        statusObject.setDurationEstimate(estimate);
        statusObject.setStatus(status);
        statusObject.setStatusText(text);
    }

    private void createInitialStatusObject() {
        statusObject.setDurationEstimate(0.0);
        statusObject.setStatus(status);
        statusObject.setStatusText("Alustetaan...");
    }

    private void fetchKoodi(KoulutusHakutulosV1RDTO koulutusData) {
        List<KoodiType> kt = koodistoClient.getAlakoodis(koulutusData.getKoulutuskoodi());
        Koodi code = new Koodi();
        for (KoodiType k : kt) {
            if (k.getKoodisto().getKoodistoUri().equals("isced2011koulutusaste")) {
                code.setIsced2011koulutusaste(k.getKoodiArvo());
            }
            if (k.getKoodisto().getKoodistoUri().equals("isced2011koulutusalataso3")) {
                code.setIsced2011koulutusalataso3(k.getKoodiArvo());
            }
            haetutKoodit.put(koulutusData.getKoulutuskoodi(), code);
        }
    }

    private <T> T execute(OphHttpRequest resource, final GenericType<T> type) {
        return resource.execute(response -> mapper.readValue(response.asInputStream(), type.getRawClass()));
    }

    private OphHttpRequest get(String key, String... params) {
        return httpclient.get(key, params);
    }

    private <T> T executeWithRetries(OphHttpRequest resource, final GenericType<T> type) {
        return execute(resource.retryOnError(5, 2500), type);
    }

    @SuppressWarnings("unchecked")
    public ResultV1RDTO<KoulutusAmmatillinenPerustutkintoV1RDTO> searchAmmatillinenPerustutkinto(String oid) throws Exception {
        return (ResultV1RDTO<KoulutusAmmatillinenPerustutkintoV1RDTO>) getWithRetries(v1KoulutusResource.path(oid).queryParam("meta", "true"),
                new GenericType<ResultV1RDTO<KoulutusAmmatillinenPerustutkintoV1RDTO>>() {
                });
}

    @SuppressWarnings("unchecked")
    public ResultV1RDTO<AmmattitutkintoV1RDTO> searchAmmattitutkinto(String oid) throws Exception {
        return (ResultV1RDTO<AmmattitutkintoV1RDTO>) getWithRetries(v1KoulutusResource.path(oid).queryParam("meta", "true"),
                new GenericType<ResultV1RDTO<AmmattitutkintoV1RDTO>>() {
                });
}

    @SuppressWarnings("unchecked")
    public ResultV1RDTO<ErikoisammattitutkintoV1RDTO> searchErikoisammattitutkinto(String oid) throws Exception {
        return (ResultV1RDTO<ErikoisammattitutkintoV1RDTO>) getWithRetries(v1KoulutusResource.path(oid).queryParam("meta", "true"),
                new GenericType<ResultV1RDTO<ErikoisammattitutkintoV1RDTO>>() {
                });
}

    @SuppressWarnings("unchecked")
    public ResultV1RDTO<KoulutusKorkeakouluV1RDTO> searchKoulutusKorkeakoulu(String oid) throws Exception {
        return (ResultV1RDTO<KoulutusKorkeakouluV1RDTO>) getWithRetries(v1KoulutusResource.path(oid).queryParam("meta", "true"),
                new GenericType<ResultV1RDTO<KoulutusKorkeakouluV1RDTO>>() {
                });
}

    @SuppressWarnings("unchecked")
    public ResultV1RDTO<ValmistavaKoulutusV1RDTO> searchValmistavaKoulutus(String oid) throws Exception {
        return (ResultV1RDTO<ValmistavaKoulutusV1RDTO>) getWithRetries(v1KoulutusResource.path(oid).queryParam("meta", "true"),
                new GenericType<ResultV1RDTO<ValmistavaKoulutusV1RDTO>>() {
                });
}

    @SuppressWarnings("unchecked")
    public ResultV1RDTO<KoulutusLukioV1RDTO> searchKoulutusLukio(String oid) throws Exception {
        return (ResultV1RDTO<KoulutusLukioV1RDTO>) getWithRetries(v1KoulutusResource.path(oid).queryParam("meta", "true"), new GenericType<ResultV1RDTO<KoulutusLukioV1RDTO>>() {
        });
}

    public OrganisaatioRDTO searchOrganisation(String oid) throws Exception {
        return (OrganisaatioRDTO) getWithRetries(v1OrganisaatioResource.path(oid).queryParam("meta", "true"), new GenericType<OrganisaatioRDTO>() {
        });
    }

    public boolean checkKoulutusValidnessFromOpintopolku(String type, String oid) {
        return get("koulutusinformaatio.validoid", type, oid).
                retryOnError(6, 2500).
                skipResponseAssertions().
                execute( response -> response.getStatusCode() == 200);
    }

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
        int retries = 5;
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


    private void addKoulutusToArray(KoulutusHakutulosV1RDTO koulutusData, int count, int current) {
        log.debug("Adding : " + koulutusData.getOid());
        haetutKoulutukset.add(koulutusData);
    }
}
