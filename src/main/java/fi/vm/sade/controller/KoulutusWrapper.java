package fi.vm.sade.controller;

import java.util.HashMap;

import eu.europa.ec.learningopportunities.v0_5_10.I18NNonEmptyString;
import eu.europa.ec.learningopportunities.v0_5_10.I18NUrl;
import eu.europa.ec.learningopportunities.v0_5_10.LanguageCode;
import eu.europa.ec.learningopportunities.v0_5_10.LearningOpportunity;
import eu.europa.ec.learningopportunities.v0_5_10.ObjectFactory;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.AmmattitutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.ErikoisammattitutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusAmmatillinenPerustutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.ValmistavaKoulutusV1RDTO;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;

public class KoulutusWrapper {
	public static String COUNTRY_CODE = "FI";
	public static String TITLE_LANG_CODE = "en";
	
	ObjectFactory of;
	I18NNonEmptyString i18Non;
	KoulutusHakutulosV1RDTO kh;
	
	public KoulutusWrapper(){
		of = new ObjectFactory();
		i18Non = of.createI18NNonEmptyString();
	}
	
	public void fetchTuntematonInfo(KoulutusAmmatillinenPerustutkintoV1RDTO k){
		LearningOpportunity lo = of.createLearningOpportunity();
		
		lo.setLearningOpportunityId(k.getOid());
		lo.setCountryCode(COUNTRY_CODE);
		
		i18Non.setValue(kh.getNimi().get(TITLE_LANG_CODE));
		i18Non.setLanguage(LanguageCode.fromValue(TITLE_LANG_CODE));
		lo.getTitle().add(i18Non);
		
		for(String s : k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis().values()){
			if(s != null && !s.isEmpty()){
				i18Non.setValue(s);
				i18Non.setLanguage(LanguageCode.fromValue(s.substring(s.length()-2, s.length()))); // Sub removes "kieli_" and leaves the country code
				lo.getDescription().add(i18Non);
			}
		}
		
		//lo.getUrl().add(new I18NUrl())
	}
	
	public void fetchAmmattiInfo(AmmattitutkintoV1RDTO k){
		
	}
	
	public void fetchErikoisInfo(ErikoisammattitutkintoV1RDTO k){
		
	}
	
	public void fetchKorkeaInfo(KoulutusKorkeakouluV1RDTO k){
		
	}
	
	public void fetchValmistavaInfo(ValmistavaKoulutusV1RDTO k){
		
	}
	
	public void fetchLukioInfo(KoulutusLukioV1RDTO k){
		
	}
	
	public void setKoulutusHakutulos(KoulutusHakutulosV1RDTO kh){
		this.kh = kh;
	}
}