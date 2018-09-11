package fr.cnes.sonarqube.plugins.framac.measures;

import org.junit.Test;
import org.sonar.api.measures.Metric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FramaCMetricsTest {

	@Test
	public void testGetMetrics() {
		ArrayList<Metric> expected=new ArrayList<Metric>();
		expected.addAll(Arrays.asList(
				FramaCMetrics.NUMBER_OF_WARNINGS, 
				FramaCMetrics.NUMBER_OF_ERRORS,
				FramaCMetrics.REPORT_FILES_WARNING,
				FramaCMetrics.REPORT_FILES_ERROR));
		FramaCMetrics framaCMetrics = new FramaCMetrics();
		List<Metric> actual = framaCMetrics.getMetrics();
		assertEquals(expected, actual);
	}

}
