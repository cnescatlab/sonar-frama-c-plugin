/*
	 * This file is part of sonar-frama-c-plugin.
	 *
	 * sonar-frama-c-plugin is free software: you can redistribute it and/or modify
	 * it under the terms of the GNU General Public License as published by
	 * the Free Software Foundation, either version 3 of the License, or
	 * (at your option) any later version.
	 *
	 * sonar-frama-c-plugin is distributed in the hope that it will be useful,
	 * but WITHOUT ANY WARRANTY; without even the implied warranty of
	 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	 * GNU General Public License for more details.
	 *
	 * You should have received a copy of the GNU General Public License
	 * along with sonar-frama-c-plugin.  If not, see <http://www.gnu.org/licenses/>.

*/
package fr.cnes.sonarqube.plugins.framac.measures;

import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;

import fr.cnes.sonarqube.plugins.framac.sensor.FramaCMetricsSensor;

/**
 * Compute a project global Frama-C report error.
 * 
 * Frama-C shall provide a report for each project code file
 * 
 * @see FramaCMetricsSensor
 * 
 * @author Cyrille FRANCOIS
 *
 */
public class ComputePluginErrors implements MeasureComputer {
	
	static final String SEE_REPORT_FILES_ERROR_MESSAGES = "See report files error messages...";

	@Override
	public MeasureComputerDefinition define(MeasureComputerDefinitionContext defContext) {
	    return defContext.newDefinitionBuilder()
	    		.setInputMetrics(new String[] {FramaCMetrics.NUMBER_OF_ERRORS.key(), FramaCMetrics.REPORT_FILES_ERROR.key()})
	    		.setOutputMetrics(new String[] {FramaCMetrics.NUMBER_OF_ERRORS.key(), FramaCMetrics.REPORT_FILES_ERROR.key()})
	    		.build();
	}

	@Override
	public void compute(MeasureComputerContext context) {
	    // measure is already defined on files by {@link SetSizeOnFilesSensor}
	    // in scanner stack
	    if (context.getComponent().getType() != Component.Type.FILE) {
	      int sum = 0;
	      for (Measure child : context.getChildrenMeasures(FramaCMetrics.NUMBER_OF_ERRORS.key())) {
	        sum += child.getIntValue();
	      }
	      context.addMeasure(FramaCMetrics.NUMBER_OF_ERRORS.key(), sum);
	      
	      int sumOfReportFiles = 0;
	      for (Measure child : context.getChildrenMeasures(FramaCMetrics.REPORT_FILES_ERROR.key())) {
	        sumOfReportFiles++;
	      }
	      
		  context.addMeasure(FramaCMetrics.REPORT_FILES_ERROR.key(), SEE_REPORT_FILES_ERROR_MESSAGES);	 
	    }
	}
	

}
