/*
 * This file is part of sonarframac.
 *
 * sonarframac is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sonarframac is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with sonarframac.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.cnes.sonarqube.plugins.framac;

import fr.cnes.sonarqube.plugins.framac.languages.FramaCLanguage;
import fr.cnes.sonarqube.plugins.framac.languages.FramaCQualityProfile;
import fr.cnes.sonarqube.plugins.framac.measures.*;
import fr.cnes.sonarqube.plugins.framac.rules.FramaCRulesDefinition;
import fr.cnes.sonarqube.plugins.framac.sensor.FramaCMetricsSensor;
import fr.cnes.sonarqube.plugins.framac.settings.FramaCLanguageProperties;
import org.sonar.api.Plugin;

/**
 * This class is the entry point for all extensions
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
		
		// Sonar scanner extension (Sensor)
		context.addExtension(
				FramaCMetricsSensor.class);	
	}
}
