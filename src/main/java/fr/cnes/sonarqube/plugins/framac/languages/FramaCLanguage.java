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
package fr.cnes.sonarqube.plugins.framac.languages;

import fr.cnes.sonarqube.plugins.framac.settings.FramaCPluginProperties;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;

import java.util.ArrayList;
import java.util.List;

/**
 * Declared language i-Code as the parent language for Fortran 77, Fortran 90 & Shell.
 *
 * @author lequal
 */
public class FramaCLanguage extends AbstractLanguage {

	/**
	 * Injected SonarQube configuration.
	 */
	private final Configuration configuration;

	/**
	 * Name of the language.
	 */
	public static final String NAME = "Frama-C";
	/**
	 * Key of the language.
	 */
	public static final String KEY = "framac";

	/**
	 * Frama-C extension for Frama-C specific properties, Metrics and Rules.
	 *
	 * @param configuration Inject SonarQube configuration into this extension.
	 */
	public FramaCLanguage(final Configuration configuration) {
		super(KEY, NAME);
		this.configuration = configuration;
	}

	/**
	 * Returns the list of suffixes which should be associated to this language.
	 *
	 * @return A strings' array with file's suffixes.
	 */
	@Override
	public String[] getFileSuffixes() {
		String[] suffixes = filterEmptyStrings(configuration.getStringArray(getSuffixKey()));
		if (suffixes.length == 0) {
			suffixes = getDefaultSuffixes().split(",");
		}
		return suffixes;
	}

	/**
	 * Return the ey corresponding to the property with suffixes.
	 *
	 * @return A String with the key.
	 */
	public String getSuffixKey() {
		return FramaCPluginProperties.SUFFIX_KEY;
	}

	/**
	 * Return default suffixes for the language.
	 *
	 * @return A String containing a coma-separated list.
	 */
	public String getDefaultSuffixes() {
		return FramaCPluginProperties.SUFFIX_DEFAULT;
	}

	/**
	 * Delete all empty string values into a input String array.
	 *
	 * @param stringArray Input String array.
	 *
	 * @return Output String array without empty string values.
	 */
	private static String[] filterEmptyStrings(String[] stringArray) {
		List<String> nonEmptyStrings = new ArrayList<>();
		for (String string : stringArray) {
			if (StringUtils.isNotBlank(string.trim())) {
				nonEmptyStrings.add(string.trim());
			}
		}
		return nonEmptyStrings.toArray(new String[nonEmptyStrings.size()]);
	}

	/**
	 * Assert obj is the same object as this.
	 *
	 * @param obj Object to compare with this.
	 * @return True if obj is this.
	 */
	@Override
	public boolean equals(Object obj) {
		return obj==this;
	}

	/**
	 * Override hashcode because equals is overridden.
	 *
	 * @return An integer hashcode.
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}