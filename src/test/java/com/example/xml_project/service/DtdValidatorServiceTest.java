package com.example.xml_project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatNoException;

/**
 * Tests unitaires du service de validation DTD.
 *
 * La validation DTD exige que le XML contienne une déclaration DOCTYPE.
 * Le service résout les fichiers DTD depuis le classpath (src/main/resources/).
 */
@DisplayName("Tests — DtdValidatorService (validation DTD)")
class DtdValidatorServiceTest {

    private DtdValidatorService dtdValidatorService;

    @BeforeEach
    void setUp() {
        dtdValidatorService = new DtdValidatorService();
    }

    // ─────────────────────────────────────────────
    // Validation product.dtd
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("Product XML valide — ne doit pas lever d'exception")
    void validate_product_shouldPass_whenXmlIsValid() {
        String validXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE product SYSTEM "product.dtd">
                <product>
                    <name>Ordinateur</name>
                    <price>1200.00</price>
                </product>
                """;

        assertThatNoException().isThrownBy(() -> dtdValidatorService.validate(validXml));
    }

    @Test
    @DisplayName("Product XML valide (avec id et description) — ne doit pas lever d'exception")
    void validate_product_shouldPass_withOptionalFields() {
        String validXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE product SYSTEM "product.dtd">
                <product>
                    <id>1</id>
                    <name>Souris</name>
                    <price>29.99</price>
                    <description>Souris sans fil ergonomique</description>
                </product>
                """;

        assertThatNoException().isThrownBy(() -> dtdValidatorService.validate(validXml));
    }

    @Test
    @DisplayName("Product XML invalide (champ inconnu) — doit lever SAXException")
    void validate_product_shouldThrow_whenUnknownElement() {
        String invalidXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE product SYSTEM "product.dtd">
                <product>
                    <name>Ordinateur</name>
                    <price>1200.00</price>
                    <color>rouge</color>
                </product>
                """;

        assertThatThrownBy(() -> dtdValidatorService.validate(invalidXml))
                .isInstanceOf(SAXException.class);
    }

    @Test
    @DisplayName("Product XML invalide (price manquant) — doit lever SAXException")
    void validate_product_shouldThrow_whenPriceMissing() {
        String invalidXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE product SYSTEM "product.dtd">
                <product>
                    <name>Ordinateur</name>
                </product>
                """;

        assertThatThrownBy(() -> dtdValidatorService.validate(invalidXml))
                .isInstanceOf(SAXException.class);
    }

    // ─────────────────────────────────────────────
    // Validation task.dtd
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("Task XML valide — ne doit pas lever d'exception")
    void validate_task_shouldPass_whenXmlIsValid() {
        String validXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE task SYSTEM "task.dtd">
                <task>
                    <title>Rédiger le rapport</title>
                    <completed>false</completed>
                </task>
                """;

        assertThatNoException().isThrownBy(() -> dtdValidatorService.validate(validXml));
    }

    @Test
    @DisplayName("Task XML invalide (title manquant) — doit lever SAXException")
    void validate_task_shouldThrow_whenTitleMissing() {
        String invalidXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE task SYSTEM "task.dtd">
                <task>
                    <completed>true</completed>
                </task>
                """;

        assertThatThrownBy(() -> dtdValidatorService.validate(invalidXml))
                .isInstanceOf(SAXException.class);
    }
}
