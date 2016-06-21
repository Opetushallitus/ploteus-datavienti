package fi.vm.sade.wrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import eu.europa.ec.learningopportunities.v0_5_10.StudyTypeType;
import eu.europa.ec.learningopportunities.v0_5_10.XsdTypeType;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioNimiRDTO;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.parser.JAXBParser;
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
    private KoulutusHakutulosV1RDTO kh;
    private fi.vm.sade.parser.JAXBParser JAXBParser;

    public KoulutusWrapper() {
        of = new ObjectFactory();
        learningOpportunities = of.createLearningOpportunities();
        learningOpportunities.setKey("ZDR5HGWBHP0J65P5VZIYEI2ZJJF18WGW");
        learningOpportunities.setXsdType(XsdTypeType.fromValue("Learning Opportunity"));
        learningOpportunities.setXsdVersion("0.5.10");
        JAXBParser = new JAXBParser();
    }

    public void fetchAmmatillinenPerustutkintoInfo(KoulutusAmmatillinenPerustutkintoV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot) {
        LearningOpportunity lo = initLearningOpportunity(k.getOid(), k.getKoulutuksenAlkamisPvms(), k.getHintaString());
        setTeachingLangs(k.getOpetuskielis().getMeta(), lo);
        setStudyType(k.getOpetusPaikkas().getUris(), k.getOpetusmuodos().getUris(), lo);
        setInformationLanguage(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
        setProviderName(k.getOpetusTarjoajat(), lo, haetutOrganisaatiot);

        setDescription(k, lo);
        setDurationInformation(k, lo);
        setQualifications(k, lo, haetutOrganisaatiot);
        setCredits(k, lo);

        learningOpportunities.getLearningOpportunity().add(lo);
    }

    private LearningOpportunity initLearningOpportunity(String kOid, Set<Date> koulutuksenAlkamisPvms, String hintaString) {
        LearningOpportunity lo = of.createLearningOpportunity();
        setDate(koulutuksenAlkamisPvms, lo);
        setCost(hintaString, lo);
        lo.setLearningOpportunityId(kOid);
        lo.setCountryCode(COUNTRY_CODE);
        lo.getUrl().add(createUrl("https://opintopolku.fi/app/#!/koulutus/" + kOid));
        lo.getTitle().add(createI18NonEmptyString(kh.getNimi().get(TITLE_LANG_CODE_EN)));
        return lo;
    }

    private void setCredits(KoulutusAmmatillinenPerustutkintoV1RDTO k, LearningOpportunity lo) {
        if (k.getOpintojenLaajuusarvo() != null && k.getOpintojenLaajuusyksikko().getMeta() != null) {
            this.setCredits(k.getOpintojenLaajuusarvo().getArvo() + " " + k.getOpintojenLaajuusyksikko().getMeta().get(LANG_CODE_KIELI_EN).getNimi(), lo);
        }
    }

    private void setQualifications(KoulutusAmmatillinenPerustutkintoV1RDTO k, LearningOpportunity lo, Map<String, OrganisaatioRDTO> haetutOrganisaatiot) {
        Qualifications qualifications = of.createQualifications();
        this.setQualificationAwarded(k.getTutkintonimikes().getMeta(), qualifications);
        if (k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null) {
            this.setQualificationDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), qualifications);
        }
        this.setQualificationAwardingBody(k.getOpetusJarjestajat(), qualifications, haetutOrganisaatiot);
        lo.getQualifications().add(qualifications);
    }

    private void setDurationInformation(KoulutusAmmatillinenPerustutkintoV1RDTO k, LearningOpportunity lo) {
        if (k.getSuunniteltuKestoArvo() != null) {
            this.setDurationInformation(k.getSuunniteltuKestoArvo() + " " + k.getSuunniteltuKestoTyyppi().getNimi(), lo);
        }
    }

    private void setDescription(KoulutusAmmatillinenPerustutkintoV1RDTO k, LearningOpportunity lo) {
        if (k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null) {
            this.setDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
        }
    }

    private I18NUrl createUrl(String src) {
        final I18NUrl url = of.createI18NUrl();
        url.setValue(src);
        return url;
    }

    public void fetchAmmattiInfo(AmmattitutkintoV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot) {
        LearningOpportunity lo = initLearningOpportunity(k.getOid(), k.getKoulutuksenAlkamisPvms(), k.getHintaString());

        this.setTeachingLangs(k.getOpetuskielis().getMeta(), lo);
        this.setStudyType(k.getOpetusPaikkas().getUris(), k.getOpetusmuodos().getUris(), lo);
        this.setInformationLanguage(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
        this.setProviderName(k.getOpetusTarjoajat(), lo, haetutOrganisaatiot);

        setDescription(k, lo);
        setDurationInformation(k, lo);
        setQualifications(k, lo, haetutOrganisaatiot);
        setCredits(k, lo);

        learningOpportunities.getLearningOpportunity().add(lo);
    }

    private void setCredits(AmmattitutkintoV1RDTO k, LearningOpportunity lo) {
        if (k.getOpintojenLaajuusarvo() != null && k.getOpintojenLaajuusyksikko().getMeta() != null) {
            this.setCredits(k.getOpintojenLaajuusarvo().getArvo() + " " + k.getOpintojenLaajuusyksikko().getMeta().get(LANG_CODE_KIELI_EN).getNimi(), lo);
        }
    }

    private void setQualifications(AmmattitutkintoV1RDTO k, LearningOpportunity lo, Map<String, OrganisaatioRDTO> haetutOrganisaatiot) {
        Qualifications qualifications = of.createQualifications();
        //this.setQualificationAwarded(k.getTutkintonimikes().getMeta(), qualifications);
        if (k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null) {
            this.setQualificationDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), qualifications);
        }
        this.setQualificationAwardingBody(k.getOpetusJarjestajat(), qualifications, haetutOrganisaatiot);
        lo.getQualifications().add(qualifications);
    }

    private void setDurationInformation(AmmattitutkintoV1RDTO k, LearningOpportunity lo) {
        if (k.getSuunniteltuKestoArvo() != null) {
            this.setDurationInformation(k.getSuunniteltuKestoArvo() + " " + k.getSuunniteltuKestoTyyppi().getNimi(), lo);
        }
    }

    private void setDescription(AmmattitutkintoV1RDTO k, LearningOpportunity lo) {
        if (k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null) {
            this.setDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
        }
    }

    public void fetchErikoisInfo(ErikoisammattitutkintoV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot) {
        LearningOpportunity lo = of.createLearningOpportunity();

        // ID & COUNTRY CODE
        lo.setLearningOpportunityId(k.getOid());
        lo.setCountryCode(COUNTRY_CODE);

        // this.setTitle(kh.getNimi().get(TITLE_LANG_CODE_EN), lo); TODO
        if (k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null) {
            this.setDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
        }

        lo.getUrl().add(createUrl("https://opintopolku.fi/app/#!/koulutus/" + k.getOid()));

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
        if (k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null) {
            this.setQualificationDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), qualifications);
        }
        this.setQualificationAwardingBody(k.getOpetusJarjestajat(), qualifications, haetutOrganisaatiot);
        lo.getQualifications().add(qualifications);

        this.setCost(k.getHintaString(), lo);
        if (k.getOpintojenLaajuusarvo() != null && k.getOpintojenLaajuusyksikko().getMeta() != null) {
            this.setCredits(k.getOpintojenLaajuusarvo().getArvo() + " " + k.getOpintojenLaajuusyksikko().getMeta().get(LANG_CODE_KIELI_EN).getNimi(), lo);
        }

        //InformationLanguage
        this.setInformationLanguage(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
        this.setProviderName(k.getOpetusTarjoajat(), lo, haetutOrganisaatiot);

        learningOpportunities.getLearningOpportunity().add(lo);
    }

    public void fetchKorkeaInfo(KoulutusKorkeakouluV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot) {
        LearningOpportunity lo = of.createLearningOpportunity();

        // ID & COUNTRY CODE
        lo.setLearningOpportunityId(k.getOid());
        lo.setCountryCode(COUNTRY_CODE);

        //this.setTitle(kh.getNimi().get(TITLE_LANG_CODE_EN), lo); //TODO

        if (k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null) {
            this.setDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
        }

        lo.getUrl().add(createUrl("https://opintopolku.fi/app/#!/koulutus/" + k.getOid()));

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
        if (k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null) {
            this.setQualificationDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), qualifications);
        }
        this.setQualificationAwardingBody(k.getOpetusTarjoajat(), qualifications, haetutOrganisaatiot);
        lo.getQualifications().add(qualifications);

        this.setCost(k.getHintaString(), lo);
        if (k.getOpintojenLaajuusarvo() != null && k.getOpintojenLaajuusyksikko().getMeta() != null) {
            this.setCredits(k.getOpintojenLaajuusarvo().getArvo() + " " + k.getOpintojenLaajuusyksikko().getMeta().get(LANG_CODE_KIELI_EN).getNimi(), lo);
        }

        //InformationLanguage
        this.setInformationLanguage(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
        this.setProviderName(k.getOpetusTarjoajat(), lo, haetutOrganisaatiot);
        this.setProviderContactInfo(k.getOpetusTarjoajat(), lo, haetutOrganisaatiot);

        learningOpportunities.getLearningOpportunity().add(lo);
    }

    public void fetchValmistavaInfo(ValmistavaKoulutusV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot) {
        LearningOpportunity lo = of.createLearningOpportunity();

        // ID & COUNTRY CODE
        lo.setLearningOpportunityId(k.getOid());
        lo.setCountryCode(COUNTRY_CODE);

        //this.setTitle(kh.getNimi().get(TITLE_LANG_CODE_EN), lo); //TODO

        if (k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null) {
            this.setDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
        }

        lo.getUrl().add(createUrl("https://opintopolku.fi/app/#!/koulutus/" + k.getOid()));

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
        if (k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null) {
            this.setQualificationDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), qualifications);
        }
        this.setQualificationAwardingBody(k.getOpetusJarjestajat(), qualifications, haetutOrganisaatiot);
        lo.getQualifications().add(qualifications);

        this.setCost(k.getHintaString(), lo);
        if (k.getOpintojenLaajuusarvo() != null && k.getOpintojenLaajuusyksikko().getMeta() != null) {
            this.setCredits(k.getOpintojenLaajuusarvo().getArvo() + " " + k.getOpintojenLaajuusyksikko().getMeta().get(LANG_CODE_KIELI_EN).getNimi(), lo);
        }

        //InformationLanguage
        this.setInformationLanguage(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
        this.setProviderName(k.getOpetusTarjoajat(), lo, haetutOrganisaatiot);

        learningOpportunities.getLearningOpportunity().add(lo);
    }

    public void fetchLukioInfo(KoulutusLukioV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot) {
        LearningOpportunity lo = of.createLearningOpportunity();

        k.getOrganisaatio().getNimet();

        // ID & COUNTRY CODE
        lo.setLearningOpportunityId(k.getOid());
        lo.setCountryCode(COUNTRY_CODE);

        // this.setTitle(kh.getNimi().get(TITLE_LANG_CODE_EN), lo); //todo
        if (k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null) {
            this.setDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
        }

        lo.getUrl().add(createUrl("https://opintopolku.fi/app/#!/koulutus/" + k.getOid()));

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
        if (k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null) {
            this.setQualificationDescription(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), qualifications);
        }
        this.setQualificationAwardingBody(k.getOpetusJarjestajat(), qualifications, haetutOrganisaatiot);
        lo.getQualifications().add(qualifications);

        this.setCost(k.getHintaString(), lo);

        if (k.getOpintojenLaajuusarvo() != null && k.getOpintojenLaajuusyksikko().getMeta() != null) {
            this.setCredits(k.getOpintojenLaajuusarvo().getArvo() + " " + k.getOpintojenLaajuusyksikko().getMeta().get(LANG_CODE_KIELI_EN).getNimi(), lo);
        }

        //InformationLanguage
        this.setInformationLanguage(k.getKuvausKomo().get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
        this.setProviderName(k.getOpetusTarjoajat(), lo, haetutOrganisaatiot);

        learningOpportunities.getLearningOpportunity().add(lo);
    }

    private void setInformationLanguage(Map<String, String> map, LearningOpportunity lo) {
        if (map.get("kieli_en") != null) {
            lo.setInformationLanguage(LanguageCode.EN);
        } else if (map.get("kieli_fi") != null) {
            lo.setInformationLanguage(LanguageCode.FI);
        } else if (map.get("kieli_sv") != null) {
            lo.setInformationLanguage(LanguageCode.SV);
        } else {
            for (LanguageCode l : LanguageCode.values()) {
                if (map.get("kieli_" + l.value().toLowerCase()) != null) {
                    lo.setInformationLanguage(l);
                }
            }
        }
    }

    private void setQualificationAwardingBody(Set<String> set, Qualifications qualifications, Map<String, OrganisaatioRDTO> haetutOrganisaatiot) {
        for (String s : set) {
            for (OrganisaatioNimiRDTO o : haetutOrganisaatiot.get(s).getNimet()) {
                I18NString temp = of.createI18NString();
                temp.setValue(o.getNimi().get("en"));
                qualifications.getAwardingBody().add(temp);
            }
        }
    }

    private void setProviderName(Set<String> set, LearningOpportunity lo, Map<String, OrganisaatioRDTO> haetutOrganisaatiot) {
        for (String s : set) {
            for (OrganisaatioNimiRDTO o : haetutOrganisaatiot.get(s).getNimet()) {
                I18NNonEmptyString temp = of.createI18NNonEmptyString();
                temp.setValue(o.getNimi().get("en"));
                lo.getProviderName().add(temp);
            }
        }
    }

    private void setProviderContactInfo(Set<String> set, LearningOpportunity lo, Map<String, OrganisaatioRDTO> haetutOrganisaatiot) {
        ArrayList<I18NString> list = new ArrayList<>();
        for (String s : set) {
            for (Map<String, String> map : haetutOrganisaatiot.get(s).getYhteystiedot()) {
                I18NString providerName = of.createI18NString(); // Mikä on hakijapalvelun nimi? sama kuin tarjoajan nimi?
                I18NString mailingAdd = of.createI18NString(); // miten koostuu? PL XXXXX, Osoite, Postinumero?
                I18NString mail = of.createI18NString();    // Ei sähköpostia?
                I18NString phone = of.createI18NString();

                if (map.get("postitoimipaikka") != null && map.get("osoite") != null && map.get("postinumeroUri") != null) {
                    mailingAdd.setValue(map.get("osoite") + ", " + map.get("postitoimipaikka") + ", " + map.get("postinumeroUri").replace("posti_", ""));
                    list.add(mailingAdd);
                }

                if (map.get("numero") != null) {
                    phone.setValue(map.get("numero"));
                    list.add(phone);
                }

                lo.getProviderContactInfo().addAll(list);
            }
        }
    }

    private void setCost(String cost, LearningOpportunity lo) {
        I18NString temp = of.createI18NString();
        temp.setValue(cost);
        lo.getCosts().add(temp);
    }

    private I18NNonEmptyString createI18NonEmptyString(String title) {
        final I18NNonEmptyString i18Non = of.createI18NNonEmptyString();
        i18Non.setValue(title);
        i18Non.setLanguage(LanguageCode.fromValue(TITLE_LANG_CODE_EN));
        return i18Non;
    }

    private void setDescription(Map<String, String> descriptions, LearningOpportunity lo) {
        descriptions.keySet().stream()
        	.filter(e -> e != null && !e.isEmpty())
        	.forEach(e -> lo.getDescription().add(createI18NonEmptyString(descriptions.get(e))));
    }

    private void setTeachingLangs(Map<String, KoodiV1RDTO> teachingLangs, LearningOpportunity lo) {
        teachingLangs.values().stream()
        	.filter(e -> !e.getArvo().toLowerCase().equals("la"))
        	.forEach(e -> lo.getTeachingLanguage().add(LanguageCode.fromValue(e.getArvo().toLowerCase())));
    }

    private void setStudyType(Map<String, Integer> paikkaList, Map<String, Integer> muotoList, LearningOpportunity lo) {
        List<StudyTypeType> studyTypeList = new ArrayList<>();
        if(paikkaList.containsKey("opetuspaikkak_1")) {
            studyTypeList.add(StudyTypeType.FF);
        }
        if(paikkaList.containsKey("opetuspaikkak_2")) {
            studyTypeList.add(StudyTypeType.DL);
        }
        if(muotoList.containsKey("opetuspaikkak_3")) {
            studyTypeList.add(StudyTypeType.BL);
        }
        if(muotoList.containsKey("opetuspaikkak_4")) {
            studyTypeList.add(StudyTypeType.ON);
        }
        lo.getStudyType().addAll(studyTypeList);
    }

    private I18NString createI18NString(String string){
    	I18NString temp = of.createI18NString();
    	temp.setValue(string);
    	return temp;
    }

    private void setDurationInformation(String duration, LearningOpportunity lo) {
        lo.getDurationInformation().add(createI18NString(duration));
    }

    private void setDate(Set<Date> dates, LearningOpportunity lo) {
        dates.stream().forEach(e -> lo.getStartDate().add(createI18NString(e.toString())));
    }

    private void setQualificationAwarded(Map<String, KoodiV1RDTO> list, Qualifications qualifications) {
       list.values().stream()
       	.forEach(e -> qualifications.getQualificationAwarded().add(createI18NString(e.getNimi())));
    }

    private void setQualificationDescription(Map<String, String> list, Qualifications qualifications) {
        list.values().stream()
        	.forEach(e -> qualifications.getQualificationAwardedDescription().add(createI18NString(e)));
    }

    private void setCredits(String credits, LearningOpportunity lo) {
        lo.getCredits().add(createI18NString(credits));
    }

    public void forwardLOtoJaxBParser() {
        JAXBParser.parseXML(learningOpportunities);
    }

    public void setKoulutusHakutulos(KoulutusHakutulosV1RDTO kh) {
        this.kh = kh;
    }
}