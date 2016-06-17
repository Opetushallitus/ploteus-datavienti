package fi.vm.sade.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import eu.europa.ec.learningopportunities.v0_5_10.LearningOpportunities;
import eu.europa.ec.learningopportunities.v0_5_10.LearningOpportunity;

public class JAXBParser {
    private static final Logger log = LoggerFactory.getLogger(JAXBParser.class);

    private static final String OUTPUT_PATH = "generated/";
    private static final String OUTPUT_FILE = "lo_full_sample";

    public void parseXML(LearningOpportunities learningOpportunities) {
        String sourceFile = OUTPUT_PATH + OUTPUT_FILE + ".xml";
        File xmlFile = createXMLFile(learningOpportunities, sourceFile);
        zipXMLFile(xmlFile);
    }

    private void zipXMLFile(File xmlFile) {
        try {
            FileInputStream in = new FileInputStream(xmlFile);
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(OUTPUT_PATH + OUTPUT_FILE + ".zip"));
            out.putNextEntry(new ZipEntry(OUTPUT_FILE + ".xml"));
            byte[] b = new byte[1024];
            int count;
            while ((count = in.read(b)) > 0) {
                out.write(b, 0, count);
            }
            out.close();
            in.close();
        } catch (IOException e) {
            log.error("XML creation error", e);
            throw new RuntimeException(e);
        }
    }

    private File createXMLFile(LearningOpportunities learningOpportunities, String sourceFile) {
        File file = new File(sourceFile);
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(LearningOpportunity.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            //poistetaan kommentit kun tarvitaan validointia
            Schema schema = schemaFactory.newSchema(new StreamSource("src/main/xsd/LearningOpportunities.xsd"));
            //jaxbMarshaller.setSchema(schema);

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(learningOpportunities, file);

        } catch (SAXException | JAXBException e) {
            log.error("XML creation error", e);
            throw new RuntimeException(e);
        }
        return file;
    }
}
