package fi.vm.sade.wrapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.hadoop.mapred.gethistory_jsp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioNimiRDTO;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.parser.JAXBParser;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;

@Component
public class KoulutusWrapper {
    public static String COUNTRY_CODE = "FI";
    public static String TITLE_LANG_CODE_EN = "en";
    public static String TITLE_LANG_CODE_FI = "fi";
    public static String TITLE_LANG_CODE_SV = "sv";
    public static String LANG_CODE_KIELI_EN = "kieli_en";
    public static String LANG_CODE_KIELI_FI = "kieli_fi";
    public static String LANG_CODE_KIELI_SV = "kieli_sv";
    public static String LEARNING_OPPORTUNITY_KEY = "ZDR5HGWBHP0J65P5VZIYEI2ZJJF18WGW";
    public static String XSD_VERSION = "0.5.10";
    public static String XSD_TYPE = "Learning Opportunity";
    public static String URL_PREFIX_FIN = "https://opintopolku.fi/app/#!/";
    public static String URL_PREFIX_EN = "https://studyinfo.fi/app/#!/";
    public static String URL_PREFIX_SV = "https://studieinfo.fi/app/#!/";

    private static final Logger log = LoggerFactory.getLogger(KoulutusWrapper.class);

    private LearningOpportunities learningOpportunities;

    private ObjectFactory of;
    private fi.vm.sade.parser.JAXBParser JAXBParser;

    private String tagString;

    @Autowired
    public KoulutusWrapper(JAXBParser jAXBParser) {
        of = new ObjectFactory();
        learningOpportunities = of.createLearningOpportunities();
        learningOpportunities.setKey(LEARNING_OPPORTUNITY_KEY);
        learningOpportunities.setXsdType(XsdTypeType.fromValue(XSD_TYPE));
        learningOpportunities.setXsdVersion(XSD_VERSION);
        JAXBParser = jAXBParser;
        InputStream in = getClass().getResourceAsStream("/HTMLTagsToBeRemoved.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        try {
            for (String line; (line = br.readLine()) != null;) {
                tagString += "|" + line;
            }
        } catch (IOException e) {
            log.error("Constructor file load failed: " + e);
        }
    }

    public void fetchAmmatillinenPerustutkintoInfo(KoulutusAmmatillinenPerustutkintoV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot,
            KoulutusHakutulosV1RDTO kh, Map<String, Koodi> haetutKoodit) {
        LearningOpportunity lo = initLearningOpportunity(k.getOid(), k.getKoulutuksenAlkamisPvms(), k.getHintaString(), k.getOpetusTarjoajat(),
                k.getKuvausKomo(), haetutOrganisaatiot, URL_PREFIX_FIN + "koulutus/" + k.getOid(), URL_PREFIX_EN + "koulutus/" + k.getOid(),
                URL_PREFIX_SV + "koulutus/" + k.getOid(), kh.getKoulutuskoodi(), k.getKoulutusohjelma(), haetutKoodit, k.getKoulutuskoodi(), k.getKuvausKomoto().get("SISALTO"));
        setTeachingLangs(k.getOpetuskielis().getMeta(), lo);
        setStudyType(k.getOpetusPaikkas().getUris(), k.getOpetusmuodos().getUris(), lo);
        if (k.getSuunniteltuKestoTyyppi() != null)
            setDurationInformation(k.getSuunniteltuKestoArvo(), k.getSuunniteltuKestoTyyppi().getNimi(), lo);
        setQualifications(k.getTutkintonimikes(), k.getKuvausKomo(), k.getOpetusJarjestajat(), lo, haetutOrganisaatiot);
        setCredits(k.getOpintojenLaajuusarvo(), k.getOpintojenLaajuusyksikko(), lo);
        learningOpportunities.getLearningOpportunity().add(lo);
    }

