package megia.exception;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

class MegiaExceptionTest {
    @Test
    void userInputException_copiesMessageArgumentsDefensively() {
        Object[] originalArguments = {"launch"};
        UserInputException exception = new UserInputException(
                ErrorCode.UNKNOWN_COMMAND, originalArguments);
        originalArguments[0] = "changed";

        Object[] returnedArguments = exception.getMessageArguments();
        returnedArguments[0] = "changed again";

        assertArrayEquals(new Object[] {"launch"}, exception.getMessageArguments());
    }

    @Test
    void userInputException_withNullArgumentArray_storesEmptyArguments() {
        UserInputException exception = new UserInputException(
                ErrorCode.TASK_ID_NOT_POSITIVE, (Object[]) null);

        assertArrayEquals(new Object[0], exception.getMessageArguments());
    }
}
