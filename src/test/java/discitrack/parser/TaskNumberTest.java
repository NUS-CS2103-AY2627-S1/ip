package discitrack.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.io.TempDir;

import discitrack.DisciTrack;

/**
 * Checks numeric syntax and range errors through the GUI's command interface.
 */
public class TaskNumberTest {
    @TempDir
    public Path directory;

    @TestFactory
    public Stream<DynamicTest> invalidTaskNumbers_rejectCommand_withoutChangingData() {
        return Stream.of("mark", "unmark", "delete").flatMap(command ->
                Stream.of("", "1.5", "abc", "-1", "0", "3", "999999999999999999999").map(number ->
                        DynamicTest.dynamicTest(command + " [" + number + "]", () -> {
                            Path file = Files.createTempDirectory(directory, "number-").resolve("tasks.txt");
                            DisciTrack app = new DisciTrack(file.toString());
                            app.getResponse("todo first");
                            app.getResponse("todo second");
                            app.getResponse("mark 2");
                            String original = Files.readString(file);

                            String response = app.getResponse(command + " " + number);
                            assertTrue(app.hasResponseError());
                            assertFalse(response.isBlank());
                            assertEquals(List.of("first", "second"),
                                    app.getTasks().stream().map(task -> task.getActivity()).toList());
                            assertFalse(app.getTasks().get(0).isDone());
                            assertTrue(app.getTasks().get(1).isDone());
                            assertEquals(original, Files.readString(file));
                        })));
    }
}
