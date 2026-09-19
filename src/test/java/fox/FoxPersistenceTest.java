package fox;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/** Integration test for saving after a command and restoring after restart. */
public class FoxPersistenceTest {
    /** Starts Fox twice in an isolated directory and checks the saved task reappears. */
    public static void main(String[] args) throws Exception {
        Path directory = Files.createTempDirectory("fox-persistence");
        String classPath = Path.of(System.getProperty("java.class.path")).toAbsolutePath().toString();
        runFox(directory, classPath, "todo remember this\nbye\n");
        Path dataFile = directory.resolve("data").resolve("fox.txt");
        assert Files.exists(dataFile);
        String output = runFox(directory, classPath, "list\nbye\n");
        assert output.contains("[T][ ] remember this");

        runFox(directory, classPath, "deadline submit report /by 2026-12-02\nbye\n");
        String datedOutput = runFox(directory, classPath, "list\nbye\n");
        assert datedOutput.contains("[D][ ] submit report (by: Dec 02 2026)");

        String invalidOutput = runFox(directory, classPath,
                "deadline invalid date /by 2026-02-30\nlist\nbye\n");
        assert invalidOutput.contains("valid date in yyyy-MM-dd format");
        assert !invalidOutput.contains("invalid date (by:");

        Path corruptDirectory = Files.createTempDirectory("fox-corrupt-startup");
        Path corruptFile = corruptDirectory.resolve("data").resolve("fox.txt");
        Files.createDirectories(corruptFile.getParent());
        String corruptContents = "corrupted content\n";
        Files.writeString(corruptFile, corruptContents, StandardCharsets.UTF_8);
        String corruptOutput = runFox(corruptDirectory, classPath, "bye\n");
        assert corruptOutput.contains("Invalid data");
        assert Files.readString(corruptFile).equals(corruptContents);
    }

    private static String runFox(Path directory, String classPath, String input)
            throws IOException, InterruptedException {
        Process process = new ProcessBuilder(System.getProperty("java.home") + "/bin/java",
                "-cp", classPath, "fox.Fox")
                .directory(directory.toFile())
                .redirectErrorStream(true)
                .start();
        process.getOutputStream().write(input.getBytes(StandardCharsets.UTF_8));
        process.getOutputStream().close();
        boolean hasFinished = process.waitFor(10, TimeUnit.SECONDS);
        assert hasFinished;
        assert process.exitValue() == 0;
        return new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}
