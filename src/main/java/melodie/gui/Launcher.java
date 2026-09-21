package melodie.gui;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javafx.application.Application;

/**
 * Provides a non-JavaFX entry point that launches Melodie's graphical interface.
 */
public final class Launcher {

    private Launcher() {
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        if (relaunchJarWithNativeAccess(args)) {
            return;
        }
        Application.launch(Main.class, args);
    }

    /**
     * Relaunches an executable JAR when a bundled JavaFX module needs native access.
     *
     * <p>The course-prescribed macOS JDK supplies JavaFX as named modules. An
     * executable JAR cannot grant those modules native access through its manifest,
     * so the first lightweight launcher process starts a second JVM with the exact
     * module permission before JavaFX initializes.</p>
     *
     * @param args Command-line arguments to preserve for the relaunched application.
     * @return {@code true} if a replacement JVM was started.
     */
    private static boolean relaunchJarWithNativeAccess(String[] args) {
        Path launchLocation = getLaunchLocation();
        Module javaFxModule = Application.class.getModule();
        if (!launchLocation.toString().endsWith(".jar")
                || javaFxModule.isNativeAccessEnabled()) {
            return false;
        }

        String nativeAccessTarget = javaFxModule.isNamed()
                ? javaFxModule.getName()
                : "ALL-UNNAMED";
        List<String> command = new ArrayList<>();
        command.add(getJavaExecutable().toString());
        command.add("--enable-native-access=" + nativeAccessTarget);
        command.add("-jar");
        command.add(launchLocation.toString());
        command.addAll(Arrays.asList(args));

        try {
            Process application = new ProcessBuilder(command).inheritIO().start();
            int exitCode = application.waitFor();
            if (exitCode != 0) {
                throw new IllegalStateException(
                        "Relaunched Melodie exited with status " + exitCode);
            }
            return true;
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to relaunch Melodie with JavaFX native access", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for Melodie", e);
        }
    }

    /**
     * Returns the classes directory or executable JAR containing this launcher.
     *
     * @return Launcher's code-source path.
     */
    private static Path getLaunchLocation() {
        try {
            return Path.of(Launcher.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to locate the Melodie application", e);
        }
    }

    /**
     * Locates the Java executable belonging to the runtime that launched Melodie.
     *
     * @return Path to {@code java} or {@code java.exe} for the current runtime.
     */
    private static Path getJavaExecutable() {
        boolean isWindows = System.getProperty("os.name")
                .toLowerCase(Locale.ROOT).contains("win");
        String executableName = isWindows ? "java.exe" : "java";
        return Path.of(System.getProperty("java.home"), "bin", executableName);
    }
}
