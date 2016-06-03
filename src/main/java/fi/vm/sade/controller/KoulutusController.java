package fi.vm.sade.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.europa.ec.learningopportunities.v0_5_10.LearningOpportunity;
import eu.europa.ec.learningopportunities.v0_5_10.ObjectFactory;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.TarjoajaHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;

@RestController
public class KoulutusController {
	private static String tarjontaURI = 	"https://testi.virkailija.opintopolku.fi/tarjonta-service/rest/";
	private static String organisaatioURI = "https://testi.virkailija.opintopolku.fi/organisaatio-service/rest/";
	private static final String JSON_UTF8 = MediaType.APPLICATION_JSON + ";charset=UTF-8";
	private ArrayList<KoulutusHakutulosV1RDTO> haetutKoulutukset;
	private ArrayList<TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO>> haetutOrganisaatiot;
	private WebResource v1KoulutusResource;
	private WebResource v1OrganisaatioResource;

	@RequestMapping("/koulutus/")
	public String getKoulutukset() throws IOException {

		haetutKoulutukset = new ArrayList<KoulutusHakutulosV1RDTO>();
		haetutOrganisaatiot = new ArrayList<TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO>>();
		ObjectMapper mapper = new ObjectMapper();
		JacksonJsonProvider jacksProv = new JacksonJsonProvider(mapper);
		ClientConfig cc = new DefaultClientConfig();
		cc.getSingletons().add(jacksProv);
		Client clientWithJacksonSerializer = Client.create(cc);
		v1KoulutusResource = clientWithJacksonSerializer.resource(tarjontaURI + "v1/koulutus");
		v1OrganisaatioResource = clientWithJacksonSerializer.resource(organisaatioURI + "organisaatio");
		/*ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> result = null;
		try {
			result = searchEducation("1.2.246.562.17.99021282233");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> hakutulokset; //= result.getResult();
		
		KoulutusHakutulosV1RDTO koulutusHakutulos; //= hakutulokset.getTulokset().get(0).getTulokset().get(0);
		
		ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> organisaatioResult = null;

		try {
			organisaatioResult = searchOrganisationsEducations(""); //1.2.246.562.10.53642770753
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> organisaationHakutulos = organisaatioResult.getResult();

		Iterator<TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO>> iter = organisaationHakutulos.getTulokset().iterator();
		while(iter.hasNext()){
			TarjoajaHakutulosV1RDTO<KoulutusHakutulosV1RDTO> organisaatioData = iter.next();
			ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> organisaatio = null;
			//try {
			//organisaatio = searchOrganisation(organisaatioData.getOid());
			//} catch (Exception e) {
			// TODO Auto-generated catch block
			//	e.printStackTrace();
			//}
			//haetutOrganisaatiot.add(organisaatio.getResult().getTulokset().get(0).getTulokset().get(0));
			Iterator<KoulutusHakutulosV1RDTO> iter2 = organisaatioData.getTulokset().iterator();
			while(iter2.hasNext()){
				KoulutusHakutulosV1RDTO koulutusData = iter2.next();
				if(koulutusData != null){
					haetutKoulutukset.add(koulutusData);
				}
			}
		}
		ObjectFactory of = new ObjectFactory();
		LearningOpportunity lo = of.createLearningOpportunity();
		lo.setLearningOpportunityId(haetutKoulutukset.get(0).getOid());
		
		System.out.println("lo id = " + lo.getLearningOpportunityId());
		koulutusHakutulos = haetutKoulutukset.get(0);
		/*
		 * BufferedReader in = new BufferedReader(new
		 * InputStreamReader(connection.getInputStream())); StringBuilder
		 * sbuilder = new StringBuilder(); String aux = ""; String output = "";
		 * while ( (aux = in.readLine()) != null) { sbuilder.append(aux); output
		 * += (aux); }
		 */
		/*
		 * ResultV1RDTO<KoulutusV1RDTO> r;
		 * 
		 * 
		 * r = mapper.readValue(connection.getInputStream(),
		 * ResultV1RDTO.class);
		 * 
		 * ResultV1RDTO<KoulutusV1RDTO> result = (ResultV1RDTO<KoulutusV1RDTO>)
		 * r;
		 */
		/*
		 * ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> r;
		 * ObjectMapper mapper = new ObjectMapper();
		 * 
		 * mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
		 * false); mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		 * 
		 * r = mapper.readValue(connection.getInputStream(), new
		 * TypeReference<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO
		 * >>>(){ });
		 * 
		 * HakutuloksetV1RDTO result = (HakutuloksetV1RDTO) r.getResult();
		 */
		// System.out.println(r.getResult());

		// KoulutusV1RDTO result = r.getResult();

		return "Koulutus" + "<br><br> getCreatedBy: " + koulutusHakutulos.getCreatedBy() 
				+ "<br><br> getKausiUri: "	+ koulutusHakutulos.getKausiUri() 
				+ "<br><br> getKomoOid: " + koulutusHakutulos.getKomoOid()
				+ "<br><br> getKoulutuksenTarjoajaKomoto: " + koulutusHakutulos.getKoulutuksenTarjoajaKomoto()
				+ "<br><br> getKoulutuskoodi: " + koulutusHakutulos.getKoulutuskoodi() 
				+ "<br><br> getKoulutuslajiUri: " + koulutusHakutulos.getKoulutuslajiUri() 
				+ "<br><br> getModifiedBy: " + koulutusHakutulos.getModifiedBy() 
				+ "<br><br> getOid: " + koulutusHakutulos.getOid()
				+ "<br><br> getParentKomoOid: " + koulutusHakutulos.getParentKomoOid() 
				+ "<br><br> getVersion: " + koulutusHakutulos.getVersion() 
				+ "<br><br> getVuosi: " + koulutusHakutulos.getVuosi()
				+ "<br><br> getCreated:" + koulutusHakutulos.getCreated() 
				+ "<br><br> getKausi:"	+ koulutusHakutulos.getKausi() 
				+ "<br><br> getKoulutuksenAlkamisPvmMax: "	+ koulutusHakutulos.getKoulutuksenAlkamisPvmMax() 
				+ "<br><br> getKoulutusasteTyyppi: " + koulutusHakutulos.getKoulutusasteTyyppi() 
				+ "<br><br> getKoulutuslaji: "	+ koulutusHakutulos.getKoulutuslaji() 
				+ "<br><br> getKoulutusmoduuliTyyppi: "	+ koulutusHakutulos.getKoulutusmoduuliTyyppi() 
				+ "<br><br> getModified: "	+ koulutusHakutulos.getModified() 
				+ "<br><br> getNimi: " + koulutusHakutulos.getNimi()
				+ "<br><br> getOpetuskielet: " + koulutusHakutulos.getOpetuskielet()
				+ "<br><br> getPohjakoulutusvaatimus: " + koulutusHakutulos.getPohjakoulutusvaatimus()
				+ "<br><br> getSiblingKomotos: " + koulutusHakutulos.getSiblingKomotos() 
				+ "<br><br> getTarjoajat: " + koulutusHakutulos.getTarjoajat() 
				+ "<br><br> getTila: " + koulutusHakutulos.getTila()
				+ "<br><br> getToteutustyyppiEnum: " + koulutusHakutulos.getToteutustyyppiEnum();
	}

	@SuppressWarnings("unchecked")
	public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> searchEducation(String oid) throws Exception {
		return (ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>) getWithRetries(
				v1KoulutusResource.path("search").queryParam("koulutusOid", oid).queryParam("tila", "KAIKKI"),
				new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
				});
	}
	
	@SuppressWarnings("unchecked")
	public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> searchOrganisation(String oid) throws Exception {
		return (ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>) getWithRetries(
				v1OrganisaatioResource.path(oid),
				new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
				});
	}

	@SuppressWarnings("unchecked")
	public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> searchOrganisationsEducations(String OrganisationOid) throws Exception {
		if (OrganisationOid == "") {
			return (ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>) getWithRetries(v1KoulutusResource
					.path("search").queryParam("tila", "JULKAISTU"),
					new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>(){});
		} else {
			return (ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>) getWithRetries(v1KoulutusResource
					.path("search").queryParam("organisationOid", OrganisationOid).queryParam("tila", "JULKAISTU"),
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
