package duncan.command;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class ListCommandTest {
    @Test
    public void isExit_neverExits_falseReturned() {
        assertFalse(new ListCommand().isExit());
    }

    // execute() is not covered here: it has no branching of its own, just
    // ui.showTaskList(tasks.getTasks()) - console output with nothing to
    // assert on except the printed text, which is already checked
    // end-to-end by the UI test plan (test/ui-test-plan.md TC04, TC05).
}
