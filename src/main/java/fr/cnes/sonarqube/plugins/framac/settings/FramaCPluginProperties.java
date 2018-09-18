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
package fr.cnes.sonarqube.plugins.framac.settings;

import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import java.util.Arrays;
import java.util.List;

/**
 * Define all SonarQube properties provided by this plugin.
 */
public class FramaCPluginProperties {

	/**
	 * Frama-C default location's path value.
	 */
	public static final String FRAMAC_PATH_DEFAULT = "${HOME}/frama-c/frama-c";

	/**
	 * Prefix used by all properties of this plugin.
	 **/
	private static final String PROPERTIES_PREFIX = "sonar.framac.";

	/**
	 * i-Code name.
	 **/
	public static final String FRAMAC_NAME = "Frama-C";

	// project code file patterns
	/**
	 * Key for the Shell suffix property
	 **/
	public static final String SUFFIX_KEY = PROPERTIES_PREFIX + "file.suffixes";
	/**
	 * Default value for the code suffix property
	 **/
	public static final String SUFFIX_DEFAULT = ".c,.cc,.i,.h,.C,.CC,.I,.H";
	/**
	 * Name for the code suffix property
	 **/
	public static final String SUFFIX_NAME = "File Suffixes";
	/**
	 * Description for the code suffix property
	 **/
	public static final String SUFFIX_DESC = "List of suffixes for C files to analyze.";

	// Reports path
	/**
	 * Key for the report path property
	 **/
	public static final String REPORT_PATH_KEY = PROPERTIES_PREFIX + "reports.path";
	/**
	 * Name for the report path property
	 **/
	public static final String REPORT_PATH_NAME = "Report files";
	/**
	 * Description for the report path property
	 **/
	public static final String REPORT_PATH_DESC = "Path to the Frama-C reports. Multiple path can be provided.";
	/**
	 * Default value for the report path property
	 **/
	public static final String REPORT_PATH_DEFAULT = "result.res";
	/**
	 * Frama-C launching mode key
	 */
	public static final String AUTOLAUNCH_PROP_KEY = PROPERTIES_PREFIX + "launch";
	/**
	 * Frama-C launching mode default value
	 */
	public static final String AUTOLAUNCH_PROP_DEFAULT = "false";
	/**
	 * Launching mode name
	 */
	public static final String AUTOLAUNCH_PROP_NAME = "Frama-C auto-launch";
	/**
	 * Launching mode description
	 */
	public static final String AUTOLAUNCH_PROP_DESC = "Auto-launch Frama-C on analysis using indicated location.";
	/**
	 * Frama-C location's path key
	 */
	public static final String FRAMAC_PATH_KEY = PROPERTIES_PREFIX + "path";
	/**
	 * i-Code CNES location's path key
	 */
	public static final String FRAMAC_PATH_NAME = "Frama-C location";
	/**
	 * i-Code CNES location's path key
	 */
	public static final String FRAMAC_PATH_DESC = "Define Frama-C executable path to auto-launch it on analysis.";

	/**
	 * Private constructor because it is a utility class.
	 */
	private FramaCPluginProperties() {
		super();
	}

	/**
	 * Plugin properties extensions.
	 *
	 * @return The list of built properties.
	 */
	public static List<PropertyDefinition> getProperties() {
		return Arrays.asList(
				PropertyDefinition.builder(AUTOLAUNCH_PROP_KEY)
						.defaultValue(AUTOLAUNCH_PROP_DEFAULT)
						.category(FRAMAC_NAME)
						.name(AUTOLAUNCH_PROP_NAME)
						.description(AUTOLAUNCH_PROP_DESC)
						.type(PropertyType.BOOLEAN)
						.onQualifiers(Qualifiers.PROJECT)
						.build()
				,
				PropertyDefinition.builder(FRAMAC_PATH_KEY)
						.defaultValue(FRAMAC_PATH_DEFAULT)
						.category(FRAMAC_NAME)
						.name(FRAMAC_PATH_NAME)
						.description(FRAMAC_PATH_DESC)
						.onQualifiers(Qualifiers.PROJECT)
						.build()
				,
				PropertyDefinition.builder(SUFFIX_KEY).multiValues(true)
						.defaultValue(SUFFIX_DEFAULT).category(FRAMAC_NAME)
						.name(SUFFIX_NAME).description(SUFFIX_DESC)
						.onQualifiers(Qualifiers.PROJECT)
						.build()
				,
				PropertyDefinition.builder(REPORT_PATH_KEY).multiValues(true)
						.defaultValue(REPORT_PATH_DEFAULT).category(FRAMAC_NAME)
						.name(REPORT_PATH_NAME).description(REPORT_PATH_DESC)
						.onQualifiers(Qualifiers.PROJECT)
						.build());
	}

}
