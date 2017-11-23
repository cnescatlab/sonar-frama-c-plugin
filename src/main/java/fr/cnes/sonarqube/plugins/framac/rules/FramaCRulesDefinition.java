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

package fr.cnes.sonarqube.plugins.framac.rules;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;

import fr.cnes.sonarqube.plugins.framac.languages.FramaCLanguage;

/**
 * Define specific Frama-C rules from a given resource file: { @link
 * FramaCRulesDefinition#PATH_TO_RULES_XML }
 * 
 * @author Cyrille FRANCOIS
 *
 */
public final class FramaCRulesDefinition implements RulesDefinition {

	public static final String PATH_TO_RULES_XML = "/default/framac-rules.xml";

	public static final String KEY = "rules";
	
	public static final String REPO_KEY = FramaCLanguage.KEY + "-" + KEY;
	protected static final String REPO_NAME = FramaCLanguage.NAME;

	private static NewRepository repository;

	protected String rulesDefinitionFilePath() {
		return PATH_TO_RULES_XML;
	}

	private void defineRulesForLanguage(Context context, String repositoryKey, String repositoryName,
			String languageKey) {
		repository = context.createRepository(repositoryKey, languageKey).setName(repositoryName);

		InputStream rulesXml = this.getClass().getResourceAsStream(rulesDefinitionFilePath());
		if (rulesXml != null) {
			RulesDefinitionXmlLoader rulesLoader = new RulesDefinitionXmlLoader();
			rulesLoader.load(repository, rulesXml, StandardCharsets.UTF_8.name());
		}

		repository.done();
	}

	@Override
	public void define(Context context) {
		defineRulesForLanguage(context, REPO_KEY, REPO_NAME, FramaCLanguage.KEY);
	}

	public static String getRepositoryKeyForLanguage() {
		    return FramaCLanguage.KEY + "-" + KEY;
	 }
}
