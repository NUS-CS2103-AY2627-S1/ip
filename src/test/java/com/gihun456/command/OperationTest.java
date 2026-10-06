package com.gihun456.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.gihun456.ErrorMessages;
import com.gihun456.GihunException;

public class OperationTest {
    @Test
    public void fromInput_supportedCommands_returnsMatchingOperation() throws GihunException {
        assertEquals(Operation.TODO, Operation.fromInput("TODO"));
        assertEquals(Operation.DEADLINE, Operation.fromInput("deadline"));
        assertEquals(Operation.EVENT, Operation.fromInput("event"));
        assertEquals(Operation.REMINDERS, Operation.fromInput("reminders"));
        assertEquals(Operation.LIST, Operation.fromInput("list"));
        assertEquals(Operation.FIND, Operation.fromInput("find"));
        assertEquals(Operation.MARK, Operation.fromInput("mark"));
        assertEquals(Operation.UNMARK, Operation.fromInput("unmark"));
        assertEquals(Operation.DELETE, Operation.fromInput("delete"));
        assertEquals(Operation.BYE, Operation.fromInput("bye"));
    }

    @Test
    public void fromInput_unknownCommand_throwsExpectedError() {
        GihunException exception = assertThrows(GihunException.class, () -> Operation.fromInput("launch"));

        assertEquals(ErrorMessages.invalidOperation("launch"), exception.getMessage());
    }
}