    public void fetchAmmattiInfo(AmmattitutkintoV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot, KoulutusHakutulosV1RDTO kh,
            Map<String, Koodi> koodisto) {
        KuvausV1RDTO<KomotoTeksti> kuvaus = k.getValmistavaKoulutus().getKuvaus();
        NimiV1RDTO kuvausTeksti = kuvaus.get("SISALTO"); //TODO: miksi? debugerilla data saatavilla
        LearningOpportunity lo = initLearningOpportunity(k.getOid(), k.getKoulutuksenAlkamisPvms(), k.getHintaString(), k.getOpetusTarjoajat(),
                k.getKuvausKomo(), haetutOrganisaatiot, URL_PREFIX_FIN + "ammatillinenaikuiskoulutus/" + k.getOid(),
                URL_PREFIX_EN + "ammatillinenaikuiskoulutus/" + k.getOid(), URL_PREFIX_SV + "ammatillinenaikuiskoulutus/" + k.getOid(),
                kh.getKoulutuskoodi(), k.getKoulutusohjelma(), koodisto, k.getKoulutuskoodi(), kuvausTeksti);
        setTeachingLangs(k.getOpetuskielis().getMeta(), lo);
        setStudyType(k.getOpetusPaikkas().getUris(), k.getOpetusmuodos().getUris(), lo);
        if (k.getSuunniteltuKestoTyyppi() != null)
            setDurationInformation(k.getSuunniteltuKestoArvo(), k.getSuunniteltuKestoTyyppi().getNimi(), lo);
        setQualifications(k.getTutkintonimike(), k.getKuvausKomo(), k.getOpetusJarjestajat(), lo, haetutOrganisaatiot);
        setCredits(k.getOpintojenLaajuusarvo(), k.getOpintojenLaajuusyksikko(), lo);
        learningOpportunities.getLearningOpportunity().add(lo);
    }

    public void fetchErikoisInfo(ErikoisammattitutkintoV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot, KoulutusHakutulosV1RDTO kh,
            Map<String, Koodi> koodisto) {
        LearningOpportunity lo = initLearningOpportunity(k.getOid(), k.getKoulutuksenAlkamisPvms(), k.getHintaString(), k.getOpetusTarjoajat(),
                k.getKuvausKomo(), haetutOrganisaatiot, URL_PREFIX_FIN + "ammatillinenaikuiskoulutus/" + k.getOid(),
                URL_PREFIX_EN + "ammatillinenaikuiskoulutus/" + k.getOid(), URL_PREFIX_SV + "ammatillinenaikuiskoulutus/" + k.getOid(),
                kh.getKoulutuskoodi(), k.getKoulutusohjelma(), koodisto, k.getKoulutuskoodi(), k.getKuvausKomoto().get("SISALTO"));
        setTeachingLangs(k.getOpetuskielis().getMeta(), lo);
        setStudyType(k.getOpetusPaikkas().getUris(), k.getOpetusmuodos().getUris(), lo);
        if (k.getSuunniteltuKestoTyyppi() != null)
            setDurationInformation(k.getSuunniteltuKestoArvo(), k.getSuunniteltuKestoTyyppi().getNimi(), lo);
        setQualifications(k.getTutkintonimike(), k.getKuvausKomo(), k.getOpetusJarjestajat(), lo, haetutOrganisaatiot);
        setCredits(k.getOpintojenLaajuusarvo(), k.getOpintojenLaajuusyksikko(), lo);
        learningOpportunities.getLearningOpportunity().add(lo);
    }

