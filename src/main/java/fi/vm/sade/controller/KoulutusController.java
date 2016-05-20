package fi.vm.sade.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KoulutusController {
	
	@RequestMapping("/koulutus/")
	public String getKoulutukset(){
		return "Koulutus";
	}
}
