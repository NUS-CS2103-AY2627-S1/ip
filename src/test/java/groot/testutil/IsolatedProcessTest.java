package groot.testutil;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Guards against Windows console encodings losing Unicode in isolated test output.
 */
public class IsolatedProcessTest {
    @TempDir
    public Path directory;

    @Test
    public void run_unicodeAndDelimiters_preservesOutputWithUtf8Streams() throws Exception {
        assertEquals("管道 | \\ notes\n", IsolatedProcess.run(directory, EncodingScenario.class, ""));
    }

    /**
     * Checks the actual child stream encodings, independent of the host's default encoding.
     */
    public static class EncodingScenario {
        /**
         * Emits Unicode and storage delimiters through both captured streams.
         */
        public static void main(String[] args) {
            assertEquals(StandardCharsets.UTF_8, System.out.charset());
            assertEquals(StandardCharsets.UTF_8, System.err.charset());
            System.out.println("管道 | \\ notes");
            System.err.println("管道 | \\ notes");
        }
    }
}
