package verity.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import verity.command.ClientAddCommand;
import verity.command.ClientAssociateCommand;
import verity.command.ClientDeleteCommand;
import verity.command.ClientDissociateCommand;
import verity.command.ClientEditCommand;
import verity.command.ClientFindCommand;
import verity.command.ClientListCommand;
import verity.command.ClientViewCommand;
import verity.exception.VerityException;

/**
 * Tests parsing of commands in the client namespace.
 */
class ClientCommandParserTest {
    private final ClientCommandParser parser = new ClientCommandParser();

    @Test
    void parse_supportedCommands_returnsExpectedCommandTypes()
            throws VerityException {
        assertInstanceOf(
                ClientAddCommand.class,
                parser.parse("client add /name Alice Tan", 2));
        assertInstanceOf(
                ClientListCommand.class,
                parser.parse("client list", 2));
        assertInstanceOf(
                ClientViewCommand.class,
                parser.parse("client view C001", 2));
        assertInstanceOf(
                ClientFindCommand.class,
                parser.parse("client find alice", 2));
        assertInstanceOf(
                ClientEditCommand.class,
                parser.parse("client edit C001 /phone +65 9123 4567", 2));
        assertInstanceOf(
                ClientAssociateCommand.class,
                parser.parse("client associate C001 /task 1 /task 2", 2));
        assertInstanceOf(
                ClientDissociateCommand.class,
                parser.parse("client dissociate C001 /task 1", 2));
        assertInstanceOf(
                ClientDeleteCommand.class,
                parser.parse("client delete C001 confirm", 2));
    }

    @Test
    void parse_addWithoutName_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> parser.parse(
                        "client add /email alice@example.com", 0));
    }

    @Test
    void parse_repeatedField_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> parser.parse(
                        "client add /name Alice /name Bob", 0));
    }

    @Test
    void parse_duplicateTaskNumber_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> parser.parse(
                        "client associate C001 /task 1 /task 1", 1));
    }

    @Test
    void parse_noteEscapes_acceptsNewlineEscape() throws VerityException {
        assertInstanceOf(
                ClientAddCommand.class,
                parser.parse(
                        "client add /name Alice Tan /notes Called\\nRequested quote",
                        0));
    }

    @Test
    void parse_caseInsensitiveCommandAndMarkers_returnsCommand()
            throws VerityException {
        assertInstanceOf(
                ClientAddCommand.class,
                parser.parse("CLIENT ADD /NAME Alice Tan /PREFERRED EMAIL", 0));
    }

    @Test
    void parse_unknownMarker_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> parser.parse("client add /name Alice /fax 123", 0));
    }

    @Test
    void parse_clearName_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> parser.parse("client edit C001 /clear name", 0));
    }

    @Test
    void parse_associateWithoutTask_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> parser.parse("client associate C001", 1));
    }

    @Test
    void parse_missingOrUnknownAction_throwsHelpfulException() {
        assertParseFailure(
                "client",
                0,
                "Please provide a client command.");
        assertParseFailure(
                "client archive C001",
                0,
                "I don't know that client command.");
    }

    @Test
    void parse_listOrFindWithInvalidArguments_throwsHelpfulException() {
        assertParseFailure(
                "client list all",
                0,
                "The client list command does not accept arguments.");
        assertParseFailure(
                "client find    ",
                0,
                "Please provide a client name to search for.");
    }

    @Test
    void parse_viewWithMissingOrExtraId_throwsHelpfulException() {
        assertParseFailure(
                "client view",
                0,
                "Please provide exactly one client ID.");
        assertParseFailure(
                "client view C001 extra",
                0,
                "Please provide exactly one client ID.");
    }

    @Test
    void parse_editWithMissingIdOrChanges_throwsHelpfulException() {
        assertParseFailure(
                "client edit",
                0,
                "Please provide a client ID.");
        assertParseFailure(
                "client edit C001",
                0,
                "Please provide at least one field to update.");
    }

    @Test
    void parse_editWithDuplicateClearOrChangedClear_throwsHelpfulException() {
        assertParseFailure(
                "client edit C001 /clear phone /clear phone",
                0,
                "A client field cannot be changed more than once.");
        assertParseFailure(
                "client edit C001 /phone 91234567 /clear phone",
                0,
                "A client field cannot be changed more than once.");
    }

    @Test
    void parse_deleteWithInvalidArguments_throwsHelpfulException() {
        String usageMessage =
                "Use client delete CLIENT_ID followed optionally by confirm.";
        assertParseFailure("client delete", 0, usageMessage);
        assertParseFailure("client delete C001 confirm extra", 0, usageMessage);
        assertParseFailure("client delete C001 yes", 0, usageMessage);
    }

    @Test
    void parse_associationWithInvalidTaskNumber_throwsHelpfulException() {
        assertParseFailure(
                "client associate C001 /task one",
                1,
                "The task number must be a number.");
        assertParseFailure(
                "client associate C001 /task 0",
                1,
                "That task number does not exist.");
        assertParseFailure(
                "client associate C001 /task 2",
                1,
                "That task number does not exist.");
    }

    @Test
    void parse_fieldsWithoutValidMarkerSyntax_throwsHelpfulException() {
        assertParseFailure(
                "client add Alice Tan",
                0,
                "Client fields must use slash-prefixed markers.");
        assertParseFailure(
                "client add Alice /name Tan",
                0,
                "Client fields must use slash-prefixed markers.");
        assertParseFailure(
                "client add /task 1 /name Alice",
                0,
                "Unknown client field '/task'.");
    }

    @Test
    void parse_emptyOrEmbeddedUnknownField_throwsHelpfulException() {
        assertParseFailure(
                "client add /name",
                0,
                "The /name value cannot be empty.");
        assertParseFailure(
                "client add /name Alice /fax 123",
                0,
                "Unknown client field '/fax'.");
    }

    private void assertParseFailure(String command, int taskCount,
            String expectedMessage) {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse(command, taskCount));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
