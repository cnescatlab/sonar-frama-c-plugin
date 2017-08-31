package fr.cnes.sonarqube.plugins.framac.report;

/**
 * Error definition
 * 
 * @author Cyrille FRANCOIS
 */
public interface ErrorInterface {

	String getLineDescriptor();

	String getRuleKey();

	String getDescription();

}
