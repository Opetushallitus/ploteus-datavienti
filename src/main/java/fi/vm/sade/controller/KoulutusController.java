package fi.vm.sade.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

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

import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;



@RestController
public class KoulutusController {
	private static String wsURI = "https://testi.virkailija.opintopolku.fi/tarjonta-service/rest/";
	private static final String JSON_UTF8 = MediaType.APPLICATION_JSON + ";charset=UTF-8";
	
	private WebResource v1KoulutusResource;
	
	@RequestMapping("/koulutus/")
	public String getKoulutukset() throws IOException{
		URL url = new URL(wsURI);
	    ObjectMapper mapper = new ObjectMapper();
	    JacksonJsonProvider jacksProv = new JacksonJsonProvider(mapper);
        ClientConfig cc = new DefaultClientConfig();
        cc.getSingletons().add(jacksProv);
        Client clientWithJacksonSerializer = Client.create(cc);
	    v1KoulutusResource = clientWithJacksonSerializer.resource(wsURI + "v1/koulutus");
	    ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> realResult = null;
		try {
			realResult = searchEducation("1.2.246.562.17.99021282233");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO> hakutulokset = realResult.getResult();
		KoulutusHakutulosV1RDTO KoulutusHakutulos = hakutulokset.getTulokset().get(0).getTulokset().get(0);
	    /*BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	    StringBuilder sbuilder = new StringBuilder();
	    String aux = "";
	    String output = "";
	    while ( (aux = in.readLine()) != null) {
	        sbuilder.append(aux);
	        output += (aux);
	    }*/
	    /*ResultV1RDTO<KoulutusV1RDTO> r;
	    
	    
	    r = mapper.readValue(connection.getInputStream(), ResultV1RDTO.class);
	    
	    ResultV1RDTO<KoulutusV1RDTO> result = 
	    		(ResultV1RDTO<KoulutusV1RDTO>) r;
	    */
	   /* ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> r;
	    ObjectMapper mapper = new ObjectMapper();
	    
	    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        
	    r = mapper.readValue(connection.getInputStream(), new TypeReference<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>(){ });
	    
	    HakutuloksetV1RDTO result = 
	    		(HakutuloksetV1RDTO) r.getResult();
	    */
	    //System.out.println(r.getResult());
	    
	    //KoulutusV1RDTO result = r.getResult();
	    
		return "Koulutus" 
		+ "<br><br> getCreatedBy: " + KoulutusHakutulos.getCreatedBy()
		+ "<br><br> getKausiUri: " + KoulutusHakutulos.getKausiUri()
		+ "<br><br> getKomoOid: " + KoulutusHakutulos.getKomoOid()
		+ "<br><br> getKoulutuksenTarjoajaKomoto: " + KoulutusHakutulos.getKoulutuksenTarjoajaKomoto()
		+ "<br><br> getKoulutuskoodi: " + KoulutusHakutulos.getKoulutuskoodi()
		+ "<br><br> getKoulutuslajiUri: " + KoulutusHakutulos.getKoulutuslajiUri()
		+ "<br><br> getModifiedBy: " + KoulutusHakutulos.getModifiedBy()
		+ "<br><br> getOid: " + KoulutusHakutulos.getOid()
		+ "<br><br> getParentKomoOid: " + KoulutusHakutulos.getParentKomoOid()
		+ "<br><br> getVersion: " + KoulutusHakutulos.getVersion()
		+ "<br><br> getVuosi: " + KoulutusHakutulos.getVuosi()
		+ "<br><br> getCreated:" + KoulutusHakutulos.getCreated()
		+ "<br><br> getKausi:" + KoulutusHakutulos.getKausi()
		+ "<br><br> getKoulutuksenAlkamisPvmMax: " + KoulutusHakutulos.getKoulutuksenAlkamisPvmMax()
		+ "<br><br> getKoulutusasteTyyppi: " + KoulutusHakutulos.getKoulutusasteTyyppi()
		+ "<br><br> getKoulutuslaji: " + KoulutusHakutulos.getKoulutuslaji()
		+ "<br><br> getKoulutusmoduuliTyyppi: " + KoulutusHakutulos.getKoulutusmoduuliTyyppi()
		+ "<br><br> getModified: " + KoulutusHakutulos.getModified()
		+ "<br><br> getNimi: " + KoulutusHakutulos.getNimi()
		+ "<br><br> getOpetuskielet: " + KoulutusHakutulos.getOpetuskielet()
		+ "<br><br> getPohjakoulutusvaatimus: " + KoulutusHakutulos.getPohjakoulutusvaatimus()
		+ "<br><br> getSiblingKomotos: " + KoulutusHakutulos.getSiblingKomotos()
		+ "<br><br> getTarjoajat: " + KoulutusHakutulos.getTarjoajat()
		+ "<br><br> getTila: " + KoulutusHakutulos.getTila()
		+ "<br><br> getToteutustyyppiEnum: " + KoulutusHakutulos.getToteutustyyppiEnum();
	}
	
	@SuppressWarnings("unchecked")
	public ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>> searchEducation(String oid) throws Exception {
        return (ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>) getWithRetries(v1KoulutusResource
                        .path("search")
                        .queryParam("koulutusOid", oid)
                        .queryParam("tila", "KAIKKI"),
                new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<KoulutusHakutulosV1RDTO>>>() {
                });
    }
	
	@SuppressWarnings("unchecked")
	private Object getWithRetries(WebResource resource, GenericType type) throws Exception {
        int retries = 2;
        while (--retries > 0) {
            try {
                return resource
                        .accept(JSON_UTF8)
                        .get(type);
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
            return resource
                    .accept(JSON_UTF8)
                    .get(type);
        } catch (Exception e) {
        	System.out.println("Calling resource failed: " + resource);
            throw e;
        }
    }
}
