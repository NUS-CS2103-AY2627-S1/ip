package trybot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

/** Tests the application entry point used by the JavaFX client. */
public class TryBotTest {
    @Test
    public void getResponse_todoCommand_returnsTaskConfirmation() throws Exception {
        Path dataFile = Files.createTempFile("trybot-test", ".txt");
        try {
            TryBot tryBot = new TryBot(dataFile.toString());
            assertEquals("Got it. I've added this task:\n[T][ ] read book\n"
                    + "Now you have 1 tasks in the list.", tryBot.getResponse("todo read book"));
        } finally {
            Files.deleteIfExists(dataFile);
        }
    }

    @Test
    public void getResponse_invalidCommand_returnsParserError() throws Exception {
        Path dataFile = Files.createTempFile("trybot-test", ".txt");
        try {
            TryBot tryBot = new TryBot(dataFile.toString());
            assertEquals("I do not recognise that command. Try help to show the list of commands.",
                    tryBot.getResponse("unknown"));
        } finally {
            Files.deleteIfExists(dataFile);
        }
    }

    @Test
    public void getResponse_byeCommand_returnsGoodbyeMessage() throws Exception {
        Path dataFile = Files.createTempFile("trybot-test", ".txt");
        try {
            TryBot tryBot = new TryBot(dataFile.toString());
            assertEquals("Bye. Hope to see you again soon!", tryBot.getResponse("BYE!"));
        } finally {
            Files.deleteIfExists(dataFile);
        }
    }

    @Test
    public void getResponse_tagCommand_tagsTaskAndListsTag() throws Exception {
        Path dataFile = Files.createTempFile("trybot-test", ".txt");
        try {
            TryBot tryBot = new TryBot(dataFile.toString());
            tryBot.getResponse("todo submit report");

            assertEquals("Tag Created for Task 1: urgent", tryBot.getResponse("tag 1 urgent"));
            assertEquals("Here are the tasks in your list:\n1.[T][ ] submit report #urgent",
                    tryBot.getResponse("list"));
        } finally {
            Files.deleteIfExists(dataFile);
        }
    }

    @Test
    public void getResponse_tagDeleteCommand_removesTagsFromTask() throws Exception {
        Path dataFile = Files.createTempFile("trybot-test", ".txt");
        try {
            TryBot tryBot = new TryBot(dataFile.toString());
            tryBot.getResponse("todo submit report");
            tryBot.getResponse("tag 1 urgent");
            tryBot.getResponse("tag 1 school");

            assertEquals("Tags Deleted for Task 1", tryBot.getResponse("tagdel 1"));
            assertEquals("Here are the tasks in your list:\n1.[T][ ] submit report",
                    tryBot.getResponse("list"));
        } finally {
            Files.deleteIfExists(dataFile);
        }
    }
}
