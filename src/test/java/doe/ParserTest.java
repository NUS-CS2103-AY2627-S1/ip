package doe;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests the conversion of user-entered menu commands into menu values. */
class ParserTest {

    @Test
    void mainMenu_aliasesAndWhitespace_commandRecognised() {
        assertEquals(Parser.MainMenu.NEIGH, Parser.MainMenu.fromString("  1  "));
        assertEquals(Parser.MainMenu.NEIGH, Parser.MainMenu.fromString("NeIgH"));
        assertEquals(Parser.MainMenu.MEOW, Parser.MainMenu.fromString("2"));
        assertEquals(Parser.MainMenu.LIST, Parser.MainMenu.fromString(" list "));
        assertEquals(Parser.MainMenu.TODO, Parser.MainMenu.fromString("TODO"));
        assertEquals(Parser.MainMenu.BYE, Parser.MainMenu.fromString("bye"));
        assertEquals(Parser.MainMenu.UNKNOWN, Parser.MainMenu.fromString("hello"));
    }

    @Test
    void todoMenu_allCommandsAndInvalidCommand_expectedValuesReturned() {
        assertEquals(Parser.TodoMenu.ADD, Parser.TodoMenu.fromString("1"));
        assertEquals(Parser.TodoMenu.REMOVE, Parser.TodoMenu.fromString("remove"));
        assertEquals(Parser.TodoMenu.VIEW, Parser.TodoMenu.fromString(" 3 "));
        assertEquals(Parser.TodoMenu.SORT, Parser.TodoMenu.fromString("4"));
        assertEquals(Parser.TodoMenu.SORT, Parser.TodoMenu.fromString(" SoRt "));
        assertEquals(Parser.TodoMenu.MARK, Parser.TodoMenu.fromString("5"));
        assertEquals(Parser.TodoMenu.FIND, Parser.TodoMenu.fromString("7"));
        assertEquals(Parser.TodoMenu.EXIT, Parser.TodoMenu.fromString("8"));
        assertEquals(Parser.TodoMenu.MARK, Parser.TodoMenu.fromString("MARK"));
        assertEquals(Parser.TodoMenu.UNMARK, Parser.TodoMenu.fromString("6"));
        assertEquals(Parser.TodoMenu.EXIT, Parser.TodoMenu.fromString("exit"));
        assertEquals(Parser.TodoMenu.UNKNOWN, Parser.TodoMenu.fromString("delete all"));
    }

    @Test
    void taskMenu_allCommandsAndInvalidCommand_expectedValuesReturned() {
        assertEquals(Parser.TaskMenu.TODO, Parser.TaskMenu.fromString("todo"));
        assertEquals(Parser.TaskMenu.DEADLINE, Parser.TaskMenu.fromString("2"));
        assertEquals(Parser.TaskMenu.EVENT, Parser.TaskMenu.fromString(" EVENT "));
        assertEquals(Parser.TaskMenu.EXIT, Parser.TaskMenu.fromString("4"));
        assertEquals(Parser.TaskMenu.UNKNOWN, Parser.TaskMenu.fromString("appointment"));
    }
}
