package fi.vm.sade.wrapper;

import eu.europa.ec.learningopportunities.v0_5_10.*;
import fi.vm.sade.model.Koodi;
import fi.vm.sade.model.StatusObject;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.parser.JAXBParser;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Component
public class KoulutusWrapper {
    private static final String COUNTRY_CODE = "FI";
    private static final String TITLE_LANG_CODE_EN = "en";
    private static final String TITLE_LANG_CODE_FI = "fi";
    private static final String TITLE_LANG_CODE_SV = "sv";
    private static final String LANG_CODE_KIELI_EN = "kieli_en";
    private static final String LANG_CODE_KIELI_FI = "kieli_fi";
    private static final String LANG_CODE_KIELI_SV = "kieli_sv";
    private static final String LEARNING_OPPORTUNITY_KEY = "ZDR5HGWBHP0J65P5VZIYEI2ZJJF18WGW";
    private static final String XSD_VERSION = "0.5.10";
    private static final String XSD_TYPE = "Learning Opportunity";
    private static final String URL_PREFIX_FI = "https://opintopolku.fi/app/#!/";
    private static final String URL_PREFIX_EN = "https://studyinfo.fi/app/#!/";
    private static final String URL_PREFIX_SV = "https://studieinfo.fi/app/#!/";

    private static final Logger log = LoggerFactory.getLogger(KoulutusWrapper.class);
    private final ObjectFactory of;
    private final fi.vm.sade.parser.JAXBParser JAXBParser;
    private LearningOpportunities learningOpportunities;
    private String tagString;

    @Autowired
    public KoulutusWrapper(JAXBParser jAXBParser) {
        of = new ObjectFactory();
        JAXBParser = jAXBParser;
        InputStream in = getClass().getResourceAsStream("/HTMLTagsToBeRemoved.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        try {
            for (String line; (line = br.readLine()) != null; ) {
                tagString += "|" + line;
            }
        } catch (IOException e) {
            log.error("Constructor file load failed: " + e);
        }
    }

    public void createNewLearningOpportunities() {
        learningOpportunities = of.createLearningOpportunities();
        learningOpportunities.setKey(LEARNING_OPPORTUNITY_KEY);
        learningOpportunities.setXsdType(XsdTypeType.fromValue(XSD_TYPE));
        learningOpportunities.setXsdVersion(XSD_VERSION);
    }

    public void fetchAmmatillinenPerustutkintoInfo(KoulutusAmmatillinenPerustutkintoV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot,
                                                   KoulutusHakutulosV1RDTO kh, Map<String, Koodi> haetutKoodit) {
        NimiV1RDTO desc = getKoulutusDesc(k);
        LearningOpportunity lo = initLearningOpportunity(k, "koulutus", haetutOrganisaatiot, kh.getKoulutuskoodi(), haetutKoodit, desc);
        setQualificationsWithTutkintonimikes(k.getTutkintonimikes(), k.getKuvausKomo(), k.getOpetusJarjestajat(), lo, haetutOrganisaatiot);
        learningOpportunities.getLearningOpportunity().add(lo);
        addMandatoryEnglish(lo);
    }

    public void fetchAmmattiInfo(AmmattitutkintoV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot, KoulutusHakutulosV1RDTO kh,
                                 Map<String, Koodi> koodisto) {
        NimiV1RDTO desc = getNayttotutkintoDesc(k);
        LearningOpportunity lo = initLearningOpportunity(k, "ammatillinenaikuiskoulutus", haetutOrganisaatiot, kh.getKoulutuskoodi(), koodisto, desc);
        setQualifications(k.getKuvausKomo(), k.getOpetusJarjestajat(), lo, haetutOrganisaatiot);
        learningOpportunities.getLearningOpportunity().add(lo);
        addMandatoryEnglish(lo);
    }

