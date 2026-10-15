package walnut;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WalnutValidationTest {
    private Walnut walnut;

    @BeforeEach
    void setUp() {
        walnut = new Walnut();
    }

    @Test
    void blankCommand_returnsFriendlyError() {
        assertTrue(walnut.getResponse("   ").toLowerCase().contains("command"));
    }

    @Test
    void unknownCommand_returnsInvalidCommandMessage() {
        assertTrue(walnut.getResponse("archive something").contains("Invalid"));
    }

    @Test
    void todoWithoutDescription_returnsDescriptionError() {
        assertTrue(walnut.getResponse("todo").toLowerCase().contains("description"));
    }

    @Test
    void deadlineWithoutBy_returnsFormatError() {
        assertTrue(walnut.getResponse("deadline submit report")
                .toLowerCase().contains("format"));
    }

    @Test
    void eventWithInvalidDate_returnsDateError() {
        assertTrue(walnut.getResponse("event meeting /from invalid /to invalid")
                .toLowerCase().contains("datetime"));
    }

    @Test
    void markWithoutTaskNumber_returnsMissingNumberErrorWhenTasksExist() {
        assertTrue(walnut.getResponse("mark").toLowerCase().contains("task"));
    }

    @Test
    void findWithoutKeyword_returnsError() {
        String response = walnut.getResponse("find").toLowerCase();

        assertTrue(
                response.contains("keyword")
                        || response.contains("empty")
                        || response.contains("task list")
        );
    }
}
