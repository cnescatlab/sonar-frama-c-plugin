package fr.cnes.sonarqube.plugins.framac.rules;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.server.rule.RulesDefinition.Context;
import org.sonar.api.server.rule.RulesDefinition.NewRepository;

import fr.cnes.sonarqube.plugins.framac.languages.FramaCLanguage;

public class FramaCRulesDefinitionTest {

	@Test
	public void given_context_when_define_then_repositoryDone() {
		String repositoryKey = FramaCRulesDefinition.REPO_KEY;
		String languageKey = FramaCLanguage.KEY;
		String repositoryName = FramaCRulesDefinition.REPO_NAME;
		NewRepository repository = Mockito.mock(NewRepository.class);
		Context context = Mockito.mock(Context.class);
		Mockito.when(context.createRepository(repositoryKey, languageKey)).thenReturn(repository);
		Mockito.when(repository.setName(repositoryName)).thenReturn(repository);
		FramaCRulesDefinition framaCRulesDefinition = new FramaCRulesDefinition(){
			
			protected String rulesDefinitionFilePath() {
				return "/default/bad-framac-rules.xml";
			}
		};
		framaCRulesDefinition.define(context);
		Mockito.verify(repository).done();
	}
	
	@Test
	public void testRulesDefinitionFilePath(){
		FramaCRulesDefinition framaCRulesDefinition = new FramaCRulesDefinition();
		String expected = FramaCRulesDefinition.PATH_TO_RULES_XML;
		String actual = framaCRulesDefinition.rulesDefinitionFilePath();
		assertEquals(expected, actual);
	}

}
