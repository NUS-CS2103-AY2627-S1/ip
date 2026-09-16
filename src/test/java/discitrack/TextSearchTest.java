package discitrack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.io.TempDir;

import discitrack.task.Task;

/**
 * Checks description preservation and literal searching across text and language settings.
 */
public class TextSearchTest {
    @TempDir
    public Path directory;

    @TestFactory
    public Stream<DynamicTest> descriptions_saveReloadAndFind_preserveOriginalText() {
        return Stream.of(
                new String[] {"Review lecture notes", "LECTURE NOTES"},
                new String[] {"Read C++ [chapter 2] (draft)?", "[chapter 2]"},
                new String[] {"复习数学笔记", "数学"},
                new String[] {"Exercise 🏃 and rest 🌙", "🏃"})
                .map(entry -> DynamicTest.dynamicTest("round trip: " + entry[0], () -> {
                    Path file = Files.createTempDirectory(directory, "text-").resolve("tasks.txt");
                    DisciTrack app = new DisciTrack(file.toString());
                    app.getResponse("todo unrelated");
                    app.getResponse("todo " + entry[0]);
                    assertFalse(app.hasResponseError());
                    app = new DisciTrack(file.toString());
                    assertFalse(app.hasLoadError());
                    assertEquals(entry[0], app.getTasks().get(1).getActivity());
                    assertEquals(List.of(entry[0]),
                            app.getMatchingTasks(entry[1]).stream().map(Task::getActivity).toList());
                    String original = Files.readString(file);
                    String response = app.getResponse("find " + entry[1]);
                    assertFalse(app.hasResponseError());
                    assertTrue(response.contains("2. [T] [ ] " + entry[0]));
                    assertEquals(original, Files.readString(file));
                }));
    }

    @TestFactory
    public Stream<DynamicTest> languageSettings_datesAndSearch_workInEnglishAndChinese() {
        return Stream.of(Locale.ENGLISH, Locale.CHINESE).map(locale ->
                DynamicTest.dynamicTest("language: " + locale, () -> {
                    Locale original = Locale.getDefault();
                    try {
                        Locale.setDefault(locale);
                        Path file = Files.createTempDirectory(directory, "locale-").resolve("tasks.txt");
                        DisciTrack app = new DisciTrack(file.toString());
                        app.getResponse("deadline Review 数学 /by 2026-09-30");
                        assertFalse(app.hasResponseError());
                        assertEquals(1, app.getMatchingTasks("review 数学").size());
                        app = new DisciTrack(file.toString());
                        assertFalse(app.hasLoadError());
                        app.getResponse("checkdate 2026-09-30");
                        assertFalse(app.hasResponseError());
                        assertTrue(app.getLastResponse().contains("Review 数学"));
                    } finally {
                        Locale.setDefault(original);
                    }
                }));
    }
}
