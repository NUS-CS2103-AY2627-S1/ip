package remy.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RemyExceptionTest {
    private static final String MESSAGE_DIVIDER = "____________________________________________________________\n";

    @Test
    void constructor_customMessage_framedMessageReturned() {
        RemyException exception = new RemyException("Custom error");

        assertEquals(MESSAGE_DIVIDER + "Custom error\n" + MESSAGE_DIVIDER, exception.getMessage());
    }

    @Test
    void createForMissingTodoDescription_descriptionMissing_guidanceReturned() {
        RemyException exception = RemyException.createForMissingTodoDescription();

        assertEquals(MESSAGE_DIVIDER
                + "Bruh :/, you need to fill in the description of your todo task, "
                + "unless you're doing nothing :/.\n"
                + "\nYour todo task should look something like this, please don't mess it up again -_-: \n"
                + "todo borrow book\n"
                + MESSAGE_DIVIDER, exception.getMessage());
    }

    @Test
    void createForMissingDeadlineDetails_bothDetailsMissing_requestedDetailsListed() {
        RemyException exception = RemyException.createForMissingDeadlineDetails(false, false);

        assertEquals(MESSAGE_DIVIDER
                + "Bruh :/, your deadline task is missing the following: \n"
                + "- description. \n"
                + "- DEADLINE of your DEADLINE task -_-. \n"
                + "\nYour deadline task should look something like this, please don't mess it up again -_-: \n"
                + "deadline return book /by Sunday\n"
                + MESSAGE_DIVIDER, exception.getMessage());
    }

    @Test
    void createForMissingEventDetails_startMissing_onlyStartListed() {
        RemyException exception = RemyException.createForMissingEventDetails(true, false, true);

        assertEquals(MESSAGE_DIVIDER
                + "Bruh :/, your event task is missing the following: \n"
                + "- when your event task starts. \n"
                + "\nYour event task should look something like this, please don't mess it up again -_-: \n"
                + "event project meeting /from Mon 2pm /to 4pm\n"
                + MESSAGE_DIVIDER, exception.getMessage());
    }
}
