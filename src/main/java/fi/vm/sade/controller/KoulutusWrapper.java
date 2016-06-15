package fi.vm.sade.controller;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import eu.europa.ec.learningopportunities.v0_5_10.I18NNonEmptyString;
import eu.europa.ec.learningopportunities.v0_5_10.I18NString;
import eu.europa.ec.learningopportunities.v0_5_10.I18NUrl;
import eu.europa.ec.learningopportunities.v0_5_10.LanguageCode;
import eu.europa.ec.learningopportunities.v0_5_10.LearningOpportunities;
import eu.europa.ec.learningopportunities.v0_5_10.LearningOpportunity;
import eu.europa.ec.learningopportunities.v0_5_10.ObjectFactory;
import eu.europa.ec.learningopportunities.v0_5_10.Qualifications;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.AmmattitutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.ErikoisammattitutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusAmmatillinenPerustutkintoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusLukioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.ValmistavaKoulutusV1RDTO;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;

public class KoulutusWrapper {
	public static String COUNTRY_CODE = "FI";
	public static String TITLE_LANG_CODE_EN = "en";
	public static String LANG_CODE_KIELI_EN = "kieli_en";

	private LearningOpportunities learningOpportunities;

	private ObjectFactory of;
	private I18NNonEmptyString i18Non;
	private KoulutusHakutulosV1RDTO kh;
	private I18NUrl i18NUrl;
	private JAXBParser JAXBParser;

	public KoulutusWrapper() {
		of = new ObjectFactory();
		i18Non = of.createI18NNonEmptyString();
		i18NUrl = of.createI18NUrl();
		learningOpportunities = of.createLearningOpportunities();
		JAXBParser = new JAXBParser();
	}
	