    public void fetchErikoisInfo(ErikoisammattitutkintoV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot, KoulutusHakutulosV1RDTO kh,
                                 Map<String, Koodi> koodisto) {
        NimiV1RDTO desc = getNayttotutkintoDesc(k);
        LearningOpportunity lo = initLearningOpportunity(k, "ammatillinenaikuiskoulutus", haetutOrganisaatiot, kh.getKoulutuskoodi(), koodisto, desc);
        setQualifications(k.getKuvausKomo(), k.getOpetusJarjestajat(), lo, haetutOrganisaatiot);
        learningOpportunities.getLearningOpportunity().add(lo);
        addMandatoryEnglish(lo);
    }

    public void fetchKorkeaInfo(KoulutusKorkeakouluV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot, KoulutusHakutulosV1RDTO kh,
                                Map<String, Koodi> koodisto) {
        NimiV1RDTO desc = getKoulutusDesc(k);
        LearningOpportunity lo = initLearningOpportunity(k, "korkeakoulu", haetutOrganisaatiot, kh.getKoulutuskoodi(), koodisto, desc);
        setQualificationsWithTutkintonimikes(k.getTutkintonimikes(), k.getKuvausKomo(), k.getOpetusJarjestajat(), lo, haetutOrganisaatiot);
        learningOpportunities.getLearningOpportunity().add(lo);
        addMandatoryEnglish(lo);
    }

    public void fetchValmistavaInfo(ValmistavaKoulutusV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot, KoulutusHakutulosV1RDTO kh,
                                    Map<String, Koodi> koodisto) {
        NimiV1RDTO desc = getKoulutusDesc(k);
        LearningOpportunity lo = initLearningOpportunity(k, "koulutus", haetutOrganisaatiot, kh.getKoulutuskoodi(), koodisto, desc);
        setQualifications(k.getKuvausKomo(), k.getOpetusJarjestajat(), lo, haetutOrganisaatiot);
        learningOpportunities.getLearningOpportunity().add(lo);
        addMandatoryEnglish(lo);
    }

    public void fetchLukioInfo(KoulutusLukioV1RDTO k, Map<String, OrganisaatioRDTO> haetutOrganisaatiot, KoulutusHakutulosV1RDTO kh,
                               Map<String, Koodi> koodisto) {
        NimiV1RDTO desc = getKoulutusDesc(k);
        LearningOpportunity lo = initLearningOpportunity(k, "koulutus", haetutOrganisaatiot, kh.getKoulutuskoodi(), koodisto, desc);
        setQualifications(k.getKuvausKomo(), k.getOpetusJarjestajat(), lo, haetutOrganisaatiot);
        learningOpportunities.getLearningOpportunity().add(lo);
        addMandatoryEnglish(lo);
    }

    public void fetchAPNayttotutkintonaInfo(KoulutusAmmatillinenPerustutkintoNayttotutkintonaV1RDTO k,
                                            Map<String, OrganisaatioRDTO> haetutOrganisaatiot, KoulutusHakutulosV1RDTO kh, Map<String, Koodi> koodisto) {
        NimiV1RDTO desc = getNayttotutkintoDesc(k);
        LearningOpportunity lo = initLearningOpportunity(k, "ammatillinenaikuiskoulutus", haetutOrganisaatiot, kh.getKoulutuskoodi(), koodisto, desc);
        setQualifications(k.getKuvausKomo(), k.getOpetusJarjestajat(), lo, haetutOrganisaatiot);
        learningOpportunities.getLearningOpportunity().add(lo);
        addMandatoryEnglish(lo);
    }

