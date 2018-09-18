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

public class ComputePluginErrorsTest {

	@Test
	public void callDefine() {
		MeasureComputerDefinition measureComputerDefinition = Mockito.mock(MeasureComputerDefinition.class);
		MeasureComputer.MeasureComputerDefinition.Builder builder = Mockito.mock(MeasureComputer.MeasureComputerDefinition.Builder.class);
		Mockito.when(builder.setInputMetrics(new String[] {FramaCMetrics.NUMBER_OF_ERRORS.key(), FramaCMetrics.REPORT_FILES_ERROR.key()})).thenReturn(builder);
		Mockito.when(builder.setOutputMetrics(new String[] {FramaCMetrics.NUMBER_OF_ERRORS.key(), FramaCMetrics.REPORT_FILES_ERROR.key()})).thenReturn(builder);
		Mockito.when(builder.build()).thenReturn(measureComputerDefinition);
		MeasureComputerDefinitionContext measureComputerDefinitionContext = Mockito.mock(MeasureComputerDefinitionContext.class); 
		Mockito.when(measureComputerDefinitionContext.newDefinitionBuilder()).thenReturn(builder);
		ComputePluginErrors computePluginErrors = new ComputePluginErrors();
		computePluginErrors.define(measureComputerDefinitionContext);
	}

	@Test
	public void given_context_when_compute_then_newComputedMeasures(){
		org.sonar.api.ce.measure.Component componentFile = Mockito.mock(org.sonar.api.ce.measure.Component.class);
		Mockito.when(componentFile.getType()).thenReturn(Component.Type.DIRECTORY);
		MeasureComputerContext context = Mockito.mock(MeasureComputerContext.class);
		Mockito.when(context.getComponent()).thenReturn(componentFile);
		ArrayList<Measure> childsMeasures = new ArrayList<Measure>();
		Measure aMeasure = Mockito.mock(Measure.class);
		Mockito.when(aMeasure.getIntValue()).thenReturn(1);
		childsMeasures.add(aMeasure);
		Mockito.when(context.getChildrenMeasures(FramaCMetrics.NUMBER_OF_ERRORS.key())).thenReturn(childsMeasures);
		Mockito.when(context.getChildrenMeasures(FramaCMetrics.REPORT_FILES_ERROR.key())).thenReturn(childsMeasures);
		ComputePluginErrors computePluginErrors = new ComputePluginErrors();
		computePluginErrors.compute(context);
		Mockito.verify(context).addMeasure(FramaCMetrics.NUMBER_OF_ERRORS.key(), 1);
		Mockito.verify(context).addMeasure(FramaCMetrics.REPORT_FILES_ERROR.key(), ComputePluginErrors.SEE_REPORT_FILES_ERROR_MESSAGES);
	}

}
