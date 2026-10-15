package cheecken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Checks user-facing exception guidance, including legacy public constructors.
     * @param command command name used in error guidance
     * @param example expected command example in the guidance
 */
class ExceptionTest {
    /**
     * Verifies empty exception with command type: provides relevant example.
     * @param command command name used in error guidance
     * @param example expected command example in the guidance
     */
    @ParameterizedTest
    @CsvSource({"todo, todo buy her flowers", "event, event love me /from",
        "deadline, deadline buy her flowers /by", "unknown, todo buy her flowers"})
    void emptyException_commandType_providesRelevantExample(String command, String example) {
        String message = new CheeckenEmptyException(command).getMessage();
        assertTrue(message.startsWith("There's no task that is empty. LOCK INNN!\n"));
        assertTrue(message.contains(example));
    }

    /**
     * Verifies legacy exceptions with missing arguments: provide guidance.
     */
    @Test
    void legacyExceptions_missingArguments_provideGuidance() {
        assertEquals("What are you trying to delete?\n(e.g. delete 3)",
                new CheeckenDeleteException().getMessage());
        assertTrue(new CheeckenEmptyException(true).getMessage().contains("without the deadline"));
        assertTrue(new CheeckenEmptyException(true, false).getMessage().contains("When your event starts"));
        assertEquals("Please provide a keyword to search for.", new CheeckenFindException().getMessage());
        assertTrue(new CheeckenUnknownException().getMessage().startsWith("What do you want?"));
    }

    /**
     * Verifies date time exception with command type: provides date syntax.
     * @param command command name used in error guidance
     * @param example expected command example in the guidance
     */
    @ParameterizedTest
    @CsvSource({"event, Try: event meeting /from", "deadline, Try: deadline report /by"})
    void dateTimeException_commandType_providesDateSyntax(String command, String example) {
        String message = new CheeckenDateTimeException(command).getMessage();
        assertTrue(message.contains(example));
        assertTrue(message.contains("Dates: d/M/yyyy"));
    }
}
