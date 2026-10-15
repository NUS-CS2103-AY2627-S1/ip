package trybot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import trybot.ui.ChatReply;

/** Tests graphical reply states without depending on JavaFX or matching error wording. */
public class ChatReplyTest {
    @TempDir
    public Path directory;

    @Test
    public void getChatReply_invalidThenValid_resetsErrorAndPreservesTasks() {
        TryBot bot = new TryBot(directory.resolve("tasks.txt").toString());
        for (String input : new String[]{"unknown", "todo", "mark 0", "deadline report", ""}) {
            ChatReply reply = bot.getChatReply(input);
            assertTrue(reply.hasError());
            assertFalse(reply.hasWarning());
            assertFalse(reply.isExit());
            assertTrue(reply.text().contains("No worries!"));
            assertTrue(reply.text().contains("You've got this!"));
        }
        ChatReply reply = bot.getChatReply("list");
        assertFalse(reply.hasError());
        assertTrue(reply.text().contains("Your list is a fresh start—"));
    }

    @Test
    public void getChatReply_addAndTag_invitesContextAndRetainsUnicode() {
        TryBot bot = new TryBot(directory.resolve("tasks.txt").toString());
        ChatReply reply = bot.getChatReply("todo café notes");
        assertFalse(reply.hasError());
        assertTrue(reply.text().contains("[T][ ] café notes"));
        assertTrue(reply.text().contains("You now have 1 task."));
        assertTrue(reply.text().contains("tag 1 school"));
        assertTrue(bot.getChatReply("todo read").text().contains("You now have 2 tasks."));
        assertTrue(bot.getChatReply("tag 1 study").text().contains("#study"));
        String list = bot.getChatReply("list").text();
        assertTrue(list.contains("1. [T][ ] café notes #study\n2. [T][ ] read"));
        assertFalse(list.contains("____"));
    }

    @Test
    public void getChatReply_completionAndReopening_updatesStatusWithEncouragement() {
        TryBot bot = new TryBot(directory.resolve("tasks.txt").toString());
        bot.getChatReply("todo read");
        assertEquals("Fantastic job! You did it—one more thing checked off:\n[T][X] read\n"
                + "You should feel proud! Keep that momentum going!",
                bot.getChatReply("mark 1").text());
        assertEquals("No worries—every step counts. I've put this back on your to-do list:\n[T][ ] read\n"
                + "You can come back to it whenever you're ready!",
                bot.getChatReply("unmark 1").text());
    }

    @Test
    public void getChatReply_errorWordsInTask_remainsNormalReply() {
        TryBot bot = new TryBot(directory.resolve("tasks.txt").toString());
        ChatReply reply = bot.getChatReply("todo Warning: fix error");
        assertFalse(reply.hasError());
        assertFalse(reply.hasWarning());
    }

    @Test
    public void getChatReply_saveFailure_reportsWarningWithoutInvitingDuplicateSubmission() throws Exception {
        Path parent = directory.resolve("blocked");
        TryBot bot = new TryBot(parent.resolve("tasks.txt").toString());
        Files.writeString(parent, "This file prevents creation of a directory.");
        ChatReply reply = bot.getChatReply("todo keep in memory");
        assertTrue(reply.hasWarning());
        assertFalse(reply.hasError());
        assertTrue(reply.text().contains("couldn't save"));
        assertTrue(bot.getChatReply("list").text().contains("1. [T][ ] keep in memory"));
    }

    @Test
    public void getChatReply_byeAlias_setsExitFlag() {
        TryBot bot = new TryBot(directory.resolve("tasks.txt").toString());
        ChatReply reply = bot.getChatReply(" BYE! ");
        assertTrue(reply.isExit());
        assertFalse(reply.hasError());
        assertEquals("You did great today! Thanks for sharing your to-do list with me. See you soon!", reply.text());
    }
}
