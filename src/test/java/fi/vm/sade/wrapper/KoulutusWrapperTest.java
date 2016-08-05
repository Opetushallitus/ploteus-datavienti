package fi.vm.sade.wrapper;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import eu.europa.ec.learningopportunities.v0_5_10.LearningOpportunities;
import fi.vm.sade.model.Koodi;
import fi.vm.sade.model.StatusObject;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.parser.JAXBParser;
import fi.vm.sade.tarjonta.service.resources.v1.dto.KoulutusHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertEquals;


@SuppressWarnings("unused")
public class KoulutusWrapperTest {
    private KoulutusWrapper w;
    private JAXBParser d = new DummyJAXBParser();

    @Before
    public void init(){
        w = new KoulutusWrapper(d);
    }

    @Test
    public void createsNewLearningOpportunitiesAndForwardsToParser() throws Exception {
        w.createNewLearningOpportunities();
        w.forwardLOtoJaxBParser();

        LearningOpportunities resl = ((DummyJAXBParser) d).learningOpportunities;
        assertEquals("Learning Opportunity", resl.getXsdType().value());

    }

    @Test
    public void fetchAmmatillinenPerustutkintoInfoMinimalTest() throws Exception {
        Map<String, OrganisaatioRDTO> givenOrganisaatios = givenOrganisaatios();
        Map<String, Koodi> givenKoodis = giveKoodis();

        KoulutusAmmatillinenPerustutkintoV1RDTO k = givenAmmatillinenPerustutkinto();

        KoulutusHakutulosV1RDTO kh = new KoulutusHakutulosV1RDTO();
        kh.setKoulutuskoodi("");

        w.createNewLearningOpportunities();
        w.fetchAmmatillinenPerustutkintoInfo(k, givenOrganisaatios, kh, givenKoodis);
    }


    // Test helpers



    private KoulutusAmmatillinenPerustutkintoV1RDTO givenAmmatillinenPerustutkinto() {
        KoulutusAmmatillinenPerustutkintoV1RDTO k = new KoulutusAmmatillinenPerustutkintoV1RDTO();
        k.setOpetuskielis(new KoodiUrisV1RDTO());
        k.setKoulutuksenAlkamisPvms(Sets.newHashSet(new Date()));
        k.setKuvausKomo(new KuvausV1RDTO<>());
        k.setOpetusTarjoajat(Sets.newHashSet("orgoid"));
        k.setKoulutusohjelma(new NimiV1RDTO(ImmutableMap.of("kieli_fi", "meh")));
        KoodiUrisV1RDTO kieli_fi = getKoodiUrisV1RDTO("kieli_fi", "fi");
        k.setOpetuskielis(kieli_fi);
        k.setOpetusPaikkas(getKoodiUrisV1RDTO("", ""));
        k.setOpetusmuodos(getKoodiUrisV1RDTO("", ""));
        k.setTutkintonimikes(getKoodiUrisV1RDTO("", "asd"));
        k.setOpetusJarjestajat(Sets.newHashSet());
        return k;
    }

    private KoodiUrisV1RDTO getKoodiUrisV1RDTO(String uri, String arvo) {
        KoodiUrisV1RDTO kieli_fi = new KoodiUrisV1RDTO(ImmutableMap.of(uri, 1));
        KoodiV1RDTO koodi = new KoodiV1RDTO(arvo, 1, arvo);
        koodi.setNimi(arvo);
        kieli_fi.setMeta(ImmutableMap.of(uri, koodi));
        return kieli_fi;
    }


    private Map<String,Koodi> giveKoodis() {
        return ImmutableMap.of("", new Koodi());
    }

    private Map<String,OrganisaatioRDTO> givenOrganisaatios() {
        return ImmutableMap.of("orgoid", new OrganisaatioRDTO());
    }

    @Test
    public void fetchAmmattiInfo() throws Exception {

    }

    private class DummyJAXBParser extends JAXBParser {
        LearningOpportunities learningOpportunities;

        @Override
        public boolean parseXML(LearningOpportunities learningOpportunities) {
            this.learningOpportunities = learningOpportunities;
            return true;
        }

        @Override
        public void forwardStatusObject(StatusObject so) {
            // Meh
        }
    }
}