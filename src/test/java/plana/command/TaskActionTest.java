package plana.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TaskActionTest {

    @Test
    void getCommandText_allTaskActions_expectedTextReturned() {
        assertEquals("delete", TaskAction.DELETE.getCommandText());
        assertEquals("mark", TaskAction.MARK.getCommandText());
        assertEquals("unmark", TaskAction.UNMARK.getCommandText());
    }
}