    public void fetchKorkeaInfo(KoulutusKorkeakouluV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot, KoulutusHakutulosV1RDTO kh,
            Map<String, Koodi> koodisto) {
        LearningOpportunity lo = initLearningOpportunity(k.getOid(), k.getKoulutuksenAlkamisPvms(), k.getHintaString(), k.getOpetusTarjoajat(),
                k.getKuvausKomo(), haetutOrganisaatiot, URL_PREFIX_FIN + "korkeakoulu/" + k.getOid(), URL_PREFIX_EN + "korkeakoulu/" + k.getOid(),
                URL_PREFIX_SV + "korkeakoulu/" + k.getOid(), kh.getKoulutuskoodi(), k.getKoulutusohjelma(), koodisto, k.getKoulutuskoodi(), k.getKuvausKomoto().get("SISALTO"));
        setTeachingLangs(k.getOpetuskielis().getMeta(), lo);
        setStudyType(k.getOpetusPaikkas().getUris(), k.getOpetusmuodos().getUris(), lo);
        if (k.getSuunniteltuKestoTyyppi() != null)
            setDurationInformation(k.getSuunniteltuKestoArvo(), k.getSuunniteltuKestoTyyppi().getNimi(), lo);
        setQualifications(k.getTutkintonimikes(), k.getKuvausKomo(), k.getOpetusJarjestajat(), lo, haetutOrganisaatiot);
        setCredits(k.getOpintojenLaajuusarvo(), k.getOpintojenLaajuusyksikko(), lo);

        learningOpportunities.getLearningOpportunity().add(lo);
    }

    public void fetchValmistavaInfo(ValmistavaKoulutusV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot, KoulutusHakutulosV1RDTO kh,
            Map<String, Koodi> koodisto) {
        LearningOpportunity lo = initLearningOpportunity(k.getOid(), k.getKoulutuksenAlkamisPvms(), k.getHintaString(), k.getOpetusTarjoajat(),
                k.getKuvausKomo(), haetutOrganisaatiot, URL_PREFIX_FIN + "koulutus/" + k.getOid(), URL_PREFIX_EN + "koulutus/" + k.getOid(),
                URL_PREFIX_SV + "koulutus/" + k.getOid(), kh.getKoulutuskoodi(), k.getKoulutusohjelma(), koodisto, k.getKoulutuskoodi(), k.getKuvausKomoto().get("SISALTO"));
        setTeachingLangs(k.getOpetuskielis().getMeta(), lo);
        setStudyType(k.getOpetusPaikkas().getUris(), k.getOpetusmuodos().getUris(), lo);
        if (k.getSuunniteltuKestoTyyppi() != null)
            setDurationInformation(k.getSuunniteltuKestoArvo(), k.getSuunniteltuKestoTyyppi().getNimi(), lo);
        setQualifications(k.getTutkinto(), k.getKuvausKomo(), k.getOpetusJarjestajat(), lo, haetutOrganisaatiot);
        setCredits(k.getOpintojenLaajuusarvo(), k.getOpintojenLaajuusyksikko(), lo);
        learningOpportunities.getLearningOpportunity().add(lo);
    }

    public void fetchLukioInfo(KoulutusLukioV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot, KoulutusHakutulosV1RDTO kh,
            Map<String, Koodi> koodisto) {
        LearningOpportunity lo = initLearningOpportunity(k.getOid(), k.getKoulutuksenAlkamisPvms(), k.getHintaString(), k.getOpetusTarjoajat(),
                k.getKuvausKomo(), haetutOrganisaatiot, URL_PREFIX_FIN + "koulutus/" + k.getOid(), URL_PREFIX_EN + "koulutus/" + k.getOid(),
                URL_PREFIX_SV + "koulutus/" + k.getOid(), kh.getKoulutuskoodi(), k.getKoulutusohjelma(), koodisto, k.getKoulutuskoodi(), k.getKuvausKomoto().get("SISALTO"));
        setTeachingLangs(k.getOpetuskielis().getMeta(), lo);
        setStudyType(k.getOpetusPaikkas().getUris(), k.getOpetusmuodos().getUris(), lo);
        if (k.getSuunniteltuKestoTyyppi() != null)
            setDurationInformation(k.getSuunniteltuKestoArvo(), k.getSuunniteltuKestoTyyppi().getNimi(), lo);
        setQualifications(k.getTutkintonimike(), k.getKuvausKomo(), k.getOpetusJarjestajat(), lo, haetutOrganisaatiot);
        setCredits(k.getOpintojenLaajuusarvo(), k.getOpintojenLaajuusyksikko(), lo);
        learningOpportunities.getLearningOpportunity().add(lo);
    }

