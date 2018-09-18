package fr.cnes.sonarqube.plugins.framac.sensor;

import fr.cnes.sonarqube.plugins.framac.report.FramaCError;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.config.internal.MapSettings;

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
		verify(sensorDescriptor).name(FramaCSensor.class.getName());
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
	public void test_save_issue_with_unknown_file() {
		final String ruleKey = "FRAMAC.ERROR";
		final String issueMessage = "This file does not exist.";
		final String filepath = "lolololol.c";
		final String line = "1";

		rule = new FramaCError(ruleKey, issueMessage, filepath, line);

		FramaCSensor.saveIssue(context, files, rule);
		Assert.assertEquals(0, context.allIssues().size());
	}
	
}
