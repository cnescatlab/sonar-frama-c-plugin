package fr.cnes.sonar.plugins.framac.settings;

import fr.cnes.sonar.plugins.framac.languages.FramaCLanguage;
import org.junit.Assert;
import org.junit.Test;
import org.sonar.api.config.PropertyDefinition;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class FramaCLanguagePropertiesTest {

	@Test
	public void test() {		
		List<PropertyDefinition> actual = FramaCPluginProperties.getProperties();
		assertEquals(5, actual.size());
		PropertyDefinition reportSubDirectory = actual.get(0);
		Assert.assertEquals(FramaCLanguage.NAME, reportSubDirectory.category());
		assertEquals(FramaCPluginProperties.AUTOLAUNCH_PROP_DEFAULT, reportSubDirectory.defaultValue());
		assertEquals(FramaCPluginProperties.AUTOLAUNCH_PROP_KEY, reportSubDirectory.key());

		PropertyDefinition reportCommandLineOptions = actual.get(1);
		assertEquals(FramaCLanguage.NAME, reportCommandLineOptions.category());
		assertEquals(FramaCPluginProperties.COMMAND_PROP_DEFAULT, reportCommandLineOptions.defaultValue());
		assertEquals(FramaCPluginProperties.COMMAND_PROP_KEY, reportCommandLineOptions.key());

		PropertyDefinition reportExtensions = actual.get(2);
		assertEquals(FramaCLanguage.NAME, reportExtensions.category());
		assertEquals(FramaCPluginProperties.FRAMAC_PATH_DEFAULT, reportExtensions.defaultValue());
		assertEquals(FramaCPluginProperties.FRAMAC_PATH_KEY, reportExtensions.key());

		PropertyDefinition csvReportExtension = actual.get(3);
		assertEquals(FramaCLanguage.NAME, csvReportExtension.category());
		assertEquals(FramaCPluginProperties.SUFFIX_DEFAULT, csvReportExtension.defaultValue());
		assertEquals(FramaCPluginProperties.SUFFIX_KEY, csvReportExtension.key());

		PropertyDefinition reportInputFileType = actual.get(4);
		assertEquals(FramaCLanguage.NAME, reportInputFileType.category());
		assertEquals(FramaCPluginProperties.REPORT_PATH_DEFAULT, reportInputFileType.defaultValue());
		assertEquals(FramaCPluginProperties.REPORT_PATH_KEY, reportInputFileType.key());
	}

}
