package fr.cnes.sonarqube.plugins.framac.sensor;

import fr.cnes.sonarqube.plugins.framac.exceptions.FramaCException;
import fr.cnes.sonarqube.plugins.framac.languages.FramaCLanguage;
import fr.cnes.sonarqube.plugins.framac.report.FramaCError;
import fr.cnes.sonarqube.plugins.framac.report.FramaCReportReader;
import fr.cnes.sonarqube.plugins.framac.rules.FramaCRulesDefinition;
import fr.cnes.sonarqube.plugins.framac.settings.FramaCPluginProperties;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.config.Configuration;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Executed during sonar-scanner call.
 * Import Frama-C reports into SonarQube.
 */
public class FramaCSensor implements Sensor {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Loggers.get(FramaCSensor.class);

    /**
     * Give information about this sensor.
     *
     * @param sensorDescriptor Descriptor injected to set the sensor.
     */
    @Override
    public void describe(final SensorDescriptor sensorDescriptor) {
        // Prevents sensor to be run during all analysis.
        sensorDescriptor.onlyOnLanguage(FramaCLanguage.KEY);

        // Defines sensor name
        sensorDescriptor.name(getClass().getName());

        // Only main files are concerned, not tests.
        sensorDescriptor.onlyOnFileType(InputFile.Type.MAIN);

        // This sensor is activated only if a rule from the following repo is activated.
        sensorDescriptor.createIssuesForRuleRepositories(FramaCRulesDefinition.getRepositoryKeyForLanguage());
    }

    /**
     * Execute the analysis.
     *
     * @param sensorContext Provide SonarQube services to register results.
     */
    @Override
    public void execute(final SensorContext sensorContext) {

        // Represent the file system used for the analysis.
        final FileSystem fileSystem = sensorContext.fileSystem();
        // Represent the configuration used for the analysis.
        final Configuration config = sensorContext.config();
        // Represent the active rules used for the analysis.
        final ActiveRules activeRules = sensorContext.activeRules();

        // run Frama-C execution
        if(config.getBoolean(FramaCPluginProperties.AUTOLAUNCH_PROP_KEY).orElse(Boolean.getBoolean(FramaCPluginProperties.AUTOLAUNCH_PROP_DEFAULT))) {
            executeFramaC(sensorContext, config);
        }

        // Report files found in file system and corresponding to SQ property.
        final List<String> reportFiles = getReportFiles(config, fileSystem);

        // If exists, unmarshal each xml result file.
        for(final String reportPath : reportFiles) {

            // Analyse CSV report
            final List<FramaCError> errors = analyseReportCsv(reportPath);
            // Retrieve file in a SonarQube format.
            final Map<String, InputFile> scannedFiles = getScannedFiles(fileSystem, errors);

            // Handles issues.
            for (final FramaCError issue : errors) {
                if (isRuleActive(activeRules, issue.getRuleKey())) { // manage active rules
                    saveIssue(sensorContext, scannedFiles, issue);
                } else { // log ignored data
                    LOGGER.info(String.format(
                            "An issue for rule '%s' was detected by Frama-C but this rule is deactivated in current analysis.",
                            issue.getRuleKey()));
                }
            }
        }

    }

    /**
     * Execute Frama-C through a system process.
     *
     * @param sensorContext Context of the sensor.
     * @param config Configuration of SonarQube instance.
     */
    private void executeFramaC(final SensorContext sensorContext, final Configuration config) {
        LOGGER.info("Frama-C auto-launch enabled.");
        final String executable = config.get(FramaCPluginProperties.FRAMAC_PATH_KEY).orElse(FramaCPluginProperties.FRAMAC_PATH_DEFAULT);
        final String[] files = sensorContext.fileSystem().baseDir().list();
        final String outputFile = config.get(FramaCPluginProperties.REPORT_PATH_KEY).orElse(FramaCPluginProperties.REPORT_PATH_DEFAULT);
        final String outputPath = Paths.get(sensorContext.fileSystem().baseDir().toString(),outputFile).toString();
        final String outputOption = "-o";
        final String command = String.join(" ", executable, String.join(" ",files), outputOption, outputPath);

        LOGGER.info("Running Frama-C and generating results to "+ outputPath);
        try {
            final Process framac =  Runtime.getRuntime().exec(command);
            int success = framac.waitFor();
            if(0!=success){
                final String message = String.format("Frama-C auto-launch analysis failed with exit code %d.",success);
                throw new FramaCException(message);
            }
            LOGGER.info("Auto-launch successfully executed Frama-C.");
        } catch (InterruptedException | IOException | FramaCException e) {
            LOGGER.error(e.getMessage(), e);
            sensorContext.newAnalysisError().message(e.getMessage()).save();
        }
    }

