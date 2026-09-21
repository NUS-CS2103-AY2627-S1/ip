package caitlyn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests keyword matching and validation for the find command. */
class FindCommandTest {
    @Test
    void findMatchingTasks_isCaseInsensitiveAndPreservesOriginalOrder() {
        Task firstMatch = new Todo("read book");
        Task nonMatch = new Todo("buy milk");
        Task secondMatch = new Deadline("return BOOK", "2025-03-14");
        FindCommand command = new FindCommand("BoOk");

        assertEquals(List.of(firstMatch, secondMatch),
                command.findMatchingTasks(List.of(firstMatch, nonMatch, secondMatch)));
    }

    @Test
    void execute_rejectsMissingKeyword() {
        FindCommand command = new FindCommand("   ");

        CaitlynException exception = assertThrows(CaitlynException.class, () ->
                command.execute(List.of(), new Ui(new java.util.Scanner(""))));

        assertEquals("I beg your pardon, master. Please provide a keyword, for example: find book.",
                exception.getMessage());
    }
}