    public void fetchAPNayttotutkintonaInfo(KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO k,
            Map<String, OrganisaatioRDTO> haetutOrganisaatiot, KoulutusHakutulosV1RDTO kh, Map<String, Koodi> koodisto) {
        LearningOpportunity lo = initLearningOpportunity(k.getOid(), k.getKoulutuksenAlkamisPvms(), k.getHintaString(), k.getOpetusTarjoajat(),
                k.getKuvausKomo(), haetutOrganisaatiot, URL_PREFIX_FIN + "ammatillinenaikuiskoulutus/" + k.getOid(),
                URL_PREFIX_EN + "ammatillinenaikuiskoulutus/" + k.getOid(), URL_PREFIX_SV + "ammatillinenaikuiskoulutus/" + k.getOid(),
                kh.getKoulutuskoodi(), k.getKoulutusohjelma(), koodisto, k.getKoulutuskoodi(), k.getKuvausKomoto().get("SISALTO"));
        setTeachingLangs(k.getOpetuskielis().getMeta(), lo);
        setStudyType(k.getOpetusPaikkas().getUris(), k.getOpetusmuodos().getUris(), lo);
        if (k.getSuunniteltuKestoTyyppi() != null)
            setDurationInformation(k.getSuunniteltuKestoArvo(), k.getSuunniteltuKestoTyyppi().getNimi(), lo);
        setQualifications(k.getTutkintonimike(), k.getKuvausKomo(), k.getOpetusJarjestajat(), lo, haetutOrganisaatiot);
        setCredits(k.getOpintojenLaajuusarvo(), k.getOpintojenLaajuusyksikko(), lo);
        learningOpportunities.getLearningOpportunity().add(lo);
    }

    private LearningOpportunity initLearningOpportunity(String kOid, Set<Date> koulutuksenAlkamisPvms, String hintaString,
            Set<String> opetusTarjoajat, KuvausV1RDTO<KomoTeksti> kuvausKomo, Map<String, OrganisaatioRDTO> haetutOrganisaatiot, String opitopolkuUrl,
            String opintopolkuUrlEn, String opintopolkuUrlSv, String koodistoID, NimiV1RDTO koulutusohjelma, Map<String, Koodi> koodisto,
            KoodiV1RDTO koulutuskoodi, NimiV1RDTO nimiV1RDTO) {
        LearningOpportunity lo = of.createLearningOpportunity();
        setDate(koulutuksenAlkamisPvms, lo);
        setCost(hintaString, lo);
        if (kuvausKomo.get(KomoTeksti.TAVOITTEET) != null) {
            setInformationLanguage(kuvausKomo.get(KomoTeksti.TAVOITTEET).getTekstis(), lo);
        }
        setDescription(nimiV1RDTO, lo);
        setProviderName(opetusTarjoajat, lo, haetutOrganisaatiot);
        setProviderContactInfo(opetusTarjoajat, lo, haetutOrganisaatiot);
        setCourseLocation(opetusTarjoajat, lo, haetutOrganisaatiot);
        setThematicAreas(koodistoID, koodisto, lo);
        lo.setLearningOpportunityId(kOid);
        lo.setCountryCode(COUNTRY_CODE);
        lo.getUrl().add(createUrl(opitopolkuUrl, "fi"));
        lo.getUrl().add(createUrl(opintopolkuUrlEn, "en"));
        lo.getUrl().add(createUrl(opintopolkuUrlSv, "sv"));
        setTitle(koulutusohjelma, lo, koulutuskoodi);
        lo.setEducationLevel(koodisto.get(koodistoID).getIsced2011koulutusaste());
        return lo;
    }

