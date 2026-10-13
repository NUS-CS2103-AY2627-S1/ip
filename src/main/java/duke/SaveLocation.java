package duke;

import java.nio.file.Path;

/**
 * Keeps task files inside the launch folder, as required by the course's file-writing constraint.
 */
public final class SaveLocation {
    private SaveLocation() {
    }

    /**
     * Returns the normal save path for the packaged app or a source-code run.
     *
     * @return Text file under the current working folder for both packaged and source runs.
     */
    public static Path getDefaultFile() {
        return resolve(Path.of("").toAbsolutePath());
    }

    /** Resolves a save path without consulting or changing the location of the application JAR. */
    static Path resolve(Path workingDirectory) {
        return workingDirectory.resolve("data").resolve("wangsa.txt");
    }
}
