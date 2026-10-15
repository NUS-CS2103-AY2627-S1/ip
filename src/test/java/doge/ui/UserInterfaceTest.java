package doge.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import doge.model.Todo;

/** Tests deterministic text formatting performed by the user interface. */
class UserInterfaceTest {

    /** Verifies that matching tasks are numbered in their supplied order. */
    @Test
    void printMatchingTasks_matchingTasks_returnsNumberedDogeMessage() {
        UI ui = new UI();

        String output = ui.printMatchingTasks(List.of(new Todo("Buy treats"), new Todo("Walk Doge")));

        assertEquals("    Sniff sniff... here are the matching tasks:\n"
                + "    1.[T][ ] Buy treats\n"
                + "    2.[T][ ] Walk Doge", output);
    }

    /** Verifies that no-match results still produce a useful heading. */
    @Test
    void printMatchingTasks_noMatches_returnsHeadingOnly() {
        UI ui = new UI();

        assertEquals("    Sniff sniff... here are the matching tasks:",
                ui.printMatchingTasks(List.of()));
    }
}