    /**
     * This method save an issue into the SonarQube service.
     *
     * @param context A SensorContext to reach SonarQube services.
     * @param files Map containing files in SQ format.
     * @param issue A FramaCError with the convenient format for Frama-C.
     */
    static void saveIssue(final SensorContext context, final Map<String, InputFile> files, final FramaCError issue) {

        // Retrieve the file containing the issue.
        final InputFile inputFile = files.getOrDefault(issue.getFilePath(), null);

        if(inputFile!=null) {
            // Retrieve the ruleKey if it exists.
            final RuleKey ruleKey = RuleKey.of(FramaCRulesDefinition.getRepositoryKeyForLanguage(),
                    issue.getRuleKey());

            // Create a new issue for SonarQube, but it must be saved using NewIssue.save().
            final NewIssue newIssue = context.newIssue();
            // Create a new location for this issue.
            final NewIssueLocation newIssueLocation = newIssue.newLocation();

            // Calculate the line number of the issue (must be between 1 and max, otherwise 1).
            int issueLine = Integer.parseInt(issue.getLine());
            issueLine = issueLine > 0 && issueLine <= inputFile.lines() ? issueLine : 1;

            // Set trivial issue's attributes from AnalysisRule fields.
            newIssueLocation.on(inputFile);
            newIssueLocation.at(inputFile.selectLine(issueLine));
            newIssueLocation.message(issue.getDescription());
            newIssue.forRule(ruleKey);
            newIssue.at(newIssueLocation);
            newIssue.save();
        } else {
            LOGGER.warn(String.format(
                    "Issue '%s' on file '%s' has not been saved because source file was not found.",
                    issue.getRuleKey(), issue.getFilePath()
            ));
        }

    }

    /**
     * Construct a map with all found source files.
     *
     * @param fileSystem The file system on which the analysis is running.
     * @param errors The Frama-C report content.
     * @return A possibly empty Map of InputFile.
     */
    private Map<String, InputFile> getScannedFiles(final FileSystem fileSystem, final List<FramaCError> errors) {
        // Contains the result to be returned.
        final Map<String, InputFile> result = new HashMap<>();

        // Looks for each file in file system, print an error if not found.
        for(final FramaCError file : errors) {
            // Checks if the file system contains a file with corresponding path (relative or absolute).
            FilePredicate predicate = fileSystem.predicates().hasPath(file.getFilePath());
            InputFile inputFile = fileSystem.inputFile(predicate);
            if(inputFile!=null) {
                result.put(file.getFilePath(), inputFile);
            } else {
                LOGGER.warn(String.format(
                        "The source file '%s' was not found.",
                        file.getFilePath()
                ));
            }
        }

        return result;
    }

    /**
     * Returns a list of processable result file's path.
     *
     * @param config Configuration of the analysis where properties are put.
     * @param fileSystem The current file system.
     * @return Return a list of path 'findable' in the file system.
     */
    private List<String> getReportFiles(final Configuration config, final FileSystem fileSystem) {
        // Contains the result to be returned.
        final List<String> result = new ArrayList<>();

        // Retrieves the non-verified path list from the SonarQube property.
        final String[] pathArray = config.getStringArray(FramaCPluginProperties.REPORT_PATH_KEY);

        // Check if each path is known by the file system and add it to the processable path list,
        // otherwise print a warning and ignore this result file.
        for(String path : pathArray) {
            final File file = new File(fileSystem.baseDir(), path);
            if(file.exists() && file.isFile()) {
                result.add(path);
                LOGGER.info(String.format("Results file %s has been found and will be processed.", path));
            } else {
                LOGGER.warn(String.format("Results file %s has not been found and wont be processed.", path));
            }
        }

        return result;
    }

    /**
     * Check if a rule is activated in current analysis.
     *
     * @param activeRules Set of active rules during an analysis.
     * @param rule Key (Frama-C) of the rule to check.
     * @return True if the rule is active and false if not or not exists.
     */
    private boolean isRuleActive(final ActiveRules activeRules, final String rule) {
        final RuleKey ruleKey = RuleKey.of(FramaCRulesDefinition.getRepositoryKeyForLanguage(), rule);
        return activeRules.find(ruleKey)!=null;
    }

    /**
     *  Analyze a report file provided by the external tool Frama-C.
     *  Check the report file integrity (exist, not empty and readable).
     *
     * @param resultFile File containing issues in csv format.
     */
    private List<FramaCError> analyseReportCsv(final String resultFile) {

        List<FramaCError> errors = null;

        if (null != resultFile) {
            final Path path = Paths.get(resultFile);
            try (FileChannel reportFile = FileChannel.open(path)) {
                FramaCReportReader framaCReportReader = new FramaCReportReader();
                errors = framaCReportReader.parseCsv(path);
            } catch (IOException e) {
                final String message = String.format("Unexpected error on report file: %s", resultFile);
                LOGGER.warn(message);
            }
        } else {
            final String message = String.format("Unexpected error on report file: %s", resultFile);
            LOGGER.warn(message);
        }

        return errors;
    }
}
