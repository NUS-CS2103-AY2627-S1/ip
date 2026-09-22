package nico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class ParserTest {
    @Test
    void parseCommand_noArgumentCommands_correctCommandCreated() throws NicoException {
        assertInstanceOf(ExitCommand.class, Parser.parseCommand("bye"));
        assertInstanceOf(ListCommand.class, Parser.parseCommand("list"));
        assertInstanceOf(UrgentCommand.class, Parser.parseCommand("urgent"));
    }

    @Test
    void parseCommand_argumentCommands_correctCommandCreated() throws NicoException {
        assertInstanceOf(MarkCommand.class, Parser.parseCommand("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parseCommand("unmark 1"));
        assertInstanceOf(TodoCommand.class, Parser.parseCommand("todo read book"));
        assertInstanceOf(DeadlineCommand.class, Parser.parseCommand("deadline read book /by 01-01-2026 0900"));
        assertInstanceOf(EventCommand.class,
                Parser.parseCommand("event meeting /from 01-01-2026 0900 /to 01-01-2026 1000"));
        assertInstanceOf(DeleteCommand.class, Parser.parseCommand("delete 1"));
    }

    @Test
    void parseCommand_emptyCommand_nicoExceptionThrown() {
        NicoException exception = assertThrows(NicoException.class, () -> Parser.parseCommand(""));

        assertEquals("Aiyoh! I don't recognise that command." + System.lineSeparator()
                + "Try this lah: todo DESCRIPTION, list, mark TASK_NUMBER, or bye", exception.getMessage());
    }

    @Test
    void parseCommand_whitespaceCommand_nicoExceptionThrown() {
        NicoException exception = assertThrows(NicoException.class, () -> Parser.parseCommand("   "));

        assertEquals("Aiyoh! I don't recognise that command." + System.lineSeparator()
                + "Try this lah: todo DESCRIPTION, list, mark TASK_NUMBER, or bye", exception.getMessage());
    }

    @Test
    void parseCommand_unknownCommand_nicoExceptionThrown() {
        NicoException exception = assertThrows(NicoException.class, () -> Parser.parseCommand("foo"));

        assertEquals("Aiyoh! I don't recognise that command." + System.lineSeparator()
                + "Try this lah: todo DESCRIPTION, list, mark TASK_NUMBER, or bye", exception.getMessage());
    }

    @Test
    void parseCommand_urgentWithArgument_nicoExceptionThrown() {
        NicoException exception = assertThrows(NicoException.class, () -> Parser.parseCommand("urgent now"));

        assertEquals("Aiyoh! `urgent` does not take extra text." + System.lineSeparator() + "Try this lah: urgent",
                exception.getMessage());
    }

    @Test
    void parseDateTime_validDateTime_parsedDateTimeReturned() throws NicoException {
        LocalDateTime result = Parser.parseDateTime("25-08-2026 1900", Nico.DATE_TIME_INPUT_FORMAT);

        assertEquals(LocalDateTime.of(2026, 8, 25, 19, 0), result);
    }

    @Test
    void parseDateTime_invalidDateTime_nicoExceptionThrown() {
        NicoException exception = assertThrows(NicoException.class, () ->
                Parser.parseDateTime("Sunday", Nico.DATE_TIME_INPUT_FORMAT));

        assertEquals("Aiyoh! The date and time is invalid." + System.lineSeparator() + "Try this lah: dd-MM-yyyy HHmm",
                exception.getMessage());
    }
}
