package common;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

/**
 * Common Constants used throughout the project
 */
public final class Constants {
    public static final Path PROJECT_DATA_DIR_PATH = Path.of("data");
    public static final Path FRIEDBERG_DATA_FILE_PATH = PROJECT_DATA_DIR_PATH.resolve("friedberg_data");
    public static final Path FRIEDBERG_ARCHIVE_FILE_PATH = PROJECT_DATA_DIR_PATH.resolve("friedberg_archive");

    public static final DateTimeFormatter DATETIME_RENDER_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyy (E)");
}