    private void setTitle(NimiV1RDTO koulutusohjelma, LearningOpportunity lo, KoodiV1RDTO koulutuskoodi) {
        if (!koulutusohjelma.getTekstis().isEmpty()) {
            if (koulutusohjelma.getTekstis().get(LANG_CODE_KIELI_EN) != null && !koulutusohjelma.getTekstis().get(LANG_CODE_KIELI_EN).trim().equals("")) {
                lo.getTitle().add(createI18NonEmptyString(koulutusohjelma.getTekstis().get(LANG_CODE_KIELI_EN), TITLE_LANG_CODE_EN));
            } else if (koulutusohjelma.getTekstis().get(LANG_CODE_KIELI_FI) != null && !koulutusohjelma.getTekstis().get(LANG_CODE_KIELI_FI).trim().equals("")) {
                lo.getTitle().add(createI18NonEmptyString(koulutusohjelma.getTekstis().get(LANG_CODE_KIELI_FI), TITLE_LANG_CODE_FI));
            } else {
                lo.getTitle().add(createI18NonEmptyString(koulutusohjelma.getTekstis().get(LANG_CODE_KIELI_SV), TITLE_LANG_CODE_SV));
            }
        } else if (koulutusohjelma.getMeta() != null && !koulutusohjelma.getMeta().isEmpty()) {
            if (koulutusohjelma.getMeta().get(LANG_CODE_KIELI_EN) != null && !koulutusohjelma.getMeta().get(LANG_CODE_KIELI_EN).getNimi().trim().equals("")) {
                lo.getTitle().add(createI18NonEmptyString(koulutusohjelma.getMeta().get(LANG_CODE_KIELI_EN).getNimi(), TITLE_LANG_CODE_EN));
            } else if (koulutusohjelma.getMeta().get(LANG_CODE_KIELI_FI) != null && !koulutusohjelma.getMeta().get(LANG_CODE_KIELI_FI).getNimi().trim().equals("")) {
                lo.getTitle().add(createI18NonEmptyString(koulutusohjelma.getMeta().get(LANG_CODE_KIELI_FI).getNimi(), TITLE_LANG_CODE_FI));
            } else {
                lo.getTitle().add(createI18NonEmptyString(koulutusohjelma.getMeta().get(LANG_CODE_KIELI_SV).getNimi(), TITLE_LANG_CODE_SV));
            }
        } else {
            if (koulutuskoodi.getMeta().get(LANG_CODE_KIELI_EN) != null && !koulutuskoodi.getMeta().get(LANG_CODE_KIELI_EN).getNimi().trim().equals("")) {
                lo.getTitle().add(createI18NonEmptyString(koulutuskoodi.getMeta().get(LANG_CODE_KIELI_EN).getNimi(), TITLE_LANG_CODE_EN));
            } else if (koulutuskoodi.getMeta().get(LANG_CODE_KIELI_FI) != null && !koulutuskoodi.getMeta().get(LANG_CODE_KIELI_FI).getNimi().trim().equals("")) {
                lo.getTitle().add(createI18NonEmptyString(koulutuskoodi.getMeta().get(LANG_CODE_KIELI_FI).getNimi(), TITLE_LANG_CODE_FI));
            } else {
                lo.getTitle().add(createI18NonEmptyString(koulutuskoodi.getMeta().get(LANG_CODE_KIELI_SV).getNimi(), TITLE_LANG_CODE_SV));
            }
        }
    }

    private void setThematicAreas(String koodistoID, Map<String, Koodi> koodisto, LearningOpportunity lo) {
        ThematicAreas areas = of.createThematicAreas();
        areas.getThematicAreas1997OrThematicAreas2013()
                .add(of.createThematicAreasThematicAreas2013(koodisto.get(koodistoID).getIsced2011koulutusalataso3()));
        lo.getThematicAreas().add(areas);
        log.debug("koodisto: " + koodisto.get(koodistoID) + ", koodiID: " + koodistoID);
    }

