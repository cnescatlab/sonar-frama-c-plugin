package fr.cnes.sonarqube.plugins.framac.report;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FramaCReportReaderTest {

    @Test
    public void must_return_null_if_old_format() {
        String line = "tests/syntax/initializer.i:29:[kernel] warning: Floating-point constant 0.1 is not represented exactly. Will use 0x1.999999999999ap-4. See documentation for option -warn-decimal-float";
        FramaCReportReader reader = new FramaCReportReader();
        FramaCError error = reader.getKernelError(line);
        assertNull(error);
    }

    @Test
    public void must_return_null_if_no_match() {
        // Following line don't match (missing : between file name and line number
        String line = "[kernel] copy_logic.i5: Warning: ";
        FramaCReportReader reader = new FramaCReportReader();
        FramaCError error = reader.getKernelError(line);
        assertNull(error);

    }

    @Test
    public void must_return_framacerror_if_match_word_warning() {
        String line = "[kernel] copy_logic.i:5: Warning: ";
        FramaCReportReader reader = new FramaCReportReader();
        FramaCError error = reader.getKernelError(line);
        assertEquals("copy_logic.i", error.getFilePath());
        assertEquals("5", error.getLine());
        assertEquals("", error.getDescription());
        assertEquals("KERNEL.0", error.getType());
    }

    @Test
    public void must_return_framacerror_if_match_word_warning_and_kernelrerror_id() {
        String line = "[kernel:parser:decimal-float] initializer.i:29: Warning: ";
        FramaCReportReader reader = new FramaCReportReader();
        FramaCError error = reader.getKernelError(line);
        assertEquals("initializer.i", error.getFilePath());
        assertEquals("29", error.getLine());
        assertEquals("", error.getDescription());
        assertEquals("KERNEL.0", error.getType());
    }

    @Test
    public void must_return_framacerror_if_match_word_warning_and_nopace_at_end() {
        String line = "[kernel:parser:decimal-float] initializer.i:29: Warning:";
        FramaCReportReader reader = new FramaCReportReader();
        FramaCError error = reader.getKernelError(line);
        assertEquals("initializer.i", error.getFilePath());
        assertEquals("29", error.getLine());
        assertEquals("", error.getDescription());
        assertEquals("KERNEL.0", error.getType());
    }

    @Test
    public void must_return_framacerror_if_description_present() {
        String line = "[kernel] initializers.i:4: Warning: Too many initializers for structure";
        FramaCReportReader reader = new FramaCReportReader();
        FramaCError error = reader.getKernelError(line);
        assertEquals("initializers.i", error.getFilePath());
        assertEquals("4", error.getLine());
        assertEquals("Too many initializers for structure", error.getDescription());
        assertEquals("KERNEL.0", error.getType());
    }

    @Test
    public void must_return_framacerror_User_Error() {
        String line = "[kernel] qualified_arrays.i:17: User Error: ";
        FramaCReportReader reader = new FramaCReportReader();
        FramaCError error = reader.getKernelError(line);
        assertEquals("qualified_arrays.i", error.getFilePath());
        assertEquals("17", error.getLine());
        assertEquals("", error.getDescription());
        assertEquals("KERNEL.0", error.getType());
    }
}
