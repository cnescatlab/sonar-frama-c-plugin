package fr.cnes.sonarqube.plugins.framac.report;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sonar.api.measures.Metric;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import fr.cnes.sonarqube.plugins.framac.measures.CyclomaticMetrics;

/**
 * Read expected data from a Frama-C report file.
 * 
 * Scan a Frama-C report file for patterns given by Metrics and Rules and produce measures and issues for each match
 * 
 * @author Cyrille FRANCOIS
 *
 */
public class FramaCReportReader {
	
	private static final Logger LOGGER = Loggers.get(FramaCReportReader.class);
	
	public static final String WORD_KERNEL = "\\Q[kernel]\\E";
	public static final String WORD_VALUE = "\\Q[value]\\E";
	public static final String WORD_WARNING = " warning:";
	
	public static final Pattern PATTERN_KERNEL = Pattern.compile(WORD_KERNEL);
	public static final Pattern PATTERN_VALUE = Pattern.compile(WORD_VALUE);

	public static final Map<String,Pattern> mapRulePattern = new HashMap<String, Pattern>();
	
	public FramaCReportReader() {
		super();
		valueRulesMatchingPatterns();
		syntaxeRulesMatchingPatterns();
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
			mapRulePattern.put("SYNTAXE"+"."+i, patterns[i]);			
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
			mapRulePattern.put("VALUE"+"."+i, patterns[i]);			
		}
	}

	private final static Charset ENCODING = StandardCharsets.UTF_8;
	
	/**
	 * Parse a XML report file.
	 * 
	 * @param fileReportPath
	 * @return a object services provider {@link ReportInterface} from the
	 *         report file
	 * 
	 * @see XmlReportReader#SAXHandler
	 */
	public ReportInterface parse(Path fileReportPath) {
		LOGGER.info("Parsing report: " + fileReportPath.getFileName() + " (Beginning)");
		AnalysisProject res = new AnalysisProject();
		int nbLines = 0;

		Matcher metricsMatcher = CyclomaticMetrics.METRICS_PATTERN.matcher("");
		Matcher kernelMatcher = PATTERN_KERNEL.matcher("");
		Matcher valueMatcher = PATTERN_VALUE.matcher("");
		Scanner scanner = null;
		try {
			scanner = new Scanner(fileReportPath, ENCODING.name());
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

					// Search KERNEL message pattern
					kernelMatcher.reset(line);
					if (kernelMatcher.find()) {

						// Syntax rules violation
						LOGGER.info("Kernel matcher :" + line);
					}
					// Search VALUE message pattern
					else {
						valueMatcher.reset(line);
						if (valueMatcher.find()) {

							// Syntax rules violation
							LOGGER.info("Value matcher :" + line);

							// Global VALUE Rule
							String rule0_Key = "VALUE.0";
							Pattern pValue0 = mapRulePattern.get(rule0_Key);
							Matcher mValue0 = pValue0.matcher(line);
							if (mValue0.find()) {
								int index = 1;
								boolean ruleFind = false;
								String ruleKey = "VALUE." + index;
								Pattern pattern = mapRulePattern.get(ruleKey);
								while (pattern != null && !ruleFind) {
									Matcher matcher = pattern.matcher(line);
									if (matcher.find()) {
										LOGGER.info("Rule = " + ruleKey);
										FramaCError err = parseError(ruleKey,pattern, matcher, line);
										LOGGER.info("Rule violation: "+err);
										res.addError(err);
										ruleFind = true;
									} else {
										index++;
										ruleKey = "VALUE." + index;
										pattern = mapRulePattern.get(ruleKey);
									}
								}
								if (!ruleFind) {
									LOGGER.info("Rule = " + rule0_Key);
								}
							}
						}
					}
				}
			}
		} catch (IOException e) {
			LOGGER.error("Error during result parsing : " + e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		LOGGER.debug("Metrics matcher :" + nbLines);
		LOGGER.info("Parsing report: " + fileReportPath.getFileName() + " (done)");
		// TODO: parser le fichier de rapport de Frama-C
		return res;
		// // New factory
		// SAXParserFactory factory = SAXParserFactory.newInstance();
		// SAXParser parser;
		// SAXHandler handler = new SAXHandler(res);
		// try {
		// parser = factory.newSAXParser();
		// parser.parse(fileReportPath.toFile(), handler);
		// } catch (ParserConfigurationException | SAXException e) {
		// LOGGER.error("Parsing error: "+e.getMessage());
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// LOGGER.error("Report file error: "+e.getMessage());
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// LOGGER.debug("parse("+fileReportPath.getFileName()+")"+handler.getAnalysisProject());
		// return handler.getAnalysisProject();
	}

	private FramaCError parseError(
			String ruleKey, 
			Pattern pattern,
			Matcher matcher, 
			String line) {
		String externalRuleKey = ruleKey;
		String description = line.substring(matcher.start());
		
		// Check file and line error: <FILE_PATH>:<LINE>:<PATTERN & DESCRIPTION>
		int filePathEndIndex = line.indexOf(":");
		String filePath = "No file error reported";
		String lineNb = "1";
		
		// The index of the first semi-coloms separator shall be before start of PATTERN
		if(filePathEndIndex > 0 && filePathEndIndex < matcher.start()){
			filePath = line.substring(0, filePathEndIndex);
			int lineNbBeginIndex = filePathEndIndex+1;
			String lineQueue = line.substring(lineNbBeginIndex);
			LOGGER.info("LineQueue="+lineQueue);
			int lineNbEndIndex = lineQueue.indexOf(":");
			// The index of the second semi-coloms separator shall be before start of PATTERN
			if(lineNbEndIndex > 0 && lineNbEndIndex < matcher.start()){
				lineNb = lineQueue.substring(0, lineNbEndIndex);
			}
		}
		
		FramaCError error = new FramaCError(externalRuleKey, description, filePath, lineNb);

		return error;
	}

	private void readGlobalMetrics(AnalysisProject res, Scanner scanner) {
		// Skip next line
		scanner.nextLine();
		Metric<?> metric = null;

		// Read sloc value
		{
			metric = CyclomaticMetrics.SLOC;
			String sloc = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
			if (sloc != null) {
				res.getGlobalMetrics().setSloc(sloc.toString());
			} else {
				LOGGER.warn("No expected value for metrics " + metric.getName());
			}
		}

		// Read Decision point value
		{
			metric = CyclomaticMetrics.DECISION_POINTS;
			String dp = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
			if (dp != null) {
				res.getGlobalMetrics().setDecisionPoint(dp.toString());
			} else {
				LOGGER.warn("No expected value for metrics " + metric.getName());
			}
		}

		// Read Global variables value
		{
			metric = CyclomaticMetrics.NUMBER_OF_GLOBAL_VARIABLES;
			String gv = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
			if (gv != null) {
				res.getGlobalMetrics().setGlobalVariables(gv.toString());
			} else {
				LOGGER.warn("No expected value for metrics " + metric.getName());
			}
		}

		// Read If statements value
		{
			metric = CyclomaticMetrics.NUMBER_OF_IF_STATEMENTS;
			String ifStatements = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
			if (ifStatements != null) {
				res.getGlobalMetrics().setIfStatements(ifStatements.toString());
			} else {
				LOGGER.warn("No expected value for metrics " + metric.getName());
			}
		}

		// Read Loop statements value
		{
			metric = CyclomaticMetrics.NUMBER_OF_LOOP_STATEMENTS;
			String loopStatements = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
			if (loopStatements != null) {
				res.getGlobalMetrics().setLoopStatements(loopStatements.toString());
			} else {
				LOGGER.warn("No expected value for metrics " + metric.getName());
			}
		}

		// Read goto statements value
		{
			metric = CyclomaticMetrics.NUMBER_OF_GOTO_STATEMENTS;
			String gotoStatements = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
			if (gotoStatements != null) {
				res.getGlobalMetrics().setGotoStatements(gotoStatements.toString());
			} else {
				LOGGER.warn("No expected value for metrics " + metric.getName());
			}
		}

		// Read assignment value
		{
			metric = CyclomaticMetrics.NUMBER_OF_ASSIGNMENT_STATEMENTS;
			String assignment = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
			if (assignment != null) {
				res.getGlobalMetrics().setAssignmentStatements(assignment.toString());
			} else {
				LOGGER.warn("No expected value for metrics " + metric.getName());
			}
		}

		// Read exit point value
		{
			metric = CyclomaticMetrics.NUMBER_OF_EXIT_POINT_STATEMENTS;
			String ep = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
			if (ep != null) {
				res.getGlobalMetrics().setExitPoint(ep.toString());
			} else {
				LOGGER.warn("No expected value for metrics " + metric.getName());
			}
		}

		// Read function value
		{
			metric = CyclomaticMetrics.NUMBER_OF_FUNCTIONS_DECLARED;
			String fd = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
			if (fd != null) {
				res.getGlobalMetrics().setFunction(fd.toString());
			} else {
				LOGGER.warn("No expected value for metrics " + metric.getName());
			}
		}

		// Read function call value
		{
			metric = CyclomaticMetrics.NUMBER_OF_FUNCTION_CALLS;
			String fc = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
			if (fc != null) {
				res.getGlobalMetrics().setFunctionCall(fc.toString());
			} else {
				LOGGER.warn("No expected value for metrics " + metric.getName());
			}
		}					

		// Read pointer dereferencing value
		{
			metric = CyclomaticMetrics.NUMBER_OF_POINTER_DEREFERENCINGS;
			String pd = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
			if (pd != null) {
				res.getGlobalMetrics().setPointerDereferencing(pd.toString());
			} else {
				LOGGER.warn("No expected value for metrics " + metric.getName());
			}
		}

		// Read cyclomatic complexity value
		{
			metric = CyclomaticMetrics.CYCLOMATIC;
			String cyclomatic = scanMetricValue(scanner, CyclomaticMetrics.getMapMetricsPattern(), metric);
			if (cyclomatic != null) {
				res.getGlobalMetrics().setCyclomaticComplexity(cyclomatic.toString());
			} else {
				LOGGER.warn("No expected value for metrics " + metric.getName());
			}
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
