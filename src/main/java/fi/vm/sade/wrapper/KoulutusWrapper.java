package fi.vm.sade.wrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import eu.europa.ec.learningopportunities.v0_5_10.ThematicAreas;
import eu.europa.ec.learningopportunities.v0_5_10.XsdTypeType;
import fi.vm.sade.model.Koodi;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.parser.JAXBParser;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;

public class KoulutusWrapper {
    public static String COUNTRY_CODE = "FI";
    public static String TITLE_LANG_CODE_EN = "en";
    public static String LANG_CODE_KIELI_EN = "kieli_en";
    public static String LEARNING_OPPORTUNITY_KEY = "ZDR5HGWBHP0J65P5VZIYEI2ZJJF18WGW";
    public static String XSD_VERSION = "0.5.10";
    public static String XSD_TYPE = "Learning Opportunity";
    public static String URL_PREFIX = "https://opintopolku.fi/app/#!/";
    
    private static final Logger log = LoggerFactory.getLogger(KoulutusWrapper.class);

    private LearningOpportunities learningOpportunities;

    private ObjectFactory of;
    private fi.vm.sade.parser.JAXBParser JAXBParser;
    
    public KoulutusWrapper() {
        of = new ObjectFactory();
        learningOpportunities = of.createLearningOpportunities();
        learningOpportunities.setKey(LEARNING_OPPORTUNITY_KEY);
        learningOpportunities.setXsdType(XsdTypeType.fromValue(XSD_TYPE));
        learningOpportunities.setXsdVersion(XSD_VERSION);
        JAXBParser = new JAXBParser();
    }

    public void fetchAmmatillinenPerustutkintoInfo(KoulutusAmmatillinenPerustutkintoV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot, KoulutusHakutulosV1RDTO kh, Map<String, Koodi> haetutKoodit) {
        LearningOpportunity lo = initLearningOpportunity(k.getOid(), k.getKoulutuksenAlkamisPvms(), k.getHintaString(), k.getOpetusTarjoajat(),
                k.getKuvausKomo(), haetutOrganisaatiot, URL_PREFIX + "koulutus/" + k.getOid(), kh.getKoulutuskoodi(), kh.getNimi(), haetutKoodit);
        setTeachingLangs(k.getOpetuskielis().getMeta(), lo);
        setStudyType(k.getOpetusPaikkas().getUris(), k.getOpetusmuodos().getUris(), lo);
        if(k.getSuunniteltuKestoTyyppi() != null)
            setDurationInformation(k.getSuunniteltuKestoArvo(), k.getSuunniteltuKestoTyyppi().getNimi(), lo);
        setQualifications(k.getTutkintonimikes(), k.getKuvausKomo(), k.getOpetusJarjestajat(), lo, haetutOrganisaatiot);
        setCredits(k.getOpintojenLaajuusarvo(), k.getOpintojenLaajuusyksikko(), lo);
        learningOpportunities.getLearningOpportunity().add(lo);
    }

    public void fetchAmmattiInfo(AmmattitutkintoV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot, KoulutusHakutulosV1RDTO kh, Map<String, Koodi> koodisto) {
        LearningOpportunity lo = initLearningOpportunity(k.getOid(), k.getKoulutuksenAlkamisPvms(), k.getHintaString(), k.getOpetusTarjoajat(),
                k.getKuvausKomo(), haetutOrganisaatiot, URL_PREFIX + "ammatillinenaikuiskoulutus/" + k.getOid(), kh.getKoulutuskoodi(), kh.getNimi(), koodisto);
        setTeachingLangs(k.getOpetuskielis().getMeta(), lo);
        setStudyType(k.getOpetusPaikkas().getUris(), k.getOpetusmuodos().getUris(), lo);
        if(k.getSuunniteltuKestoTyyppi() != null)
            setDurationInformation(k.getSuunniteltuKestoArvo(), k.getSuunniteltuKestoTyyppi().getNimi(), lo);
        setQualifications(k.getTutkintonimike(), k.getKuvausKomo(), k.getOpetusJarjestajat(), lo, haetutOrganisaatiot);
        setCredits(k.getOpintojenLaajuusarvo(), k.getOpintojenLaajuusyksikko(), lo);
        learningOpportunities.getLearningOpportunity().add(lo);
    }

