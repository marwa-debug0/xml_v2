package com.example.xml_project.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.StringReader;

@Service
public class XmlValidatorService {

    private final Validator xsdValidator;

    // On startup, load the XSD file once
    public XmlValidatorService() throws SAXException, IOException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new ClassPathResource("user.xsd").getURL());
        this.xsdValidator = schema.newValidator();
    }

    // Validate an XML string against the XSD -> throws an exception if invalid
    public void validate(String xml) throws SAXException, IOException {
        xsdValidator.validate(new StreamSource(new StringReader(xml)));
    }

    // Validate an XML string against the DTD (user.dtd) -> throws if invalid.
    // The parser resolves the DTD from the classpath, so the XML does NOT need
    // its own DOCTYPE declaration; we inject one pointing at our DTD.
    public void validateWithDtd(String xml) throws SAXException, IOException, ParserConfigurationException {
        String dtdUrl = new ClassPathResource("user.dtd").getURL().toExternalForm();

        // Inject a DOCTYPE referencing our DTD if the document doesn't define one
        String xmlToValidate = xml;
        if (!xml.contains("<!DOCTYPE")) {
            xmlToValidate = injectDoctype(xml, dtdUrl);
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(true);
        dbf.setNamespaceAware(false);

        DocumentBuilder builder = dbf.newDocumentBuilder();
        builder.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(SAXParseException e) { /* ignore warnings */ }

            @Override
            public void error(SAXParseException e) throws SAXException {
                throw e; // validation error -> reject
            }

            @Override
            public void fatalError(SAXParseException e) throws SAXException {
                throw e;
            }
        });

        Document doc = builder.parse(new InputSource(new StringReader(xmlToValidate)));
        if (doc == null) {
            throw new SAXException("Impossible de parser le document XML");
        }
    }

    // Insert a <!DOCTYPE user SYSTEM "..."> right after the XML declaration (or at the top)
    private String injectDoctype(String xml, String dtdUrl) {
        String doctype = "<!DOCTYPE user SYSTEM \"" + dtdUrl + "\">";
        String trimmed = xml.stripLeading();
        if (trimmed.startsWith("<?xml")) {
            int end = trimmed.indexOf("?>");
            if (end != -1) {
                return trimmed.substring(0, end + 2) + "\n" + doctype + "\n" + trimmed.substring(end + 2);
            }
        }
        return doctype + "\n" + trimmed;
    }
}
