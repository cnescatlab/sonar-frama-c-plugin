package fr.cnes.sonarqube.plugins.framac.languages;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class FramaCLanguageTest {

	@Test
	public void test_given_settings_when_getFileSuffixes_then_settings() {
		FramaCLanguage language = new FramaCLanguage();
		String[] expected = new String[]{};
		assertArrayEquals(expected, language.getFileSuffixes());
	}

}
