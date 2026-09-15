package luke;

import org.junit.jupiter.api.Test;

/**
 * Tests Luke's command-line behavior.
 */
final class LukeCliTest {
    @Test
    void addsAndListsSupportedTaskTypes() {
        String output = TestSupport.runLuke("""
                todo read book
                deadline return book /by Sunday
                event project meeting /from Mon 2pm /to 4pm
                list
                bye
                """);

        TestSupport.assertContains(output, "[T][ ] read book");
        TestSupport.assertContains(output, "[D][ ] return book (by: Sunday)");
        TestSupport.assertContains(output, "[E][ ] project meeting (from: Mon 2pm to: 4:00 PM)");
        TestSupport.assertContains(output, "1. [T][ ] read book");
        TestSupport.assertContains(output, "2. [D][ ] return book (by: Sunday)");
        TestSupport.assertContains(output, "3. [E][ ] project meeting (from: Mon 2pm to: 4:00 PM)");
    }

    @Test
    void acceptsCaseInsensitiveKeywordsAndFlags() {
        String output = TestSupport.runLuke("""
                ToDo Case Test
                DEADLINE return book /BY Sunday
                EVENT reversed /TO 5pm /FROM 4pm
                list
                bye
                """);

        TestSupport.assertContains(output, "1. [T][ ] Case Test");
        TestSupport.assertContains(output, "2. [D][ ] return book (by: Sunday)");
        TestSupport.assertContains(output, "3. [E][ ] reversed (from: 4:00 PM to: 5:00 PM)");
    }

    @Test
    void marksAndUnmarksExistingTask() {
        String output = TestSupport.runLuke("""
                todo read book
                mark 1
                unmark 1
                bye
                """);

        TestSupport.assertContains(output, "Nice! I've marked this task as done:");
        TestSupport.assertContains(output, "[T][X] read book");
        TestSupport.assertContains(output, "OK! I've marked this task as not done yet:");
        TestSupport.assertContains(output, "[T][ ] read book");
    }

    @Test
    void deletesExistingTask() {
        String output = TestSupport.runLuke("""
                todo idk
                deadline ddl /by 1
                mark 2
                delete 2
                list
                delete 1
                list
                bye
                """);

        TestSupport.assertContains(output, "Noted. I've removed this task:");
        TestSupport.assertContains(output, "[D][X] ddl (by: 1)");
        TestSupport.assertContains(output, "Now you have 1 tasks in the list.");
        TestSupport.assertContains(output, "1. [T][ ] idk");
        TestSupport.assertContains(output, "Now you have 0 tasks in the list.");
        TestSupport.assertContains(output, "No items added.");
    }

    @Test
    void formatsDateAndTimeFlagValues() {
        String output = TestSupport.runLuke("""
                deadline submit report /by 2/12/2019 1800
                event meeting /from 2/12/2019 1800 /to 2/12/2019 1900
                list
                bye
                """);

        TestSupport.assertContains(output, "[D][ ] submit report (by: Dec 2 2019 6:00 PM)");
        TestSupport.assertContains(
                output,
                "[E][ ] meeting (from: Dec 2 2019 6:00 PM to: Dec 2 2019 7:00 PM)");
    }

    @Test
    void keepsUnrecognizedDateFlagValues() {
        String output = TestSupport.runLuke("""
                deadline submit report /by Sunday
                list
                bye
                """);

        TestSupport.assertContains(output, "[D][ ] submit report (by: Sunday)");
    }

    @Test
    void sortsListByTime() {
        String output = TestSupport.runLuke("""
                todo floating task
                deadline late task /by 2/12/2019 1900
                deadline early task /by 2/12/2019 1800
                event time only /from 1600 /to 1700
                deadline unknown task /by Sunday
                list /sort time
                bye
                """);

        TestSupport.assertContains(output, "1. [E][ ] time only (from: 4:00 PM to: 5:00 PM)");
        TestSupport.assertContains(output, "2. [D][ ] early task (by: Dec 2 2019 6:00 PM)");
        TestSupport.assertContains(output, "3. [D][ ] late task (by: Dec 2 2019 7:00 PM)");
        TestSupport.assertContains(output, "4. [T][ ] floating task");
        TestSupport.assertContains(output, "5. [D][ ] unknown task (by: Sunday)");
    }

    @Test
    void sortsListAlphabeticallyByTaskName() {
        String output = TestSupport.runLuke("""
                todo Zebra task
                deadline apple task /by Sunday
                event Banana task /from 4pm /to 5pm
                list /sort alpha
                bye
                """);

        TestSupport.assertContains(output, """
                1. [D][ ] apple task (by: Sunday)
                2. [E][ ] Banana task (from: 4:00 PM to: 5:00 PM)
                3. [T][ ] Zebra task""");
    }

    @Test
    void findsTasksByDescription() {
        String output = TestSupport.runLuke("""
                todo read book
                deadline return book /by Sunday
                event project meeting /from Mon 2pm /to 4pm
                find book
                bye
                """);

        TestSupport.assertContains(output, "Here are the matching tasks in your list:");
        TestSupport.assertContains(output, "1. [T][ ] read book");
        TestSupport.assertContains(output, "2. [D][ ] return book (by: Sunday)");
    }

    @Test
    void findsTasksWithCaseAndWhitespaceLeniency() {
        String output = TestSupport.runLuke("""
                todo read   CS notes
                todo read book
                find READ cs
                bye
                """);

        TestSupport.assertContains(output, "1. [T][ ] read   CS notes");
    }

    @Test
    void reportsNoMatchingTasks() {
        String output = TestSupport.runLuke("""
                todo read book
                find homework
                bye
                """);

        TestSupport.assertContains(output, "No matching tasks found.");
    }

    @Test
    void showsPlaceholderForEmptyList() {
        String output = TestSupport.runLuke("""
                list
                bye
                """);

        TestSupport.assertContains(output, "No items added.");
    }

}
