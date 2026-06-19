
package com.example.xml_project.service;

import org.springframework.stereotype.Service;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * Service de validation XML via DTD.
 *
 * La validation DTD utilise le parser SAX/DOM avec la fonctionnalité
 * de validation activée. Le document XML doit inclure une déclaration
 * DOCTYPE pointant vers la DTD correspondante.
 *
 * Exemple de XML valide pour un produit :
 * 
 * <pre>
 *   <?xml version="1.0" encoding="UTF-8"?>
 *   <!DOCTYPE product SYSTEM "product.dtd">
 *   <product>
 *     <name>Ordinateur</name>
 *     <price>1200.00</price>
 *   </product>
 * </pre>
 */
@Service
public class DtdValidatorService {

    /**
     * Valide un document XML contre sa DTD déclarée en DOCTYPE.
     *
     * @param xmlContent le contenu XML sous forme de String
     * @throws SAXException si le XML ne respecte pas la DTD
     * @throws IOException  si une erreur d'E/S survient
     */
    public void validate(String xmlContent) throws SAXException, IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // Active la validation DTD
            factory.setValidating(true);
            // Désactive les namespaces (DTD ne supporte pas les namespaces)
            factory.setNamespaceAware(false);

            DocumentBuilder builder = factory.newDocumentBuilder();

            // Résout la DTD depuis le classpath (resources/)
            builder.setEntityResolver((publicId, systemId) -> {
                // Extrait le nom de fichier depuis le chemin SYSTEM (ex: "product.dtd")
                String dtdFile = systemId.substring(systemId.lastIndexOf('/') + 1);
                var stream = DtdValidatorService.class.getClassLoader()
                        .getResourceAsStream(dtdFile);
                if (stream == null) {
                    throw new RuntimeException("DTD introuvable dans le classpath : " + dtdFile);
                }
                return new InputSource(stream);
            });

            // Transforme les erreurs de validation en exceptions
            builder.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException e) throws SAXException {
                    // Les avertissements sont ignorés
                }

                @Override
                public void error(SAXParseException e) throws SAXException {
                    throw new SAXException("Erreur DTD à la ligne " + e.getLineNumber()
                            + " : " + e.getMessage());
                }

                @Override
                public void fatalError(SAXParseException e) throws SAXException {
                    throw new SAXException("Erreur fatale DTD : " + e.getMessage());
                }
            });

            // Parse et valide le XML
            builder.parse(new InputSource(new StringReader(xmlContent)));

        } catch (ParserConfigurationException e) {
            throw new IOException("Erreur de configuration du parser XML : " + e.getMessage(), e);
        }
    }
}
