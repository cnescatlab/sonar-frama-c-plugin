package fr.cnes.sonarqube.plugins.framac.measures;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mockitoSession;

import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.Issue;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.ce.measure.Settings;
import org.sonar.api.ce.measure.MeasureComputer.MeasureComputerContext;
import org.sonar.api.ce.measure.MeasureComputer.MeasureComputerDefinition;
import org.sonar.api.ce.measure.MeasureComputer.MeasureComputerDefinitionContext;

public class ComputeProjectLocStatisticsTest {

	@Test
	public void callDefine() {
		MeasureComputerDefinition measureComputerDefinition = new MeasureComputerDefinition() {
			
			@Override
			public Set<String> getOutputMetrics() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Set<String> getInputMetrics() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		MeasureComputer.MeasureComputerDefinition.Builder builder = Mockito.mock(MeasureComputer.MeasureComputerDefinition.Builder.class);
		Mockito.when(builder.setInputMetrics(new String[] {CyclomaticMetrics.SLOC.key(),CyclomaticMetrics.SLOC_MEAN.key(),CyclomaticMetrics.SLOC_MIN.key(),CyclomaticMetrics.SLOC_MAX.key()})).thenReturn(builder);
		Mockito.when(builder.setOutputMetrics(new String[] {CyclomaticMetrics.SLOC.key(),CyclomaticMetrics.SLOC_MEAN.key(),CyclomaticMetrics.SLOC_MIN.key(),CyclomaticMetrics.SLOC_MAX.key()})).thenReturn(builder);
		Mockito.when(builder.build()).thenReturn(measureComputerDefinition);
		MeasureComputerDefinitionContext measureComputerDefinitionContext = Mockito.mock(MeasureComputerDefinitionContext.class); 
		Mockito.when(measureComputerDefinitionContext.newDefinitionBuilder()).thenReturn(builder);
		ComputeProjectLocStatistics computeProjectLocStatistics = new ComputeProjectLocStatistics();
		computeProjectLocStatistics.define(measureComputerDefinitionContext);
	}

	@Test
	public void given_context_when_compute_then_newComputedMeasures(){
		org.sonar.api.ce.measure.Component componentFile = Mockito.mock(org.sonar.api.ce.measure.Component.class);
		Mockito.when(componentFile.getType()).thenReturn(Component.Type.DIRECTORY);
		MeasureComputerContext context = Mockito.mock(MeasureComputerContext.class);
		Mockito.when(context.getComponent()).thenReturn(componentFile);
		ArrayList<Measure> childsMeasures = new ArrayList<Measure>();
		Measure aMeasureMin = Mockito.mock(Measure.class);
		Mockito.when(aMeasureMin.getIntValue()).thenReturn(1);
		Mockito.when(aMeasureMin.getDoubleValue()).thenReturn(1.);
		Measure aMeasureMax = Mockito.mock(Measure.class);
		Mockito.when(aMeasureMax.getIntValue()).thenReturn(5);
		Mockito.when(aMeasureMax.getDoubleValue()).thenReturn(5.);
		childsMeasures.add(aMeasureMin);
		childsMeasures.add(aMeasureMax);
		Mockito.when(context.getChildrenMeasures(CyclomaticMetrics.SLOC.key())).thenReturn(childsMeasures);
		Mockito.when(context.getChildrenMeasures(CyclomaticMetrics.SLOC_MEAN.key())).thenReturn(childsMeasures);
		Mockito.when(context.getChildrenMeasures(CyclomaticMetrics.SLOC_MIN.key())).thenReturn(childsMeasures);
		Mockito.when(context.getChildrenMeasures(CyclomaticMetrics.SLOC_MAX.key())).thenReturn(childsMeasures);
		ComputeProjectLocStatistics computeProjectLocStatistics = new ComputeProjectLocStatistics();
		computeProjectLocStatistics.compute(context);
		Mockito.verify(context).addMeasure(CyclomaticMetrics.SLOC.key(), 6);
		Mockito.verify(context).addMeasure(CyclomaticMetrics.SLOC_MEAN.key(), 3.0);
		Mockito.verify(context).addMeasure(CyclomaticMetrics.SLOC_MIN.key(), 1);
		Mockito.verify(context).addMeasure(CyclomaticMetrics.SLOC_MAX.key(), 5);
	}
}
