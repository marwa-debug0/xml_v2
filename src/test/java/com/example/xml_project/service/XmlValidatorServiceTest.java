package com.example.xml_project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class XmlValidatorServiceTest {

    private XmlValidatorService validatorService;

    @BeforeEach
    void setUp() throws Exception {
        validatorService = new XmlValidatorService();
    }

    @Test
    void validate_shouldPass_forValidUserXml() {
        String xml = """
                <user>
                    <name>Alice</name>
                    <email>alice@mail.com</email>
                    <password>1234</password>
                </user>
                """;

        assertThatCode(() -> validatorService.validate(xml)).doesNotThrowAnyException();
    }

    @Test
    void validate_shouldFail_whenEmailInvalid() {
        String xml = """
                <user>
                    <name>Alice</name>
                    <email>not-an-email</email>
                    <password>1234</password>
                </user>
                """;

        assertThatThrownBy(() -> validatorService.validate(xml))
                .isInstanceOf(SAXException.class);
    }

    @Test
    void validate_shouldFail_whenNameTooShort() {
        String xml = """
                <user>
                    <name>A</name>
                    <email>alice@mail.com</email>
                    <password>1234</password>
                </user>
                """;

        assertThatThrownBy(() -> validatorService.validate(xml))
                .isInstanceOf(SAXException.class);
    }

    @Test
    void validate_shouldFail_whenPasswordTooShort() {
        String xml = """
                <user>
                    <name>Alice</name>
                    <email>alice@mail.com</email>
                    <password>12</password>
                </user>
                """;

        assertThatThrownBy(() -> validatorService.validate(xml))
                .isInstanceOf(SAXException.class);
    }

    @Test
    void validate_shouldFail_whenRequiredFieldMissing() {
        String xml = """
                <user>
                    <name>Alice</name>
                    <password>1234</password>
                </user>
                """;

        assertThatThrownBy(() -> validatorService.validate(xml))
                .isInstanceOf(SAXException.class);
    }

    // ---------- DTD validation ----------

    @Test
    void validateWithDtd_shouldPass_forWellStructuredUserXml() {
        String xml = """
                <user>
                    <name>Alice</name>
                    <email>alice@mail.com</email>
                    <password>1234</password>
                </user>
                """;

        assertThatCode(() -> validatorService.validateWithDtd(xml)).doesNotThrowAnyException();
    }

    @Test
    void validateWithDtd_shouldFail_whenRequiredElementMissing() {
        // password is required by the DTD
        String xml = """
                <user>
                    <name>Alice</name>
                    <email>alice@mail.com</email>
                </user>
                """;

        assertThatThrownBy(() -> validatorService.validateWithDtd(xml))
                .isInstanceOf(SAXException.class);
    }

    @Test
    void validateWithDtd_shouldFail_whenUnexpectedElementPresent() {
        // 'role' is not declared in the DTD
        String xml = """
                <user>
                    <name>Alice</name>
                    <email>alice@mail.com</email>
                    <password>1234</password>
                    <role>admin</role>
                </user>
                """;

        assertThatThrownBy(() -> validatorService.validateWithDtd(xml))
                .isInstanceOf(SAXException.class);
    }
}
