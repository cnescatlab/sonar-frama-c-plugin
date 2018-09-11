package fr.cnes.sonarqube.plugins.framac.measures;

import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.ce.measure.MeasureComputer.MeasureComputerContext;
import org.sonar.api.ce.measure.MeasureComputer.MeasureComputerDefinition;
import org.sonar.api.ce.measure.MeasureComputer.MeasureComputerDefinitionContext;

import java.util.ArrayList;

public class ComputeProjectCyclomaticStatisticsTest {

	@Test
	public void callDefine() {
		MeasureComputerDefinition measureComputerDefinition = Mockito.mock(MeasureComputerDefinition.class);
		MeasureComputer.MeasureComputerDefinition.Builder builder = Mockito.mock(MeasureComputer.MeasureComputerDefinition.Builder.class);
		Mockito.when(builder.setInputMetrics(new String[] {CyclomaticMetrics.CYCLOMATIC.key(),CyclomaticMetrics.CYCLOMATIC_MEAN.key(),CyclomaticMetrics.CYCLOMATIC_MIN.key(),CyclomaticMetrics.CYCLOMATIC_MAX.key()})).thenReturn(builder);
		Mockito.when(builder.setOutputMetrics(new String[] {CyclomaticMetrics.CYCLOMATIC.key(),CyclomaticMetrics.CYCLOMATIC_MEAN.key(),CyclomaticMetrics.CYCLOMATIC_MIN.key(),CyclomaticMetrics.CYCLOMATIC_MAX.key()})).thenReturn(builder);
		Mockito.when(builder.build()).thenReturn(measureComputerDefinition);
		MeasureComputerDefinitionContext measureComputerDefinitionContext = Mockito.mock(MeasureComputerDefinitionContext.class); 
		Mockito.when(measureComputerDefinitionContext.newDefinitionBuilder()).thenReturn(builder);
		ComputeProjectCyclomaticStatistics computeProjectCyclomaticStatistics = new ComputeProjectCyclomaticStatistics();
		computeProjectCyclomaticStatistics.define(measureComputerDefinitionContext);

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
		Mockito.when(context.getChildrenMeasures(CyclomaticMetrics.CYCLOMATIC_MEAN.key())).thenReturn(childsMeasures);
		Mockito.when(context.getChildrenMeasures(CyclomaticMetrics.CYCLOMATIC_MIN.key())).thenReturn(childsMeasures);
		Mockito.when(context.getChildrenMeasures(CyclomaticMetrics.CYCLOMATIC_MAX.key())).thenReturn(childsMeasures);
		ComputeProjectCyclomaticStatistics computeProjectCyclomaticStatistics = new ComputeProjectCyclomaticStatistics();
		computeProjectCyclomaticStatistics.compute(context);
		Mockito.verify(context).addMeasure(CyclomaticMetrics.CYCLOMATIC_MEAN.key(), 3.0);
		Mockito.verify(context).addMeasure(CyclomaticMetrics.CYCLOMATIC_MIN.key(), 1);
		Mockito.verify(context).addMeasure(CyclomaticMetrics.CYCLOMATIC_MAX.key(), 5);
	}
}
