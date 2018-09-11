package fr.cnes.sonarqube.plugins.framac.settings;

import fr.cnes.sonarqube.plugins.framac.languages.FramaCLanguage;
import org.junit.Test;
import org.sonar.api.config.PropertyDefinition;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class FramaCLanguagePropertiesTest {

	@Test
	public void test() {		
		List<PropertyDefinition> actual = FramaCLanguageProperties.getProperties();
		assertEquals(4, actual.size());
		PropertyDefinition reportSubDirectory = actual.get(0);
		assertEquals(FramaCLanguage.NAME, reportSubDirectory.category());
		assertEquals(FramaCLanguageProperties.REPORT_SUBDIR_DEFAULT_VALUE, reportSubDirectory.defaultValue());
		assertEquals(FramaCLanguageProperties.REPORT_SUBDIR_KEY, reportSubDirectory.key());

		PropertyDefinition reportExtensions = actual.get(1);
		assertEquals(FramaCLanguage.NAME, reportExtensions.category());
		assertEquals(FramaCLanguageProperties.REPORT_OUT_EXT_DEFAULT_VALUE, reportExtensions.defaultValue());
		assertEquals(FramaCLanguageProperties.REPORT_OUT_EXT_KEY, reportExtensions.key());

		PropertyDefinition csvReportExtension = actual.get(2);
		assertEquals(FramaCLanguage.NAME, csvReportExtension.category());
		assertEquals(FramaCLanguageProperties.REPORT_CSV_EXT_DEFAULT_VALUE, csvReportExtension.defaultValue());
		assertEquals(FramaCLanguageProperties.REPORT_CSV_EXT_KEY, csvReportExtension.key());

		PropertyDefinition reportInputFileType = actual.get(3);
		assertEquals(FramaCLanguage.NAME, reportInputFileType.category());
		assertEquals(FramaCLanguageProperties.EXPECTED_REPORT_INPUT_FILE_TYPES_DEFAULT_VALUE, reportInputFileType.defaultValue());
		assertEquals(FramaCLanguageProperties.EXPECTED_REPORT_INPUT_FILE_TYPES_KEY, reportInputFileType.key());
	}

}
