package fr.cnes.sonarqube.plugins.framac.sensor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.Plugin;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputComponent;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.measure.Metric;
import org.sonar.api.batch.rule.Severity;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.batch.sensor.measure.NewMeasure;
import org.sonar.api.rule.RuleKey;

import fr.cnes.sonarqube.plugins.framac.FramaCPlugin;

public class FramaCMetricsSensorTest {
	
	@BeforeClass
	public static void beforeClass(){
		Plugin.Context context = mock(Plugin.Context.class);
		FramaCPlugin plugin = new FramaCPlugin();
		plugin.define(context);
	}
	
	@Test
	public void getLineAsIntTest(){
		int expected = 1;
		int actual = FramaCMetricsSensor.getLineAsInt("-5", 100);
		assertEquals(expected, actual);
		expected = 100;
		actual = FramaCMetricsSensor.getLineAsInt("200", 100);
		assertEquals(expected, actual);
		expected = -1;
		actual = FramaCMetricsSensor.getLineAsInt("aa", 100);
		assertEquals(expected, actual);
	}
	
	@Test
	public void given_sensorDescriptor_when_describe_then_callSensorDescriptorName() {
		SensorDescriptor sensorDescriptor = Mockito.mock(SensorDescriptor.class);
		FramaCMetricsSensor framaCMetricsSensor = new FramaCMetricsSensor();
		framaCMetricsSensor.describe(sensorDescriptor);
		verify(sensorDescriptor).name(FramaCMetricsSensor.class.getName());
	}
	