    private void setDescription(NimiV1RDTO nimiV1RDTO, LearningOpportunity lo) {
       // if (nimiV1RDTO != null ) {
            System.out.println(nimiV1RDTO);
            Map<String, String> descriptions = nimiV1RDTO.getTekstis();
            lo.getDescription().clear();
            List<String> descs = new ArrayList<String>();
            descriptions.keySet().stream().filter(e -> e.equals("kieli_en")).forEach(e -> {
                if (!descriptions.get(e).trim().equals("")) {
                    descs.add(descriptions.get(e));
                }
            });
            String lang = "en";
            if (descs.isEmpty()) {
                lang = "fi";
                descriptions.keySet().stream().filter(e -> e.equals("kieli_fi")).forEach(e -> {
                    if (!descriptions.get(e).trim().equals("")) {
                        descs.add(descriptions.get(e));
                    }
                });
                if (descs.isEmpty()) {
                    lang = "sv";
                    descriptions.keySet().stream().filter(e -> e.equals("kieli_sv")).forEach(e -> {
                        if (!descriptions.get(e).trim().equals("")) {
                            descs.add(descriptions.get(e));
                        }
                    });
                }
            }
            if (!descs.isEmpty()) {
                lo.getDescription().add(createI18NonEmptyString(removeUnwantedHTMLTags(descs.get(0)), lang));
            }
        //}
    }

    private I18NUrl createUrl(String src, String lang) {
        final I18NUrl url = of.createI18NUrl();
        url.setLanguage(LanguageCode.fromValue(lang));
        url.setValue(src);
        return url;
    }

    private I18NNonEmptyString createI18NonEmptyString(String string, String lang) {
        final I18NNonEmptyString i18Non = of.createI18NNonEmptyString();
        i18Non.setValue(string);
        i18Non.setLanguage(LanguageCode.fromValue(lang));
        return i18Non;
    }

    private I18NString createI18NString(String string, String lang) {
        I18NString temp = of.createI18NString();
        temp.setValue(string);
        temp.setLanguage(LanguageCode.fromValue(lang));
        return temp;
    }

    private I18NString createI18NString(String string) {
        I18NString temp = of.createI18NString();
        temp.setValue(string);
        return temp;
    }

