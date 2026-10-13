package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/** Ensures point-form help stays complete, immutable, and consistent with the parser. */
class CommandHelpTest {
    @Test
    void getSections_allExamples_areValidAndCoverEveryCommand() throws WangsaException {
        Parser parser = new Parser();
        Set<Parser.CommandType> types = new HashSet<>();
        int entries = 0;
        for (CommandHelp.Section section : CommandHelp.getSections()) {
            for (CommandHelp.Entry entry : section.entries()) {
                types.add(parser.parse(entry.example()).type());
                assertTrue(CommandHelp.getText().contains(entry.format()));
                entries++;
            }
        }
        assertEquals(4, CommandHelp.getSections().size());
        assertEquals(12, entries);
        assertEquals(Set.of(Parser.CommandType.values()), types);
    }

    @Test
    void getSections_sharedReference_cannotBeChangedByViews() {
        assertThrows(UnsupportedOperationException.class, () -> CommandHelp.getSections().clear());
        assertThrows(UnsupportedOperationException.class, () -> CommandHelp.getSections().getFirst().entries().clear());
    }

    @Test
    void getText_usageRules_keepsSafetyAndFeatureLimits() {
        String help = CommandHelp.getText();

        assertTrue(help.contains("no undo"));
        assertTrue(help.contains("up to 100 tasks"));
        assertTrue(help.contains("do not execute commands"));
        assertTrue(help.contains("free-form text"));
        assertTrue(help.contains("reminders"));
        assertTrue(help.contains("•"));
    }
}
