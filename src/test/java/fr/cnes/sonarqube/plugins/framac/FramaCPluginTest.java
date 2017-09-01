package fr.cnes.sonarqube.plugins.framac;

import org.junit.Test;
import org.sonar.api.Plugin;

import fr.cnes.sonarqube.plugins.framac.rules.FramaCRulesDefinition;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FramaCPluginTest {

	@Test
	public void testExtension() {
		
		Plugin.Context context = mock(Plugin.Context.class);
		FramaCPlugin plugin = new FramaCPlugin();
		plugin.define(context);
		
		verify(context).addExtension(FramaCRulesDefinition.class);
	}

}