	public void fetchAmmatillinenPerustutkintoInfo(KoulutusAmmatillinenPerustutkintoV1RDTO k) {
		LearningOpportunity lo = of.createLearningOpportunity();

		// ID & COUNTRY CODE
		lo.setLearningOpportunityId(k.getOid());
		lo.setCountryCode(COUNTRY_CODE);

		this.setTitle(kh.getNimi().get(TITLE_LANG_CODE_EN), lo);
		if(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null){
			this.setDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
		}	
		// Url
		i18NUrl.setValue("https://opintopolku.fi/app/#!/koulutus/" + k.getOid());
		lo.getUrl().add(i18NUrl);

		// Teaching Language
		this.setTeachingLangs(k.getOpetuskielis().getMeta(), lo);

		// DurationInformation
		if (k.getSuunniteltuKestoArvo() != null) {
			this.setDurationInformation(k.getSuunniteltuKestoArvo() + " " + k.getSuunniteltuKestoTyyppi().getNimi(), lo);
		}
		this.setDate(k.getKoulutuksenAlkamisPvms(), lo);

		Qualifications qualifications = of.createQualifications();
		this.setQualificationAwarded(k.getTutkintonimikes().getMeta(), qualifications);
		if(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null){
			this.setQualificationDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), qualifications);
		}		
		lo.getQualifications().add(qualifications);
		
		/*I18NString temp = of.createI18NString();
		temp.setValue(k.getHintaString());
		lo.getCosts().add(temp);*/
		
		if(k.getOpintojenLaajuusarvo() != null && k.getOpintojenLaajuusyksikko().getMeta() != null){
			this.setCredits(k.getOpintojenLaajuusarvo().getArvo() + " " + k.getOpintojenLaajuusyksikko().getMeta().get(LANG_CODE_KIELI_EN).getNimi(), lo);
		}
		
		//InformationLanguage
		//k.getKuvausKomo().get("TAVOITTEET").getTekstis().get("kieli_" + k.getKuvausKomo().get("TAVOITTEET").getMeta().get("kieliArvo").getArvo().toLowerCase().trim());
		//lo.setInformationLanguage(LanguageCode.fromValue(k.getKuvausKomo().get("TAVOITTEET").getMeta().get("kieliArvo").getArvo().toLowerCase().trim()));
		
		learningOpportunities.getLearningOpportunity().add(lo);
	}

	public void fetchAmmattiInfo(AmmattitutkintoV1RDTO k) {
		LearningOpportunity lo = of.createLearningOpportunity();

		// ID & COUNTRY CODE
		lo.setLearningOpportunityId(k.getOid());
		lo.setCountryCode(COUNTRY_CODE);

		this.setTitle(kh.getNimi().get(TITLE_LANG_CODE_EN), lo);
		if(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null){
			this.setDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
		}		
		// Url
		i18NUrl.setValue("https://opintopolku.fi/app/#!/koulutus/" + k.getOid());
		lo.getUrl().add(i18NUrl);

		// Teaching Language
		this.setTeachingLangs(k.getOpetuskielis().getMeta(), lo);

		// DurationInformation
		if (k.getSuunniteltuKestoArvo() != null) {
			this.setDurationInformation(k.getSuunniteltuKestoArvo() + " " + k.getSuunniteltuKestoTyyppi().getNimi(), lo);
		}
		this.setDate(k.getKoulutuksenAlkamisPvms(), lo);

		Qualifications qualifications = of.createQualifications();
		//this.setQualificationAwarded(k.getTutkintonimikes().getMeta(), qualifications);
		if(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null){
			this.setQualificationDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), qualifications);
		}		
		lo.getQualifications().add(qualifications);
		
		/*I18NString temp = of.createI18NString();
		temp.setValue(k.getHintaString());
		lo.getCosts().add(temp);*/
		
		if(k.getOpintojenLaajuusarvo() != null && k.getOpintojenLaajuusyksikko().getMeta() != null){
			this.setCredits(k.getOpintojenLaajuusarvo().getArvo() + " " + k.getOpintojenLaajuusyksikko().getMeta().get(LANG_CODE_KIELI_EN).getNimi(), lo);
		}
		
		//InformationLanguage
		//k.getKuvausKomo().get("TAVOITTEET").getTekstis().get("kieli_" + k.getKuvausKomo().get("TAVOITTEET").getMeta().get("kieliArvo").getArvo().toLowerCase().trim());
		//lo.setInformationLanguage(LanguageCode.fromValue(k.getKuvausKomo().get("TAVOITTEET").getMeta().get("kieliArvo").getArvo().toLowerCase().trim()));
		
		learningOpportunities.getLearningOpportunity().add(lo);
	}

	public void fetchErikoisInfo(ErikoisammattitutkintoV1RDTO k) {
		LearningOpportunity lo = of.createLearningOpportunity();

		// ID & COUNTRY CODE
		lo.setLearningOpportunityId(k.getOid());
		lo.setCountryCode(COUNTRY_CODE);

		this.setTitle(kh.getNimi().get(TITLE_LANG_CODE_EN), lo);
		if(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null){
			this.setDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
		}		
		// Url
		i18NUrl.setValue("https://opintopolku.fi/app/#!/koulutus/" + k.getOid());
		lo.getUrl().add(i18NUrl);

		// Teaching Language
		this.setTeachingLangs(k.getOpetuskielis().getMeta(), lo);

		// DurationInformation
		if (k.getSuunniteltuKestoArvo() != null) {
			this.setDurationInformation(k.getSuunniteltuKestoArvo() + " " + k.getSuunniteltuKestoTyyppi().getNimi(), lo);
		}
		this.setDate(k.getKoulutuksenAlkamisPvms(), lo);

		Qualifications qualifications = of.createQualifications();
		//this.setQualificationAwarded(k.getTutkintonimikes().getMeta(), qualifications);
		if(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null){
			this.setQualificationDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), qualifications);
		}		
		lo.getQualifications().add(qualifications);
		
		/*I18NString temp = of.createI18NString();
		temp.setValue(k.getHintaString());
		lo.getCosts().add(temp);*/
		
		if(k.getOpintojenLaajuusarvo() != null && k.getOpintojenLaajuusyksikko().getMeta() != null){
			this.setCredits(k.getOpintojenLaajuusarvo().getArvo() + " " + k.getOpintojenLaajuusyksikko().getMeta().get(LANG_CODE_KIELI_EN).getNimi(), lo);
		}
		
		//InformationLanguage
		//k.getKuvausKomo().get("TAVOITTEET").getTekstis().get("kieli_" + k.getKuvausKomo().get("TAVOITTEET").getMeta().get("kieliArvo").getArvo().toLowerCase().trim());
		//lo.setInformationLanguage(LanguageCode.fromValue(k.getKuvausKomo().get("TAVOITTEET").getMeta().get("kieliArvo").getArvo().toLowerCase().trim()));
		
		learningOpportunities.getLearningOpportunity().add(lo);
	}

	public void fetchKorkeaInfo(KoulutusKorkeakouluV1RDTO k) {
		LearningOpportunity lo = of.createLearningOpportunity();

		// ID & COUNTRY CODE
		lo.setLearningOpportunityId(k.getOid());
		lo.setCountryCode(COUNTRY_CODE);

		this.setTitle(kh.getNimi().get(TITLE_LANG_CODE_EN), lo);
		
		if(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null){
			this.setDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
		}
		
		// Url
		i18NUrl.setValue("https://opintopolku.fi/app/#!/koulutus/" + k.getOid());
		lo.getUrl().add(i18NUrl);

		// Teaching Language
		this.setTeachingLangs(k.getOpetuskielis().getMeta(), lo);

		// DurationInformation
		if (k.getSuunniteltuKestoArvo() != null) {
			this.setDurationInformation(k.getSuunniteltuKestoArvo() + " " + k.getSuunniteltuKestoTyyppi().getNimi(), lo);
		}
		this.setDate(k.getKoulutuksenAlkamisPvms(), lo);

		Qualifications qualifications = of.createQualifications();
		this.setQualificationAwarded(k.getTutkintonimikes().getMeta(), qualifications);
		if(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null){
			this.setQualificationDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), qualifications);
		}
		lo.getQualifications().add(qualifications);
		
		/*I18NString temp = of.createI18NString();
		temp.setValue(k.getHintaString());
		lo.getCosts().add(temp);*/
		
		if(k.getOpintojenLaajuusarvo() != null && k.getOpintojenLaajuusyksikko().getMeta() != null){
			this.setCredits(k.getOpintojenLaajuusarvo().getArvo() + " " + k.getOpintojenLaajuusyksikko().getMeta().get(LANG_CODE_KIELI_EN).getNimi(), lo);
		}
		
		//InformationLanguage
		//k.getKuvausKomo().get("TAVOITTEET").getTekstis().get("kieli_" + k.getKuvausKomo().get("TAVOITTEET").getMeta().get("kieliArvo").getArvo().toLowerCase().trim());
		//lo.setInformationLanguage(LanguageCode.fromValue(k.getKuvausKomo().get("TAVOITTEET").getMeta().get("kieliArvo").getArvo().toLowerCase().trim()));
		
		learningOpportunities.getLearningOpportunity().add(lo);
	}

	public void fetchValmistavaInfo(ValmistavaKoulutusV1RDTO k) {
		LearningOpportunity lo = of.createLearningOpportunity();

		// ID & COUNTRY CODE
		lo.setLearningOpportunityId(k.getOid());
		lo.setCountryCode(COUNTRY_CODE);

		this.setTitle(kh.getNimi().get(TITLE_LANG_CODE_EN), lo);
		if(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null){
			this.setDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
		}	
		// Url
		i18NUrl.setValue("https://opintopolku.fi/app/#!/koulutus/" + k.getOid());
		lo.getUrl().add(i18NUrl);

		// Teaching Language
		this.setTeachingLangs(k.getOpetuskielis().getMeta(), lo);

		// DurationInformation
		if (k.getSuunniteltuKestoArvo() != null) {
			this.setDurationInformation(k.getSuunniteltuKestoArvo() + " " + k.getSuunniteltuKestoTyyppi().getNimi(), lo);
		}
		this.setDate(k.getKoulutuksenAlkamisPvms(), lo);

		Qualifications qualifications = of.createQualifications();
		//this.setQualificationAwarded(k.getTutkintonimikes().getMeta(), qualifications);
		if(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null){
			this.setQualificationDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), qualifications);
		}		
		lo.getQualifications().add(qualifications);
		
		/*I18NString temp = of.createI18NString();
		temp.setValue(k.getHintaString());
		lo.getCosts().add(temp);*/
		
		if(k.getOpintojenLaajuusarvo() != null && k.getOpintojenLaajuusyksikko().getMeta() != null){
			this.setCredits(k.getOpintojenLaajuusarvo().getArvo() + " " + k.getOpintojenLaajuusyksikko().getMeta().get(LANG_CODE_KIELI_EN).getNimi(), lo);
		}
		
		//InformationLanguage
		//k.getKuvausKomo().get("TAVOITTEET").getTekstis().get("kieli_" + k.getKuvausKomo().get("TAVOITTEET").getMeta().get("kieliArvo").getArvo().toLowerCase().trim());
		//lo.setInformationLanguage(LanguageCode.fromValue(k.getKuvausKomo().get("TAVOITTEET").getMeta().get("kieliArvo").getArvo().toLowerCase().trim()));
		
		learningOpportunities.getLearningOpportunity().add(lo);
	}

	public void fetchLukioInfo(KoulutusLukioV1RDTO k) {
		LearningOpportunity lo = of.createLearningOpportunity();

		// ID & COUNTRY CODE
		lo.setLearningOpportunityId(k.getOid());
		lo.setCountryCode(COUNTRY_CODE);

		this.setTitle(kh.getNimi().get(TITLE_LANG_CODE_EN), lo);
		if(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null){
			this.setDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
		}		
		// Url
		i18NUrl.setValue("https://opintopolku.fi/app/#!/koulutus/" + k.getOid());
		lo.getUrl().add(i18NUrl);

		// Teaching Language
		this.setTeachingLangs(k.getOpetuskielis().getMeta(), lo);

		// DurationInformation
		if (k.getSuunniteltuKestoArvo() != null) {
			this.setDurationInformation(k.getSuunniteltuKestoArvo() + " " + k.getSuunniteltuKestoTyyppi().getNimi(), lo);
		}
		this.setDate(k.getKoulutuksenAlkamisPvms(), lo);

		Qualifications qualifications = of.createQualifications();
		//this.setQualificationAwarded(k.getTutkintonimikes().getMeta(), qualifications);
		if(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null){
			this.setQualificationDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), qualifications);
		}		
		lo.getQualifications().add(qualifications);
		
		/*I18NString temp = of.createI18NString();
		temp.setValue(k.getHintaString());
		lo.getCosts().add(temp);*/
		
		if(k.getOpintojenLaajuusarvo() != null && k.getOpintojenLaajuusyksikko().getMeta() != null){
			this.setCredits(k.getOpintojenLaajuusarvo().getArvo() + " " + k.getOpintojenLaajuusyksikko().getMeta().get(LANG_CODE_KIELI_EN).getNimi(), lo);
		}
		
		//InformationLanguage
		//k.getKuvausKomo().get("TAVOITTEET").getTekstis().get("kieli_" + k.getKuvausKomo().get("TAVOITTEET").getMeta().get("kieliArvo").getArvo().toLowerCase().trim());
		//lo.setInformationLanguage(LanguageCode.fromValue(k.getKuvausKomo().get("TAVOITTEET").getMeta().get("kieliArvo").getArvo().toLowerCase().trim()));
		
		learningOpportunities.getLearningOpportunity().add(lo);
	}

	private void setTitle(String title, LearningOpportunity lo){
		i18Non.setValue(title);
		i18Non.setLanguage(LanguageCode.fromValue(TITLE_LANG_CODE_EN));
		lo.getTitle().add(i18Non);
	}
	
	private void setDescription(Map<String, String> descriptions, LearningOpportunity lo){
		for (String s : descriptions.keySet()) {
			if (s != null && !s.isEmpty()) {
				i18Non.setValue(descriptions.get(s));
				i18Non.setLanguage(LanguageCode.fromValue(s.substring(s.length() - 2, s.length()))); // Leave only the country code
				lo.getDescription().add(i18Non);
			}
		}
	}
	
	private void setTeachingLangs(Map<String, KoodiV1RDTO> teachingLangs,  LearningOpportunity lo){
		for (KoodiV1RDTO s : teachingLangs.values()) {
			if(!s.getArvo().toLowerCase().equals("la"))
				lo.getTeachingLanguage().add(LanguageCode.fromValue(s.getArvo().toLowerCase()));
		}
	}
	
	private void setDurationInformation(String duration, LearningOpportunity lo){
		I18NString durationInfo = of.createI18NString();
		durationInfo.setValue(duration);
		lo.getDurationInformation().add(durationInfo);
	}
	
	private void setDate(Set<Date> dates, LearningOpportunity lo){
		I18NString dateString = of.createI18NString();
		for (Date d : dates) {
			dateString.setValue(d.toString());
			lo.getStartDate().add(dateString);
		}
	}
	
	private void setQualificationAwarded(Map<String, KoodiV1RDTO> list, Qualifications qualifications){
		for (KoodiV1RDTO s : list.values()) {
			I18NString temp = of.createI18NString();
			temp.setValue(s.getNimi());
			qualifications.getQualificationAwarded().add(temp);
		}
	}
	
	private void setQualificationDescription(Map<String, String> list, Qualifications qualifications) {
		for (String s : list.values()) {
			I18NString temp = of.createI18NString();
			temp.setValue(s);
			qualifications.getQualificationAwardedDescription().add(temp);
		}
	}
	
	private void setCredits(String credits, LearningOpportunity lo){
		I18NString temp = of.createI18NString();
		temp.setValue(credits);
		lo.getCredits().add(temp);
	}
	
	public void forwardLOtoJaxBParser() {
		JAXBParser.parseXML(learningOpportunities);
	}

	public void setKoulutusHakutulos(KoulutusHakutulosV1RDTO kh) {
		this.kh = kh;
	}
}