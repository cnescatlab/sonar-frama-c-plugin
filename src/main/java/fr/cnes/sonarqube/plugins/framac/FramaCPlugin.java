package fr.cnes.sonarqube.plugins.framac;

import org.sonar.api.Plugin;

import fr.cnes.sonarqube.plugins.framac.languages.FramaCLanguage;
import fr.cnes.sonarqube.plugins.framac.languages.FramaCQualityProfile;
import fr.cnes.sonarqube.plugins.framac.measures.ComputePluginErrors;
import fr.cnes.sonarqube.plugins.framac.measures.ComputeProjectCyclomaticStatistics;
import fr.cnes.sonarqube.plugins.framac.measures.ComputeProjectLocStatistics;
import fr.cnes.sonarqube.plugins.framac.measures.CyclomaticMetrics;
import fr.cnes.sonarqube.plugins.framac.measures.FramaCMetrics;
import fr.cnes.sonarqube.plugins.framac.rules.FramaCRulesDefinition;
import fr.cnes.sonarqube.plugins.framac.sensor.FramaCMetricsSensor;
import fr.cnes.sonarqube.plugins.framac.settings.FramaCLanguageProperties;
import fr.cnes.sonarqube.plugins.framac.web.FramaCDashboard;

/**
 * This class is the entry point for all extensions
 * 
 * @author Cyrille FRANCOIS
 */
public class FramaCPlugin implements Plugin {

	public void define(Context context) {
		
		// Setting plugin FramaC
		context.addExtensions(FramaCLanguage.class, FramaCQualityProfile.class);
		context.addExtensions(FramaCLanguageProperties.getProperties());

		// Metrics definition and computed measures
		context.addExtensions(
				FramaCMetrics.class, 
				ComputePluginErrors.class,
				CyclomaticMetrics.class, 
				ComputeProjectCyclomaticStatistics.class,
				ComputeProjectLocStatistics.class);

		// Rules definition and scan issues
		context.addExtension(FramaCRulesDefinition.class);
		
		// Dashboard
		context.addExtension(
				FramaCDashboard.class);
		
		// Sonar scanner extension (Sensor)
		context.addExtension(
				FramaCMetricsSensor.class);	
	}
}