    private void setCredits(KoodiV1RDTO laajuusArvo, KoodiV1RDTO laajuusYksikko, LearningOpportunity lo) {
        if (laajuusArvo != null && laajuusYksikko.getMeta() != null) {
            lo.getCredits().add(createI18NString(laajuusArvo.getArvo() + " " + laajuusYksikko.getMeta().get(LANG_CODE_KIELI_EN).getNimi(), "en"));
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
        if (qualifications.getQualificationAwarded() != null && !qualifications.getQualificationAwarded().isEmpty()) {
            lo.getQualifications().add(qualifications);
        }
    }

    private void setQualifications(KoodiV1RDTO tutkintoNimike, KuvausV1RDTO<KomoTeksti> kuvausKomo, Set<String> opetusJarjestajat,
            LearningOpportunity lo, Map<String, OrganisaatioRDTO> haetutOrganisaatiot) {
        Qualifications qualifications = of.createQualifications();
        if (kuvausKomo.get(KomoTeksti.TAVOITTEET) != null) {
            setQualificationDescription(kuvausKomo.get(KomoTeksti.TAVOITTEET).getTekstis(), qualifications);
        }
        setQualificationAwardingBody(opetusJarjestajat, qualifications, haetutOrganisaatiot);
        if (qualifications.getQualificationAwarded() != null && !qualifications.getQualificationAwarded().isEmpty()) {
            lo.getQualifications().add(qualifications);
        }
    }

    private void setQualificationAwardingBody(Set<String> set, Qualifications qualifications, Map<String, OrganisaatioRDTO> haetutOrganisaatiot) {
        set.forEach(s -> {
            haetutOrganisaatiot.get(s).getNimet().stream().forEach((o) -> {
                qualifications.getAwardingBody().add(createI18NString(o.getNimi().get("en"), "en"));
            });
        });
    }

    private void setQualificationAwarded(Map<String, KoodiV1RDTO> list, Qualifications qualifications) {
        String qualification = "";
        for (Entry<String, KoodiV1RDTO> e : list.entrySet()) {
            if (qualification.equals("")) {
                qualification = qualification.concat(e.getValue().getNimi());
            } else {
                qualification = qualification.concat(", " + e.getValue().getNimi());
            }
        }
        qualifications.getQualificationAwarded().add(createI18NString(qualification));
    }

    private void setQualificationDescription(Map<String, String> list, Qualifications qualifications) {

        list.keySet().stream().forEach(e -> {
            qualifications.getQualificationAwardedDescription().add(createI18NString(removeUnwantedHTMLTags(list.get(e)), e.replace("kieli_", "")));
        });
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
            if (haetutOrganisaatiot.get(s) != null) {

                haetutOrganisaatiot.get(s).getNimet().stream().forEach((o) -> {
                    if (lo.getProviderName().isEmpty()) {
                        if (o.getNimi().get("en") != null && !o.getNimi().get("en").isEmpty()) {
                            lo.getProviderName().add(createI18NonEmptyString(o.getNimi().get("en"), "en"));
                        } else if (o.getNimi().get("fi") != null && !o.getNimi().get("fi").isEmpty()) {
                            lo.getProviderName().add(createI18NonEmptyString(o.getNimi().get("fi"), "fi"));
                        } else {
                            lo.getProviderName().add(createI18NonEmptyString(o.getNimi().get("sv"), "sv"));
                        }
                    }
                });

            }
        });
    }

    private void setProviderContactInfo(Set<String> set, LearningOpportunity lo, Map<String, OrganisaatioRDTO> haetutOrganisaatiot) {
        ArrayList<I18NString> list = new ArrayList<>();
        set.forEach(s -> {
            if (haetutOrganisaatiot.get(s) != null) {
                haetutOrganisaatiot.get(s).getYhteystiedot().forEach(p -> {
                    String info = "";

                    if (p.get("osoite") != null && !p.get("osoite").trim().isEmpty()) {
                        info = info.concat(p.get("osoite"));

                        if (p.get("postinumeroUri") != null && !p.get("postinumeroUri").trim().isEmpty()
                                && !p.get("postinumeroUri").contains("00000")) {
                            info = info.concat(", " + p.get("postinumeroUri").replace("posti_", ""));

                            if (p.get("postitoimipaikka") != null && !p.get("postitoimipaikka").trim().isEmpty()) {
                                info = info.concat(", " + p.get("postitoimipaikka"));
                            }
                        }
                        I18NString locationInfo = createI18NString(info, "fi");
                        if (!list.stream().filter(o -> o.getValue().equals(locationInfo.getValue())).findFirst().isPresent()) {
                            list.add(locationInfo);
                        }
                    }

                    if (p.get("numero") != null) {
                        list.add(createI18NString(p.get("numero"), "fi"));
                    }
                    if (haetutOrganisaatiot.get(s).getNimi().get("en") != null) {
                        list.add(createI18NString(haetutOrganisaatiot.get(s).getNimi().get("en"), "en"));
                    }

                    if (haetutOrganisaatiot.get(s).getMetadata() != null) {
                        haetutOrganisaatiot.get(s).getMetadata().getYhteystiedot().stream().forEach(m -> {
                            if (m.get("email") != null) {
                                I18NString email = createI18NString(m.get("email").trim(), "fi");
                                if (!list.stream().filter(o -> o.getValue().equals(email.getValue())).findFirst().isPresent()) {
                                    list.add(email);
                                }
                            }
                        });
                    }
                });

            }
        });
        String providerContactInfo = "";
        for (I18NString s : list) {
            providerContactInfo += s.getValue() + "<br>";
        }
        lo.getProviderContactInfo().add(createI18NString(providerContactInfo, "en"));
    }

    private void setCourseLocation(Set<String> opetusTarjoajat, LearningOpportunity lo, Map<String, OrganisaatioRDTO> haetutOrganisaatiot) {
        CourseLocation co = of.createCourseLocation();
        setCourseAddress(opetusTarjoajat, co, haetutOrganisaatiot);
        setSpecialArrangements(opetusTarjoajat, co, haetutOrganisaatiot);

        if (!co.getCourseAddress().isEmpty() || !co.getCourseLocationInfo().isEmpty()) {
            lo.getCourseLocation().add(co);
        }
    }

    private void setCourseAddress(Set<String> set, CourseLocation co, Map<String, OrganisaatioRDTO> haetutOrganisaatiot) {
        set.forEach(s -> {
            if (haetutOrganisaatiot.get(s) != null) {
                haetutOrganisaatiot.get(s).getYhteystiedot().forEach(p -> {
                    String address = "";

                    if (p.get("osoite") != null && !p.get("osoite").trim().isEmpty() && !p.get("osoite").startsWith("PL")) {
                        address = address.concat(p.get("osoite"));

                        if (p.get("postinumeroUri") != null && !p.get("postinumeroUri").trim().isEmpty()
                                && !p.get("postinumeroUri").contains("00000")) {
                            address = address.concat(", " + p.get("postinumeroUri").replace("posti_", ""));

                            if (p.get("postitoimipaikka") != null && !p.get("postitoimipaikka").trim().isEmpty()) {
                                address = address.concat(", " + p.get("postitoimipaikka"));
                            }
                        }
                    }
                    if (!address.isEmpty()) {
                        I18NString addressInfo = createI18NString(address, "fi");
                        if (!co.getCourseAddress().stream().filter(o -> o.getValue().equals(addressInfo.getValue())).findFirst().isPresent()) {
                            if (co.getCourseAddress().isEmpty()) {
                                co.getCourseAddress().add(createI18NString(address, "fi"));
                            } else {
                                String addressTemp = co.getCourseAddress().get(0).getValue();
                                addressTemp += co.getCourseAddress().get(0).getValue().concat("<br>" + address);
                                co.getCourseAddress().get(0).setValue(addressTemp);
                            }
                        }
                    }
                });
            }
        });
    }

    private void setSpecialArrangements(Set<String> set, CourseLocation co, Map<String, OrganisaatioRDTO> haetutOrganisaatiot) {
        set.forEach(s -> {
            if (haetutOrganisaatiot.get(s) != null) {
                haetutOrganisaatiot.get(s).getYhteystiedot().forEach(p -> {
                    if (haetutOrganisaatiot.get(s).getMetadata() != null
                            && haetutOrganisaatiot.get(s).getMetadata().getData().get("ESTEETOMYYS") != null
                            && !haetutOrganisaatiot.get(s).getMetadata().getData().get("ESTEETOMYYS").isEmpty()
                            && haetutOrganisaatiot.get(s).getMetadata().getData().get("ESTEETOMYYS").get("kieli_en#1") != null
                            && !haetutOrganisaatiot.get(s).getMetadata().getData().get("ESTEETOMYYS").get("kieli_en#1").trim().isEmpty()) {
                        I18NString esteettomyys = createI18NString(
                                haetutOrganisaatiot.get(s).getMetadata().getData().get("ESTEETOMYYS").get("kieli_en#1"), "en");
                        if (co.getSpecialArrangements().stream().filter(o -> o.getValue().equals(esteettomyys.getValue())).findFirst().isPresent()) {
                            co.getSpecialArrangements().add(esteettomyys);
                        }
                    }
                });
            }
        });
    }

    private void setCost(String cost, LearningOpportunity lo) {
        lo.getCosts().add(createI18NString(cost, "fi"));
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
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy");
        String dateString = "";
        for (Date e : dates) {
            if (dateString.equals("")) {
                dateString = formatter.format(e);
            } else {
                dateString += ", " + formatter.format(e);
            }
        }
        I18NString dateTemp = createI18NString(dateString, "fi");
        lo.getStartDate().add(dateTemp);
    }

    private String removeUnwantedHTMLTags(String input) {
        String stripped = null;
        if (input != null) {
            stripped = input.replaceAll("</?(" + tagString + "){1}.*?/?>", "");
        }
        return stripped;
    }

    public void forwardLOtoJaxBParser() {
        JAXBParser.parseXML(learningOpportunities);
    }

}