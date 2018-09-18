package fr.cnes.sonarqube.plugins.framac.languages;

import org.junit.Test;
import org.sonar.api.config.Configuration;
import org.sonar.api.config.internal.MapSettings;

import static org.junit.Assert.assertArrayEquals;

public class FramaCLanguageTest {

	@Test
	public void test_given_settings_when_getFileSuffixes_then_settings() {
		Configuration settings = new MapSettings().asConfig();
		FramaCLanguage language = new FramaCLanguage(settings);
		String[] expected = new String[]{".c",".cc",".i",".h",".C",".CC",".I",".H"};
		assertArrayEquals(expected, language.getFileSuffixes());
	}

}
