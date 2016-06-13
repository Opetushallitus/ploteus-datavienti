package fi.vm.sade.controller;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import eu.europa.ec.learningopportunities.v0_5_10.LearningOpportunities;
import eu.europa.ec.learningopportunities.v0_5_10.LearningOpportunity;

public class JAXBParser {
	
	public void parseXML(LearningOpportunities learningOpportunities) {
		try {
			File file = new File("eu_tiedonsiirto.xml");
			JAXBContext jaxbContext = JAXBContext.newInstance(LearningOpportunity.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(new StreamSource("src/main/xsd/LearningOpportunities.xsd")); 

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			/*jaxbMarshaller.setSchema(schema); poistetaan kommentit kun tarvitaan validointia*/
			jaxbMarshaller.marshal(learningOpportunities, file);
			jaxbMarshaller.marshal(learningOpportunities, System.out);

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (JAXBException e){
			e.printStackTrace();
		}

	}

}
