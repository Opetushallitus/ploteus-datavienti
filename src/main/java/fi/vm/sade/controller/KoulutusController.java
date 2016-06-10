package fi.vm.sade.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.helpers.IOUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.europa.ec.learningopportunities.v0_5_10.I18NNonEmptyString;
import eu.europa.ec.learningopportunities.v0_5_10.LanguageCode;
import eu.europa.ec.learningopportunities.v0_5_10.LearningOpportunity;
import eu.europa.ec.learningopportunities.v0_5_10.ObjectFactory;
import fi.vm.sade.model.KoulutusAsteTyyppi;
import fi.vm.sade.model.StatusObject;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.TarjoajaHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.AmmattitutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.ErikoisammattitutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusAmmatillinenPerustutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.ValmistavaKoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;

@RestController
public class KoulutusController {
	private static String tarjontaURI = 	"https://testi.virkailija.opintopolku.fi/tarjonta-service/rest/";
	private static String organisaatioURI = "https://virkailija.opintopolku.fi/organisaatio-service/rest/";
	private static final String JSON_UTF8 = MediaType.APPLICATION_JSON + ";charset=UTF-8";
	
	private ArrayList<KoulutusHakutulosV1RDTO> haetutKoulutukset;
	private ArrayList<OrganisaatioRDTO> haetutOrganisaatiot;
	private ArrayList<LearningOpportunity> LearningOpportunitys;
	
	private WebResource v1KoulutusResource;
	private WebResource v1OrganisaatioResource;

	private static final String FILE_PATH = "pom.xml";
	
	private double status;
	private StatusObject statusObject;
	
