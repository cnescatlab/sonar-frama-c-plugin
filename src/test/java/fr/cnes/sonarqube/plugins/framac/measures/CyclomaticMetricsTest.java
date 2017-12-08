package fr.cnes.sonarqube.plugins.framac.measures;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.sonar.api.measures.Metric;

public class CyclomaticMetricsTest extends CyclomaticMetrics{

	@Test
	public void testGetMetrics() {
		ArrayList<Metric> expected=new ArrayList<Metric>();
		expected.addAll(Arrays.asList(
				DECISION_POINTS, 
				NUMBER_OF_GLOBAL_VARIABLES,
				NUMBER_OF_IF_STATEMENTS,
				NUMBER_OF_LOOP_STATEMENTS,
				NUMBER_OF_GOTO_STATEMENTS,
				NUMBER_OF_ASSIGNMENT_STATEMENTS,
				NUMBER_OF_EXIT_POINT_STATEMENTS,
				NUMBER_OF_FUNCTIONS_DECLARED,
				NUMBER_OF_FUNCTION_CALLS,
				NUMBER_OF_POINTER_DEREFERENCINGS
				));
		expected.addAll(Arrays.asList(
				SLOC,
				SLOC_MEAN,
				SLOC_MAX,
				SLOC_MIN
				));
		expected.addAll(Arrays.asList(
				CYCLOMATIC,
				CYCLOMATIC_MEAN,
				CYCLOMATIC_MAX,
				CYCLOMATIC_MIN
				));
		CyclomaticMetrics cyclomaticMetrics = new CyclomaticMetrics();
		List<Metric> actual = cyclomaticMetrics.getMetrics();
		assertEquals(expected, actual);
	}

}
