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
package fr.cnes.sonarqube.plugins.framac.report;

import fr.cnes.sonarqube.plugins.framac.rules.FramaCRulesDefinition;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Read expected data from a Frama-C report file.
 * 
 * Scan a Frama-C report file for patterns given by Metrics and Rules and produce measures and issues for each match
 */
public class FramaCReportReader {

	private static final String CSV_MODULE_NAME = "CSV";
	private static final String CSV_DEFAULT_TYPE = CSV_MODULE_NAME + ".0";

	private static final Logger LOGGER = Loggers.get(FramaCReportReader.class);
	
	// For managing regexp related to kernel error analysis
	private static final String GROUP_FILE = "file";
	private static final String GROUP_LINE_NB = "lineNumber";
	private static final String GROUP_DESCRITPION = "description";
	private static final String REGEXP_WARNING = String.format("\\[kernel.*\\] (?<%s>.*):(?<%s>.*): Warning:(?<%s>.*)", GROUP_FILE, GROUP_LINE_NB, GROUP_DESCRITPION);
	private static final String REGEXP_USER_ERROR = String.format("\\[kernel.*\\] (?<%s>.*):(?<%s>.*): User Error:(?<%s>.*)", GROUP_FILE, GROUP_LINE_NB, GROUP_DESCRITPION);
	private List<Pattern> kernelPatterns = new ArrayList<>();


	protected static final Map<String,String> mapDefaultCsvRulePattern = new HashMap<>();
	protected static Map<String, String> mapCsvRulePattern = new HashMap<>();


	public FramaCReportReader() {
		super();
		csvRulesMatchingPatterns();
		initKernelMatchingPattern();
		loadXMLRules();
	}

	/* Initialize the list of regexp  Pattern used to analyse kernel errors */
	private void initKernelMatchingPattern() {
		kernelPatterns.add(Pattern.compile(REGEXP_WARNING));
		kernelPatterns.add(Pattern.compile(REGEXP_USER_ERROR));
	}

	private void loadXMLRules() {
		String pathToRule = FramaCRulesDefinition.PATH_TO_RULES_XML;

		Document xmlRules;
		InputStream inputStream = this.getClass().getResourceAsStream(pathToRule);
		if (inputStream != null) {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				
				// disable external entities
				factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
				factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
				
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
						LOGGER.debug(String.format("Adding definedRules to map (%s) (%s)", internalKey, key));
						mapCsvRulePattern.put(internalKey, key);
					}
				}
			} catch (ParserConfigurationException | IOException | SAXException e) {
				LOGGER.error(String.format("error reading definedRules file [%s]", pathToRule), e);
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
		mapDefaultCsvRulePattern.put("unsigned_downcast", CSV_MODULE_NAME + "." + i);
	}

	private static final Charset ENCODING = StandardCharsets.UTF_8;

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

				String[] data = line .split("\t");
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
					LOGGER.debug("VALUE RULE VIOLATION: " + error);
					errors.add(error);
				}
			}
		} catch (IOException e) {
			LOGGER.error("Error during result parsing : " + e);
		}
		LOGGER.info("Parsing CSV report: " + fileReportPath.getFileName() + " (done)");
		return errors;
	}

	public List<FramaCError> parseOut(Path fileReportPath) {
		LOGGER.info("Parsing out report: " + fileReportPath.getFileName() + " (Beginning)");

		List<FramaCError> errors = new ArrayList<>();

		try (Scanner scanner = new Scanner(fileReportPath, ENCODING.name())) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				FramaCError error = getKernelError(line);
				if (error != null &&
					(error.getDescription() == null	|| error.getDescription().length() == 0)
					&& (scanner.hasNextLine())) {
					// Get the description on the next line
					error.setDescription(scanner.nextLine().trim());
					LOGGER.debug("KERNEL RULE VIOLATION: " + error);
					errors.add(error);
				}
			}
		} catch (IOException e) {
			LOGGER.error("Error during result parsing : " + e);
		}
		LOGGER.info("Parsing out report: " + fileReportPath.getFileName() + " (done)");
		return errors;
	}

	protected FramaCError getKernelError(String line) {
		LOGGER.debug("Kernel Error: Processing line: " + line);
		FramaCError error = null;
		for (Pattern p : kernelPatterns) {
			Matcher matcher = p.matcher(line);
			if (matcher.matches()) {
				LOGGER.debug("Match !!!");
				error = new FramaCError("KERNEL.0",
					matcher.group(GROUP_DESCRITPION).trim(),
					matcher.group(GROUP_FILE),
					matcher.group(GROUP_LINE_NB));
				break;
			}
		}
		return error;
	}
}