    private LearningOpportunity initLearningOpportunity(KoulutusV1RDTO k, String koulutustypeForURL, Map<String, OrganisaatioRDTO> haetutOrganisaatiot, String koulutusohjelma, Map<String, Koodi> haetutKoodit, NimiV1RDTO desc) {
        Set<String> opetusTarjoajat = k.getOpetusTarjoajat();
        KuvausV1RDTO<KomoTeksti> kuvausKomo = k.getKuvausKomo();
        Map<String, KoodiV1RDTO> opetuskielis = k.getOpetuskielis().getMeta();
        LearningOpportunity lo = of.createLearningOpportunity();
        setDate(k.getKoulutuksenAlkamisPvms(), lo);
        setCost(k.getHintaString(), lo);
        if (kuvausKomo.get(KomoTeksti.TAVOITTEET) != null) {
            setInformationLanguage(kuvausKomo.get(KomoTeksti.TAVOITTEET).getTekstis(), lo); //FIXME: voidaanko kayttaa
        } else if (opetuskielis != null) {
            Map<String, String> newMap = new HashMap<>();
            for (String key : opetuskielis.keySet()) {
                newMap.put(key, opetuskielis.get(key).getNimi());
            }
            setInformationLanguage(newMap, lo);
        }
        setDescription(desc.getTekstis(), lo);
        setProviderName(opetusTarjoajat, lo, haetutOrganisaatiot);
        setProviderContactInfo(opetusTarjoajat, lo, haetutOrganisaatiot);
        setCourseLocation(opetusTarjoajat, lo, haetutOrganisaatiot);
        setThematicAreas(koulutusohjelma, haetutKoodit, lo);
        setTitle(k.getKoulutusohjelma(), lo, k.getKoulutuskoodi());
        lo.setLearningOpportunityId(k.getOid());
        lo.setCountryCode(COUNTRY_CODE);
        lo.getUrl().add(createUrl(URL_PREFIX_FI + koulutustypeForURL + "/" + k.getOid(), "fi"));
        lo.getUrl().add(createUrl(URL_PREFIX_EN + koulutustypeForURL + "/" + k.getOid(), "en"));
        lo.getUrl().add(createUrl(URL_PREFIX_SV + koulutustypeForURL + "/" + k.getOid(), "sv"));
        lo.setEducationLevel(haetutKoodit.get(koulutusohjelma).getIsced2011koulutusaste());

        setTeachingLangs(k.getOpetuskielis().getMeta(), lo);
        setStudyType(k.getOpetusPaikkas().getUris(), k.getOpetusmuodos().getUris(), lo);
        if (k.getSuunniteltuKestoTyyppi() != null)
            setDurationInformation(k.getSuunniteltuKestoArvo(), k.getSuunniteltuKestoTyyppi().getNimi(), lo);
        setCredits(k.getOpintojenLaajuusarvo(), k.getOpintojenLaajuusyksikko(), lo);

        return lo;
    }

    private void addMandatoryEnglish(LearningOpportunity lo) {

        addMandatoryEnglish(lo.getAccessRequirements());
        addMandatoryEnglish(lo.getAdmissionProcedure());
        addMandatoryEnglish(lo.getCosts());
        addMandatoryEnglish(lo.getCredits());
        addMandatoryEnglish(lo.getDurationInformation());
        addMandatoryEnglish(lo.getGrants());
        addMandatoryEnglish(lo.getProviderContactInfo());
        addMandatoryEnglish(lo.getProviderType());
        addMandatoryEnglish(lo.getStartDate());

        addMandatoryEnglishNonEmpty(lo.getDescription());
        addMandatoryEnglishNonEmpty(lo.getMoreInfo());
        addMandatoryEnglishNonEmpty(lo.getNonPreferredTerm());
        addMandatoryEnglishNonEmpty(lo.getProviderName());
        addMandatoryEnglishNonEmpty(lo.getTitle());

        addMandatoryEnglishQualifications(lo.getQualifications());

//        List<SpecialTargetGroupType> specialTargetGroup; <- Ei ole xml:ssä ollenkaan
//        List<ThematicAreas> thematicAreas; <- Ei kielimäärittelyä

    }

    private void addMandatoryEnglishQualifications(List<Qualifications> qualifications) {
        for (Qualifications q : qualifications) {
            addMandatoryEnglish(q.getAwardingBody());
            addMandatoryEnglish(q.getAwardingBodyContactInfo());
            addMandatoryEnglish(q.getOtherQualificationAwardedTerm());
            addMandatoryEnglish(q.getQualificationAwarded());
            addMandatoryEnglish(q.getQualificationAwardedDescription());
        }
    }