    public void fetchErikoisInfo(ErikoisammattitutkintoV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot, KoulutusHakutulosV1RDTO kh, Map<String, Koodi> koodisto) {
        LearningOpportunity lo = initLearningOpportunity(k.getOid(), k.getKoulutuksenAlkamisPvms(), k.getHintaString(), k.getOpetusTarjoajat(),
                k.getKuvausKomo(), haetutOrganisaatiot, URL_PREFIX + "ammatillinenaikuiskoulutus/" + k.getOid(), kh.getKoulutuskoodi(), kh.getNimi(), koodisto);
        setTeachingLangs(k.getOpetuskielis().getMeta(), lo);
        setStudyType(k.getOpetusPaikkas().getUris(), k.getOpetusmuodos().getUris(), lo);
        if(k.getSuunniteltuKestoTyyppi() != null)
            setDurationInformation(k.getSuunniteltuKestoArvo(), k.getSuunniteltuKestoTyyppi().getNimi(), lo);
        setQualifications(k.getTutkintonimike(), k.getKuvausKomo(), k.getOpetusJarjestajat(), lo, haetutOrganisaatiot);
        setCredits(k.getOpintojenLaajuusarvo(), k.getOpintojenLaajuusyksikko(), lo);
        learningOpportunities.getLearningOpportunity().add(lo);
    }

    public void fetchKorkeaInfo(KoulutusKorkeakouluV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot, KoulutusHakutulosV1RDTO kh, Map<String, Koodi> koodisto) {
        LearningOpportunity lo = initLearningOpportunity(k.getOid(), k.getKoulutuksenAlkamisPvms(), k.getHintaString(), k.getOpetusTarjoajat(),
                k.getKuvausKomo(), haetutOrganisaatiot, URL_PREFIX + "korkeakoulu/" + k.getOid(), kh.getKoulutuskoodi(), kh.getNimi(), koodisto);
        setTeachingLangs(k.getOpetuskielis().getMeta(), lo);
        setStudyType(k.getOpetusPaikkas().getUris(), k.getOpetusmuodos().getUris(), lo);
        if(k.getSuunniteltuKestoTyyppi() != null)
            setDurationInformation(k.getSuunniteltuKestoArvo(), k.getSuunniteltuKestoTyyppi().getNimi(), lo);
        setQualifications(k.getTutkintonimikes(), k.getKuvausKomo(), k.getOpetusJarjestajat(), lo, haetutOrganisaatiot);
        setCredits(k.getOpintojenLaajuusarvo(), k.getOpintojenLaajuusyksikko(), lo);
        
        learningOpportunities.getLearningOpportunity().add(lo);
    }

    public void fetchValmistavaInfo(ValmistavaKoulutusV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot, KoulutusHakutulosV1RDTO kh, Map<String, Koodi> koodisto) {
        LearningOpportunity lo = initLearningOpportunity(k.getOid(), k.getKoulutuksenAlkamisPvms(), k.getHintaString(), k.getOpetusTarjoajat(),
                k.getKuvausKomo(), haetutOrganisaatiot, URL_PREFIX + "koulutus/" + k.getOid(), kh.getKoulutuskoodi(), kh.getNimi(), koodisto); 
        setTeachingLangs(k.getOpetuskielis().getMeta(), lo);
        setStudyType(k.getOpetusPaikkas().getUris(), k.getOpetusmuodos().getUris(), lo);
        if(k.getSuunniteltuKestoTyyppi() != null)
            setDurationInformation(k.getSuunniteltuKestoArvo(), k.getSuunniteltuKestoTyyppi().getNimi(), lo);
        setQualifications(k.getTutkinto(), k.getKuvausKomo(), k.getOpetusJarjestajat(), lo, haetutOrganisaatiot);
        setCredits(k.getOpintojenLaajuusarvo(), k.getOpintojenLaajuusyksikko(), lo);
        learningOpportunities.getLearningOpportunity().add(lo);
    }

