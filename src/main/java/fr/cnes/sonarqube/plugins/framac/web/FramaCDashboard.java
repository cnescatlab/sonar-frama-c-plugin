package fr.cnes.sonarqube.plugins.framac.web;

import org.sonar.api.web.page.Context;
import org.sonar.api.web.page.Page;
import org.sonar.api.web.page.Page.Scope;
import org.sonar.api.web.page.PageDefinition;

public class FramaCDashboard implements PageDefinition {

	  @Override
	  public void define(Context context) {
	    context
	      .addPage(Page.builder("framac/custom_page_4_project")
	    	        .setName("custom_page_4_project")
	    	        .setScope(Scope.COMPONENT).build())
	      .addPage(Page.builder("framac/static_dashboard_framac")
	    	        .setName("Frama-C Dashboard")
	    	        .setScope(Scope.COMPONENT).build())
	      .addPage(Page.builder("framac/static_dashboard_icode")
	    	        .setName("ICode CNES Dashboard")
	    	        .setScope(Scope.COMPONENT).build())
	      .addPage(Page.builder("framac/metrics_summary")
	        .setName("Frama-C metrics summary")
	        .setScope(Scope.COMPONENT).build());
	  }


}
