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
package fr.cnes.sonarqube.plugins.framac.settings;

import static java.util.Arrays.asList;

import java.util.List;

import org.sonar.api.config.PropertyDefinition;

import fr.cnes.sonarqube.plugins.framac.languages.FramaCLanguage;

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
		
	private static final String SONAR_DOT = "sonar.";
	/** Report sub directory */

	public static final String REPORT_SUBDIR_KEY = SONAR_DOT+FramaCLanguage.KEY+".report.subdir";
	public static final String REPORT_SUBDIR_DEFAULT_VALUE = "frama-c-results";
	/** Report extension */
	public static final String REPORT_OUT_EXT_KEY = SONAR_DOT+FramaCLanguage.KEY+".report.out.ext";
	public static final String REPORT_OUT_EXT_DEFAULT_VALUE = ".res.oracle";
	/** CSV Report extension */
	public static final String REPORT_CSV_EXT_KEY = SONAR_DOT+FramaCLanguage.KEY+".report.csv.ext";
	public static final String REPORT_CSV_EXT_DEFAULT_VALUE = ".res.csv";
	/** project code file patterns */
	public static final String EXPECTED_REPORT_INPUT_FILE_TYPES_KEY = SONAR_DOT+FramaCLanguage.KEY+".file.suffixes";
	public static final String EXPECTED_REPORT_INPUT_FILE_TYPES_DEFAULT_VALUE = "*.c,*.i";
	public static final String FILE_SUFFIXES_SEPARATOR = ",";

	private FramaCLanguageProperties() {
		throw new IllegalStateException("Utility class");
	}
	
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
			    PropertyDefinition.builder(REPORT_CSV_EXT_KEY)
				.defaultValue(REPORT_CSV_EXT_DEFAULT_VALUE).category(FramaCLanguage.NAME)
				.name("CSV Report file Suffixes").description("The report file have the same name as source file followed by this extension name.")
				// .onQualifiers(Qualifiers.PROJECT)
				.build(),
				PropertyDefinition.builder(EXPECTED_REPORT_INPUT_FILE_TYPES_KEY)
				.defaultValue(EXPECTED_REPORT_INPUT_FILE_TYPES_DEFAULT_VALUE).category(FramaCLanguage.NAME)
				.name("File Suffixes").description("Comma-separated list of suffixes for files to analyze.")
				// .onQualifiers(Qualifiers.PROJECT)
				.build());
	}

}
