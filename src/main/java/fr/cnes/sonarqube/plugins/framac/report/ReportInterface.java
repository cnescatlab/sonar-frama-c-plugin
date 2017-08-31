package fr.cnes.sonarqube.plugins.framac.report;

/**
 * Report container for Sonar scanner
 * 
 * @author Cyrille FRANCOIS
 *
 */
public interface ReportInterface {
	
	ReportModuleRuleInterface getModuleCyclomaticMeasure();

	ReportFunctionRuleInterface[] getCyclomaticMeasureByFunction();
	
	ErrorInterface[] getErrors();

}
