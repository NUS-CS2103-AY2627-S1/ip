package luke;

import org.junit.jupiter.api.Test;

/**
 * Tests chatbot errors for invalid command-line input.
 */
final class LukeValidationTest {
    @Test
    void rejectsMalformedTaskInput() {
        String output = TestSupport.runLuke("""
                todo
                deadline /by someday
                deadline return book /by Sunday /by Monday
                deadline return book /er idk
                todo read book /by Sunday
                deadline return book /to Sunday
                event call /from now /by later
                bye
                """);

        TestSupport.assertContains(output, "Missing description for todo task.");
        TestSupport.assertContains(output, "Missing description for deadline task.");
        TestSupport.assertContains(output, "Duplicate flag: /by");
        TestSupport.assertContains(output, "Unidentified flag: /er");
        TestSupport.assertContains(output, "Unsupported flag: /by");
        TestSupport.assertContains(output, "Unsupported flag: /to");
    }

    @Test
    void rejectsMissingFlagValues() {
        String output = TestSupport.runLuke("""
                deadline return book /by
                event call /from /to later
                event call /from now /to
                bye
                """);

        TestSupport.assertContains(output, "Missing value for flag: /by");
        TestSupport.assertContains(output, "Missing value for flag: /from");
        TestSupport.assertContains(output, "Missing value for flag: /to");
    }

    @Test
    void rejectsExtraArguments() {
        String output = TestSupport.runLuke("""
                list ijdad
                bye jiadjiajd
                bye
                """);

        TestSupport.assertContains(output, "`list` command does not take arguments: ijdad");
        TestSupport.assertContains(output, "`bye` command does not take arguments: jiadjiajd");
    }

    @Test
    void rejectsInvalidListSortValues() {
        String output = TestSupport.runLuke("""
                list /sort name
                bye
                """);

        TestSupport.assertContains(output, "Unsupported value for /sort: name");
    }

    @Test
    void rejectsMissingFindArguments() {
        String output = TestSupport.runLuke("""
                find
                bye
                """);

        TestSupport.assertContains(output, "`find` command requires an argument.");
    }

    @Test
    void rejectsFlagsForFindCommand() {
        String output = TestSupport.runLuke("""
                find book /sort time
                bye
                """);

        TestSupport.assertContains(output, "Unsupported flag: /sort");
    }

    @Test
    void rejectsSortFlagForTaskCommands() {
        String output = TestSupport.runLuke("""
                todo read book /sort time
                bye
                """);

        TestSupport.assertContains(output, "Unsupported flag: /sort");
    }

    @Test
    void rejectsBadMarkIndexes() {
        String output = TestSupport.runLuke("""
                mark banana
                mark 0
                mark -1
                mark 1
                bye
                """);

        TestSupport.assertContains(output, "`mark` command received invalid index: banana");
        TestSupport.assertContains(output, "`mark` command received out-of-bounds index: 0");
        TestSupport.assertContains(output, "`mark` command received out-of-bounds index: -1");
        TestSupport.assertContains(output, "`mark` command received out-of-bounds index: 1");
    }

    @Test
    void rejectsUnsupportedControlCharacters() {
        String output = TestSupport.runLuke("event project meeting /from Mon /to Tue\u001B[D\nbye\n");

        TestSupport.assertContains(output, "Input contains unsupported control characters.");
    }

    @Test
    void rejectsEventsThatDoNotEndAfterTheirStart() {
        String output = TestSupport.runLuke("""
                event meeting /from 2/12/2019 1900 /to 2/12/2019 1800
                event call /from 5pm /to 5pm
                list
                bye
                """);

        TestSupport.assertContains(output, "Event end must be after its start.");
        TestSupport.assertContains(output, "No items added.");
    }
}