    public void fetchLukioInfo(KoulutusLukioV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot, KoulutusHakutulosV1RDTO kh, Map<String, Koodi> koodisto) {
        LearningOpportunity lo = initLearningOpportunity(k.getOid(), k.getKoulutuksenAlkamisPvms(), k.getHintaString(), k.getOpetusTarjoajat(),
                k.getKuvausKomo(), haetutOrganisaatiot, URL_PREFIX + "koulutus/" + k.getOid(), kh.getKoulutuskoodi(), kh.getNimi(), koodisto); 
        setTeachingLangs(k.getOpetuskielis().getMeta(), lo);
        setStudyType(k.getOpetusPaikkas().getUris(), k.getOpetusmuodos().getUris(), lo);
        if(k.getSuunniteltuKestoTyyppi() != null)
            setDurationInformation(k.getSuunniteltuKestoArvo(), k.getSuunniteltuKestoTyyppi().getNimi(), lo);
        setQualifications(k.getTutkintonimike(), k.getKuvausKomo(), k.getOpetusJarjestajat(), lo, haetutOrganisaatiot);
        setCredits(k.getOpintojenLaajuusarvo(), k.getOpintojenLaajuusyksikko(), lo);
        learningOpportunities.getLearningOpportunity().add(lo);
    }

