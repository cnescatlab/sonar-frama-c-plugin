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
package fr.cnes.sonarqube.plugins.framac.report;

import fr.cnes.sonarqube.plugins.framac.rules.FramaCRulesDefinition;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.sonar.api.measures.Metric;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import fr.cnes.sonarqube.plugins.framac.measures.CyclomaticMetrics;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Read expected data from a Frama-C report file.
 * 
 * Scan a Frama-C report file for patterns given by Metrics and Rules and produce measures and issues for each match
 * 
 * @author Cyrille FRANCOIS
 *
 */
public class FramaCReportReader {
	
	private static final String NO_EXPECTED_VALUE_FOR_METRICS = "No expected value for metrics ";

	private static final String VALUE_MODULE_NAME = "VALUE";
	private static final String SYNTAX_MODULE_NAME = "SYNTAX";
	private static final String CSV_MODULE_NAME = "CSV";
	private static final String CSV_DEFAULT_TYPE = CSV_MODULE_NAME + ".0";

	private static final Logger LOGGER = Loggers.get(FramaCReportReader.class);
	
	public static final String WORD_KERNEL = "\\Q[kernel]\\E";
	public static final String WORD_VALUE = "\\Q[value]\\E";
	public static final String WORD_WARNING = " warning:";
	
	public static final Pattern PATTERN_KERNEL = Pattern.compile(WORD_KERNEL);

	protected static final Map<String,Pattern> mapRulePattern = new HashMap<>();
	protected static final Map<String,String> mapDefaultCsvRulePattern = new HashMap<>();
	protected static Map<String, String> mapCsvRulePattern = new HashMap<>();


	public FramaCReportReader() {
		super();
		valueRulesMatchingPatterns();
		syntaxeRulesMatchingPatterns();
		csvRulesMatchingPatterns();
		loadXMLRules();
	}

