package megia.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.junit.jupiter.api.Test;

class PropertiesServiceTest {
    private static final String DEFAULT_LANGUAGE = "en";
    private static final String DEFAULT_FILE_PATH = "./task_storage.csv";

    @Test
    void loadProperties_missingConfiguration_returnsCompleteDefaults() {
        Properties properties = PropertiesService.loadProperties(null);

        assertEquals(DEFAULT_LANGUAGE, properties.getProperty("language"));
        assertEquals(DEFAULT_FILE_PATH, properties.getProperty("storage.task.path"));
    }

    @Test
    void loadProperties_partialConfiguration_preservesOmittedDefault() {
        InputStream inputStream = new ByteArrayInputStream(
                "language=cn\n".getBytes(StandardCharsets.UTF_8));

        Properties properties = PropertiesService.loadProperties(inputStream);

        assertEquals("cn", properties.getProperty("language"));
        assertEquals(DEFAULT_FILE_PATH, properties.getProperty("storage.task.path"));
    }

    @Test
    void loadProperties_unreadableConfiguration_reportsProblemAndReturnsCompleteDefaults() {
        InputStream inputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("test read failure");
            }
        };
        ByteArrayOutputStream errorOutput = new ByteArrayOutputStream();
        PrintStream originalError = System.err;
        Properties properties;

        try (PrintStream capturedError = new PrintStream(errorOutput, true, StandardCharsets.UTF_8)) {
            System.setErr(capturedError);
            properties = PropertiesService.loadProperties(inputStream);
        } finally {
            System.setErr(originalError);
        }

        assertEquals(DEFAULT_LANGUAGE, properties.getProperty("language"));
        assertEquals(DEFAULT_FILE_PATH, properties.getProperty("storage.task.path"));
        assertTrue(errorOutput.toString(StandardCharsets.UTF_8)
                .contains("Can't read application.properties. Using default settings: test read failure"));
    }
}
