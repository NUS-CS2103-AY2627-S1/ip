package botavius.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import botavius.exception.BotaviusException;
import botavius.tasklist.TaskList;

/** Tests extraction of named parameters from Botavius commands. */
class ParserTest {

    /** Verifies that an unsupported command is rejected instead of creating a task. */
    @Test
    void process_unknownCommand_doesNotCreateTask() {
        TaskList taskList = new TaskList("");

        assertThrows(BotaviusException.class, () -> Parser.process("buy milk", taskList));
        assertEquals("Here are the tasks in your list:\n", taskList.listTasks());
    }

    /** Verifies that the implicit task value and one named value are extracted. */
    @Test
    void getNamedParameters_taskAndOneParameter_returnsBothValues() {
        Map<String, String> parameters = Parser.getNamedParameters(
                "return book /by Sunday");

        assertEquals(Map.of("/task", "return book", "/by", "Sunday"), parameters);
    }

    /** Verifies that values containing spaces retain their complete trimmed text. */
    @Test
    void getNamedParameters_multipleParameters_preservesEachValue() {
        Map<String, String> parameters = Parser.getNamedParameters(
                "project meeting /from Mon 2pm /to 4pm");

        assertEquals("project meeting", parameters.get("/task"));
        assertEquals("Mon 2pm", parameters.get("/from"));
        assertEquals("4pm", parameters.get("/to"));
        assertEquals(3, parameters.size());
    }

    /** Verifies that surrounding whitespace is not included in parameter values. */
    @Test
    void getNamedParameters_extraWhitespace_trimsValues() {
        Map<String, String> parameters = Parser.getNamedParameters(
                "  buy milk   /by   tomorrow  ");

        assertEquals("buy milk", parameters.get("/task"));
        assertEquals("tomorrow", parameters.get("/by"));
    }

    /** Verifies that a command with no slash-prefixed parameter is still treated as a task. */
    @Test
    void getNamedParameters_noNamedParameter_returnsTaskOnly() {
        Map<String, String> parameters = Parser.getNamedParameters("borrow book");

        assertEquals(Map.of("/task", "borrow book"), parameters);
    }

    /** Verifies that an empty value is represented rather than omitted. */
    @Test
    void getNamedParameters_parameterWithoutValue_returnsEmptyValue() {
        Map<String, String> parameters = Parser.getNamedParameters("return book /by");

        assertEquals("return book", parameters.get("/task"));
        assertTrue(parameters.containsKey("/by"));
        assertEquals("", parameters.get("/by"));
    }
}