    private LearningOpportunity initLearningOpportunity(String kOid, Set<Date> koulutuksenAlkamisPvms, String hintaString,
            Set<String> opetusTarjoajat, KuvausV1RDTO<KomoTeksti> kuvausKomo, Map<String, OrganisaatioRDTO> haetutOrganisaatiot,
            String opitopolkuUrl, String koodistoID, Map<String, String> khNimi, Map<String, Koodi> koodisto) {
        LearningOpportunity lo = of.createLearningOpportunity();
        setDate(koulutuksenAlkamisPvms, lo);
        setCost(hintaString, lo);
        setInformationLanguage(kuvausKomo.get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
        setDescription(kuvausKomo, lo);
        setProviderName(opetusTarjoajat, lo, haetutOrganisaatiot);
        setProviderContactInfo(opetusTarjoajat, lo, haetutOrganisaatiot);
        setCourseLocation(opetusTarjoajat, lo, haetutOrganisaatiot);
        lo.setLearningOpportunityId(kOid);
        lo.setCountryCode(COUNTRY_CODE);
        lo.getUrl().add(createUrl(opitopolkuUrl));
        lo.getTitle().add(createI18NonEmptyString(khNimi.get(TITLE_LANG_CODE_EN)));
        lo.setEducationLevel(koodisto.get(koodistoID).getIsced2011koulutusaste());
        ThematicAreas areas = of.createThematicAreas();
        areas.getThematicAreas1997OrThematicAreas2013().add(new JAXBElement<String>(new QName("ThematicAreas2013") , String.class, koodisto.get(koodistoID).getIsced2011koulutusalataso3()));
        lo.getThematicAreas().add(areas);
        log.debug("koodisto: " + koodisto.get(koodistoID) + ", koodiID: " + koodistoID);
        return lo;
    }

    private void setDescription(KuvausV1RDTO<KomoTeksti> kuvausKomo, LearningOpportunity lo) {
        if (kuvausKomo.get(KomoTeksti.TAVOITTEET) != null) {
            setDescription(kuvausKomo.get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
        }
    }

    private I18NUrl createUrl(String src) {
        final I18NUrl url = of.createI18NUrl();
        url.setValue(src);
        return url;
    }
    
    private I18NNonEmptyString createI18NonEmptyString(String title) {
        final I18NNonEmptyString i18Non = of.createI18NNonEmptyString();
        i18Non.setValue(title);
        i18Non.setLanguage(LanguageCode.fromValue(TITLE_LANG_CODE_EN));
        return i18Non;
    }
    
    private I18NString createI18NString(String string) {
        I18NString temp = of.createI18NString();
        temp.setValue(string);
        return temp;
    }

    private void setCredits(KoodiV1RDTO laajuusArvo, KoodiV1RDTO laajuusYksikko, LearningOpportunity lo) {
        if (laajuusArvo != null && laajuusYksikko.getMeta() != null) {
            lo.getCredits().add(createI18NString(laajuusArvo.getArvo() + " " + laajuusYksikko.getMeta().get(LANG_CODE_KIELI_EN).getNimi()));
        }
    }

    private void setQualifications(KoodiUrisV1RDTO tutkintonimikes, KuvausV1RDTO<KomoTeksti> kuvausKomo, Set<String> opetusJarjestajat,
            LearningOpportunity lo, Map<String, OrganisaatioRDTO> haetutOrganisaatiot) {
        Qualifications qualifications = of.createQualifications();
        setQualificationAwarded(tutkintonimikes.getMeta(), qualifications);
        if (kuvausKomo.get(KomoTeksti.TAVOITTEET) != null) {
            setQualificationDescription(kuvausKomo.get(KomoTeksti.TAVOITTEET).getTekstis(), qualifications);
        }
        setQualificationAwardingBody(opetusJarjestajat, qualifications, haetutOrganisaatiot);
        lo.getQualifications().add(qualifications);
    }

    private void setQualifications(KoodiV1RDTO tutkintoNimike, KuvausV1RDTO<KomoTeksti> kuvausKomo, Set<String> opetusJarjestajat, LearningOpportunity lo,
            Map<String, OrganisaatioRDTO> haetutOrganisaatiot) {
        Qualifications qualifications = of.createQualifications();
        if (kuvausKomo.get(KomoTeksti.TAVOITTEET) != null) {
            setQualificationDescription(kuvausKomo.get(KomoTeksti.TAVOITTEET).getTekstis(), qualifications);
        }
        setQualificationAwardingBody(opetusJarjestajat, qualifications, haetutOrganisaatiot);
        lo.getQualifications().add(qualifications);
    }
    
    private void setQualificationAwardingBody(Set<String> set, Qualifications qualifications, Map<String, OrganisaatioRDTO> haetutOrganisaatiot) {
    	set.forEach(s -> {
        	haetutOrganisaatiot.get(s).getNimet().stream().forEach((o) -> {
                qualifications.getAwardingBody().add(createI18NString(o.getNimi().get("en")));
            });
        });
    }

    private void setQualificationAwarded(Map<String, KoodiV1RDTO> list, Qualifications qualifications) {
        list.values().stream().forEach(e -> {
            qualifications.getQualificationAwarded().add(createI18NString(e.getNimi()));
        });
    }

    private void setQualificationDescription(Map<String, String> list, Qualifications qualifications) {
        list.values().stream().forEach(e -> qualifications.getQualificationAwardedDescription().add(createI18NString(e)));
    }
    
    private void setDurationInformation(String suunniteltuKestoArvo, String suunniteltuNimi, LearningOpportunity lo) {
        if (suunniteltuKestoArvo != null) {
            lo.getDurationInformation().add(createI18NString(suunniteltuKestoArvo + " " + suunniteltuNimi));
        }
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

    private void setProviderName(Set<String> set, LearningOpportunity lo, Map<String, OrganisaatioRDTO> haetutOrganisaatiot) {
        set.forEach(s -> {
        	haetutOrganisaatiot.get(s).getNimet().stream().forEach((o) -> {
                lo.getProviderName().add(createI18NonEmptyString(o.getNimi().get("en")));
            });
        });
    }

    private void setProviderContactInfo(Set<String> set, LearningOpportunity lo, Map<String, OrganisaatioRDTO> haetutOrganisaatiot) {
        ArrayList<I18NString> list = new ArrayList<>();
        set.forEach(s -> {
        	haetutOrganisaatiot.get(s).getYhteystiedot().forEach(p -> { 
        		if (p.get("postitoimipaikka") != null && p.get("osoite") != null && p.get("postinumeroUri") != null) {
                    list.add(createI18NString(p.get("osoite") + ", " + p.get("postinumeroUri").replace("posti_", "") + ", " + p.get("postitoimipaikka")));
                }
                if (p.get("numero") != null) {
                    list.add(createI18NString(p.get("numero")));
                }
                if(haetutOrganisaatiot.get(s).getNimi().get("en") != null){
                	list.add(createI18NString(haetutOrganisaatiot.get(s).getNimi().get("en")));
                }
                
                if(haetutOrganisaatiot.get(s).getMetadata() != null){
                    haetutOrganisaatiot.get(s).getMetadata().getYhteystiedot().stream().forEach(m -> {
                        if(m.get("email") != null){
                            list.add(createI18NString(haetutOrganisaatiot.get(s).getMetadata().getYhteystiedot().get(2).get("email")));
                        }
                    }); 
                }
                lo.getProviderContactInfo().addAll(list);
        	});
        });
    }
    
    private void setCourseLocation(Set<String> opetusTarjoajat, LearningOpportunity lo, Map<String, OrganisaatioRDTO> haetutOrganisaatiot){
        CourseLocation co = of.createCourseLocation();
        setCourseAddress(opetusTarjoajat, co, haetutOrganisaatiot);
        setSpecialArrangements(opetusTarjoajat, co, haetutOrganisaatiot);
        lo.getCourseLocation().add(co);
    }
    
    private void setCourseAddress(Set<String> set, CourseLocation co, Map<String, OrganisaatioRDTO> haetutOrganisaatiot){
        set.forEach(s -> {
            haetutOrganisaatiot.get(s).getYhteystiedot().forEach(p -> {
                if (p.get("postitoimipaikka") != null && p.get("osoite") != null && p.get("postinumeroUri") != null) {
                    co.getCourseAddress().add(createI18NString(p.get("osoite") + ", " + p.get("postinumeroUri").replace("posti_", "") + ", " + p.get("postitoimipaikka")));
                }
            });
        });
    }
    
    private void setSpecialArrangements(Set<String> set, CourseLocation co, Map<String, OrganisaatioRDTO> haetutOrganisaatiot){
        set.forEach(s -> {
            haetutOrganisaatiot.get(s).getYhteystiedot().forEach(p -> {
                if (haetutOrganisaatiot.get(s).getMetadata() != null
                        && haetutOrganisaatiot.get(s).getMetadata().getData().get("ESTEETOMYYS") != null) {
                    co.getSpecialArrangements().add(createI18NString(haetutOrganisaatiot.get(s).getMetadata().getData().get("ESTEETOMYYS").get("kieli_en#1")));
                }
            });
        });
    }

    private void setCost(String cost, LearningOpportunity lo) {
        lo.getCosts().add(createI18NString(cost));
    }

    private void setDescription(Map<String, String> descriptions, LearningOpportunity lo) {
        descriptions.keySet().stream().filter(e -> e != null && !e.isEmpty())
                .forEach(e -> lo.getDescription().add(createI18NonEmptyString(descriptions.get(e))));
    }

    private void setTeachingLangs(Map<String, KoodiV1RDTO> teachingLangs, LearningOpportunity lo) {
        teachingLangs.values().stream().filter(e -> !e.getArvo().toLowerCase().equals("la"))
                .forEach(e -> lo.getTeachingLanguage().add(LanguageCode.fromValue(e.getArvo().toLowerCase())));
    }

    private void setStudyType(Map<String, Integer> paikkaList, Map<String, Integer> muotoList, LearningOpportunity lo) {
        List<StudyTypeType> studyTypeList = new ArrayList<>();
        if (paikkaList.containsKey("opetuspaikkak_1")) {
            studyTypeList.add(StudyTypeType.FF);
        }
        if (paikkaList.containsKey("opetuspaikkak_2")) {
            studyTypeList.add(StudyTypeType.DL);
        }
        if (muotoList.containsKey("opetusmuotokk_3")) {
            studyTypeList.add(StudyTypeType.BL);
        }
        if (muotoList.containsKey("opetusmuotokk_4")) {
            studyTypeList.add(StudyTypeType.ON);
        }
        lo.getStudyType().addAll(studyTypeList);
    }

    private void setDate(Set<Date> dates, LearningOpportunity lo) {
        dates.stream().forEach(e -> lo.getStartDate().add(createI18NString(e.toString())));
    }

    public void forwardLOtoJaxBParser() {
        JAXBParser.parseXML(learningOpportunities);
    }
}