	@Test
	public void given_NoReportFile_when_execute_then_reportErrors(){
		FramaCMetricsSensor framaCMetricsSensor = new FramaCMetricsSensor(){
			// Stub plugin settings
			void readPluginSettings(SensorContext context) {};
		};
		framaCMetricsSensor.expectedReportInputFileTypes = "*.c";
		framaCMetricsSensor.reportOutExt = ".res";
		framaCMetricsSensor.reportSubdir = "reports";
		
		// Mock sensor context
		SensorContext sensorContext = Mockito.mock(SensorContext.class);
		FileSystem fs = Mockito.mock(FileSystem.class);
		FilePredicates filePredicates = Mockito.mock(FilePredicates.class);
		FilePredicate filePredicate = Mockito.mock(FilePredicate.class);
		InputFile inputFile = Mockito.mock(InputFile.class);

		Mockito.when(sensorContext.fileSystem()).thenReturn(fs);
		Mockito.when(fs.predicates()).thenReturn(filePredicates);
		ArrayList<InputFile> filesC = new ArrayList<InputFile>();
		filesC.add(inputFile);
		Mockito.when(filePredicates.matchesPathPatterns(new String[]{framaCMetricsSensor.expectedReportInputFileTypes})).thenReturn(filePredicate);
		Mockito.when(fs.inputFiles(filePredicate)).thenReturn(filesC);
//		Mockito.doCallRealMethod().when(framaCMetricsSensor).execute(sensorContext);
		File file = new File("./src/test/resources/test.c");
		Mockito.when(inputFile.absolutePath()).thenReturn(file.getAbsolutePath());
		Mockito.when(inputFile.file()).thenReturn(file);
		NewMeasure<Serializable> newMeasure = new NewMeasure<Serializable>() {
			
			@Override
			public NewMeasure<Serializable> withValue(Serializable arg0) {
				// TODO Auto-generated method stub
				return this;
			}
			
			@Override
			public void save() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public NewMeasure<Serializable> on(InputComponent arg0) {
				// TODO Auto-generated method stub
				return this;
			}
			
			@Override
			public NewMeasure<Serializable> forMetric(Metric<Serializable> arg0) {
				// TODO Auto-generated method stub
				return this;
			}
		};
		Mockito.when(sensorContext.<Serializable>newMeasure()).thenReturn(newMeasure);
		NewIssue newIssue = new NewIssue() {
			NewIssueLocation newIssueLocation = new NewIssueLocation() {
				
				@Override
				public NewIssueLocation on(InputComponent arg0) {
					// TODO Auto-generated method stub
					return this;
				}
				
				@Override
				public NewIssueLocation message(String arg0) {
					// TODO Auto-generated method stub
					return this;
				}
				
				@Override
				public NewIssueLocation at(TextRange arg0) {
					// TODO Auto-generated method stub
					return this;
				}
			};
			@Override
			public void save() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public NewIssue overrideSeverity(Severity arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public NewIssueLocation newLocation() {
				
				return newIssueLocation;
			}
			
			@Override
			public NewIssue gap(Double arg0) {
				// TODO Auto-generated method stub
				return this;
			}
			
			@Override
			public NewIssue forRule(RuleKey arg0) {
				// TODO Auto-generated method stub
				return this;
			}
			
			@Override
			public NewIssue effortToFix(Double arg0) {
				// TODO Auto-generated method stub
				return this;
			}
			
			@Override
			public NewIssue at(NewIssueLocation arg0) {
				// TODO Auto-generated method stub
				return this;
			}
			
			@Override
			public NewIssue addLocation(NewIssueLocation arg0) {
				newIssueLocation = arg0;
				return this;
			}
			
			@Override
			public NewIssue addFlow(Iterable<NewIssueLocation> arg0) {
				// TODO Auto-generated method stub
				return this;
			}
		};
		Mockito.when(sensorContext.newIssue()).thenReturn(newIssue);
		framaCMetricsSensor.execute(sensorContext);
	}

	
	@Test
	public void given_EmptyReportFile_when_execute_then_reportErrors(){
//		FramaCMetricsSensor framaCMetricsSensor = Mockito.mock(FramaCMetricsSensor.class);
		FramaCMetricsSensor framaCMetricsSensor = new FramaCMetricsSensor(){
			void readPluginSettings(SensorContext context) {};
		};
		SensorContext sensorContext = Mockito.mock(SensorContext.class);
		FileSystem fs = Mockito.mock(FileSystem.class);
		FilePredicates filePredicates = Mockito.mock(FilePredicates.class);
		FilePredicate filePredicate = Mockito.mock(FilePredicate.class);
		InputFile inputFile = Mockito.mock(InputFile.class);
//		doNothing().when(framaCMetricsSensor).readPluginSettings(sensorContext);
		framaCMetricsSensor.expectedReportInputFileTypes = "*.c";
		framaCMetricsSensor.reportOutExt = ".res";
		framaCMetricsSensor.reportSubdir = "reports";
		Mockito.when(sensorContext.fileSystem()).thenReturn(fs);
		Mockito.when(fs.predicates()).thenReturn(filePredicates);
		ArrayList<InputFile> filesC = new ArrayList<InputFile>();
		filesC.add(inputFile);
		Mockito.when(filePredicates.matchesPathPatterns(new String[]{framaCMetricsSensor.expectedReportInputFileTypes})).thenReturn(filePredicate);
		Mockito.when(fs.inputFiles(filePredicate)).thenReturn(filesC);
//		Mockito.doCallRealMethod().when(framaCMetricsSensor).execute(sensorContext);
		File file = new File("./src/test/resources/testEmptyReport.c");
		Mockito.when(inputFile.absolutePath()).thenReturn(file.getAbsolutePath());
		Mockito.when(inputFile.file()).thenReturn(file);
		NewMeasure<Serializable> newMeasure = new NewMeasure<Serializable>() {
			
			@Override
			public NewMeasure<Serializable> withValue(Serializable arg0) {
				// TODO Auto-generated method stub
				return this;
			}
			
			@Override
			public void save() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public NewMeasure<Serializable> on(InputComponent arg0) {
				// TODO Auto-generated method stub
				return this;
			}
			
			@Override
			public NewMeasure<Serializable> forMetric(Metric<Serializable> arg0) {
				// TODO Auto-generated method stub
				return this;
			}
		};
		Mockito.when(sensorContext.<Serializable>newMeasure()).thenReturn(newMeasure);
		framaCMetricsSensor.execute(sensorContext);
	}
	
	@Test
	public void given_reportFile_when_execute_then_reportIssues(){
		FramaCMetricsSensor framaCMetricsSensor = new FramaCMetricsSensor(){
			void readPluginSettings(SensorContext context) {
				this.expectedReportInputFileTypes = "*.c";
				this.reportOutExt = ".res";
				this.reportSubdir = "reports";
			};
		};

		// Mock sensor context
		SensorContext sensorContext = Mockito.mock(SensorContext.class);
		FileSystem fs = Mockito.mock(FileSystem.class);
		FilePredicates filePredicates = Mockito.mock(FilePredicates.class);
		FilePredicate filePredicate = Mockito.mock(FilePredicate.class);
		InputFile inputFile = Mockito.mock(InputFile.class);

		
		// Mock input files
		ArrayList<InputFile> filesC = new ArrayList<InputFile>();
		filesC.add(inputFile);
		Mockito.when(filePredicates.matchesPathPatterns(
				new String[]{framaCMetricsSensor.expectedReportInputFileTypes})).thenReturn(filePredicate);
		Mockito.when(fs.inputFiles(filePredicate)).thenReturn(filesC);
		Mockito.when(sensorContext.fileSystem()).thenReturn(fs);
		Mockito.when(fs.predicates()).thenReturn(filePredicates);		
		File file = new File("./src/test/resources/test.c");
		Mockito.when(inputFile.absolutePath()).thenReturn(file.getAbsolutePath());
		Mockito.when(inputFile.file()).thenReturn(file);
		
		// Stub newMeasure
		NewMeasure<Serializable> newMeasure = new NewMeasure<Serializable>() {
			
			@Override
			public NewMeasure<Serializable> withValue(Serializable arg0) {
				// TODO Auto-generated method stub
				return this;
			}
			
			@Override
			public void save() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public NewMeasure<Serializable> on(InputComponent arg0) {
				// TODO Auto-generated method stub
				return this;
			}
			
			@Override
			public NewMeasure<Serializable> forMetric(Metric<Serializable> arg0) {
				// TODO Auto-generated method stub
				return this;
			}
		};
		Mockito.when(sensorContext.<Serializable>newMeasure()).thenReturn(newMeasure);
		
		// Stub issue
		NewIssue newIssue = new NewIssue() {
			NewIssueLocation newIssueLocation = new NewIssueLocation() {
				
				@Override
				public NewIssueLocation on(InputComponent arg0) {
					// TODO Auto-generated method stub
					return this;
				}
				
				@Override
				public NewIssueLocation message(String arg0) {
					// TODO Auto-generated method stub
					return this;
				}
				
				@Override
				public NewIssueLocation at(TextRange arg0) {
					// TODO Auto-generated method stub
					return this;
				}
			};
			@Override
			public void save() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public NewIssue overrideSeverity(Severity arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public NewIssueLocation newLocation() {
				
				return newIssueLocation;
			}
			
			@Override
			public NewIssue gap(Double arg0) {
				// TODO Auto-generated method stub
				return this;
			}
			
			@Override
			public NewIssue forRule(RuleKey arg0) {
				// TODO Auto-generated method stub
				return this;
			}
			
			@Override
			public NewIssue effortToFix(Double arg0) {
				// TODO Auto-generated method stub
				return this;
			}
			
			@Override
			public NewIssue at(NewIssueLocation arg0) {
				// TODO Auto-generated method stub
				return this;
			}
			
			@Override
			public NewIssue addLocation(NewIssueLocation arg0) {
				newIssueLocation = arg0;
				return this;
			}
			
			@Override
			public NewIssue addFlow(Iterable<NewIssueLocation> arg0) {
				// TODO Auto-generated method stub
				return this;
			}
		};
		Mockito.when(sensorContext.newIssue()).thenReturn(newIssue);
		
		// call sensor
		framaCMetricsSensor.execute(sensorContext);
	}
	
}
