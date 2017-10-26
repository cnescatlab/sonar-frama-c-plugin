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
 * Compute lines of code statistics for all source files from a same project .
 * 
 * Global metrics are computed by Frama-C (option -metrics) into each source file and stored into a result file.
 * Sonar sensor add source file global metrics measure into Sonar data base.
 * This sub-class of MeasureComputer provide project measures 
 * 
 * @see FramaCMetricsSensor
 * 
 * @author Cyrille FRANCOIS
 *
 */
public class ComputeProjectLocStatistics implements MeasureComputer {

	@Override
	public MeasureComputerDefinition define(MeasureComputerDefinitionContext defContext) {
		
	    return defContext.newDefinitionBuilder()
	    		.setInputMetrics(new String[] {CyclomaticMetrics.SLOC.key(),CyclomaticMetrics.SLOC_MEAN.key(),CyclomaticMetrics.SLOC_MIN.key(),CyclomaticMetrics.SLOC_MAX.key()})
	    		.setOutputMetrics(new String[] {CyclomaticMetrics.SLOC.key(),CyclomaticMetrics.SLOC_MEAN.key(),CyclomaticMetrics.SLOC_MIN.key(),CyclomaticMetrics.SLOC_MAX.key()})
	    		.build();
	}

	@Override
	public void compute(MeasureComputerContext context) {
		Iterable<Measure> childrenMeasures = null;
		// Create module measures
		if (context.getComponent().getType() != Component.Type.FILE) {
			
			// Search Cyclomatic measure for children files
			childrenMeasures = context.getChildrenMeasures(CyclomaticMetrics.SLOC.key());
			if(childrenMeasures.iterator().hasNext()){
				int sum = 0;
				for (Measure child : childrenMeasures) {
					sum += child.getIntValue();
				}			
				context.addMeasure(CyclomaticMetrics.SLOC.key(),sum);				
			}
			
			// Search Cyclomatic mean measure for children files
			childrenMeasures = context.getChildrenMeasures(CyclomaticMetrics.SLOC_MEAN.key());
			if(childrenMeasures.iterator().hasNext()){
				double sum = 0;
				int nbItem = 0;
				for (Measure child : childrenMeasures) {
					sum += child.getDoubleValue();
					nbItem++;
				}
				context.addMeasure(CyclomaticMetrics.SLOC_MEAN.key(),(nbItem!=0)?sum/nbItem:sum);							
			}

			// Search Cyclomatic minimum measure for children files
			childrenMeasures = context.getChildrenMeasures(CyclomaticMetrics.SLOC_MIN.key());
			if(childrenMeasures.iterator().hasNext()){
				int min = 1000;
//				String msg = "";
				for (Measure child : childrenMeasures){
//					msg += "child value for type "+context.getComponent().getType()+" = "+child.getIntValue();
					if(child.getIntValue() < min){
						min = child.getIntValue();
					}
				}
				context.addMeasure(CyclomaticMetrics.SLOC_MIN.key(), min);
//				context.addMeasure(DBG.key(), msg);
			}
						
			// Search Cyclomatic minimum measure for children files
			childrenMeasures = context.getChildrenMeasures(CyclomaticMetrics.SLOC_MAX.key());
			if(childrenMeasures.iterator().hasNext()){
				int max = 0;
				for (Measure child : childrenMeasures){
					if(child.getIntValue() > max){
						max = child.getIntValue();
					}
				}
				context.addMeasure(CyclomaticMetrics.SLOC_MAX.key(), max);
			}
		}
	}
}