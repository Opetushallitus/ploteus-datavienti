package fi.vm.sade.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.europa.ec.learningopportunities.v0_5_10.I18NNonEmptyString;
import eu.europa.ec.learningopportunities.v0_5_10.I18NString;
import eu.europa.ec.learningopportunities.v0_5_10.I18NUrl;
import eu.europa.ec.learningopportunities.v0_5_10.LanguageCode;
import eu.europa.ec.learningopportunities.v0_5_10.LearningOpportunity;
import eu.europa.ec.learningopportunities.v0_5_10.ObjectFactory;
import eu.europa.ec.learningopportunities.v0_5_10.Qualifications;
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
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;

public class KoulutusWrapper {
	public static String COUNTRY_CODE = "FI";
	public static String TITLE_LANG_CODE = "en";

	private ArrayList<LearningOpportunity> LearningOpportunitys;

	private ObjectFactory of;
	private I18NNonEmptyString i18Non;
	private KoulutusHakutulosV1RDTO kh;
	private I18NUrl i18NUrl;

	public KoulutusWrapper() {
		of = new ObjectFactory();
		i18Non = of.createI18NNonEmptyString();
		i18NUrl = of.createI18NUrl();
		LearningOpportunitys = new ArrayList<LearningOpportunity>();
	}

	public void fetchAmmatillinenPerustutkintoInfo(KoulutusAmmatillinenPerustutkintoV1RDTO k) {
		LearningOpportunity lo = of.createLearningOpportunity();

		// ID & COUNTRY CODE
		lo.setLearningOpportunityId(k.getOid());
		lo.setCountryCode(COUNTRY_CODE);

		// Title
		i18Non.setValue(kh.getNimi().get(TITLE_LANG_CODE));
		i18Non.setLanguage(LanguageCode.fromValue(TITLE_LANG_CODE));
		lo.getTitle().add(i18Non);
		i18Non = of.createI18NNonEmptyString();
		
		// Description
		for (String s : k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis().keySet()) {
			if (s != null && !s.isEmpty()) {
				i18Non.setValue( k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis().get(s));
				// Sub removes "kieli_" and leaves the country code
				i18Non.setLanguage(LanguageCode.fromValue(s.substring(s.length()-2, s.length())));
				lo.getDescription().add(i18Non);
				i18Non = of.createI18NNonEmptyString();
			}
		}
		i18NUrl.setValue("https://opintopolku.fi/app/#!/koulutus/" + k.getOid());
		lo.getUrl().add(i18NUrl);
		i18NUrl = of.createI18NUrl();
		
		// Teaching Language
		List<LanguageCode> langCodes = new ArrayList<>();
		
		for(KoodiV1RDTO s : k.getOpetuskielis().getMeta().values()){
			langCodes.add(LanguageCode.fromValue(s.getArvo().toLowerCase()));
		}
		lo.getTeachingLanguage().addAll(langCodes);
		
		// DurationInformation
		I18NString durationInfo = of.createI18NString();
		if(k.getSuunniteltuKestoArvo() != null){
			System.out.println("Ammatillinen perustutkinto: " + k.getSuunniteltuKestoArvo());
			durationInfo.setValue(k.getSuunniteltuKestoArvo() + " " + k.getSuunniteltuKestoTyyppi().getNimi());
			lo.getDurationInformation().add(durationInfo);
		} else {
			System.out.println("Ammatillinen perustutkinto oli null");
		}
		
		// Dates
		List<I18NString> dates = new ArrayList<>();
		I18NString dateString = of.createI18NString();
		
		// StartDate
		for(Date d : k.getKoulutuksenAlkamisPvms()){
			dateString.setValue(d.toString());
			dates.add(dateString);
		}
		
		lo.getStartDate().addAll(dates);
		
		// Qualifications
		Qualifications qualifications = of.createQualifications();
		for(KoodiV1RDTO s : k.getTutkintonimikes().getMeta().values()){
			I18NString temp = of.createI18NString();
			temp.setValue(s.getNimi());
			qualifications.getQualificationAwarded().add(temp);
		}
		
		for(String s : k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis().values()){
			I18NString temp = of.createI18NString();
			temp.setValue(s);
			qualifications.getQualificationAwardedDescription().add(temp);
		}
		
		lo.getQualifications().add(qualifications);
		
		LearningOpportunitys.add(lo);
		
	}

