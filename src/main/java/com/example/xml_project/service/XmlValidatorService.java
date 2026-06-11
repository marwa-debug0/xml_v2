package com.example.xml_project.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.StringReader;

@Service
public class XmlValidatorService {

    private final Validator validator;

    // Au démarrage, on charge le fichier XSD une seule fois
    public XmlValidatorService() throws SAXException, IOException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new ClassPathResource("user.xsd").getURL());
        this.validator = schema.newValidator();
    }

    // Valide un XML en string → lance une exception si invalide
    public void validate(String xml) throws SAXException, IOException {
        validator.validate(new StreamSource(new StringReader(xml)));
    }
}