	private void loadXMLRules() {
		String pathToRule = FramaCRulesDefinition.PATH_TO_RULES_XML;

		Document xmlRules;
		InputStream inputStream = this.getClass().getResourceAsStream(pathToRule);
		if (inputStream != null) {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder;
				builder = factory.newDocumentBuilder();
				xmlRules = builder.parse(inputStream);
				Element rulesElt = (Element) xmlRules.getElementsByTagName(FramaCRulesDefinition.getRepositoryKeyForLanguage()).item(0);
				NodeList ruleList = rulesElt.getElementsByTagName("rule");
				int nbRules = ruleList.getLength();
				for (int i = 0; i < nbRules; i++) {
					Element rule = (Element) ruleList.item(i);
					String key = rule.getElementsByTagName("key").item(0).getTextContent();
					if (key.startsWith("CSV")) {
						String internalKey = rule.getElementsByTagName("internalKey").item(0).getTextContent();
						LOGGER.debug(String.format("Adding rules to map (%s) (%s)", internalKey, key));
						mapCsvRulePattern.put(internalKey, key);
					}
				}
			} catch (ParserConfigurationException | IOException | SAXException e) {
				LOGGER.error(String.format("error reading rules file [%s]", pathToRule), e);
			}
		}
	}

	private void csvRulesMatchingPatterns() {
		int i = 1;
		mapDefaultCsvRulePattern.put("division_by_zero", CSV_MODULE_NAME + "." + i++);
		mapDefaultCsvRulePattern.put("mem_access", CSV_MODULE_NAME + "." + i++);
		mapDefaultCsvRulePattern.put("index_bound", CSV_MODULE_NAME + "." + i++);
		mapDefaultCsvRulePattern.put("shift", CSV_MODULE_NAME + "." + i++);
		mapDefaultCsvRulePattern.put("ptr_comparison", CSV_MODULE_NAME + "." + i++);
		mapDefaultCsvRulePattern.put("differing_blocks", CSV_MODULE_NAME + "." + i++);
		mapDefaultCsvRulePattern.put("separation", CSV_MODULE_NAME + "." + i++);
		mapDefaultCsvRulePattern.put("overlap", CSV_MODULE_NAME + "." + i++);
		mapDefaultCsvRulePattern.put("initialization", CSV_MODULE_NAME + "." + i++);
		mapDefaultCsvRulePattern.put("dangling_pointer", CSV_MODULE_NAME + "." + i++);
		mapDefaultCsvRulePattern.put("is_nan_or_infinite", CSV_MODULE_NAME + "." + i++);
		mapDefaultCsvRulePattern.put("is_nan", CSV_MODULE_NAME + "." + i++);
		mapDefaultCsvRulePattern.put("float_to_int", CSV_MODULE_NAME + "." + i++);
		mapDefaultCsvRulePattern.put("function_pointer", CSV_MODULE_NAME + "." + i++);
		mapDefaultCsvRulePattern.put("initialization_of_union", CSV_MODULE_NAME + "." + i++);
		mapDefaultCsvRulePattern.put("signed_overflow", CSV_MODULE_NAME + "." + i++);
		mapDefaultCsvRulePattern.put("unsigned_overflow", CSV_MODULE_NAME + "." + i++);
		mapDefaultCsvRulePattern.put("signed_downcast", CSV_MODULE_NAME + "." + i++);
		mapDefaultCsvRulePattern.put("unsigned_downcast", CSV_MODULE_NAME + "." + i++);
	}

	private void syntaxeRulesMatchingPatterns() {
		Pattern[] patterns = new Pattern[]{
				Pattern.compile(WORD_KERNEL+WORD_WARNING),
				Pattern.compile(WORD_KERNEL+WORD_WARNING+" dropping duplicate def'n of func"),
				Pattern.compile(WORD_KERNEL+WORD_WARNING+" Variable-sized local"),
				Pattern.compile(WORD_KERNEL+WORD_WARNING+" Clobber list contain \"memory\" argument"),
				Pattern.compile(WORD_KERNEL+WORD_WARNING+" Too many initializers for structure"),
				Pattern.compile(WORD_KERNEL+WORD_WARNING+" merging definitions of enum E using int type"),
				Pattern.compile(WORD_KERNEL+WORD_WARNING+" Calling undeclared function"),
				Pattern.compile(WORD_KERNEL+WORD_WARNING+" Body of function"),
				Pattern.compile(WORD_KERNEL+WORD_WARNING+" Neither code nor specification for function"),
				Pattern.compile(WORD_KERNEL+" imprecise size for variable"),
				Pattern.compile(WORD_KERNEL+WORD_WARNING+" Unspecified sequence with side effect"),
				Pattern.compile(WORD_KERNEL+WORD_WARNING+" Floating-point constant")
		};
		for (int i = 0; i < patterns.length; i++) {
			mapRulePattern.put(SYNTAX_MODULE_NAME+"."+i, patterns[i]);			
		}
	}

	private void valueRulesMatchingPatterns() {
		Pattern[] patterns = new Pattern[]{
				Pattern.compile(WORD_VALUE+WORD_WARNING),
				Pattern.compile(WORD_VALUE+WORD_WARNING+" accessing uninitialized left-value"),
				Pattern.compile(WORD_VALUE+WORD_WARNING+" signed overflow"),
				Pattern.compile(WORD_VALUE+WORD_WARNING+" global initialization of volatile variable"),
				Pattern.compile(WORD_VALUE+WORD_WARNING+" non-finite"),
				Pattern.compile(WORD_VALUE+WORD_WARNING+" initialization of volatile variable"),
				Pattern.compile(WORD_VALUE+WORD_WARNING+" pointer comparison"),
				Pattern.compile(WORD_VALUE+WORD_WARNING+" division by zero"),
				Pattern.compile(WORD_VALUE+WORD_WARNING+" locals"),
				Pattern.compile(WORD_VALUE+WORD_WARNING+" detected recursive call"),
				Pattern.compile(WORD_VALUE+WORD_WARNING+" during initialization of variable"),
				Pattern.compile(WORD_VALUE+WORD_WARNING+" ignoring non-existing function")
		};
		for (int i = 0; i < patterns.length; i++) {
			mapRulePattern.put(VALUE_MODULE_NAME+"."+i, patterns[i]);			
		}
	}

	private static final Charset ENCODING = StandardCharsets.UTF_8;
	
	/**
	 * Parse a XML report file.
	 * 
	 * @param fileReportPath Report file to parse
	 * @return a object services provider {@link ReportInterface} from the
	 *         report file
	 * 
	 * @see XmlReportReader#SAXHandler
	 */
	public ReportInterface parse(Path fileReportPath) {
		LOGGER.info("Parsing Ouput report: " + fileReportPath.getFileName() + " (Beginning)");
		AnalysisProject res = new AnalysisProject();
		int nbLines = 0;

		Matcher metricsMatcher = CyclomaticMetrics.METRICS_PATTERN.matcher("");
		Matcher kernelMatcher = PATTERN_KERNEL.matcher("");
		try (Scanner scanner = new Scanner(fileReportPath, ENCODING.name())) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				nbLines++;
				metricsMatcher.reset(line); // reset the input each time
				if (metricsMatcher.find()) {
					LOGGER.info("Metrics matcher :" + line);

					readGlobalMetrics(res, scanner);
				}
				// Search issues...
				else {
					searchIssues(res, kernelMatcher, line);
				}
			}
		} catch (IOException e) {
			LOGGER.error("Error during result parsing : " + e);
		} 
		LOGGER.debug("Metrics matcher :" + nbLines);
		LOGGER.info("Parsing Ouput report: " + fileReportPath.getFileName() + " (done)");
		return res;
	}

	/**
	 * Parse a CSV report file.
	 *
	 * @param fileReportPath Path to the CSV file.
	 * @return a object services provider {@link ReportInterface} from the
	 *         report file
	 *
	 */
	public List<FramaCError> parseCsv(Path fileReportPath) {
		LOGGER.info("Parsing CSV report: " + fileReportPath.getFileName() + " (Beginning)");

		List<FramaCError> errors = new ArrayList<>();
		int nbLines = 0;

		try (Scanner scanner = new Scanner(fileReportPath, ENCODING.name())) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				LOGGER.debug("CSV analysis line read : " + line);
				// switch the csv header file.
				nbLines++;
				if(nbLines == 1)
					continue;

				String data[] = line .split("\t");
				if (data.length >= 6) {
					String type = (mapCsvRulePattern.size() > 0)
						? mapCsvRulePattern.get(data[4])
						: mapDefaultCsvRulePattern.get(data[4]);

					if(type == null) {
						type = CSV_DEFAULT_TYPE;
					}
					String source = data[0].endsWith("/")
						? data[0] + data[1]
						: data[0] + "/" + data[1];
					String description = data[4] + " " + data[6];
					String lineNumber = data[2];

					FramaCError error = new FramaCError(type, description, source, lineNumber);
					LOGGER.debug("RULE VIOLATION: " + error);
					errors.add(error);
				}
			}
		} catch (IOException e) {
			LOGGER.error("Error during result parsing : " + e);
		}
		LOGGER.info("Parsing CSV report: " + fileReportPath.getFileName() + " (done)");
		return errors;
	}


	private void searchIssues(AnalysisProject res, Matcher kernelMatcher, String line) {
		// Search KERNEL message pattern
		kernelMatcher.reset(line);
		if (kernelMatcher.find()) {

			// Syntax rules violation
			LOGGER.info("Kernel matcher :" + line);
			
			// Global SYNTAXE Rule
			FramaCError err = searchError(line, SYNTAX_MODULE_NAME);
			if(err != null){
				res.addError(err);									
			}
		}
	}

	private FramaCError searchError(String line, String ruleModuleKey) {
		FramaCError err = null;
		String rule0Key = ruleModuleKey+".0";
		Pattern pValue0 = mapRulePattern.get(rule0Key);
		Matcher mValue0 = pValue0.matcher(line);
		if (mValue0.find()) {
			int index = 1;
			boolean ruleFind = false;
			String ruleKey = ruleModuleKey + "." + index;
			Pattern pattern = mapRulePattern.get(ruleKey);
			while (pattern != null && !ruleFind) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					LOGGER.info("Rule = " + ruleKey);
					err = parseError(ruleKey, matcher, line);
					LOGGER.info("Rule violation: "+err);

					ruleFind = true;
				} else {
					index++;
					ruleKey = ruleModuleKey + "." + index;
					pattern = mapRulePattern.get(ruleKey);
				}
			}
			if (!ruleFind) {
				err = parseError(rule0Key, mValue0, line);
				LOGGER.info("Rule = " + rule0Key);
			}

		}
		return err;
	}

	private FramaCError parseError(
			String ruleKey,
			Matcher matcher, 
			String line) {
		String externalRuleKey = ruleKey;
		String description = line.substring(matcher.start());
		
		// Check file and line error: <FILE_PATH>:<LINE>:<PATTERN & DESCRIPTION>
		int filePathEndIndex = line.indexOf(':');
		String filePath = "No file error reported";
		String lineNb = "1";
		
		// The index of the first semi-coloms separator shall be before start of PATTERN
		if(filePathEndIndex > 0 && filePathEndIndex < matcher.start()){
			filePath = line.substring(0, filePathEndIndex);
			int lineNbBeginIndex = filePathEndIndex+1;
			String lineQueue = line.substring(lineNbBeginIndex);
			LOGGER.info("LineQueue="+lineQueue);
			int lineNbEndIndex = lineQueue.indexOf(':');
			// The index of the second semi-coloms separator shall be before start of PATTERN
			if(lineNbEndIndex > 0 && lineNbEndIndex < matcher.start()){
				lineNb = lineQueue.substring(0, lineNbEndIndex);
			}
		}
		
		return new FramaCError(externalRuleKey, description, filePath, lineNb);
	}

	private void readGlobalMetrics(AnalysisProject res, Scanner scanner) {
		// Skip next line
		scanner.nextLine();

		// Read sloc value
		readSloc(res, scanner);

		// Read Decision point value
		readDecisionPoints(res, scanner);

		// Read Global variables value
		readGlobalVariables(res, scanner);

		// Read If statements value
		readIfStatements(res, scanner);

		// Read Loop statements value
		readNumberOfLoops(res, scanner);

		// Read goto statements value
		readNumberOfGotos(res, scanner);

		// Read assignment value
		readNumberOfAssignment(res, scanner);

		// Read exit point value
		readNumberOfExitPoints(res, scanner);

		// Read function value
		readNumberOfFunctions(res, scanner);

		// Read function call value
		readNumberOfCalls(res, scanner);					

		// Read pointer dereferencing value
		readNumberOfDereferencings(res, scanner);

		// Read cyclomatic complexity value
		readCyclomatic(res, scanner);
	}

	private void readCyclomatic(AnalysisProject res, Scanner scanner) {
		Metric<?> metric;
		metric = CyclomaticMetrics.CYCLOMATIC;
		String cyclomatic = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
		if (cyclomatic != null) {
			res.getGlobalMetrics().setCyclomaticComplexity(cyclomatic);
		} else {
			LOGGER.warn(NO_EXPECTED_VALUE_FOR_METRICS + metric.getName());
		}
	}

	private void readNumberOfDereferencings(AnalysisProject res, Scanner scanner) {
		Metric<?> metric;
		metric = CyclomaticMetrics.NUMBER_OF_POINTER_DEREFERENCINGS;
		String pd = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
		if (pd != null) {
			res.getGlobalMetrics().setPointerDereferencing(pd);
		} else {
			LOGGER.warn(NO_EXPECTED_VALUE_FOR_METRICS + metric.getName());
		}
	}

	private void readNumberOfCalls(AnalysisProject res, Scanner scanner) {
		Metric<?> metric;
		metric = CyclomaticMetrics.NUMBER_OF_FUNCTION_CALLS;
		String fc = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
		if (fc != null) {
			res.getGlobalMetrics().setFunctionCall(fc);
		} else {
			LOGGER.warn(NO_EXPECTED_VALUE_FOR_METRICS + metric.getName());
		}
	}

	private void readNumberOfFunctions(AnalysisProject res, Scanner scanner) {
		Metric<?> metric;
		metric = CyclomaticMetrics.NUMBER_OF_FUNCTIONS_DECLARED;
		String fd = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
		if (fd != null) {
			res.getGlobalMetrics().setFunction(fd);
		} else {
			LOGGER.warn(NO_EXPECTED_VALUE_FOR_METRICS + metric.getName());
		}
	}

	private void readNumberOfExitPoints(AnalysisProject res, Scanner scanner) {
		Metric<?> metric;
		metric = CyclomaticMetrics.NUMBER_OF_EXIT_POINT_STATEMENTS;
		String ep = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
		if (ep != null) {
			res.getGlobalMetrics().setExitPoint(ep);
		} else {
			LOGGER.warn(NO_EXPECTED_VALUE_FOR_METRICS + metric.getName());
		}
	}

	private void readNumberOfAssignment(AnalysisProject res, Scanner scanner) {
		Metric<?> metric;
		metric = CyclomaticMetrics.NUMBER_OF_ASSIGNMENT_STATEMENTS;
		String assignment = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
		if (assignment != null) {
			res.getGlobalMetrics().setAssignmentStatements(assignment);
		} else {
			LOGGER.warn(NO_EXPECTED_VALUE_FOR_METRICS + metric.getName());
		}
	}

	private void readNumberOfGotos(AnalysisProject res, Scanner scanner) {
		Metric<?> metric;
		metric = CyclomaticMetrics.NUMBER_OF_GOTO_STATEMENTS;
		String gotoStatements = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
		if (gotoStatements != null) {
			res.getGlobalMetrics().setGotoStatements(gotoStatements);
		} else {
			LOGGER.warn(NO_EXPECTED_VALUE_FOR_METRICS + metric.getName());
		}
	}

	private void readNumberOfLoops(AnalysisProject res, Scanner scanner) {
		Metric<?> metric;
		metric = CyclomaticMetrics.NUMBER_OF_LOOP_STATEMENTS;
		String loopStatements = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
		if (loopStatements != null) {
			res.getGlobalMetrics().setLoopStatements(loopStatements);
		} else {
			LOGGER.warn(NO_EXPECTED_VALUE_FOR_METRICS + metric.getName());
		}
	}

	private void readIfStatements(AnalysisProject res, Scanner scanner) {
		Metric<?> metric;
		metric = CyclomaticMetrics.NUMBER_OF_IF_STATEMENTS;
		String ifStatements = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
		if (ifStatements != null) {
			res.getGlobalMetrics().setIfStatements(ifStatements);
		} else {
			LOGGER.warn(NO_EXPECTED_VALUE_FOR_METRICS + metric.getName());
		}
	}

	private void readGlobalVariables(AnalysisProject res, Scanner scanner) {
		Metric<?> metric;
		metric = CyclomaticMetrics.NUMBER_OF_GLOBAL_VARIABLES;
		String gv = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
		if (gv != null) {
			res.getGlobalMetrics().setGlobalVariables(gv);
		} else {
			LOGGER.warn(NO_EXPECTED_VALUE_FOR_METRICS + metric.getName());
		}
	}

	private void readDecisionPoints(AnalysisProject res, Scanner scanner) {
		Metric<?> metric;
		metric = CyclomaticMetrics.DECISION_POINTS;
		String dp = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
		if (dp != null) {
			res.getGlobalMetrics().setDecisionPoint(dp);
		} else {
			LOGGER.warn(NO_EXPECTED_VALUE_FOR_METRICS + metric.getName());
		}
	}

	private void readSloc(AnalysisProject res, Scanner scanner) {
		Metric<?> metric;
		metric = CyclomaticMetrics.SLOC;
		String sloc = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
		if (sloc != null) {
			res.getGlobalMetrics().setSloc(sloc);
		} else {
			LOGGER.warn(NO_EXPECTED_VALUE_FOR_METRICS + metric.getName());
		}
	}

	private String scanMetricValue(Scanner scanner, Map<String, Pattern> map, Metric<?> metric) {
		String res = null;
		
		// Read Metric value
		String globalMetricsNextLine = scanner.nextLine();
		Matcher slocMatcher = map.get(
				metric.key())
				.matcher(globalMetricsNextLine);
		if(slocMatcher.find()){
			LOGGER.debug("Match :"+slocMatcher.pattern()+" on line: "+globalMetricsNextLine+" res="+globalMetricsNextLine.substring(slocMatcher.end())); 
			res = globalMetricsNextLine.substring(slocMatcher.end());
		}
		return res;
	}

}
