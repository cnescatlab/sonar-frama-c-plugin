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

import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * General metrics of Frama-C plugin for sonar.
 * 
 * Report files warning: last warning message from sensor 
 * Report files error: last error message from sensor
 * Number of warnings: 0 equals no warning messages
 * Number of errors: 0 equals no error messages
 */
public class FramaCMetrics implements Metrics {

	public static final String DOMAIN = "Frama-c";

		/** Metric for number of warnings */
		public static final Metric<Integer> NUMBER_OF_WARNINGS = new Metric.Builder(
				"warnings",
				"Number of warning messages",
				Metric.ValueType.INT)
				.setDirection(Metric.DIRECTION_WORST) // Metric.DIRECTION_NONE, Metric.DIRECTION_BETTER, Metric.DIRECTION_WORST
	            .setQualitative(false) // by definedRules false, tru => Highlighted into gui
				.setDomain(DOMAIN).create();

		/** Metric for number of errors */
		public static final Metric<Integer> NUMBER_OF_ERRORS = new Metric.Builder(
				"errors",
				"Number of error messages",
				Metric.ValueType.INT)
				.setDirection(Metric.DIRECTION_WORST) // Metric.DIRECTION_NONE, Metric.DIRECTION_BETTER, Metric.DIRECTION_WORST
	            .setQualitative(false) // by definedRules false, tru => Highlighted into gui
				.setDomain(DOMAIN).create();

		/** Metric for report files warning */
		public static final Metric<String> REPORT_FILES_WARNING = new Metric.Builder(
				"report-files-warning",
				"Report files warning",
				Metric.ValueType.STRING)
				.setDirection(Metric.DIRECTION_WORST) // Metric.DIRECTION_NONE, Metric.DIRECTION_BETTER, Metric.DIRECTION_WORST
	            .setQualitative(false) // by definedRules false, tru => Highlighted into gui
				.setDomain(DOMAIN).create();

		/** Metric for report files error */
		public static final Metric<String> REPORT_FILES_ERROR = new Metric.Builder(
				"report-files-error",
				"Report files error",
				Metric.ValueType.STRING)
				.setDirection(Metric.DIRECTION_WORST) // Metric.DIRECTION_NONE, Metric.DIRECTION_BETTER, Metric.DIRECTION_WORST
	            .setQualitative(false) // by definedRules false, tru => Highlighted into gui
				.setDomain(DOMAIN).create();
	
	@SuppressWarnings({ "rawtypes" })
	public List<Metric> getMetrics() {
		ArrayList<Metric> res=new ArrayList<>();
		res.addAll(Arrays.asList(
				NUMBER_OF_WARNINGS, 
				NUMBER_OF_ERRORS,
				REPORT_FILES_WARNING,
				REPORT_FILES_ERROR));
		return res;
	}
}