	public void fetchAmmattiInfo(AmmattitutkintoV1RDTO k) {
		LearningOpportunity lo = of.createLearningOpportunity();

		lo.setLearningOpportunityId(k.getOid());
		lo.setCountryCode(COUNTRY_CODE);

		i18Non.setValue(kh.getNimi().get(TITLE_LANG_CODE));
		i18Non.setLanguage(LanguageCode.fromValue(TITLE_LANG_CODE));
		lo.getTitle().add(i18Non);
		i18Non = of.createI18NNonEmptyString();
		
		List<LanguageCode> langCodes = new ArrayList<>();
		
		for(KoodiV1RDTO s : k.getOpetuskielis().getMeta().values()){
			langCodes.add(LanguageCode.fromValue(s.getArvo().toLowerCase()));
		}
		lo.getTeachingLanguage().addAll(langCodes);
		
		// DurationInformation
		I18NString durationInfo = of.createI18NString();
		if(k.getSuunniteltuKestoArvo() != null){
			System.out.println("Ammattitutkinto: " + k.getSuunniteltuKestoArvo());
			durationInfo.setValue(k.getSuunniteltuKestoArvo() + " " + k.getSuunniteltuKestoTyyppi().getNimi());
			lo.getDurationInformation().add(durationInfo);
		}else {
			System.out.println("Ammattitutkinto oli null");
		}
		
		// Dates
		List<I18NString> dates = new ArrayList<>();
		I18NString dateString = of.createI18NString();
				
		// StartDate
		for(Date d : k.getKoulutuksenAlkamisPvms()){
			dateString.setValue(d.toString());
			dates.add(dateString);
		}
				
		lo.getStartDate().addAll(dates);
		
		// Qualifications
		Qualifications qualifications = of.createQualifications();
		for(KoodiV1RDTO s : k.getTutkintonimikes().getMeta().values()){
			I18NString temp = of.createI18NString();
			temp.setValue(s.getNimi());
			qualifications.getQualificationAwarded().add(temp);
		}
				
		for(String s : k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis().values()){
			I18NString temp = of.createI18NString();
			temp.setValue(s);
			qualifications.getQualificationAwardedDescription().add(temp);
		}
				
		lo.getQualifications().add(qualifications);
		
		LearningOpportunitys.add(lo);
	}

	public void fetchErikoisInfo(ErikoisammattitutkintoV1RDTO k) {
		LearningOpportunity lo = of.createLearningOpportunity();

		lo.setLearningOpportunityId(k.getOid());
		lo.setCountryCode(COUNTRY_CODE);

		i18Non.setValue(kh.getNimi().get(TITLE_LANG_CODE));
		i18Non.setLanguage(LanguageCode.fromValue(TITLE_LANG_CODE));
		lo.getTitle().add(i18Non);
		i18Non = of.createI18NNonEmptyString();

		List<LanguageCode> langCodes = new ArrayList<>();
		
		for(KoodiV1RDTO s : k.getOpetuskielis().getMeta().values()){
			langCodes.add(LanguageCode.fromValue(s.getArvo().toLowerCase()));
		}
		lo.getTeachingLanguage().addAll(langCodes);
		
		// DurationInformation
		I18NString durationInfo = new I18NString();
		if(k.getSuunniteltuKestoArvo() != null){
			System.out.println("Erikoisammattitutkinto: " + k.getSuunniteltuKestoArvo());
			durationInfo.setValue(k.getSuunniteltuKestoArvo() + " " + k.getSuunniteltuKestoTyyppi().getNimi());
			lo.getDurationInformation().add(durationInfo);
		} else {
			System.out.println("Erikoisammattitutkinto oli null");
		}
		
		// Dates
		List<I18NString> dates = new ArrayList<>();
		I18NString dateString = of.createI18NString();
				
		// StartDate
		for(Date d : k.getKoulutuksenAlkamisPvms()){
			dateString.setValue(d.toString());
			dates.add(dateString);
		}
				
		lo.getStartDate().addAll(dates);
		
		// Qualifications
		Qualifications qualifications = of.createQualifications();
		for(KoodiV1RDTO s : k.getTutkintonimikes().getMeta().values()){
			I18NString temp = of.createI18NString();
			temp.setValue(s.getNimi());
			qualifications.getQualificationAwarded().add(temp);
		}
				
		for(String s : k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis().values()){
			I18NString temp = of.createI18NString();
			temp.setValue(s);
			qualifications.getQualificationAwardedDescription().add(temp);
		}
				
		lo.getQualifications().add(qualifications);
		
		LearningOpportunitys.add(lo);
	}

