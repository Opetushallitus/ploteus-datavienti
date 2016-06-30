package fi.vm.sade.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.validation.Validator;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import eu.europa.ec.learningopportunities.v0_5_10.LearningOpportunities;
import eu.europa.ec.learningopportunities.v0_5_10.LearningOpportunity;

@Component
public class JAXBParser {
    private static final Logger log = LoggerFactory.getLogger(JAXBParser.class);

    private static final String OUTPUT_FILE_NAME = "lo_full_sample";
    @Value("${xml.output.dir}")
    private String OUTPUT_PATH;

    public void parseXML(LearningOpportunities learningOpportunities) {
        String sourceFile = OUTPUT_PATH + OUTPUT_FILE_NAME + ".xml";
        File xmlFile = createXMLFile(learningOpportunities, sourceFile);
        validateXMLtoSchema(xmlFile);
        zipXMLFile(xmlFile);
    }

    private void validateXMLtoSchema(File xmlFile) {
        HashMap<String, SAXParseException> exceptions = new HashMap<>();
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(getClass().getClassLoader().getResource("LearningOpportunities.xsd"));
            Validator validator = schema.newValidator();
            ErrorHandler errorHandler = new ErrorHandler()
            {
                @Override
                public void warning(SAXParseException exception) throws SAXException
                {
                  if(!exceptions.containsKey(exception.getMessage())){exceptions.put(exception.getMessage(), exception);}
                }
    
                @Override
                public void fatalError(SAXParseException exception) throws SAXException
                {
                    if(!exceptions.containsKey(exception.getMessage())){exceptions.put(exception.getMessage(), exception);}
                }
    
                @Override
                public void error(SAXParseException exception) throws SAXException
                {
                    if(!exceptions.containsKey(exception.getMessage())){exceptions.put(exception.getMessage(), exception);}
                }
              };
            validator.setErrorHandler(errorHandler);
            
            validator.validate(new StreamSource(xmlFile));
        } catch (SAXException | IOException e) {
            //Custom Errorhandler
        }
        //Logitetaan kaikki virheet, joita validoinnissa tuli
        for (Map.Entry<String, SAXParseException> entry : exceptions.entrySet()){
             log.warn("XSD Validation warning on line: " + entry.getValue().getLineNumber() + " : " + entry.getValue().getColumnNumber() + " : " +  entry.getValue().getMessage());
        }
        
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
            log.error("XML creation error", e);
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
            throw new RuntimeException(e);
        }
        return file;
    }
}
