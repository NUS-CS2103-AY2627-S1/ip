package groot.testutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Runs scenarios with isolated storage, bounded execution, and separate coverage files.
 */
public final class IsolatedProcess {
    private IsolatedProcess() {
    }

    /**
     * Runs a Java entry point and returns its standard output, preserving standard error for failures.
     *
     * @param directory Temporary working directory owned by the test.
     * @param entryPoint Scenario to execute.
     * @param input Console input to supply.
     * @param arguments Scenario arguments.
     * @return Standard output with normalized line endings.
     * @throws Exception If execution or output collection fails.
     */
    public static String run(Path directory, Class<?> entryPoint, String input, String... arguments)
            throws Exception {
        List<String> command = new ArrayList<>();
        command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
        command.add("-ea");
        // Match Files.readString's UTF-8 decoding, including on Windows with a legacy console encoding.
        command.add("-Dstdout.encoding=UTF-8");
        command.add("-Dstderr.encoding=UTF-8");
        if (entryPoint.getPackageName().equals("groot.ui")) {
            // The project's Java 25 FX distribution supplies native libraries for the host architecture.
            command.add("--add-modules=javafx.controls,javafx.fxml");
            command.add("--enable-native-access=javafx.graphics");
        }
        String agent = System.getProperty("groot.test.coverageAgent");
        if (agent != null) {
            Path coverageDirectory = Path.of(System.getProperty("groot.test.coverageDirectory"));
            Files.createDirectories(coverageDirectory);
            command.add("-javaagent:" + agent + "=destfile="
                    + coverageDirectory.resolve(UUID.randomUUID() + ".exec") + ",includes=groot.*");
        }
        command.addAll(List.of("-cp", System.getProperty("groot.test.classpath",
                System.getProperty("java.class.path")), entryPoint.getName()));
        command.addAll(List.of(arguments));
        Path outputFile = Files.createTempFile(directory, "stdout-", ".txt");
        Path errorFile = Files.createTempFile(directory, "stderr-", ".txt");
        Process process = new ProcessBuilder(command).directory(directory.toFile())
                .redirectOutput(outputFile.toFile()).redirectError(errorFile.toFile()).start();
        try {
            try (var processInput = process.getOutputStream()) {
                processInput.write(input.getBytes(StandardCharsets.UTF_8));
            }
            assertTrue(process.waitFor(30, TimeUnit.SECONDS), "Scenario timed out: " + entryPoint.getName());
            String output = Files.readString(outputFile).replace("\r\n", "\n");
            assertEquals(0, process.exitValue(), output + Files.readString(errorFile));
            return output;
        } finally {
            process.destroyForcibly();
        }
    }
}
