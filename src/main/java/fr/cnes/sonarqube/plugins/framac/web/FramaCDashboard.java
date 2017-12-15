package fr.cnes.sonarqube.plugins.framac.web;

import org.sonar.api.web.page.Context;
import org.sonar.api.web.page.Page;
import org.sonar.api.web.page.Page.Scope;
import org.sonar.api.web.page.PageDefinition;

public class FramaCDashboard implements PageDefinition {
	
	static final String PAGE_KEY_FRAMAC_FRAMAC_METRICS_SUMMARY = "framac/framac_metrics_summary";
	static final String PAGE_NAME_FRAMA_C_METRICS_SUMMARY = "Frama-C metrics summary";

	protected Page buildMetricsSummaryPage(){
		  return Page.builder(PAGE_KEY_FRAMAC_FRAMAC_METRICS_SUMMARY)
			        .setName(PAGE_NAME_FRAMA_C_METRICS_SUMMARY)
			        .setScope(Scope.COMPONENT).build();
	  }

	  @Override
	  public void define(Context context) {
	    context
	      .addPage(buildMetricsSummaryPage());
	  }


}
