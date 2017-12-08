package fr.cnes.sonarqube.plugins.framac.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar.api.Plugin;
import org.sonar.api.web.page.Page;
import org.sonar.api.web.page.Page.Scope;
import org.sonar.api.web.page.Context;

import fr.cnes.sonarqube.plugins.framac.FramaCPlugin;

public class FramaCDashboardTest {

	@BeforeClass
	public static void beforeClass(){
		Plugin.Context context = mock(Plugin.Context.class);
		FramaCPlugin plugin = new FramaCPlugin();
		plugin.define(context);
	}
	
	@Test
	public void given_framaCDashboard_when_buildMetricsSummaryPage_then_dashboardPage() {
		FramaCDashboard framaCDashboard = new FramaCDashboard();
		Page actual = framaCDashboard.buildMetricsSummaryPage();
		assertEquals(FramaCDashboard.PAGE_KEY_FRAMAC_FRAMAC_METRICS_SUMMARY, actual.getKey());
		assertEquals(FramaCDashboard.PAGE_NAME_FRAMA_C_METRICS_SUMMARY, actual.getName());
		assertEquals(Scope.COMPONENT, actual.getScope());
	}
	
	@Test
	public void given_framaCDashboard_when_callDefineWithNullContext_then_exception(){
		org.sonar.api.web.page.Context context = new org.sonar.api.web.page.Context();
		FramaCDashboard framaCDashboard = new FramaCDashboard();
		framaCDashboard.define(context);
		assertEquals(1, context.getPages().size());
	}

}
