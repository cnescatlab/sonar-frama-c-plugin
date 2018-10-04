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

import org.sonar.api.resources.AbstractLanguage;

/**
 * Declared language i-Code as the parent language for Fortran 77, Fortran 90 & Shell.
 */
public class FramaCLanguage extends AbstractLanguage {

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
	 */
	public FramaCLanguage() {
		super(KEY, NAME);
	}

	/**
	 * Returns the list of suffixes which should be associated to this language.
	 *
	 * @return An empty array: workaround for conflicts with sonar-cxx
	 * but the plugin is executed on each analysis.
	 */
	@Override
	public String[] getFileSuffixes() {
		return new String[0];
	}
}