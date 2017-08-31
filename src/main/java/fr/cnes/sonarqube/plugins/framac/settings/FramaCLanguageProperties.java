package fr.cnes.sonarqube.plugins.framac.settings;

import static java.util.Arrays.asList;

import java.util.List;

import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import fr.cnes.sonarqube.plugins.framac.languages.FramaCLanguage;
import fr.cnes.sonarqube.plugins.framac.sensor.FramaCMetricsSensor;

/**
 * Sonar plugin Frama-C properties.
 * 
 * According to a abstract FramaCLanguage definition, this property class define all plugin's properties:
 * 
 * For all project code file : <b>FILE</b>, Frama-C create a report file <b>FILE{@link FramaCLanguageProperties#REPORT_EXT}</b> into the {@link FramaCLanguageProperties#REPORT_SUBDIR} shall be
 * 
 * @author Cyrille FRANCOIS
 *
 */
public class FramaCLanguageProperties {
		
	/** Report sub directory */
	public static final String REPORT_SUBDIR_KEY = "sonar."+FramaCLanguage.KEY+".report.subdir";
	public static final String REPORT_SUBDIR_DEFAULT_VALUE = "oracle";
	/** Report extension */
	public static final String REPORT_OUT_EXT_KEY = "sonar."+FramaCLanguage.KEY+".report.out.ext";
	public static final String REPORT_OUT_EXT_DEFAULT_VALUE = ".res.oracle";
	/** project code file patterns */
	public static final String EXPECTED_REPORT_INPUT_FILE_TYPES_KEY = "sonar."+FramaCLanguage.KEY+".file.suffixes";
	public static final String EXPECTED_REPORT_INPUT_FILE_TYPES_DEFAULT_VALUE = "*.c,*.i";
	public static final String FILE_SUFFIXES_SEPARATOR = ",";

	/**
	 * Plugin properties extensions
	 */
	public static List<PropertyDefinition> getProperties() {
		return asList(PropertyDefinition.builder(REPORT_SUBDIR_KEY)
				.defaultValue(REPORT_SUBDIR_DEFAULT_VALUE).category(FramaCLanguage.NAME)
				.name("Report subdirectory").description("Name of Frama-C output report subdirectory.")
				// .onQualifiers(Qualifiers.PROJECT)
				.build(),
				PropertyDefinition.builder(REPORT_OUT_EXT_KEY)
				.defaultValue(REPORT_OUT_EXT_DEFAULT_VALUE).category(FramaCLanguage.NAME)
				.name("Report file Suffixes").description("The report file have the same name as source file followed by this extension name.")
				// .onQualifiers(Qualifiers.PROJECT)
				.build(),
				PropertyDefinition.builder(EXPECTED_REPORT_INPUT_FILE_TYPES_KEY)
				.defaultValue(EXPECTED_REPORT_INPUT_FILE_TYPES_DEFAULT_VALUE).category(FramaCLanguage.NAME)
				.name("File Suffixes").description("Comma-separated list of suffixes for files to analyze.")
				// .onQualifiers(Qualifiers.PROJECT)
				.build());
	}

}
