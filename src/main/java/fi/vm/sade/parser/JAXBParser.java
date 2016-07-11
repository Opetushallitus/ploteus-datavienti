package fi.vm.sade.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import eu.europa.ec.learningopportunities.v0_5_10.LearningOpportunities;
import eu.europa.ec.learningopportunities.v0_5_10.LearningOpportunity;
import fi.vm.sade.model.StatusObject;

@Component
public class JAXBParser { // TODO: better logging //TODO: UI logging
    private static final Logger log = LoggerFactory.getLogger(JAXBParser.class);
    private StatusObject statusObject;
    private static final String OUTPUT_FILE_NAME = "lo_full_sample";
    @Value("${xml.output.dir}")
    private String OUTPUT_PATH;

    public boolean parseXML(LearningOpportunities learningOpportunities) {
        statusObject.addFrontendOutput("Luodaan XML tiedosto haetuista koulutuksista...");
        statusObject.setStatusText("Luodaan XML tiedosto haetuista koulutuksista...");
        String sourceFile = OUTPUT_PATH + OUTPUT_FILE_NAME + ".xml";
        File xmlFile = createXMLFile(learningOpportunities, sourceFile);
        statusObject.setStatus(0.96);
        statusObject.addFrontendOutput("XML tiedosto luotu.");
        statusObject.addFrontendOutput("Validoidaan XML tiedosto schema tiedostoa vasten...");
        statusObject.setStatusText("Validoidaan XML...");
        if(validateXMLtoSchema(xmlFile)){
            statusObject.setStatus(0.97);
            statusObject.setStatusText("Pakataan XML...");
            statusObject.addFrontendOutput("XML tiedosto validoitu.");
            statusObject.addFrontendOutput("Pakataan XML ZIP-tiedostoksi...");
            zipXMLFile(xmlFile);
            statusObject.setStatus(0.99);
            statusObject.addFrontendOutput("XML tiedosto pakattu.");
            return true;
        }else{
            return false;
        }
    }

    private boolean validateXMLtoSchema(File xmlFile) {
        HashMap<String, SAXParseException> exceptions = new HashMap<>();
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(getClass().getClassLoader().getResource("LearningOpportunities.xsd"));
            Validator validator = schema.newValidator();
            ErrorHandler errorHandler = new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    if (!exceptions.containsKey(exception.getMessage())) {
                        exceptions.put(exception.getMessage(), exception);
                    }
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    if (!exceptions.containsKey(exception.getMessage())) {
                        exceptions.put(exception.getMessage(), exception);
                    }
                    printErrors(exceptions);
                    statusObject.addFrontendOutput("Validation fatal exception");
                    throw new RuntimeException("Validation fatal exception", exception);
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    if (!exceptions.containsKey(exception.getMessage())) {
                        exceptions.put(exception.getMessage(), exception);
                    }
                }
            };
            validator.setErrorHandler(errorHandler);
            validator.validate(new StreamSource(xmlFile));
        } catch (SAXException e) {
            log.error("SaxException", e);
            statusObject.addFrontendOutput("XML:ää ei voitu tarkistaa schema tiedostoa vasten. Ota yhteyttä järjestelmä vastaavaan.");
            return false;
        } catch (IOException e) {
            log.error("Schema file not found", e);
            statusObject.addFrontendOutput("XML:ää ei voitu tarkistaa schema tiedostoa vasten. Ota yhteyttä järjestelmä vastaavaan.");
            return false;
        }
        // Logitetaan kaikki virheet, joita validoinnissa tuli
        return (printErrors(exceptions));
        }

    private boolean printErrors(HashMap<String, SAXParseException> exceptions) {
        if(!exceptions.isEmpty()){
            statusObject.addFrontendOutput("XML tiedoston validoinnissa esiintyi virhe/virheitä. Ota yhteyttä järjestelmä vastaavaan.");
            for (Map.Entry<String, SAXParseException> entry : exceptions.entrySet()) {
                log.warn("XSD Validation warning on line: " + entry.getValue().getLineNumber() + " : " + entry.getValue().getColumnNumber() + " : "
                        + entry.getValue().getMessage());
                statusObject.addFrontendOutput(entry.getValue().getLineNumber() + " : " + entry.getValue().getColumnNumber() + " : " + entry.getValue().getMessage());
            }
            return false;
        }
        return true;
    }

    private void zipXMLFile(File xmlFile) {
        try {
            FileInputStream in = new FileInputStream(xmlFile);
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(OUTPUT_PATH + OUTPUT_FILE_NAME + ".zip"));
            out.putNextEntry(new ZipEntry(OUTPUT_FILE_NAME + ".xml"));
            byte[] b = new byte[1024];
            int count;
            while ((count = in.read(b)) > 0) {
                out.write(b, 0, count);
            }
            out.close();
            in.close();
        } catch (IOException e) {
            log.error("ZIP error", e);
            statusObject.addFrontendOutput("XML tiedoston pakkauksessa tapahtui virhe.");
            throw new RuntimeException(e);
        }
    }

    private File createXMLFile(LearningOpportunities learningOpportunities, String sourceFile) {
        File file = new File(sourceFile);
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(LearningOpportunity.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(learningOpportunities, file);
        } catch (JAXBException e) {
            log.error("XML creation error", e);
            statusObject.addFrontendOutput("XML tiedoston luonnissa tapahtui virhe.");
            throw new RuntimeException(e);
        }
        return file;
    }
    
    public void forwardStatusObject(StatusObject so){
        this.statusObject = so;
    }
}