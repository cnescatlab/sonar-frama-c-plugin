package fr.cnes.sonarqube.plugins.framac.report;

/**
 * Expected services for a rule function
 * 
 * @author Cyrille FRANCOIS
 *
 */
public interface ReportFunctionRuleInterface extends ReportModuleRuleInterface {

	String getFunction();
	String getLine();
}
