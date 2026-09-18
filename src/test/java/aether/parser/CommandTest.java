package aether.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CommandTest {
    @Test
    void commandStoresTypeAndArguments() {
        Command command = new Command(CommandType.FIND, "book");

        assertEquals(CommandType.FIND, command.getType());
        assertEquals("book", command.getArguments());
    }

    @Test
    void everyCommandTypeExposesItsTypedWord() {
        String[] expectedWords = {
            "list", "find", "sort", "mark", "unmark", "delete", "todo", "deadline", "event", "bye"
        };

        CommandType[] commandTypes = CommandType.values();
        assertEquals(expectedWords.length, commandTypes.length);
        for (int index = 0; index < commandTypes.length; index++) {
            assertEquals(expectedWords[index], commandTypes[index].getCommandWord());
        }
    }
}
