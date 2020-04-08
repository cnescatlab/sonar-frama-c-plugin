package fr.cnes.sonar.plugins.framac.sensor;

import fr.cnes.sonar.plugins.framac.report.FramaCError;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.config.Configuration;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.internal.google.common.collect.Lists;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;

public class FramaCSensorTest {

	private DefaultFileSystem fs;
	private SensorContextTester context;
	private Map<String, InputFile> files;

	private DefaultInputFile memexec_c;
	private DefaultInputFile not_i;
	private FramaCError rule;

	@Before
	public void prepare() throws URISyntaxException {
		fs = new DefaultFileSystem(new File(getClass().getResource("/TestsPluginFramaC/value").toURI()));
		fs.setEncoding(Charset.forName("UTF-8"));

		memexec_c = TestInputFileBuilder.create(
				"ProjectKey",
				fs.resolvePath("memexec.c").getPath())
				.setLanguage("framac")
				.setType(InputFile.Type.MAIN)
				.setLines(10)
				.setOriginalLineOffsets(new int[]{0,0,0,0,0,0,0,0,0,0})
				.setLastValidOffset(100)
				.setContents("blablabla\nblablabla\nblablabla\nblablabla\nblablabla\nblablabla\nblablabla\nblablabla\nblablabla\n")
				.build();
		fs.add(memexec_c);
		not_i = TestInputFileBuilder.create(
				"ProjectKey",
				fs.resolvePath("not.i").getPath())
				.setLanguage("framac")
				.setType(InputFile.Type.MAIN).setLines(10)
				.setLines(10)
				.setOriginalLineOffsets(new int[]{0,0,0,0,0,0,0,0,0,0})
				.setLastValidOffset(100)
				.build();
		fs.add(not_i);

		context = SensorContextTester.create(fs.baseDir());
		files = new HashMap<>();
		rule = new FramaCError("","","","");

		files.put("memexec.c", memexec_c);
		files.put("not.i", not_i);

		context = SensorContextTester.create(fs.baseDir());
		context.setFileSystem(fs);
		MapSettings settings = new MapSettings();
		settings.setProperty("sonar.framac.reports.path", "");
		context.setSettings(settings);
	}

	@Test
	public void test_given_sensorDescriptor_when_describe_then_callSensorDescriptorName() {
		SensorDescriptor sensorDescriptor = Mockito.mock(SensorDescriptor.class);
		FramaCSensor icodeMetricsSensor = new FramaCSensor();
		icodeMetricsSensor.describe(sensorDescriptor);
		verify(sensorDescriptor).name("SonarFrama-C");
	}

	@Test
	public void test_normal_work() {
		final FramaCSensor sensor = new FramaCSensor();

		sensor.execute(context);

		Assert.assertNotNull(sensor);
		Assert.assertTrue(context.config().hasKey("sonar.framac.reports.path"));
	}

	@Test(expected = NullPointerException.class)
	public void test_normal_work_with_framac_launch_failed() {
		final FramaCSensor sensor = new FramaCSensor();

		final MapSettings settings = new MapSettings();
		settings.setProperty("sonar.framac.launch",true);
		context.setSettings(settings);

		sensor.execute(context);
	}

	@Test
	public void test_save_issue() {
		final String ruleKey = "FRAMAC.ERROR";
		final String issueMessage = "This file exists.";
		final String filepath = "memexec.c";
		final String line = "4";

		rule = new FramaCError(ruleKey, issueMessage, filepath, line);

		FramaCSensor.saveIssue(context, files, rule);
		Assert.assertEquals(1, context.allIssues().size());
	}

	@Test
	public void test_get_scanned_files() {
		final FramaCSensor sensor = new FramaCSensor();

		final List<FramaCError> errors = Lists.newArrayList();
		final FramaCError error1 = new FramaCError(
				"example", "this is an example", "badaboum.c", "2");
		final FramaCError error2 = new FramaCError(
				"example2", "this is an example too", "miam.c", "5");

		errors.add(error1);
		errors.add(error2);

		Assert.assertNotNull(sensor);

		Map<String, InputFile> relevantFile = sensor.getScannedFiles(context.fileSystem(), errors);

		Assert.assertEquals(0, relevantFile.size());
	}

	@Test
	public void test_check_rules_activation() {
		final FramaCSensor sensor = new FramaCSensor();

		final boolean active = sensor.isRuleActive(context.activeRules(), "No rules are activated");

		Assert.assertFalse(active);
	}

