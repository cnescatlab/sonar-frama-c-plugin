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
 * Compute cyclomatic complexity into the project.
 * 
 * Each file cyclomatic complexited is computed by Frama-C (option -metrics) and stored into a result file
 * 
 * @see FramaCMetricsSensor
 * 
 * @author Cyrille FRANCOIS
 *
 */
public class ComputeProjectCyclomaticStatistics implements MeasureComputer {

	@Override
	public MeasureComputerDefinition define(MeasureComputerDefinitionContext defContext) {
		
	    return defContext.newDefinitionBuilder()
	    		.setInputMetrics(new String[] {CyclomaticMetrics.CYCLOMATIC.key(),CyclomaticMetrics.CYCLOMATIC_MEAN.key(),CyclomaticMetrics.CYCLOMATIC_MIN.key(),CyclomaticMetrics.CYCLOMATIC_MAX.key()})
	    		.setOutputMetrics(new String[] {CyclomaticMetrics.CYCLOMATIC.key(),CyclomaticMetrics.CYCLOMATIC_MEAN.key(),CyclomaticMetrics.CYCLOMATIC_MIN.key(),CyclomaticMetrics.CYCLOMATIC_MAX.key()})
	    		.build();
	}

	@Override
	public void compute(MeasureComputerContext context) {
		Iterable<Measure> childrenMeasures = null;
		// Create module measures
		if (context.getComponent().getType() != Component.Type.FILE) {
			
			// Search Cyclomatic mean measure for children files
			childrenMeasures = context.getChildrenMeasures(CyclomaticMetrics.CYCLOMATIC_MEAN.key());
			computeMean(context, childrenMeasures);

			// Search Cyclomatic minimum measure for children files
			childrenMeasures = context.getChildrenMeasures(CyclomaticMetrics.CYCLOMATIC_MIN.key());
			computeMin(context, childrenMeasures);
						
			// Search Cyclomatic minimum measure for children files
			childrenMeasures = context.getChildrenMeasures(CyclomaticMetrics.CYCLOMATIC_MAX.key());
			computeMax(context, childrenMeasures);
		}
	}

	private void computeMax(MeasureComputerContext context, Iterable<Measure> childrenMeasures) {
		if(childrenMeasures.iterator().hasNext()){
			int max = 0;
			for (Measure child : childrenMeasures){
				if(child.getIntValue() > max){
					max = child.getIntValue();
				}
			}
			context.addMeasure(CyclomaticMetrics.CYCLOMATIC_MAX.key(), max);
		}
	}

	private void computeMin(MeasureComputerContext context, Iterable<Measure> childrenMeasures) {
		if(childrenMeasures.iterator().hasNext()){
			int min = 1000;
			for (Measure child : childrenMeasures){
				if(child.getIntValue() < min){
					min = child.getIntValue();
				}
			}
			context.addMeasure(CyclomaticMetrics.CYCLOMATIC_MIN.key(), min);
		}
	}

	private void computeMean(MeasureComputerContext context, Iterable<Measure> childrenMeasures) {
		if(childrenMeasures.iterator().hasNext()){
			double sum = 0;
			int nbItem = 0;
			for (Measure child : childrenMeasures) {
				sum += child.getDoubleValue();
				nbItem++;
			}
			context.addMeasure(CyclomaticMetrics.CYCLOMATIC_MEAN.key(),(nbItem!=0)?sum/nbItem:sum);							
		}
	}
}