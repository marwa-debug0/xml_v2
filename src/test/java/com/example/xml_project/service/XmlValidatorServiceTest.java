package com.example.xml_project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatNoException;

/**
 * Tests unitaires du service de validation XSD.
 *
 * Vérifie que :
 *  - Un XML conforme au schéma user.xsd est accepté sans exception
 *  - Un XML non conforme lève une SAXException
 */
@DisplayName("Tests — XmlValidatorService (validation XSD)")
class XmlValidatorServiceTest {

    private XmlValidatorService xmlValidatorService;

    @BeforeEach
    void setUp() throws Exception {
        xmlValidatorService = new XmlValidatorService();
    }

    // ─────────────────────────────────────────────
    // Cas valides
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("XML valide (tous les champs) — ne doit pas lever d'exception")
    void validate_shouldPass_whenXmlIsValid() {
        String validXml = """
                <user>
                    <name>Alice</name>
                    <email>alice@mail.com</email>
                    <password>motdepasse</password>
                </user>
                """;

        assertThatNoException().isThrownBy(() -> xmlValidatorService.validate(validXml));
    }

    @Test
    @DisplayName("XML valide avec id — ne doit pas lever d'exception")
    void validate_shouldPass_whenXmlHasId() {
        String validXml = """
                <user>
                    <id>1</id>
                    <name>Bob</name>
                    <email>bob@example.com</email>
                    <password>1234</password>
                </user>
                """;

        assertThatNoException().isThrownBy(() -> xmlValidatorService.validate(validXml));
    }

    // ─────────────────────────────────────────────
    // Cas invalides
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("XML invalide (email sans @) — doit lever SAXException")
    void validate_shouldThrowSAXException_whenEmailMissingAt() {
        String invalidXml = """
                <user>
                    <name>Alice</name>
                    <email>emailSansArobase.com</email>
                    <password>1234</password>
                </user>
                """;

        assertThatThrownBy(() -> xmlValidatorService.validate(invalidXml))
                .isInstanceOf(SAXException.class);
    }

    @Test
    @DisplayName("XML invalide (name trop court) — doit lever SAXException")
    void validate_shouldThrowSAXException_whenNameTooShort() {
        String invalidXml = """
                <user>
                    <name>A</name>
                    <email>a@mail.com</email>
                    <password>1234</password>
                </user>
                """;

        assertThatThrownBy(() -> xmlValidatorService.validate(invalidXml))
                .isInstanceOf(SAXException.class);
    }

    @Test
    @DisplayName("XML invalide (password trop court) — doit lever SAXException")
    void validate_shouldThrowSAXException_whenPasswordTooShort() {
        String invalidXml = """
                <user>
                    <name>Alice</name>
                    <email>alice@mail.com</email>
                    <password>abc</password>
                </user>
                """;

        assertThatThrownBy(() -> xmlValidatorService.validate(invalidXml))
                .isInstanceOf(SAXException.class);
    }

    @Test
    @DisplayName("XML invalide (champ email manquant) — doit lever SAXException")
    void validate_shouldThrowSAXException_whenEmailMissing() {
        String invalidXml = """
                <user>
                    <name>Alice</name>
                    <password>motdepasse</password>
                </user>
                """;

        assertThatThrownBy(() -> xmlValidatorService.validate(invalidXml))
                .isInstanceOf(SAXException.class);
    }
}
