package laby.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** Tests contact command types and parsed contact arguments. */
class CommandTest {

    @Test
    void from_contactCommandWords_returnsContactTypes() {
        assertEquals(CommandType.CONTACT, CommandType.from("contact"));
        assertEquals(CommandType.DELETE_CONTACT, CommandType.from("deletecontact"));
        assertEquals(CommandType.UNKNOWN, CommandType.from("listcontact"));
    }

    @Test
    void addContactCommand_storesContactFields() {
        Command command = new Command(CommandType.CONTACT, 0, "John Doe", "91234567",
                "john@example.com", null, null);

        assertEquals(CommandType.CONTACT, command.getCommandType());
        assertEquals("John Doe", command.getDescription());
        assertEquals("91234567", command.getPhone());
        assertEquals("john@example.com", command.getEmail());
        assertEquals(0, command.getId());
        assertNull(command.getFirstTime());
        assertNull(command.getSecondTime());
    }

    @Test
    void deleteAndListContactCommands_useExpectedArguments() {
        Command deleteCommand = new Command(CommandType.DELETE_CONTACT, 2, null, null, null, null, null);

        assertEquals(2, deleteCommand.getId());
    }
}