    private void addMandatoryEnglishNonEmpty(List<I18NNonEmptyString> list) {
        if (list == null || list.isEmpty()) return;

        Map<LanguageCode, I18NNonEmptyString> map = list.stream().collect(Collectors.toMap(I18NNonEmptyString::getLanguage, s -> s));
        if (!map.containsKey(LanguageCode.EN)) {
            if (!map.containsKey(LanguageCode.FI)) {
                list.add(createI18NonEmptyString(map.get(LanguageCode.FI).getValue(), LanguageCode.EN));
            } else if (!map.containsKey(LanguageCode.SV)) {
                list.add(createI18NonEmptyString(map.get(LanguageCode.SV).getValue(), LanguageCode.EN));
            } else {
                list.add(createI18NonEmptyString(map.values().iterator().next().getValue(), LanguageCode.EN));
            }
        }
    }

    private void addMandatoryEnglish(List<I18NString> list) {
        if (list == null || list.isEmpty()) return;

        Map<LanguageCode, I18NString> map = list.stream().collect(Collectors.toMap(I18NString::getLanguage, s -> s));
        if (!map.containsKey(LanguageCode.EN)) {
            if (!map.containsKey(LanguageCode.FI)) {
                list.add(createI18NString(map.get(LanguageCode.FI).getValue(), LanguageCode.EN));
            } else if (!map.containsKey(LanguageCode.SV)) {
                list.add(createI18NString(map.get(LanguageCode.SV).getValue(), LanguageCode.EN));
            } else {
                list.add(createI18NString(map.values().iterator().next().getValue(), LanguageCode.EN));
            }
        }
    }


    //// Common helpers
    private NimiV1RDTO getNayttotutkintoDesc(NayttotutkintoV1RDTO k) {
        NimiV1RDTO kuvauskomoSisalto = getKuvausKomoSisalto(k);
        NimiV1RDTO valmistavaKoututusKuvaus = getValmistavaKoututusKuvaus(k);
        NimiV1RDTO kuvauskomoTavoitteet = getKuvausKomoTavoitteet(k);
        return findDescription(kuvauskomoSisalto, valmistavaKoututusKuvaus, kuvauskomoTavoitteet);
    }

    private NimiV1RDTO getKoulutusDesc(KoulutusV1RDTO k) {
        NimiV1RDTO kuvauskomoSisalto = getKuvausKomoSisalto(k);
        NimiV1RDTO kuvauskomoTavoitteet = getKuvausKomoTavoitteet(k);
        return findDescription(kuvauskomoSisalto, kuvauskomoTavoitteet);
    }

    private NimiV1RDTO getKuvausKomoSisalto(KoulutusV1RDTO k) {
        if (k != null && k.getKuvausKomoto() != null && k.getKuvausKomoto().get(KomotoTeksti.SISALTO) != null) {
            return k.getKuvausKomoto().get(KomotoTeksti.SISALTO);
        }
        return null;
    }