	@Test
	public void test_save_issue_with_unknown_file() {
		final String ruleKey = "FRAMAC.ERROR";
		final String issueMessage = "This file does not exist.";
		final String filepath = "lolololol.c";
		final String line = "1";

		rule = new FramaCError(ruleKey, issueMessage, filepath, line);

		FramaCSensor.saveIssue(context, files, rule);
		Assert.assertEquals(0, context.allIssues().size());
	}

	@Test
	public void test_execute_unmarshall() {
		final FramaCSensor sensor = new FramaCSensor() {
			@Override
			public List<String> getReportFiles(final Configuration config, final FileSystem fileSystem) {
				List<String> list;
				try {
					list = Lists.newArrayList(
                            new File(getClass().getResource("/TestsPluginFramaC/value/frama-c-results/alias.4.res.oracle").toURI()).getPath(),
                            new File(getClass().getResource("/TestsPluginFramaC/value/frama-c-results/alias.4.res.csv").toURI()).getPath()
                    );
				} catch (URISyntaxException e) {
					list = Lists.newArrayList();
				}
				return list;
			}
		};

		final MapSettings settings = new MapSettings();
		settings.setProperty("sonar.framac.launch", false);
		context.setSettings(settings);

		sensor.execute(context);
	}

	@Test
	public void method_isCsvFile_with_csvfile()
		throws URISyntaxException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		URI uri = getClass().getResource("/TestsPluginFramaC/reports/result.csv").toURI();
		String filePath = uri.getPath();
		final FramaCSensor sensor = new FramaCSensor();
		Method method = sensor.getClass().getDeclaredMethod("isCsvFile", String.class);
		method.setAccessible(true);
		Boolean isScv = (Boolean) method.invoke(sensor, filePath);
		Assert.assertEquals(true, isScv);
	}

	@Test
	public void method_isCsvFile_with_outfile()
		throws URISyntaxException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		URI uri = getClass().getResource("/TestsPluginFramaC/reports/result.out").toURI();
		String filePath = uri.getPath();
		final FramaCSensor sensor = new FramaCSensor();
		Method method = sensor.getClass().getDeclaredMethod("isCsvFile", String.class);
		method.setAccessible(true);
		Boolean isScv = (Boolean) method.invoke(sensor, filePath);
		Assert.assertEquals(false, isScv);
	}

	@Test
	public void method_analyseReports_with_outfile()
		throws URISyntaxException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		URI uri = getClass().getResource("/TestsPluginFramaC/reports/result.out").toURI();
		String filePath = uri.getRawPath();
		final FramaCSensor sensor = new FramaCSensor();
		Method method = sensor.getClass().getDeclaredMethod("analyseReport", String.class);
		method.setAccessible(true);

		List<FramaCError> errors = (List<FramaCError> ) method.invoke(sensor, Paths.get(uri).toFile().getPath());
		Assert.assertEquals(7, errors.size());
		FramaCError error = errors.get(0);
		// Check content of error 1
		Assert.assertEquals("KERNEL.0", error.getType());
		Assert.assertEquals("RobotSeeVM_v0001/Keyword.c", error.getFilePath());
		Assert.assertEquals("43", error.getLine());
		Assert.assertEquals("expected 'char *' but got argument of type 'unsigned char *': tmp", error.getDescription());
	}

	@Test
	public void method_analyseReports_with_csvfile()
		throws URISyntaxException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		URI uri = getClass().getResource("/TestsPluginFramaC/reports/result.csv").toURI();
		String filePath = uri.getRawPath();
		final FramaCSensor sensor = new FramaCSensor();
		Method method = sensor.getClass().getDeclaredMethod("analyseReport", String.class);
		method.setAccessible(true);

		List<FramaCError> errors = (List<FramaCError> ) method.invoke(sensor, Paths.get(uri).toFile().getPath());
		Assert.assertEquals(292, errors.size());
		FramaCError error = errors.get(0);
		// Check content of error 1
		Assert.assertEquals("CSV.0", error.getType());
		Assert.assertEquals("FRAMAC_SHARE/libc/stdio.h", error.getFilePath());
		Assert.assertEquals("70", error.getLine());
		Assert.assertEquals("assigns clause assigns \\nothing;", error.getDescription());
	}
	
}
