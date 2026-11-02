package megia.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads application configuration with safe defaults for optional settings.
 */
public final class PropertiesService {
    private static final String LANGUAGE_KEY = "language";
    private static final String STORAGE_TASK_PATH_KEY = "storage.task.path";
    private static final String DEFAULT_LANGUAGE = "en";
    private static final String DEFAULT_FILE_PATH = "./task_storage.csv";

    private PropertiesService() {
    }

    /**
     * Loads application properties with defaults for the language and task-storage path.
     *
     * @return Loaded application properties.
     */
    public static Properties getProperties() {
        InputStream inputStream = PropertiesService.class.getClassLoader()
                .getResourceAsStream("application.properties");
        return loadProperties(inputStream);
    }

    static Properties loadProperties(InputStream inputStream) {
        Properties properties = getDefaultProperties();
        if (inputStream == null) {
            System.err.println("Can't find application.properties. Using default settings.");
            return properties;
        }

        try (inputStream) {
            properties.load(inputStream);
        } catch (IOException exception) {
            System.err.printf("Can't read application.properties. Using default settings: %s%n",
                    exception.getMessage());
            return getDefaultProperties();
        }
        return properties;
    }

    private static Properties getDefaultProperties() {
        Properties properties = new Properties();
        properties.setProperty(LANGUAGE_KEY, DEFAULT_LANGUAGE);
        properties.setProperty(STORAGE_TASK_PATH_KEY, DEFAULT_FILE_PATH);
        return properties;
    }
}
