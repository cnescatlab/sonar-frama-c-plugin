package fr.cnes.sonarqube.plugins.framac.languages;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.config.Settings;
import org.sonar.api.resources.AbstractLanguage;

import fr.cnes.sonarqube.plugins.framac.settings.FramaCLanguageProperties;

/**
 * This class defines a specific Sonar language for tool FramaC
 * 
 * @author Cyrille FRANCOIS
 */
public final class FramaCLanguage extends AbstractLanguage {

	public static final String NAME = "FramaC";
	public static final String KEY = "framac";

	private final Settings settings;

	/**
	 * Sonar extension for Frama-C specific properties, Metrics and Rules.
	 * 
	 * @param settings Inject Sonar settings into this extension
	 */
	public FramaCLanguage(Settings settings) {
		super(KEY, NAME);
		this.settings = settings;
	}

	@Override
	public String[] getFileSuffixes() {
		String[] suffixes = filterEmptyStrings(
				settings.getStringArray(FramaCLanguageProperties.EXPECTED_REPORT_INPUT_FILE_TYPES_KEY));
		if (suffixes.length == 0) {
			suffixes = FramaCLanguageProperties.EXPECTED_REPORT_INPUT_FILE_TYPES_DEFAULT_VALUE
					.split(FramaCLanguageProperties.FILE_SUFFIXES_SEPARATOR);
		}
		return suffixes;
	}

	/**
	 * Delete all empty string values into a input String array
	 * 
	 * @param stringArray Input String array
	 * 
	 * @return Output String array without empty string values
	 */
	private String[] filterEmptyStrings(String[] stringArray) {
		List<String> nonEmptyStrings = new ArrayList<>();
		for (String string : stringArray) {

			// Add only not empty string values
			if (!(string.trim()).isEmpty()) {
				nonEmptyStrings.add(string.trim());
			}
		}
		return nonEmptyStrings.toArray(new String[nonEmptyStrings.size()]);
	}
}