    private NimiV1RDTO getValmistavaKoututusKuvaus(NayttotutkintoV1RDTO k) {
        if (k != null && k.getValmistavaKoulutus() != null && k.getValmistavaKoulutus().getKuvaus() != null
                && k.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.SISALTO) != null) {
            return k.getValmistavaKoulutus().getKuvaus().get(KomotoTeksti.SISALTO);
        }
        return null;
    }

    private NimiV1RDTO getKuvausKomoTavoitteet(KoulutusV1RDTO k) {
        if (k != null && k.getKuvausKomo() != null && k.getKuvausKomo().get(KomoTeksti.TAVOITTEET) != null) {
            return k.getKuvausKomo().get(KomoTeksti.TAVOITTEET);
        }
        return null;
    }

    private NimiV1RDTO findDescription(NimiV1RDTO... nimiV1RDTOs) { //FIXME: voidaanko kayttaa
        for (NimiV1RDTO n : nimiV1RDTOs) {
            if (isNotEmpty(n, LANG_CODE_KIELI_EN)) {
                return n;
            } else if (isNotEmpty(n, LANG_CODE_KIELI_FI)) {
                return n;
            } else if (isNotEmpty(n, LANG_CODE_KIELI_SV)) {
                return n;
            }
        }
        NimiV1RDTO desc = new NimiV1RDTO();
        desc.setTekstis(new HashMap<>());
        desc.getTekstis().put("kieli_en", "No description available");
        return desc;
    }

    private boolean isNotEmpty(NimiV1RDTO n, String lang) {
        return n != null && n.getTekstis() != null && n.getTekstis().get(lang) != null && !n.getTekstis().get(lang).trim().equals("");
    }

    private boolean isNotEmptyMeta(NimiV1RDTO n, String lang) {
        return n != null && n.getMeta() != null && n.getMeta().get(lang) != null
                && !n.getMeta().get(lang).getNimi().trim().equals("");
    }

    private boolean isNotEmpty(KoodiV1RDTO k, String lang) {
        return k != null && k.getMeta() != null && k.getMeta().get(lang) != null
                && !k.getMeta().get(lang).getNimi().trim().equals("");
    }


    private void setTitle(NimiV1RDTO koulutusohjelma, LearningOpportunity lo, KoodiV1RDTO koulutuskoodi) {

        if (isNotEmpty(koulutusohjelma, LANG_CODE_KIELI_EN)) {
            lo.getTitle().add(createI18NonEmptyString(koulutusohjelma.getTekstis().get(LANG_CODE_KIELI_EN), TITLE_LANG_CODE_EN));
        } else if (isNotEmpty(koulutusohjelma, LANG_CODE_KIELI_FI)) {
            lo.getTitle().add(createI18NonEmptyString(koulutusohjelma.getTekstis().get(LANG_CODE_KIELI_FI), TITLE_LANG_CODE_FI));
        } else if (isNotEmpty(koulutusohjelma, LANG_CODE_KIELI_SV)) {
            lo.getTitle().add(createI18NonEmptyString(koulutusohjelma.getTekstis().get(LANG_CODE_KIELI_SV), TITLE_LANG_CODE_SV));

        } else if (isNotEmptyMeta(koulutusohjelma, LANG_CODE_KIELI_EN)) {
            lo.getTitle().add(createI18NonEmptyString(koulutusohjelma.getMeta().get(LANG_CODE_KIELI_EN).getNimi(), TITLE_LANG_CODE_EN));
        } else if (isNotEmptyMeta(koulutusohjelma, LANG_CODE_KIELI_FI)) {
            lo.getTitle().add(createI18NonEmptyString(koulutusohjelma.getMeta().get(LANG_CODE_KIELI_FI).getNimi(), TITLE_LANG_CODE_FI));
        } else if (isNotEmptyMeta(koulutusohjelma, LANG_CODE_KIELI_SV)) {
            lo.getTitle().add(createI18NonEmptyString(koulutusohjelma.getMeta().get(LANG_CODE_KIELI_SV).getNimi(), TITLE_LANG_CODE_SV));

        } else if (isNotEmpty(koulutuskoodi, LANG_CODE_KIELI_EN)) {
            lo.getTitle().add(createI18NonEmptyString(koulutuskoodi.getMeta().get(LANG_CODE_KIELI_EN).getNimi(), TITLE_LANG_CODE_EN));
        } else if (isNotEmpty(koulutuskoodi, LANG_CODE_KIELI_FI)) {
            lo.getTitle().add(createI18NonEmptyString(koulutuskoodi.getMeta().get(LANG_CODE_KIELI_FI).getNimi(), TITLE_LANG_CODE_FI));
        } else if (isNotEmpty(koulutuskoodi, LANG_CODE_KIELI_FI)) {
            lo.getTitle().add(createI18NonEmptyString(koulutuskoodi.getMeta().get(LANG_CODE_KIELI_SV).getNimi(), TITLE_LANG_CODE_SV));

        } else {
            throw new RuntimeException("No title for lo " + lo.getLearningOpportunityId());
        }
    }

    private void setThematicAreas(String koodistoID, Map<String, Koodi> koodisto, LearningOpportunity lo) {
        ThematicAreas areas = of.createThematicAreas();
        areas.getThematicAreas1997OrThematicAreas2013()
                .add(of.createThematicAreasThematicAreas2013(koodisto.get(koodistoID).getIsced2011koulutusalataso3()));
        lo.getThematicAreas().add(areas);
        log.debug("koodisto: " + koodisto.get(koodistoID) + ", koodiID: " + koodistoID);
    }

    private void setDescription(Map<String, String> descriptions, LearningOpportunity lo) {
        lo.getDescription().clear();
        List<String> descs = new ArrayList<>();
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
    }

    private I18NUrl createUrl(String src, String lang) {
        final I18NUrl url = of.createI18NUrl();
        url.setLanguage(LanguageCode.fromValue(lang));
        url.setValue(src);
        return url;
    }

    private I18NNonEmptyString createI18NonEmptyString(String string, String lang) {
        return createI18NonEmptyString(string, LanguageCode.fromValue(lang));
    }

    private I18NNonEmptyString createI18NonEmptyString(String string, LanguageCode lang) {
        final I18NNonEmptyString i18Non = of.createI18NNonEmptyString();
        i18Non.setValue(string);
        i18Non.setLanguage(lang);
        return i18Non;
    }

    private I18NString createI18NString(String string, String lang) {
        return createI18NString(string, LanguageCode.fromValue(lang));
    }

    private I18NString createI18NString(String string, LanguageCode lang) {
        I18NString temp = of.createI18NString();
        temp.setValue(string);
        temp.setLanguage(lang);
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

    private void setQualificationsWithTutkintonimikes(KoodiUrisV1RDTO tutkintonimikes, KuvausV1RDTO<KomoTeksti> kuvausKomo, Set<String> opetusJarjestajat,
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

    private void setQualifications(KuvausV1RDTO<KomoTeksti> kuvausKomo, Set<String> opetusJarjestajat,
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
        set.forEach(s -> haetutOrganisaatiot.get(s).getNimet().forEach(o -> qualifications.getAwardingBody().add(createI18NString(o.getNimi().get("en"), "en"))));
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
        list.keySet().forEach(e -> qualifications.getQualificationAwardedDescription().add(createI18NString(removeUnwantedHTMLTags(list.get(e)), e.replace("kieli_", ""))));
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
        } else if (map.get("kieli_la") != null) {
            lo.setInformationLanguage(LanguageCode.FI); // TODO: Latina? //FIXME: voidaanko kayttaa
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
                haetutOrganisaatiot.get(s).getNimet().forEach((o) -> {
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
                        haetutOrganisaatiot.get(s).getMetadata().getYhteystiedot().forEach(m -> {
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
                        I18NString addressInfo = createI18NString(address, "en");
                        if (!co.getCourseAddress().stream().filter(o -> o.getValue().equals(addressInfo.getValue())).findFirst().isPresent()) {
                            if (co.getCourseAddress().isEmpty()) {
                                co.getCourseAddress().add(createI18NString(address, "en"));
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
        teachingLangs
                .values()
                .stream()
                .filter(e -> !e.getArvo().toLowerCase().equals("la"))
                .forEach(e ->
                        lo.getTeachingLanguage().add(LanguageCode.fromValue(e.getArvo().toLowerCase())));
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
            stripped = input.replaceAll("</?(" + tagString + ").*?/?>", "");
        }
        return stripped;
    }

    public boolean forwardLOtoJaxBParser() {
        return JAXBParser.parseXML(learningOpportunities);
    }

    public void forwardStatusObject(StatusObject statusObject) {
        JAXBParser.forwardStatusObject(statusObject);
    }

}