	public void fetchKorkeaInfo(KoulutusKorkeakouluV1RDTO k) {
		LearningOpportunity lo = of.createLearningOpportunity();

		lo.setLearningOpportunityId(k.getOid());
		lo.setCountryCode(COUNTRY_CODE);

		i18Non.setValue(kh.getNimi().get(TITLE_LANG_CODE));
		i18Non.setLanguage(LanguageCode.fromValue(TITLE_LANG_CODE));
		lo.getTitle().add(i18Non);
		i18Non = of.createI18NNonEmptyString();

		List<LanguageCode> langCodes = new ArrayList<>();
		
		for(KoodiV1RDTO s : k.getOpetuskielis().getMeta().values()){
			langCodes.add(LanguageCode.fromValue(s.getArvo().toLowerCase()));
		}
		lo.getTeachingLanguage().addAll(langCodes);
		
		// DurationInformation
		I18NString durationInfo = of.createI18NString();
		if(k.getSuunniteltuKestoArvo() != null){
			
			System.out.println("KoulutusKorkeakoulu: " + k.getSuunniteltuKestoArvo());
			durationInfo.setValue(k.getSuunniteltuKestoArvo() + " " + k.getSuunniteltuKestoTyyppi().getNimi());
			lo.getDurationInformation().add(durationInfo);
		}else {
			System.out.println("KoulutusKorkeaKoulu oli null");
		}
		
		// Dates
		List<I18NString> dates = new ArrayList<>();
		I18NString dateString = of.createI18NString();
				
		// StartDate
		for(Date d : k.getKoulutuksenAlkamisPvms()){
			dateString.setValue(d.toString());
			dates.add(dateString);
		}
				
		lo.getStartDate().addAll(dates);
		
		// Qualifications
		Qualifications qualifications = of.createQualifications();
		for(KoodiV1RDTO s : k.getTutkintonimikes().getMeta().values()){
			I18NString temp = of.createI18NString();
			temp.setValue(s.getNimi());
			qualifications.getQualificationAwarded().add(temp);
		}
				
		for(String s : k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis().values()){
			I18NString temp = of.createI18NString();
			temp.setValue(s);
			qualifications.getQualificationAwardedDescription().add(temp);
		}
				
		lo.getQualifications().add(qualifications);
		
		LearningOpportunitys.add(lo);
	}

