package sevensix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import javafx.application.Platform;

/** Verifies actual JavaFX launch and exit in a bounded child JVM with its own task storage. */
class LauncherTest {
    @TempDir
    private Path temporaryDirectory;

    /** The launcher must display the GUI and return successfully after its bye command. */
    @Test
    void launcherStartsWindowAndExitsThroughBye() throws Exception {
        verifyLauncherProcess();
    }

    /** IDE runners must launch successfully without Gradle's custom classpath property. */
    @Test
    void launcherUsesRunnerClasspathWithoutGradleConfiguration() throws Exception {
        String configuredClasspath = System.getProperty("sevensix.test.classpath");
        String runnerClasspath = System.getProperty("java.class.path");
        try {
            // Gradle's worker classpath contains only its bootstrap JAR, unlike a direct JUnit runner.
            if (configuredClasspath != null) {
                System.setProperty("java.class.path", configuredClasspath);
            }
            System.clearProperty("sevensix.test.classpath");
            verifyLauncherProcess();
        } finally {
            System.setProperty("java.class.path", runnerClasspath);
            if (configuredClasspath != null) {
                System.setProperty("sevensix.test.classpath", configuredClasspath);
            }
        }
    }

    /** Runs the launch-and-exit scenario with isolated storage and a bounded lifetime. */
    private void verifyLauncherProcess() throws Exception {
        Path outputFile = temporaryDirectory.resolve("launcher.log");
        Process process = new ProcessBuilder(launchCommand()).directory(temporaryDirectory.toFile())
                .redirectErrorStream(true).redirectOutput(outputFile.toFile()).start();
        try {
            boolean hasExited = process.waitFor(30, TimeUnit.SECONDS);
            String output = Files.readString(outputFile);
            assertTrue(hasExited, "GUI process did not terminate:\n" + output);
            assertEquals(0, process.exitValue(), output);
            assertTrue(output.contains("GUI launch and exit verified"), output);
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly().waitFor(5, TimeUnit.SECONDS);
            }
        }
    }

    /** Supplies the current JDK, test runtime dependencies, and inherited coverage instrumentation.
     *
     * @return the child JVM launch arguments.
     */
    private List<String> launchCommand() {
        String executable = System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
        List<String> command = new ArrayList<>(List.of(
                Path.of(System.getProperty("java.home"), "bin", executable).toString(), "-ea",
                nativeAccessArgument()));
        command.addAll(coverageArguments());
        command.addAll(List.of("-Duser.language=" + System.getProperty("user.language"),
                "-Duser.country=" + System.getProperty("user.country", ""),
                "-cp", runtimeClasspath(), "sevensix.GuiLaunchProbe"));
        return command;
    }

    /** Uses Gradle's explicit runtime when available, or the IDE's direct JUnit classpath.
     *
     * @return the classpath inherited by the child JVM.
     */
    private String runtimeClasspath() {
        return System.getProperty("sevensix.test.classpath", System.getProperty("java.class.path"));
    }

    /** Grants native access to JavaFX whether it comes from the JDK or the classpath.
     *
     * @return the JVM option for the module that loads JavaFX's native libraries.
     */
    private String nativeAccessArgument() {
        Module graphicsModule = Platform.class.getModule();
        String moduleName = graphicsModule.isNamed() ? graphicsModule.getName() : "ALL-UNNAMED";
        return "--enable-native-access=" + moduleName;
    }

    /** Includes child execution in the same JaCoCo report as the test worker.
     *
     * @return the inherited JaCoCo agent arguments.
     */
    private List<String> coverageArguments() {
        return ManagementFactory.getRuntimeMXBean().getInputArguments().stream()
                .filter(argument -> argument.startsWith("-javaagent:") && argument.contains("jacoco"))
                .map(LauncherTest::absoluteCoverageDestination).toList();
    }

    /** Keeps the coverage destination stable when the child changes its working directory.
     *
     * @param argument the original agent argument.
     * @return the argument with an absolute coverage destination.
     */
    private static String absoluteCoverageDestination(String argument) {
        Matcher destination = Pattern.compile("destfile=([^,]+)").matcher(argument);
        if (!destination.find()) {
            return argument;
        }
        String replacement = "destfile=" + Path.of(destination.group(1)).toAbsolutePath();
        return destination.replaceFirst(Matcher.quoteReplacement(replacement));
    }
}