	@RequestMapping("koulutus/status")
	public String getStatus() throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		String JsonStatus = mapper.writeValueAsString(statusObject);
		return JsonStatus;
	}
	
	@GET
	@RequestMapping("/download")
	public void download(HttpServletResponse response) throws IOException{
	    File file = new File(FILE_PATH);
	    InputStream myStream = new FileInputStream(file);
	 // Set the content type and attachment header.
		response.addHeader("Content-disposition", "attachment;filename=" + file.getName());
		response.setContentType("txt/plain");

		// Copy the stream to the response's output stream.
		IOUtils.copy(myStream, response.getOutputStream());
		response.flushBuffer();
	    /*System.out.println(file.getAbsolutePath());
	    System.out.println(file.isFile());
	    System.out.println(file.getName());
	    Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
	      .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" ) //optional
	      .build();*/
	    
	}
	@RequestMapping("/koulutus/")
	public String getKoulutukset() throws Exception {
		status = 0.01;
		statusObject = new StatusObject();
		statusObject.setDurationEstimate(0.0);
		statusObject.setStatus(status);
		statusObject.setStatusText("Alustetaan...");
		haetutKoulutukset = new ArrayList<KoulutusHakutulosV1RDTO>();
		haetutOrganisaatiot = new ArrayList<OrganisaatioRDTO>();
		LearningOpportunitys = new ArrayList<LearningOpportunity>();
		
		ObjectMapper mapper = new ObjectMapper();	//Jacksonin mapper ja confaus
		JacksonJsonProvider jacksProv = new JacksonJsonProvider(mapper);
		ClientConfig cc = new DefaultClientConfig();
		cc.getSingletons().add(jacksProv);
		Client clientWithJacksonSerializer = Client.create(cc);
		
		
		v1KoulutusResource = clientWithJacksonSerializer.resource(tarjontaURI + "v1/koulutus"); //tarjonnan koulutus url
		v1OrganisaatioResource = clientWithJacksonSerializer.resource(organisaatioURI + "organisaatio"); //organisaatio palvelun url
		
		ObjectFactory of = new ObjectFactory();
		
		ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> organisaatioResult = null;

		statusObject.setStatusText("Haetaan Organisaatio dataa...");

		organisaatioResult = searchOrganisationsEducations("1.2.246.562.10.53642770753"); //1.2.246.562.10.53642770753 tai tyhja kaikille tuloksille
		HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> hakutulokset = organisaatioResult.getResult(); //poistetaan result container
		Iterator<TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO>> iter = hakutulokset.getTulokset().iterator();
		double numberOfOrganisations = hakutulokset.getTulokset().size();
		double numberOfCurrentOrganisation = 0.0;
		while(iter.hasNext()){	//iteroidaan kaikki organisaatiot lapi tuloksesta
			TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> organisaatioData = iter.next();
			OrganisaatioRDTO organisaatio = null;
			organisaatio = searchOrganisation(organisaatioData.getOid());
			haetutOrganisaatiot.add(organisaatio);	//lisataan organisaatio
			numberOfCurrentOrganisation++;
			
			Iterator<KoulutusHakutulosV1RDTO> iter2 = organisaatioData.getTulokset().iterator();
			while(iter2.hasNext()){	//iteroidaan kaikki koulukset lapi organisaatiolta
				KoulutusHakutulosV1RDTO koulutusData = iter2.next();
				if(koulutusData != null){
					haetutKoulutukset.add(koulutusData);	//lisataan koulutus
					status = numberOfCurrentOrganisation / numberOfOrganisations  * 0.30;
					//System.out.println(status);
					status = (Math.ceil(status * 100.0) / 100.0);
					statusObject.setStatus(status);
					//System.out.println(status);
				}
			}
		}
		statusObject.setDurationEstimate(haetutKoulutukset.size() / 1200);	//noin 1200 koulutusta minuutissa
		statusObject.setStatusText("Haetaan ja parsitaan Koulutus dataa...");

		Iterator<KoulutusHakutulosV1RDTO> iter3 = haetutKoulutukset.iterator();
		ArrayList<String> myList = new ArrayList<String>();
		myList.add("");
		double i = 0.0;
		while(iter3.hasNext()){	//iteroidaan koulutukset ja luodaan niista LearningOpportunityja
			KoulutusHakutulosV1RDTO kh = iter3.next();
			KuvausV1RDTO<KomoTeksti> kuvaus = null;
			
			switch(kh.getToteutustyyppiEnum().name()) {
				case KoulutusAsteTyyppi.TUNTEMATON:
					ResultV1RDTO<KoulutusAmmatillinenPerustutkintoV1RDTO> tuntematonResult = searchAmmatillinenPerustutkinto(kh.getOid());
					KoulutusAmmatillinenPerustutkintoV1RDTO tuntematonKoulutus = tuntematonResult.getResult();
					kuvaus = tuntematonKoulutus.getKuvausKomo();
					break;
					
				case KoulutusAsteTyyppi.AMMATTITUTKINTO:
					ResultV1RDTO<AmmattitutkintoV1RDTO> ammattiResult = searchAmmattitutkinto(kh.getOid());
					AmmattitutkintoV1RDTO ammattiKoulutus = ammattiResult.getResult();
					
					//System.out.println("PLSISISISDI" + kuvaus.get(KomoTeksti.TAVOITTEET).getTekstis().get("kieli_sv"));
					
					//for(NimiV1RDTO s : ammattiKoulutus.getKuvausKomo().values()){
						//
					//System.out.println(s.toString());
					//}
					
					break;
					
				case KoulutusAsteTyyppi.ERIKOISAMMATTITUTKINTO:
					ResultV1RDTO<ErikoisammattitutkintoV1RDTO> erikoisResult = searchErikoisammattitutkinto(kh.getOid());
					ErikoisammattitutkintoV1RDTO erikoisKoulutus = erikoisResult.getResult();
					kuvaus = erikoisKoulutus.getKuvausKomo();

					break;
					
				case KoulutusAsteTyyppi.KORKEAKOULUTUS:
					ResultV1RDTO<KoulutusKorkeakouluV1RDTO> koulutusResult = searchKoulutusKorkeakoulu(kh.getOid());
					KoulutusKorkeakouluV1RDTO koulutus = koulutusResult.getResult();
					kuvaus = koulutus.getKuvausKomo();
					break;
					
				case KoulutusAsteTyyppi.AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS:
					ResultV1RDTO<ValmistavaKoulutusV1RDTO> ammValmistavaResult = searchValmistavaKoulutus(kh.getOid());
					ValmistavaKoulutusV1RDTO ammValmistavaKoulutus = ammValmistavaResult.getResult();
					kuvaus = ammValmistavaKoulutus.getKuvausKomo();
					break;
					
				case KoulutusAsteTyyppi.LUKIOKOULUTUS:
					ResultV1RDTO<KoulutusLukioV1RDTO> lukioResult = searchKoulutusLukio(kh.getOid());
					KoulutusLukioV1RDTO lukioKoulutus = lukioResult.getResult();
					kuvaus = lukioKoulutus.getKuvausKomo();
					break;
			}
			if(kuvaus != null)
				System.out.println("LOLNUB:" + kuvaus.get("SV"));
			
			String search = kh.getKoulutusasteTyyppi().name();
			if(!myList.contains(search)){
			    myList.add(search);
			}
			
			
			/* 
			 * getToteutustyyppiEnum()
			 * ----------------------
			 * AMMATILLINEN_PERUSTUTKINTO
			 * AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA
			 * AMMATTITUTKINTO
			 * ERIKOISAMMATTITUTKINTO
			 * KORKEAKOULUTUS
			 * KORKEAKOULUOPINTO
			 * AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS
			 * LUKIOKOULUTUS
			 * MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS
			 * AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA
			 * VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS
			 * AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA
			 * AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER
			 * VAPAAN_SIVISTYSTYON_KOULUTUS
			 * PERUSOPETUKSEN_LISAOPETUS
			 * MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS
			 * LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA
			 * AIKUISTEN_PERUSOPETUS
			 * EB_RP_ISH
			 * 
			 * getKoulutusasteTyyppi()
			 * -----------------------
			 * AMMATILLINEN_PERUSKOULUTUS
			 * AMMATTITUTKINTO
			 * ERIKOISAMMATTITUTKINTO
			 * KORKEAKOULUTUS
			 * AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS
			 * LUKIOKOULUTUS
			 * MAAHANM_AMM_VALMISTAVA_KOULUTUS
			 * TUNTEMATON
			 * VALMENTAVA_JA_KUNTOUTTAVA_OPETUS
			 * VAPAAN_SIVISTYSTYON_KOULUTUS
			 * PERUSOPETUKSEN_LISAOPETUS
			 * MAAHANM_LUKIO_VALMISTAVA_KOULUTUS
			 */
			
			LearningOpportunity lo = of.createLearningOpportunity();
			//System.out.println("komoto: " + kh.getKoulutuksenTarjoajaKomoto());
			lo.setLearningOpportunityId(kh.getOid());
			lo.setCountryCode("FI");
			I18NNonEmptyString i18NonEmptyString = of.createI18NNonEmptyString();
			i18NonEmptyString.setValue(kh.getNimi().get("en"));
			LanguageCode l = LanguageCode.fromValue("en");
			i18NonEmptyString.setLanguage(l);
			lo.getTitle().add(i18NonEmptyString);
			
			i++;
			status =  0.3 + (i / (double) haetutKoulutukset.size() * 0.66); //337.00;
			System.out.println(status);
			status = (Math.ceil(status * 100.0) / 100.0);
			statusObject.setStatus(status);
			statusObject.setDurationEstimate((haetutKoulutukset.size() - i) / 1200);	//noin 1200 koulutusta minuutissa
			LearningOpportunitys.add(lo);
		}
		for(String elem : myList){
			System.out.println(elem);
		}
		status = 1.0;
		statusObject.setStatus(status);
		statusObject.setStatusText("Valmis");
		return "";
	}
	
	@SuppressWarnings("unchecked")
	public ResultV1RDTO<KoulutusAmmatillinenPerustutkintoV1RDTO> searchAmmatillinenPerustutkinto(String oid) throws Exception {
		return (ResultV1RDTO<KoulutusAmmatillinenPerustutkintoV1RDTO>) getWithRetries(
				v1KoulutusResource.path(oid),
				new GenericType<ResultV1RDTO<KoulutusAmmatillinenPerustutkintoV1RDTO>>() {
				});
	}
	
	@SuppressWarnings("unchecked")
	public ResultV1RDTO<AmmattitutkintoV1RDTO> searchAmmattitutkinto(String oid) throws Exception {
		return (ResultV1RDTO<AmmattitutkintoV1RDTO>) getWithRetries(
				v1KoulutusResource.path(oid),
				new GenericType<ResultV1RDTO<AmmattitutkintoV1RDTO>>() {
				});
	}
	
	@SuppressWarnings("unchecked")
	public ResultV1RDTO<ErikoisammattitutkintoV1RDTO> searchErikoisammattitutkinto(String oid) throws Exception {
		return (ResultV1RDTO<ErikoisammattitutkintoV1RDTO>) getWithRetries(
				v1KoulutusResource.path(oid),
				new GenericType<ResultV1RDTO<ErikoisammattitutkintoV1RDTO>>() {
				});
	}
	
	@SuppressWarnings("unchecked")
	public ResultV1RDTO<KoulutusKorkeakouluV1RDTO> searchKoulutusKorkeakoulu(String oid) throws Exception {
		return (ResultV1RDTO<KoulutusKorkeakouluV1RDTO>) getWithRetries(
				v1KoulutusResource.path(oid),
				new GenericType<ResultV1RDTO<KoulutusKorkeakouluV1RDTO>>() {
				});
	}
	@SuppressWarnings("unchecked")
	public ResultV1RDTO<ValmistavaKoulutusV1RDTO> searchValmistavaKoulutus(String oid) throws Exception {
		return (ResultV1RDTO<ValmistavaKoulutusV1RDTO>) getWithRetries(
				v1KoulutusResource.path(oid),
				new GenericType<ResultV1RDTO<ValmistavaKoulutusV1RDTO>>() {
				});
	}
	
	@SuppressWarnings("unchecked")
	public ResultV1RDTO<KoulutusLukioV1RDTO> searchKoulutusLukio(String oid) throws Exception {
		return (ResultV1RDTO<KoulutusLukioV1RDTO>) getWithRetries(
				v1KoulutusResource.path(oid),
				new GenericType<ResultV1RDTO<KoulutusLukioV1RDTO>>() {
				});
	}
	
	public OrganisaatioRDTO searchOrganisation(String oid) throws Exception {
		return (OrganisaatioRDTO ) getWithRetries(
				v1OrganisaatioResource.path(oid),
				new GenericType<OrganisaatioRDTO>() {
				});
	}

	@SuppressWarnings("unchecked")
	public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> searchOrganisationsEducations(String OrganisationOid) throws Exception {
		if (OrganisationOid == "") {
			return (ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>) getWithRetries(v1KoulutusResource
					.path("search").queryParam("tila", "JULKAISTU").queryParam("meta", "true").queryParam("img", "false"),
					new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>(){});
		} else {
			return (ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>) getWithRetries(v1KoulutusResource
					.path("search").queryParam("organisationOid", OrganisationOid).queryParam("tila", "JULKAISTU").queryParam("meta", "true").queryParam("img", "false"),
					new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>(){});
		}
	}

	@SuppressWarnings("unchecked")
	private Object getWithRetries(WebResource resource, GenericType type) throws Exception {
		int retries = 2;
		System.out.println(resource.getURI());
		while (--retries > 0) {
			try {
				return resource.accept(JSON_UTF8).get(type);
			} catch (Exception e) {
				System.out.println("Calling resource failed: " + resource);
				try {
					Thread.sleep(2500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		System.out.println("Calling resource failed, last retry: " + resource);
		try {
			return resource.accept(JSON_UTF8).get(type);
		} catch (Exception e) {
			System.out.println("Calling resource failed: " + resource);
			throw e;
		}
	}
}