	public void fetchValmistavaInfo(ValmistavaKoulutusV1RDTO k) {
		LearningOpportunity lo = of.createLearningOpportunity();

		lo.setLearningOpportunityId(k.getOid());
		lo.setCountryCode(COUNTRY_CODE);

		i18Non.setValue(kh.getNimi().get(TITLE_LANG_CODE));
		i18Non.setLanguage(LanguageCode.fromValue(TITLE_LANG_CODE));
		lo.getTitle().add(i18Non);
		i18Non = of.createI18NNonEmptyString();

		List<LanguageCode> langCodes = new ArrayList<>();
		
		for(KoodiV1RDTO s : k.getOpetuskielis().getMeta().values()){
			langCodes.add(LanguageCode.fromValue(s.getArvo().toLowerCase()));
		}
		lo.getTeachingLanguage().addAll(langCodes);
		
		// DurationInformation
		I18NString durationInfo = of.createI18NString();
		if(k.getSuunniteltuKestoArvo() != null){
			System.out.println("ValmistavaKoulutus " + k.getSuunniteltuKestoArvo()); 
			durationInfo.setValue(k.getSuunniteltuKestoArvo() + " " + k.getSuunniteltuKestoTyyppi().getNimi());
			lo.getDurationInformation().add(durationInfo);
		}else {
			System.out.println("ValmistavaKoulutus oli null" );
		}
		
		// Dates
		List<I18NString> dates = new ArrayList<>();
		I18NString dateString = of.createI18NString();
				
		// StartDate
		for(Date d : k.getKoulutuksenAlkamisPvms()){
			dateString.setValue(d.toString());
			dates.add(dateString);
		}
				
		lo.getStartDate().addAll(dates);
		
		// Qualifications
		Qualifications qualifications = of.createQualifications();
		for(KoodiV1RDTO s : k.getTutkintonimikes().getMeta().values()){
			I18NString temp = of.createI18NString();
			temp.setValue(s.getNimi());
			qualifications.getQualificationAwarded().add(temp);
		}
				
		for(String s : k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis().values()){
			I18NString temp = of.createI18NString();
			temp.setValue(s);
			qualifications.getQualificationAwardedDescription().add(temp);
		}
				
		lo.getQualifications().add(qualifications);
		
		LearningOpportunitys.add(lo);
	}

	public void fetchLukioInfo(KoulutusLukioV1RDTO k) {
		LearningOpportunity lo = of.createLearningOpportunity();

		lo.setLearningOpportunityId(k.getOid());
		lo.setCountryCode(COUNTRY_CODE);

		i18Non.setValue(kh.getNimi().get(TITLE_LANG_CODE));
		i18Non.setLanguage(LanguageCode.fromValue(TITLE_LANG_CODE));
		lo.getTitle().add(i18Non);
		i18Non = of.createI18NNonEmptyString();

		List<LanguageCode> langCodes = new ArrayList<>();
		
		for(KoodiV1RDTO s : k.getOpetuskielis().getMeta().values()){
			langCodes.add(LanguageCode.fromValue(s.getArvo().toLowerCase()));
		}
		lo.getTeachingLanguage().addAll(langCodes);
		
		// DurationInformation
		I18NString durationInfo = of.createI18NString();
		if(k.getSuunniteltuKestoArvo() != null){
			System.out.println("KouolutusLukio: " + k.getSuunniteltuKestoArvo());
			durationInfo.setValue(k.getSuunniteltuKestoArvo() + " " + k.getSuunniteltuKestoTyyppi().getNimi());
			lo.getDurationInformation().add(durationInfo);
		}else {
			System.out.println("KoulutusLukio oli null");
		}
		
		// Dates
		List<I18NString> dates = new ArrayList<>();
		I18NString dateString = of.createI18NString();
				
		// StartDate
		for(Date d : k.getKoulutuksenAlkamisPvms()){
			dateString.setValue(d.toString());
			dates.add(dateString);
		}
				
		lo.getStartDate().addAll(dates);
		
		// Qualifications
		Qualifications qualifications = of.createQualifications();
		for(KoodiV1RDTO s : k.getTutkintonimikes().getMeta().values()){
			I18NString temp = of.createI18NString();
			temp.setValue(s.getNimi());
			qualifications.getQualificationAwarded().add(temp);
		}
				
		for(String s : k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis().values()){
			I18NString temp = of.createI18NString();
			temp.setValue(s);
			qualifications.getQualificationAwardedDescription().add(temp);
		}
				
		lo.getQualifications().add(qualifications);
		
		LearningOpportunitys.add(lo);
	}

	public void setKoulutusHakutulos(KoulutusHakutulosV1RDTO kh) {
		this.kh = kh;
	}
}