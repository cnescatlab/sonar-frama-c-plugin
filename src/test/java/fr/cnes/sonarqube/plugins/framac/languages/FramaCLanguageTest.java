package fr.cnes.sonarqube.plugins.framac.languages;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.config.Settings;

import fr.cnes.sonarqube.plugins.framac.settings.FramaCLanguageProperties;

public class FramaCLanguageTest {	

	@Test
	public void given_settings_when_getFileSuffixes_then_settings() {
		String[] expected = new String[]{"*.c","*.i"};
		org.sonar.api.config.Settings settings = Mockito.mock(org.sonar.api.config.Settings.class);
		Mockito.when(settings.getStringArray(FramaCLanguageProperties.EXPECTED_REPORT_INPUT_FILE_TYPES_KEY))
		.thenReturn(new String[]{" "+expected[0]+" ", " "+expected[1]+" "});
		FramaCLanguage framaCLanguage = new FramaCLanguage(settings);
		assertEquals(expected, framaCLanguage.getFileSuffixes());
	}

}
