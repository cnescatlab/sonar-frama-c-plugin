package fr.cnes.sonarqube.plugins.framac.exceptions;

import org.junit.Assert;
import org.junit.Test;

public class FramaCExceptionTest {

	final String message = "This is a text message.";
	final FramaCException exception = new FramaCException(message);

	@Test
	public void test_exception_is_reliable() {
		try {
			throw exception;
		} catch (FramaCException e) {
			Assert.assertEquals(message, e.getMessage());
		}
	}
}
