package elsa.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link CommandType}, the list of words the chatbot understands.
 *
 * <p>Nearly all of the behaviour is in {@link CommandType#fromKeyword}, which
 * decides whether a word names a command at all. Two of its answers are special:
 * an empty line is NOTHING rather than UNKNOWN, so the user can be told they
 * typed nothing instead of being told their nothing was not understood; and
 * UNKNOWN itself has a null keyword so that no input can match it by accident.
 */
public class CommandTypeTest {

    @Test
    public void fromKeyword_everyRealKeyword_findsItsOwnCommand() {
        for (CommandType command : CommandType.values()) {
            if (command == CommandType.UNKNOWN) {
                continue;
            }
            assertEquals(command, CommandType.fromKeyword(command.getKeyword()),
                    "the keyword of " + command + " no longer finds it");
        }
    }

    /**
     * The window introduces itself with this list, so a command missing from it
     * is one a new user has no way of discovering.
     */
    @Test
    public void getUsages_everyCommandAUserCanType_isListed() {
        List<String> usages = CommandType.getUsages();

        for (CommandType command : CommandType.values()) {
            boolean isTypeable = command != CommandType.NOTHING && command != CommandType.UNKNOWN;
            if (isTypeable) {
                assertTrue(usages.contains(command.getUsage()),
                        command + " is missing from the list shown to the user");
            }
        }
    }

    /**
     * NOTHING and UNKNOWN are what the chatbot calls a blank line and a word it
     * does not know. Listing either would tell the user to type something that
     * is not a command.
     */
    @Test
    public void getUsages_nothingAndUnknown_areLeftOut() {
        List<String> usages = CommandType.getUsages();

        assertEquals(CommandType.values().length - 2, usages.size(),
                "exactly the two commands nobody types should be left out");
        assertFalse(usages.contains(""), "NOTHING has no usage and must not be listed");
        assertFalse(usages.contains(null), "UNKNOWN has no usage and must not be listed");
    }

    /**
     * Help is read by scanning it for the one line that matters, so a command
     * filed under the wrong heading is worse than an unfiled one: the user reads
     * the group they want and concludes the chatbot cannot do it.
     */
    @Test
    public void getUsagesByGroup_everyCommandAUserCanType_isUnderExactlyOneHeading() {
        Map<String, List<String>> groups = CommandType.getUsagesByGroup();

        for (String usage : CommandType.getUsages()) {
            long headings = groups.values().stream()
                    .filter(usages -> usages.contains(usage))
                    .count();
            assertEquals(1, headings, usage + " is not under exactly one heading");
        }
    }

    /**
     * The headings have to come out in the order a new user should meet them,
     * which is the order the constants are declared in. A plain HashMap would
     * hand them back in whatever order suited it, putting leaving before adding
     * as readily as not, and nothing else would notice.
     */
    @Test
    public void getUsagesByGroup_theHeadings_runFromAddingToLeaving() {
        List<String> headings = List.copyOf(CommandType.getUsagesByGroup().keySet());

        assertEquals(4, headings.size(), "there should be four headings");
        assertTrue(headings.get(0).contains("Adding"), "adding comes first");
        assertTrue(headings.get(headings.size() - 1).contains("Anything else"),
                "the commands about the chatbot itself come last");
    }

    /** No heading should be shown with nothing under it. */
    @Test
    public void getUsagesByGroup_everyHeading_hasACommandUnderIt() {
        for (Map.Entry<String, List<String>> group : CommandType.getUsagesByGroup().entrySet()) {
            assertFalse(group.getValue().isEmpty(), group.getKey() + " has nothing under it");
        }
    }

    /** A user should meet the commands that put a task in before the one that leaves. */
    @Test
    public void getUsages_theOrderShown_startsWithAddingAndEndsWithLeaving() {
        List<String> usages = CommandType.getUsages();

        assertEquals(CommandType.TODO.getUsage(), usages.get(0));
        assertEquals(CommandType.BYE.getUsage(), usages.get(usages.size() - 1));
    }

    /**
     * The window's greeting sends the user to help by name, so the keyword has
     * to stay the word the greeting says.
     */
    @Test
    public void fromKeyword_help_returnsHelp() {
        assertEquals(CommandType.HELP, CommandType.fromKeyword("help"));
        assertEquals("help", CommandType.HELP.getKeyword());
    }

    @Test
    public void fromKeyword_knownKeyword_returnsThatCommand() {
        assertEquals(CommandType.BYE, CommandType.fromKeyword("bye"));
        assertEquals(CommandType.DEADLINE, CommandType.fromKeyword("deadline"));
    }

    @Test
    public void fromKeyword_unknownWord_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.fromKeyword("blah"));
    }

    /**
     * Keywords are matched exactly, so a command typed in the wrong case is not
     * that command. The text UI tests check the same thing from the outside: BYE
     * does not end the session.
     */
    @Test
    public void fromKeyword_rightWordWrongCase_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.fromKeyword("BYE"));
        assertEquals(CommandType.UNKNOWN, CommandType.fromKeyword("Todo"));
    }

    /** An empty line has its own answer, so the user can be told they typed nothing. */
    @Test
    public void fromKeyword_emptyString_returnsNothing() {
        assertEquals(CommandType.NOTHING, CommandType.fromKeyword(""));
    }

    /**
     * UNKNOWN carries a null keyword precisely so that nothing can match it. This
     * checks the search does not fall over when it reaches that constant, which it
     * does on every unmatched word.
     */
    @Test
    public void fromKeyword_wordThatMatchesNothing_doesNotFailOnTheNullKeyword() {
        assertEquals(CommandType.UNKNOWN, CommandType.fromKeyword("zzz"));
        assertEquals(CommandType.UNKNOWN, CommandType.fromKeyword("null"));
    }

    /**
     * The usage is what the user is shown when a command is typed wrongly, so it
     * has to begin with the command they typed. A usage left over from another
     * command would send them to the wrong place, which is the mistake this pairing
     * of keyword and usage in one constant exists to prevent.
     */
    @Test
    public void getUsage_everyRealCommand_beginsWithItsOwnKeyword() {
        for (CommandType command : CommandType.values()) {
            if (command == CommandType.UNKNOWN || command == CommandType.NOTHING) {
                continue;
            }
            assertNotNull(command.getUsage(), command + " has no usage");
            assertTrue(command.getUsage().startsWith(command.getKeyword()),
                    "the usage of " + command + " does not begin with its keyword: "
                            + command.getUsage());
        }
    }
}
