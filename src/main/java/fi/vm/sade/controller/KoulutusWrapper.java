package fi.vm.sade.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.europa.ec.learningopportunities.v0_5_10.CourseLocation;
import eu.europa.ec.learningopportunities.v0_5_10.I18NNonEmptyString;
import eu.europa.ec.learningopportunities.v0_5_10.I18NString;
import eu.europa.ec.learningopportunities.v0_5_10.I18NUrl;
import eu.europa.ec.learningopportunities.v0_5_10.LanguageCode;
import eu.europa.ec.learningopportunities.v0_5_10.LearningOpportunities;
import eu.europa.ec.learningopportunities.v0_5_10.LearningOpportunity;
import eu.europa.ec.learningopportunities.v0_5_10.ObjectFactory;
import eu.europa.ec.learningopportunities.v0_5_10.Qualifications;
import eu.europa.ec.learningopportunities.v0_5_10.StudyTypeType;
import eu.europa.ec.learningopportunities.v0_5_10.XsdTypeType;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioNimiRDTO;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.LokalisointiV1RDTO;
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
	private HashMap<String, OrganisaatioRDTO> organisaatioMap;

	public KoulutusWrapper() {
		organisaatioMap = new HashMap<String, OrganisaatioRDTO>();
		of = new ObjectFactory();
		i18Non = of.createI18NNonEmptyString();
		i18NUrl = of.createI18NUrl();
		learningOpportunities = of.createLearningOpportunities();
		learningOpportunities.setKey("ZDR5HGWBHP0J65P5VZIYEI2ZJJF18WGW");
		learningOpportunities.setXsdType(XsdTypeType.fromValue("Learning Opportunity"));
		learningOpportunities.setXsdVersion("0.5.10");
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
		this.setStudyType(k.getOpetusPaikkas().getUris(), k.getOpetusmuodos().getUris(), lo);

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
		this.setQualificationAwardingBody(k.getOpetusJarjestajat(), qualifications);
		lo.getQualifications().add(qualifications);
		
		this.setCost(k.getHintaString(), lo);
		if(k.getOpintojenLaajuusarvo() != null && k.getOpintojenLaajuusyksikko().getMeta() != null){
			this.setCredits(k.getOpintojenLaajuusarvo().getArvo() + " " + k.getOpintojenLaajuusyksikko().getMeta().get(LANG_CODE_KIELI_EN).getNimi(), lo);
		}
		
		//InformationLanguage
		this.setInformationLanguage(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
		this.setProviderName(k.getOpetusTarjoajat(), lo);
		this.setProviderContactInfo(k.getOpetusTarjoajat(), lo);
		
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
		this.setStudyType(k.getOpetusPaikkas().getUris(), k.getOpetusmuodos().getUris(), lo);

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
		this.setQualificationAwardingBody(k.getOpetusJarjestajat(), qualifications);
		lo.getQualifications().add(qualifications);
		
		this.setCost(k.getHintaString(), lo);
		if(k.getOpintojenLaajuusarvo() != null && k.getOpintojenLaajuusyksikko().getMeta() != null){
			this.setCredits(k.getOpintojenLaajuusarvo().getArvo() + " " + k.getOpintojenLaajuusyksikko().getMeta().get(LANG_CODE_KIELI_EN).getNimi(), lo);
		}
		
		//InformationLanguage
		this.setInformationLanguage(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
		this.setProviderName(k.getOpetusTarjoajat(), lo);
		this.setProviderContactInfo(k.getOpetusTarjoajat(), lo);
		
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
		this.setStudyType(k.getOpetusPaikkas().getUris(), k.getOpetusmuodos().getUris(), lo);

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
		this.setQualificationAwardingBody(k.getOpetusJarjestajat(), qualifications);
		lo.getQualifications().add(qualifications);
		
		this.setCost(k.getHintaString(), lo);
		if(k.getOpintojenLaajuusarvo() != null && k.getOpintojenLaajuusyksikko().getMeta() != null){
			this.setCredits(k.getOpintojenLaajuusarvo().getArvo() + " " + k.getOpintojenLaajuusyksikko().getMeta().get(LANG_CODE_KIELI_EN).getNimi(), lo);
		}
		
		//InformationLanguage
		this.setInformationLanguage(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
		this.setProviderName(k.getOpetusTarjoajat(), lo);
		this.setProviderContactInfo(k.getOpetusTarjoajat(), lo);
		
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
		this.setStudyType(k.getOpetusPaikkas().getUris(), k.getOpetusmuodos().getUris(), lo);

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
		this.setQualificationAwardingBody(k.getOpetusTarjoajat(), qualifications);
		lo.getQualifications().add(qualifications);
		
		this.setCost(k.getHintaString(), lo);
		if(k.getOpintojenLaajuusarvo() != null && k.getOpintojenLaajuusyksikko().getMeta() != null){
			this.setCredits(k.getOpintojenLaajuusarvo().getArvo() + " " + k.getOpintojenLaajuusyksikko().getMeta().get(LANG_CODE_KIELI_EN).getNimi(), lo);
		}
		
		//InformationLanguage
		this.setInformationLanguage(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
		this.setProviderName(k.getOpetusTarjoajat(), lo);
		this.setProviderContactInfo(k.getOpetusTarjoajat(), lo);
		this.setCourseAddress(k.getOpetusTarjoajat(), lo);
		
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
		this.setStudyType(k.getOpetusPaikkas().getUris(), k.getOpetusmuodos().getUris(), lo);

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
		this.setQualificationAwardingBody(k.getOpetusJarjestajat(), qualifications);
		lo.getQualifications().add(qualifications);
		
		this.setCost(k.getHintaString(), lo);
		if(k.getOpintojenLaajuusarvo() != null && k.getOpintojenLaajuusyksikko().getMeta() != null){
			this.setCredits(k.getOpintojenLaajuusarvo().getArvo() + " " + k.getOpintojenLaajuusyksikko().getMeta().get(LANG_CODE_KIELI_EN).getNimi(), lo);
		}
		
		//InformationLanguage
		this.setInformationLanguage(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
		this.setProviderName(k.getOpetusTarjoajat(), lo);
		this.setProviderContactInfo(k.getOpetusTarjoajat(), lo);
		
		learningOpportunities.getLearningOpportunity().add(lo);
	}

	public void fetchLukioInfo(KoulutusLukioV1RDTO k) {
		LearningOpportunity lo = of.createLearningOpportunity();
		
		k.getOrganisaatio().getNimet();
		
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
		this.setStudyType(k.getOpetusPaikkas().getUris(), k.getOpetusmuodos().getUris(), lo);

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
		this.setQualificationAwardingBody(k.getOpetusJarjestajat(), qualifications);
		lo.getQualifications().add(qualifications);
		
		this.setCost(k.getHintaString(), lo);
		
		if(k.getOpintojenLaajuusarvo() != null && k.getOpintojenLaajuusyksikko().getMeta() != null){
			this.setCredits(k.getOpintojenLaajuusarvo().getArvo() + " " + k.getOpintojenLaajuusyksikko().getMeta().get(LANG_CODE_KIELI_EN).getNimi(), lo);
		}
		
		//InformationLanguage
		this.setInformationLanguage(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
		this.setProviderName(k.getOpetusTarjoajat(), lo);
		this.setProviderContactInfo(k.getOpetusTarjoajat(), lo);
		
		learningOpportunities.getLearningOpportunity().add(lo);
	}
	
	private void setInformationLanguage(Map<String, String> map, LearningOpportunity lo){
		if(map.get("kieli_en") != null){
			lo.setInformationLanguage(LanguageCode.EN);
		} else if(map.get("kieli_fi") != null){
			lo.setInformationLanguage(LanguageCode.FI);
		} else if(map.get("kieli_sv") != null){
			lo.setInformationLanguage(LanguageCode.SV);
		} else {
			for(LanguageCode l : LanguageCode.values()){
				if(map.get("kieli_" + l.value().toLowerCase()) != null){
					lo.setInformationLanguage(l);
				}
			}
		}
	}
	
	private void setQualificationAwardingBody(Set<String> set, Qualifications qualifications){
		for(String s : set){
			for(OrganisaatioNimiRDTO o : organisaatioMap.get(s).getNimet()){
				I18NString temp = of.createI18NString();
				temp.setValue(o.getNimi().get("en"));
				qualifications.getAwardingBody().add(temp);
			}
		}
	}
	
	private void setProviderName(Set<String> set, LearningOpportunity lo){
		for(String s : set){
			for(OrganisaatioNimiRDTO o : organisaatioMap.get(s).getNimet()){
				I18NNonEmptyString temp = of.createI18NNonEmptyString();
				temp.setValue(o.getNimi().get("en"));
				lo.getProviderName().add(temp);
			}
		}
	}
	
	private void setProviderContactInfo(Set<String> set, LearningOpportunity lo){
		ArrayList<I18NString> list = new ArrayList<>();
		for(String s : set){
			for(Map<String, String> map : organisaatioMap.get(s).getYhteystiedot()){
				I18NString providerName = of.createI18NString(); // Mikä on hakijapalvelun nimi? sama kuin tarjoajan nimi?
				I18NString mailingAdd = of.createI18NString(); // miten koostuu? PL XXXXX, Osoite, Postinumero?
				I18NString mail = of.createI18NString();	// Ei sähköpostia?
				I18NString phone = of.createI18NString();
								
				if(map.get("postitoimipaikka") != null && map.get("osoite") != null && map.get("postinumeroUri") != null){
					mailingAdd.setValue(map.get("osoite") + ", " + map.get("postitoimipaikka") + ", " + map.get("postinumeroUri").replace("posti_", ""));
					list.add(mailingAdd);
				}
				
				if(map.get("numero") != null){
					phone.setValue(map.get("numero"));
					list.add(phone);
				}
				
				lo.getProviderContactInfo().addAll(list);
			}
		}
	}
	
	private void setCourseAddress(Set<String> set, CourseLocation co){
		for(String s : set){
			for(Map<String, String> map : organisaatioMap.get(s).getYhteystiedot()){
				I18NString address = of.createI18NString(); // Mikä on hakijapalvelun nimi? sama kuin tarjoajan nimi?
								
				if(map.get("osoiteTyyppi") != null && map.get("osoiteTyyppi").equals("kaynti") && map.get("osoite") != null){
					co.getCourseAddress().add(address);
				}
				
				//TODO: TEE LOPPUUN
			}
		}
	}
	
	private void setCost(String cost, LearningOpportunity lo){
		I18NString temp = of.createI18NString();
		temp.setValue(cost);
		lo.getCosts().add(temp);
	}

	private void setTitle(String title, LearningOpportunity lo){
		i18Non.setValue(title);
		i18Non.setLanguage(LanguageCode.fromValue(TITLE_LANG_CODE_EN));
		lo.getTitle().add(i18Non);
	}
	
	private void setDescription(Map<String, String> descriptions, LearningOpportunity lo){
		for (String s : descriptions.keySet()) {
			if (s != null && !s.isEmpty()) {
				i18Non = of.createI18NNonEmptyString();
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
	
	private void setStudyType(Map<String, Integer> paikkaList, Map<String, Integer> muotoList, LearningOpportunity lo){
		List<StudyTypeType> studyTypeList = new ArrayList<>();
		
		for(String s : paikkaList.keySet()){
			if(s.equals("opetuspaikkak_1")){
				studyTypeList.add(StudyTypeType.FF);
			} else if(s.equals("opetuspaikkak_2")){
				studyTypeList.add(StudyTypeType.DL);
			}
		}
		
		for(String s : muotoList.keySet()){
			if(s.equals("opetusmuotokk_3")){
				studyTypeList.add(StudyTypeType.BL);
			} else if(s.equals("opetusmuotokk_4")){
				studyTypeList.add(StudyTypeType.ON);
			}
		}
		
		lo.getStudyType().addAll(studyTypeList);
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

	public void setOrganisaatiot(ArrayList<OrganisaatioRDTO> haetutOrganisaatiot) {
		for(OrganisaatioRDTO organisaatio : haetutOrganisaatiot){
			organisaatioMap.put(organisaatio.getOid(), organisaatio);
		}
	}
}