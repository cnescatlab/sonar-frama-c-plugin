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
package fr.cnes.sonarqube.plugins.framac.measures;

import fr.cnes.sonarqube.plugins.framac.sensor.FramaCMetricsSensor;
import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;

/**
 * Compute a project global Frama-C report error.
 * 
 * Frama-C shall provide a report for each project code file
 * 
 * @see FramaCMetricsSensor
 */
public class ComputePluginErrors implements MeasureComputer {
	
	static final String SEE_REPORT_FILES_ERROR_MESSAGES = "See report files error messages...";

	@Override
	public MeasureComputerDefinition define(MeasureComputerDefinitionContext defContext) {
	    String[] metricTab = new String[] {FramaCMetrics.NUMBER_OF_ERRORS.key(), FramaCMetrics.REPORT_FILES_ERROR.key()};
		return defContext.newDefinitionBuilder()
	    		.setInputMetrics(metricTab)
	    		.setOutputMetrics(metricTab)
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
	      
		  context.addMeasure(FramaCMetrics.REPORT_FILES_ERROR.key(), SEE_REPORT_FILES_ERROR_MESSAGES);	 
	    }
	}
	

}
