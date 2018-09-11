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
package fr.cnes.sonarqube.plugins.framac.sensor;

import fr.cnes.sonarqube.plugins.framac.measures.CyclomaticMetrics;
import fr.cnes.sonarqube.plugins.framac.measures.FramaCMetrics;
import fr.cnes.sonarqube.plugins.framac.report.*;
import fr.cnes.sonarqube.plugins.framac.rules.FramaCRulesDefinition;
import fr.cnes.sonarqube.plugins.framac.settings.FramaCLanguageProperties;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * Scan Frama-C report file.
 * For all project code file : <b>FILE</b>, Frama-C create a report
 * file <b>FILE{@link FramaCMetricsSensor#REPORT_EXT}</b> into
 * the {@link FramaCMetricsSensor#REPORT_SUBDIR}
 */
public class FramaCMetricsSensor implements Sensor {
	
	private static final Logger LOGGER = Loggers.get(FramaCMetricsSensor.class);
	
	String expectedReportInputFileTypes = null;

	String reportOutExt = null;

	String reportCsvExt = null;

	String reportSubdir = null;
	
	FramaCReportReader framaCReportReader = null;

	// For reporting analyse of report file
	StringBuilder warningMsgs = new StringBuilder();
	int nbWarningMsgs = 0;
	StringBuilder errorMsgs = new StringBuilder();
	int nbErrorMsgs = 0;

	@Override
	public void describe(SensorDescriptor descriptor) {
		descriptor.name(getClass().getName());
	}
	
	@Override
	public void execute(SensorContext context) {
		LOGGER.info("FramaCSensor is running...");
		FileSystem fs = context.fileSystem();
		LOGGER.info("FramaCSensor : file system base dir = " + fs.baseDir());
		FilePredicates p = fs.predicates();
		LOGGER.info("FramaCSensor : file system base dir = " + fs.hasFiles(p.all()));
		
		readPluginSettings(context);
		
		// Only "main" files, but not "tests"
		String[] aMatchingPatterns = matchingPatterns();
		Iterable<InputFile> filesC = fs.inputFiles(fs.predicates().matchesPathPatterns(aMatchingPatterns));
		for (InputFile file : filesC) {
			LOGGER.debug("FramaCSensor : current input file = " + file.absolutePath());

			// Check for report out
			String outputFileRelativePathNameReportOut = outReportFileName(file);

			// Check for CSV report
			String csvFileRelativePathNameReportOut = csvReportFileName(file);

			// Analyse report out
			ReportInterface report = analyseReportOut(context, file, outputFileRelativePathNameReportOut);

			// Analyse CSV report
			List<FramaCError> errors = analyseReportCsv(context, file, csvFileRelativePathNameReportOut);

			// Add a Frama-C report warning
			computeWarnings(context, file, warningMsgs, nbWarningMsgs);
			// Add a Frama-C report error
			computeErrors(context, file, errorMsgs, nbErrorMsgs);

			if(report != null){
				parseReportMeasures(context, file, report);
				parseReportIssues(context, file, errors);
			}
		}
		LOGGER.info("FramaCSensor done!");
	}

	void readPluginSettings(SensorContext context) {
		// Read Plugin settings
		expectedReportInputFileTypes = String.valueOf(context.config().get(FramaCLanguageProperties.EXPECTED_REPORT_INPUT_FILE_TYPES_KEY));
		reportOutExt = String.valueOf(context.config().get(FramaCLanguageProperties.REPORT_OUT_EXT_KEY));
		reportCsvExt = String.valueOf(context.config().get(FramaCLanguageProperties.REPORT_CSV_EXT_KEY));
		reportSubdir = String.valueOf(context.config().get(FramaCLanguageProperties.REPORT_SUBDIR_KEY));
	}

	/**
	 * @return all expected file code patterns
	 */
	private String[] matchingPatterns() {
		String patternSeparator = FramaCLanguageProperties.FILE_SUFFIXES_SEPARATOR;
		return expectedReportInputFileTypes.trim().split(patternSeparator);
	}
	
	/**
	 * @param file input code file
	 * @return relative report file for this input code file
	 */
	private String outReportFileName(InputFile file) {		
		return relativeReportFileName(file, reportOutExt);
	}

	/**
	 * @param file input code file
	 * @return relative CSV report file for this input code file
	 */
	private String csvReportFileName(InputFile file) {
		return relativeReportFileName(file, reportCsvExt);
	}

	/**
	 * @param file input code file
	 * @param extension report file extension
	 * @return relative report file for this input code file
	 */
	private String relativeReportFileName(InputFile file, String extension) {
		String separator = File.separator;
		@SuppressWarnings("deprecation")
		String name = file.file().getName();
		int extensionPoint = name.lastIndexOf('.');
		return reportSubdir+separator+name.substring(0,extensionPoint)+extension;
	}

	/**
	 *  Analyze a report file provided by the external tool ICode.
	 *  Check the report file integrity (exist, not empty and readable)
	 *  
	 * @param context Sonar sensor context
	 * @param file input code file 
	 * @param fileRelativePathNameReportOut name of the expected report file for this input code file
	 */
	private ReportInterface analyseReportOut(
			SensorContext context, 
			InputFile file, 
			String fileRelativePathNameReportOut) {
		ReportInterface report = null;

		LOGGER.debug("file.absolutePath():"+file.absolutePath());
		LOGGER.debug("Paths.get(file.absolutePath()).getParent():"+Paths.get(file.absolutePath()).getParent());
		LOGGER.debug("fileRelativePathNameReportOut:"+fileRelativePathNameReportOut);
		Path fileReportPath = Paths.get(file.absolutePath()).getParent().resolve(fileRelativePathNameReportOut);
	    if(existReportFile(fileReportPath)){
  	    	  
			try (FileChannel reportFile = FileChannel.open(fileReportPath)){
				framaCReportReader = new FramaCReportReader();
				report = framaCReportReader.parse(fileReportPath);
			    long reportFileSize = reportFile.size();
			    if(reportFileSize == 0){
			    	errorMsgs.append("Empty report file : "+fileRelativePathNameReportOut);
			    	nbErrorMsgs++;
			    }
			    if(report == null){
			    	errorMsgs.append("Report file : "+fileRelativePathNameReportOut+" cannot be analysed !");
			    	nbErrorMsgs++;
			    }
			} catch (IOException e) {
				errorMsgs.append("Unexpected error report file for : "+fileRelativePathNameReportOut);
		    	nbErrorMsgs++;
			}
	    }
	    else{
	    	errorMsgs.append("No report file for : "+fileRelativePathNameReportOut);
	    	nbErrorMsgs++;
	    }
	    return report;
	}

	/**
	 *  Analyze a report file provided by the external tool ICode.
	 *  Check the report file integrity (exist, not empty and readable)
	 *
	 * @param context Sonar sensor context
	 * @param file input code file
	 * @param fileRelativePathNameReportOut name of the expected report file for this input code file
	 */
	private List<FramaCError> analyseReportCsv(
		SensorContext context,
		InputFile file,
		String fileRelativePathNameReportOut) {

		LOGGER.debug("file.absolutePath():"+file.absolutePath());
		LOGGER.debug("Paths.get(file.absolutePath()).getParent():"+Paths.get(file.absolutePath()).getParent());
		LOGGER.debug("fileRelativePathNameReportOut:"+fileRelativePathNameReportOut);
		List<FramaCError> errors = null;
		Path fileReportPath = Paths.get(file.absolutePath()).getParent().resolve(fileRelativePathNameReportOut);
		if(existReportFile(fileReportPath)){

			try (FileChannel reportFile = FileChannel.open(fileReportPath)){
				framaCReportReader = new FramaCReportReader();
				errors = framaCReportReader.parseCsv(fileReportPath);
			} catch (IOException e) {
				errorMsgs.append("Unexpected error report file for : "+fileRelativePathNameReportOut);
				nbErrorMsgs++;
			}
		}
		else{
			errorMsgs.append("No report file for : "+fileRelativePathNameReportOut);
			nbErrorMsgs++;
		}
		return errors;
	}

	final void computeErrors(SensorContext context, InputFile file, StringBuilder errorMsgs, int nbErrorMsgs) {
		if(nbErrorMsgs>0){
		      context.<String>newMeasure()
		        .forMetric(FramaCMetrics.REPORT_FILES_ERROR)
		        .on(file)
		        .withValue(errorMsgs.toString())
		        .save();	    	  			
		      context.<Integer>newMeasure()
		        .forMetric(FramaCMetrics.NUMBER_OF_ERRORS)
		        .on(file)
		        .withValue(nbErrorMsgs)
		        .save();	    	  			
		}
	}

	final void computeWarnings(SensorContext context, InputFile file, StringBuilder warningMsgs, int nbWarningMsgs) {
		if(nbWarningMsgs>0){
		      context.<String>newMeasure()
		        .forMetric(FramaCMetrics.REPORT_FILES_WARNING)
		        .on(file)
		        .withValue(warningMsgs.toString())
		        .save();	    	  			
		      context.<Integer>newMeasure()
		        .forMetric(FramaCMetrics.NUMBER_OF_WARNINGS)
		        .on(file)
		        .withValue(nbWarningMsgs)
		        .save();	    	  			
		}
	}

	/**
	 * Parse all measures from a valid report file. Measures shall be defined by
	 * Metrics {@link ICodeMetric} Only one measure by file and Metrics Measures
	 * by project are computed by {@link MeasureComputer#compute}
	 * 
	 * @param context
	 *            Sonar sensor context
	 * @param file
	 *            input code file
	 * @param report
	 *            report file analyzed
	 * 
	 * @see XmlReportReader#parse
	 */
	private void parseReportMeasures(SensorContext context, InputFile file, ReportInterface report) {
		LOGGER.info("Parse and store report measures (doing...)");
		// Add metrics results
		ReportModuleRuleInterface reportModuleRuleInterface = report.getModuleCyclomaticMeasure();
		double cyclomaticValueSum = 0;
		double cyclomaticValueMin = Double.MAX_VALUE;
		double cyclomaticValueMax = 0;
		double locValueSum = 0;
		double locValueMin = Double.MAX_VALUE;
		double locValueMax = 0;
		double currentCyclomaticValue = 0;
		double currentLocValue = 0;

		// Create module measures (from each function measures provided by
		// ICode)
		if (reportModuleRuleInterface != null) {

			try {
				currentCyclomaticValue = Double.valueOf(reportModuleRuleInterface.getComplexity());
				currentLocValue = Double.valueOf(reportModuleRuleInterface.getLoc());

				// Sum all elements values for mean computation
				cyclomaticValueSum += currentCyclomaticValue;
				locValueSum += currentLocValue;

				// Search maximum
				if (currentCyclomaticValue > cyclomaticValueMax) {
					cyclomaticValueMax = currentCyclomaticValue;
				}
				if (currentLocValue > locValueMax) {
					locValueMax = currentLocValue;
				}

				// Search minimum
				if (currentCyclomaticValue < cyclomaticValueMin) {
					cyclomaticValueMin = currentCyclomaticValue;
				}
				if (currentLocValue < locValueMin) {
					locValueMin = currentLocValue;
				}
			} catch (Exception e) {
				LOGGER.error("No readable metrics measure: " + reportModuleRuleInterface.getComplexity());
				LOGGER.error("No readable metrics measure: " + reportModuleRuleInterface.getLoc());
			}
		}

		// Create measure for this file
		double cyclomaticValueMean = cyclomaticValueSum;
		double locValueMean = locValueSum;

		storeCyclomaticMeasures(context, file, cyclomaticValueMin, cyclomaticValueMax, cyclomaticValueMean,
				currentCyclomaticValue);
		storeLocMeasures(context, file, locValueMin, locValueMax, locValueMean, currentLocValue);
		LOGGER.info("Parse and store report measures (done)");
	}
	
	/**
	 * Store measures from a valid report file into Frama-C Metrics.
	 * 
	 * @param context Sonar sensor context
	 * @param file input code file 
	 * @param cyclomaticValueMin minimum
	 * @param cyclomaticValueMax maximum
	 * @param cyclomaticValueMean mean
	 * @param value cyclomatic complexity value
	 * 
	 * @see ICodeMetrics
	 */
	private void storeCyclomaticMeasures(SensorContext context, InputFile file, double cyclomaticValueMin,
			double cyclomaticValueMax, double cyclomaticValueMean, double value) {
		// Store module CYCLOMATIC, MEAN, MIN, MAX
		context.<Integer>newMeasure()
		.forMetric(CyclomaticMetrics.CYCLOMATIC)
		.on(file)
		.withValue(Integer.valueOf((int)value))
		.save();
		context.<Integer>newMeasure()
		.forMetric(CyclomaticMetrics.CYCLOMATIC_MAX)
		.on(file)
		.withValue(Integer.valueOf((int)cyclomaticValueMax))
		.save();
		context.<Integer>newMeasure()
		.forMetric(CyclomaticMetrics.CYCLOMATIC_MIN)
		.on(file)
		.withValue(Integer.valueOf((int)cyclomaticValueMin))
		.save();
		context.<Double>newMeasure()
		.forMetric(CyclomaticMetrics.CYCLOMATIC_MEAN)
		.on(file)
		.withValue(Double.valueOf(cyclomaticValueMean))
		.save();
	}
	/**
	 * Store measures from a valid report file into Frama-C Metrics.
	 * 
	 * @param context Sonar sensor context
	 * @param file input code file 
	 * @param locValueMin minimum
	 * @param locValueMax maximum
	 * @param locValueMean mean
	 * @param value loc complexity value
	 * 
	 * @see ICodeMetrics
	 */
	private void storeLocMeasures(SensorContext context, InputFile file, double locValueMin,
			double locValueMax, double locValueMean, double value) {
		// Store module loc, MEAN, MIN, MAX
		context.<Integer>newMeasure()
		.forMetric(CyclomaticMetrics.SLOC)
		.on(file)
		.withValue(Integer.valueOf((int)value))
		.save();
		context.<Integer>newMeasure()
		.forMetric(CyclomaticMetrics.SLOC_MAX)
		.on(file)
		.withValue(Integer.valueOf((int)locValueMax))
		.save();
		context.<Integer>newMeasure()
		.forMetric(CyclomaticMetrics.SLOC_MIN)
		.on(file)
		.withValue(Integer.valueOf((int)locValueMin))
		.save();
		context.<Double>newMeasure()
		.forMetric(CyclomaticMetrics.SLOC_MEAN)
		.on(file)
		.withValue(Double.valueOf(locValueMean))
		.save();
	}
		
	private void parseReportIssues(SensorContext context, InputFile file, List<? extends ErrorInterface> errors) {
		LOGGER.info("Parse and store report issues (doing...)");
		
		// Create issues for this file
		if(errors != null){
			InputFile inputFile = file;
			for (ErrorInterface error : errors) {
				String lineString = error.getLineDescriptor();
				String message = error.getDescription();
				String externalRuleKey = error.getRuleKey();
				saveIssue(context, inputFile, lineString, externalRuleKey , message);
			}
		}
		LOGGER.info("Parse and store report issues (done)");
	}

	/**
	 * Format line number according to the sonar expected format for issue
	 * 
	 * @param line string value of a line
	 * @param maxLine file line numbers
	 * @return Sonar complaint fine number (strictly positive)
	 */
	static final int getLineAsInt(String line, int maxLine) {
	    int lineNr = 0;
	    if (line != null) {
	      try {
	        lineNr = Integer.parseInt(line);
	        if (lineNr < 1) {
	          lineNr = 1;
	        } else if (lineNr > maxLine) {
	        	lineNr = maxLine;
	        }
	      } catch (java.lang.NumberFormatException nfe) {
	        LOGGER.warn("Skipping invalid line number: {}", line);
	        lineNr = -1;
	      }
	    }
	    return lineNr;
	}
	
	/**
	 * Check a expected report file.
	 * 
	 * @param fileReportPath
	 * @return true if the report file exist
	 */
	private static boolean existReportFile(Path fileReportPath) {
		boolean res=false;
		LOGGER.debug("existFile ?:"+fileReportPath.toAbsolutePath());
		res=fileReportPath.toFile().exists();
		return res;
	}

	  private void saveIssue(final SensorContext context, final InputFile inputFile, String lineString, final String externalRuleKey, final String message) {
		    RuleKey ruleKey = RuleKey.of(FramaCRulesDefinition.getRepositoryKeyForLanguage(), externalRuleKey);

		    LOGGER.info("externalRuleKey: "+externalRuleKey);
		    LOGGER.info("Repo: "+FramaCRulesDefinition.getRepositoryKeyForLanguage());
		    LOGGER.info("RuleKey: "+ruleKey);
		    NewIssue newIssue = context.newIssue()
		      .forRule(ruleKey);

		    NewIssueLocation primaryLocation = newIssue.newLocation()
		      .on(inputFile)
		      .message(message);
		    
		    int maxLine = inputFile.lines();
		    int iLine = getLineAsInt(lineString, maxLine);
		    if (iLine > 0) {
		      primaryLocation.at(inputFile.selectLine(iLine));
		    }
		    newIssue.at(primaryLocation);

		    newIssue.save();
		  }
}
