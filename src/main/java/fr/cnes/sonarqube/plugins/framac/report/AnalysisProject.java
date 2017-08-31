package fr.cnes.sonarqube.plugins.framac.report;

import java.util.ArrayList;
import java.util.List;

/**
 * Analyze Frama-C report file
 * 
 * @author Cyrille FRANCOIS
 *
 */
public class AnalysisProject  implements ReportInterface{
	
	/** Global metrics measures computed by Frama-C */
	MetricsModuleMeasure globalMetrics;
	
	List<FramaCError> errors;

	public AnalysisProject() {
		super();
		this.globalMetrics = new MetricsModuleMeasure();
	}

	MetricsModuleMeasure getGlobalMetrics() {
		return globalMetrics;
	}

	@Override
	public ReportModuleRuleInterface getModuleCyclomaticMeasure() {
		// TODO Auto-generated method stub
		return this.globalMetrics;
	}

	@Override
	public ReportFunctionRuleInterface[] getCyclomaticMeasureByFunction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorInterface[] getErrors() {
		if(this.errors == null){
			this.errors = new ArrayList<FramaCError>();
		}
		ErrorInterface[] res = new ErrorInterface[this.errors.size()];
		res = this.errors.toArray(res);
		return res;
	}
	
	public void addError(FramaCError err) {
		if(this.errors == null){
			this.errors = new ArrayList<FramaCError>();
		}
		this.errors.add(err);
	}

}

class MetricsModuleMeasure implements ReportModuleRuleInterface{
	
	String sloc;
	String decisionPoint;
	String globalVariables;
	String ifStatements;
	String loopStatements;
	String gotoStatements;
	String assignmentStatements;
	String exitPoint;
	String function;
	String functionCall;
	String pointerDereferencing;
	String cyclomaticComplexity;

	@Override
	public String getLoc() {
		// TODO Auto-generated method stub
		return this.sloc;
	}

	@Override
	public String getComplexity() {
		// TODO Auto-generated method stub
		return this.cyclomaticComplexity;
	}

	void setSloc(String sloc) {
		this.sloc = sloc;
	}

	void setDecisionPoint(String decisionPoint) {
		this.decisionPoint = decisionPoint;
	}

	void setGlobalVariables(String globalVariables) {
		this.globalVariables = globalVariables;
	}

	void setIfStatements(String ifStatements) {
		this.ifStatements = ifStatements;
	}

	void setLoopStatements(String loopStatements) {
		this.loopStatements = loopStatements;
	}

	void setGotoStatements(String gotoStatements) {
		this.gotoStatements = gotoStatements;
	}

	void setAssignmentStatements(String assignmentStatements) {
		this.assignmentStatements = assignmentStatements;
	}

	void setExitPoint(String exitPoint) {
		this.exitPoint = exitPoint;
	}

	void setFunction(String function) {
		this.function = function;
	}

	void setFunctionCall(String functionCall) {
		this.functionCall = functionCall;
	}

	void setPointerDereferencing(String pointerDereferencing) {
		this.pointerDereferencing = pointerDereferencing;
	}

	void setCyclomaticComplexity(String cyclomaticComplexity) {
		this.cyclomaticComplexity = cyclomaticComplexity;
	}
}