package allmind;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests the chatbot's command-response behavior. */
public class AllMindTest {
    @Test
    public void getResponse_addAndList_returnsCreatedTask() {
        AllMind allMind = new AllMind();

        String addResponse = allMind.getResponse("todo test response");
        String listResponse = allMind.getResponse("list");

        assertTrue(addResponse.contains("test response"));
        assertTrue(listResponse.contains("test response"));
    }

    @Test
    public void getResponse_findMarkUnmarkAndDelete_updatesTasks() {
        AllMind allMind = new AllMind();
        String description = "searchable task " + System.nanoTime();
        allMind.getResponse("todo " + description);
        int taskNumber = allMind.getResponse("list").lines()
                .filter(line -> line.contains(description))
                .map(line -> line.substring(0, line.indexOf('.')))
                .mapToInt(Integer::parseInt)
                .findFirst()
                .orElseThrow();

        assertTrue(allMind.getResponse("find SEARCH").contains(description));
        assertEquals("Affirmative. Task marked as done.", allMind.getResponse("mark " + taskNumber));
        assertTrue(allMind.getResponse("list").contains("[X] " + description));
        assertEquals("Affirmative. Task marked as not done.", allMind.getResponse("unmark " + taskNumber));
        assertEquals("Affirmative. Task deleted.", allMind.getResponse("delete " + taskNumber));
        assertFalse(allMind.getResponse("list").contains(description));
    }

    @Test
    public void getResponse_invalidCommands_returnsError() {
        AllMind allMind = new AllMind();

        assertEquals("You have not provided a keyword.", allMind.getResponse("find"));
        assertEquals("Please provide a valid task number.", allMind.getResponse("mark abc"));
        assertEquals("I could not understand that command.", allMind.getResponse("unknown"));
        assertEquals("Goodbye!", allMind.getResponse("bye"));
    }

    @Test
    public void getResponse_conflictingEvent_yesAddsEventAndNoDiscardsEvent() {
        AllMind allMind = new AllMind();
        allMind.getResponse("event first /from 2099-10-20 /to 2099-10-22");

        String warning = allMind.getResponse("event second /from 2099-10-21 /to 2099-10-23");
        assertTrue(warning.contains("This event clashes with:"));
        assertTrue(allMind.getResponse("maybe").contains("'yes' or 'no'"));
        assertTrue(allMind.getResponse("yes").contains("second"));

        allMind.getResponse("event third /from 2099-10-21 /to 2099-10-23");
        assertEquals("Understood. Your input was discarded.", allMind.getResponse("no"));
    }
}
