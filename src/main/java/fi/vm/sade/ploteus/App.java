package fi.vm.sade.ploteus;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import fi.vm.sade.controller.IndexController;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;

@SpringBootApplication
@Configuration
@ImportResource({"classpath*:applicationContext.xml"})
@ComponentScan("fi.vm.sade")
public class App {
	private static String wsURI = "https://testi.virkailija.opintopolku.fi/tarjonta-service/rest/v1/koulutus/1.2.246.562.17.99021282233";

	public static void main(String[] args) {
		System.out.println("aloitus");
		/*HakukohdeDTO h = new HakukohdeDTO();
		KoulutusHakutulosV1RDTO kh = new KoulutusHakutulosV1RDTO();
		HakuV1RDTO haku = new HakuV1RDTO();
		ResultV1RDTO<KoulutusV1RDTO> hk = new ResultV1RDTO<KoulutusV1RDTO>();
		ResultV1RDTO r = new ResultV1RDTO();
		
		try {
		    URL url = new URL(wsURI);
		    URLConnection connection = url.openConnection();
		    /*BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		    StringBuilder sbuilder = new StringBuilder();
		    String aux = "";
		    while ( (aux = in.readLine()) != null) {
		        sbuilder.append(aux);
		        System.out.println(aux);
		    } 
		    ObjectMapper mapper = new ObjectMapper();
		    //hk = mapper.readValue(connection.getInputStream(), ResultV1RDTO<KoulutusV1RDTO>.class);
		    System.out.println(( hk.getResult()).getOid());
		    System.out.println(hk.toString());
		    System.out.println("lopetus");
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		ApplicationContext ctx = SpringApplication.run(App.class, args);
        
        System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
    }
	/*
	@Path("/organisaatio")
	public interface OrganisaatioResource {

	    /**
	     * NOTE: USED BY SECURITY FRAMEWORK - DON'T CHANGE Find oids of
	     * organisaatio's parents, result oids start from root, ends to given oid
	     * itself, and are separated by '/'.
	     *
	     */ /*
	    @GET
	    @Produces(MediaType.TEXT_PLAIN)
	    @Path("/{oid}/parentoids")
	    public String parentoids(@PathParam("oid") String oid) throws Exception;

	    @GET
	    @Produces(MediaType.TEXT_PLAIN)
	    @Path("/hello")
	    public String hello();

	    /**
	     * Get list of Organisaatio oids mathching the query.
	     * <p/>
	     * Search terms:
	     * <ul>
	     * <li>searchTerms=type=KOULUTUSTOIMIJA / OPPILAITOS / TOIMIPISTE ==
	     * OrganisaatioTyyppi.name()</li>
	     * </ul>
	     *
	     */ /*
	    @GET
	    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	    public List<String> search(@QueryParam("searchTerms") String searchTerms, @QueryParam("count") int count,
	            @QueryParam("startIndex") int startIndex, @QueryParam("lastModifiedBefore") Date lastModifiedBefore,
	            @QueryParam("lastModifiedSince") Date lastModifiedSince);

	    /**
	     * Organisaatio DTO as JSON.
	     *
	     * @param oid OID or Y-TUNNUS or VIRASTOTUNNUS or OPETUSPISTEKOODI or
	     * TOIMIPISTEKOODI
	     */ /*
	    @GET
	    @Path("{oid}")
	    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	    public OrganisaatioV1RDTO getOrganisaatioByOID(@PathParam("oid") String oid);

	    @GET
	    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	    @Path("/{oid}/children")
	    public List<OrganisaatioV1RDTO> children(
	            @PathParam("oid") String oid,
	            @DefaultValue("false") @QueryParam("includeImage") boolean includeImage) throws Exception;

	}
	*/

}
