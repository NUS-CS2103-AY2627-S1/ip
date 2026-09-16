package walter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import walter.storage.Storage;

/** Exhaustively checks hostile index spellings against every index-based command. */
public class IndexAdversarialTest {
    private static final String[] INDEX_FORMS = {
        "", "0", "-0", "-1", "-2", "-999", "+1", "+0", "01", "001",
        "1", "2", "999999", "2147483647", "2147483648", "2147483649",
        "-2147483648", "-2147483649", "9223372036854775807", "1.0", "1.5",
        ".5", "1e3", "NaN", "Infinity", "abc", "1abc", "abc1", "--", "++",
        " 1 ", "1 2"
    };

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void everyHostileIndex_everyIndexCommand_exactOutputStateAndPersistence() {
        String[] taskCommands = {"mark", "done", "unmark", "delete"};
        int caseNumber = 0;
        for (String commandWord : taskCommands) {
            for (String indexText : INDEX_FORMS) {
                Storage storage = storageFor("task-" + caseNumber++);
                Walter walter = new Walter(storage);
                walter.getResponse("todo first");
                walter.getResponse("todo second");
                boolean initiallyDone = commandWord.equals("unmark");
                if (initiallyDone) {
                    walter.getResponse("mark 1");
                    walter.getResponse("mark 2");
                }

                String command = toCommand(commandWord, indexText);
                IndexExpectation index = expectation(indexText, "Task");
                String expectedResponse = expectedTaskResponse(commandWord, index);
                String actualResponse = walter.getResponse(command);

                assertEquals(expectedResponse, actualResponse,
                        "Unexpected response for: " + command);
                Walter restarted = new Walter(storage);
                assertEquals(expectedTaskList(commandWord, index, initiallyDone),
                        restarted.getResponse("list"),
                        "Unexpected persisted state for: " + command);
            }
        }

        for (String indexText : INDEX_FORMS) {
            Storage storage = storageFor("place-" + caseNumber++);
            Walter walter = new Walter(storage);
            walter.getResponse("place first /at address one");
            walter.getResponse("place second /at address two");
            String command = toCommand("deleteplace", indexText);
            IndexExpectation index = expectation(indexText, "Place");

            assertEquals(expectedPlaceResponse(index), walter.getResponse(command),
                    "Unexpected response for: " + command);
            assertEquals(expectedPlaceList(index), new Walter(storage).getResponse("places"),
                    "Unexpected persisted state for: " + command);
        }
    }

    private Storage storageFor(String name) {
        return new Storage(temporaryDirectory.resolve(name).resolve("walter.txt"));
    }

    private static String toCommand(String commandWord, String indexText) {
        return indexText.isEmpty() ? commandWord : commandWord + " " + indexText;
    }

    private static IndexExpectation expectation(String indexText, String entity) {
        String normalized = indexText.strip();
        if (normalized.isEmpty()) {
            return new IndexExpectation(-1, error(entity + " number is required."));
        }
        int oneBased;
        try {
            oneBased = Integer.parseInt(normalized);
        } catch (NumberFormatException exception) {
            return new IndexExpectation(-1,
                    error(entity + " number must be an integer."));
        }
        int index = oneBased - 1;
        if (index < 0 || index >= 2) {
            return new IndexExpectation(-1, error(entity + " number is out of range."));
        }
        return new IndexExpectation(index, null);
    }

    private static String expectedTaskResponse(String commandWord, IndexExpectation index) {
        if (index.error != null) {
            return index.error;
        }
        String description = index.index == 0 ? "first" : "second";
        String task;
        if (commandWord.equals("delete")) {
            task = "[T][ ] " + description;
            return "Done. I've removed this from the list:\n" + task
                    + "\nThe list now contains 1 task.";
        }
        if (commandWord.equals("unmark")) {
            task = "[T][ ] " + description;
            return "Understood. This goes back into the mix:\n" + task;
        }
        task = "[T][X] " + description;
        return "Done. Consider this one handled:\n" + task;
    }

    private static String expectedTaskList(String commandWord, IndexExpectation index,
            boolean initiallyDone) {
        boolean firstDone = initiallyDone;
        boolean secondDone = initiallyDone;
        boolean firstPresent = true;
        boolean secondPresent = true;
        if (index.error == null) {
            if (commandWord.equals("delete")) {
                firstPresent = index.index != 0;
                secondPresent = index.index != 1;
            } else if (commandWord.equals("unmark")) {
                if (index.index == 0) {
                    firstDone = false;
                } else {
                    secondDone = false;
                }
            } else if (index.index == 0) {
                firstDone = true;
            } else {
                secondDone = true;
            }
        }
        StringBuilder result = new StringBuilder("Here are your tasks:");
        int displayIndex = 1;
        if (firstPresent) {
            result.append('\n').append(displayIndex++).append(". [T][")
                    .append(firstDone ? "X" : " ").append("] first");
        }
        if (secondPresent) {
            result.append('\n').append(displayIndex).append(". [T][")
                    .append(secondDone ? "X" : " ").append("] second");
        }
        return result.toString();
    }

    private static String expectedPlaceResponse(IndexExpectation index) {
        if (index.error != null) {
            return index.error;
        }
        String place = index.index == 0
                ? "first — address one"
                : "second — address two";
        return "Done. I've removed this location:\n" + place
                + "\nThe location list now contains 1 place.";
    }

    private static String expectedPlaceList(IndexExpectation index) {
        if (index.error != null) {
            return "Here are the recorded locations:\n"
                    + "1. first — address one\n"
                    + "2. second — address two";
        }
        String remaining = index.index == 0
                ? "second — address two"
                : "first — address one";
        return "Here are the recorded locations:\n1. " + remaining;
    }

    private static String error(String message) {
        return message + " Stay sharp, Jesse.";
    }

    private record IndexExpectation(int index, String error) {
    }
}
