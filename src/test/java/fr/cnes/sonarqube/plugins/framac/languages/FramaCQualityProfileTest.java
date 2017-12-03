package fr.cnes.sonarqube.plugins.framac.languages;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sonar.api.profiles.RulesProfile;

public class FramaCQualityProfileTest {

	@Test
	public void test() {
		FramaCQualityProfile framaCQualityProfile = new FramaCQualityProfile();
		RulesProfile actual = framaCQualityProfile.createProfile(null);
		assertEquals(FramaCLanguage.KEY, actual.getLanguage());
		assertEquals(FramaCQualityProfile.FRAMA_C_RULES_PROFILE_NAME, actual.getName());
	}

}
