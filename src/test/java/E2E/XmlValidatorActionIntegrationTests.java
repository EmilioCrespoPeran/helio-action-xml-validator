package E2E;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;

import helio.blueprints.components.ComponentType;
import helio.blueprints.components.Components;
import helio.blueprints.exceptions.ExtensionNotFoundException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import utils.E2EUtils;

import org.junit.Test;

/**
 * Set of test which validates XML documents.
 * 
 * @author Emilio
 *
 */
public class XmlValidatorActionIntegrationTests {

	
	@BeforeClass
    public static void setup() throws ExtensionNotFoundException {
        Components.registerAndLoad(
            "https://github.com/helio-ecosystem/helio-action-xml-validator/releases/download/v0.1.0/helio-action-xml-validator-0.1.0.jar",
            "helio.actions.validator.XmlValidatorAction",
            ComponentType.ACTION);
        
        Components.registerAndLoad(
                "https://github.com/helio-ecosystem/helio-action-json-validator/releases/download/v0.1.0/helio-action-json-validator-0.1.0.jar",
                "helio.actions.validator.JsonValidatorAction",
                ComponentType.ACTION);
    }
	

	/**
	 * The XML source is incorrect and the validator throws an error.
	 */
	@Test
	public void test01_ValidateInvalidXmlDataWithXmlSchema() {
		try {
			String expected = "error";
			JsonObject obtained = JsonParser.parseString(
					E2EUtils.executeTestWithTemplate("01_xml-template.txt")).getAsJsonObject();

			assertTrue(obtained.has("status"));
			assertTrue(obtained.has("message"));
			assertEquals(expected, obtained.get("status").getAsString().strip().toLowerCase());
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}

	/**
	 * The XML source is correct and the validator verifies it.
	 */
	@Test
	public void test02_ValidateCorrectXmlDataWithXmlSchema() {
		try {
			String expected = "ok";
			JsonObject obtained = JsonParser.parseString(
					E2EUtils.executeTestWithTemplate("02_xml-template.txt")).getAsJsonObject();

			assertTrue(obtained.has("status"));
			assertTrue(!obtained.has("message"));
			assertEquals(expected, obtained.get("status").getAsString().strip().toLowerCase());
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}

	/**
	 * The XML source is correct but the validator expected a JSON data.
	 */
	@Test
	public void test03_ValidateXmlDataWithJsonSchema() {
		try {
			String expected = "error";
			JsonObject obtained = JsonParser.parseString(
					E2EUtils.executeTestWithTemplate("03_xml-template.txt")).getAsJsonObject();

			assertTrue(obtained.has("status"));
			assertTrue(obtained.has("message"));
			assertEquals(expected, obtained.get("status").getAsString().strip().toLowerCase());
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}

}
