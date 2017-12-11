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
package fr.cnes.sonarqube.plugins.framac.languages;

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.utils.ValidationMessages;

/**
 * Default Quality profile for the projects having files of language "foo"
 */
public final class FramaCQualityProfile extends ProfileDefinition {

  public static final String FRAMA_C_RULES_PROFILE_NAME = "FramaC Rules";

@Override
  public RulesProfile createProfile(ValidationMessages validation) {
    return RulesProfile.create(FRAMA_C_RULES_PROFILE_NAME, FramaCLanguage.KEY);
  }
}
