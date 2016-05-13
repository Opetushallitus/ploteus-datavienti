package fi.vm.sade.ploteus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.vm.sade.tarjonta.service.resources.dto.BaseRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;

/**
 * Hello world!
 *
 */
public class App {
	private static String wsURI = "https://testi.virkailija.opintopolku.fi/tarjonta-service/rest/v1/koulutus/1.2.246.562.17.99021282233";

	public static void main(String[] args) {
		System.out.println("aloitus");
		HakukohdeDTO h = new HakukohdeDTO();
		KoulutusHakutulosV1RDTO kh = new KoulutusHakutulosV1RDTO();
		HakuV1RDTO haku = new HakuV1RDTO();
		ResultV1RDTO<HakukohdeHakutulosV1RDTO> hk = new ResultV1RDTO<HakukohdeHakutulosV1RDTO>();
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
		    }*/
		    ObjectMapper mapper = new ObjectMapper();
		    hk = mapper.readValue(connection.getInputStream(), ResultV1RDTO.class);
		    System.out.println(((HakukohdeHakutulosV1RDTO) hk.getResult()).getOid());
		    System.out.println(hk.toString());
		    System.out.println("lopetus");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
