package fr.cnes.sonarqube.plugins.framac.languages;

import org.junit.Assert;
import org.junit.Test;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

public class FramaCQualityProfileTest {

	@Test
	public void test_should_create_sonar_way_profile() {
		FramaCQualityProfile profileDef = new FramaCQualityProfile();
		BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
		profileDef.define(context);
		Assert.assertNotNull(profileDef);
		Assert.assertEquals(1, context.profilesByLanguageAndName().keySet().size());
	}

}
