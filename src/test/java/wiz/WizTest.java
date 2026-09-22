package wiz;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class WizTest {

    @TempDir
    Path tempDir;

    private Wiz createWiz() {
        Path testFile = tempDir.resolve("wiz_test_" + System.nanoTime() + ".txt");
        return new Wiz(testFile.toString());
    }

    @Test
    public void getResponse_emptyInput_promptResponse() {
        Wiz wiz = createWiz();
        String response = wiz.getResponse("   ");
        assertTrue(response.contains("Please enter a valid command"));
        assertTrue(Wiz.isErrorResponse(response));
    }

    @Test
    public void getResponse_unknownCommand_errorResponse() {
        Wiz wiz = createWiz();
        String response = wiz.getResponse("abracadabra");
        assertTrue(response.contains("Alas! I do not recognize the spell 'abracadabra'"));
        assertTrue(Wiz.isErrorResponse(response));
    }

    @Test
    public void getResponse_helpCommand_showsSpellList() {
        Wiz wiz = createWiz();
        String response = wiz.getResponse("help");
        assertTrue(response.contains("Here are the incantations and spells you can cast:"));
        assertTrue(response.contains("todo"));
        assertTrue(response.contains("deadline"));
        assertTrue(response.contains("event"));
        assertFalse(Wiz.isErrorResponse(response));
    }

    @Test
    public void getResponse_todoCommand_successAndValidation() {
        Wiz wiz = createWiz();

        String emptyResponse = wiz.getResponse("todo ");
        assertTrue(emptyResponse.contains("Alas! A todo incantation requires a description"));

        String successResponse = wiz.getResponse("todo read spellbook");
        assertTrue(successResponse.contains("inscribed this task into your spellbook"));
        assertTrue(successResponse.contains("read spellbook"));

        String duplicateResponse = wiz.getResponse("todo read spellbook");
        assertTrue(duplicateResponse.contains("Halt! A todo task with the same description already exists"));
    }

    @Test
    public void getResponse_deadlineCommand_successAndErrors() {
        Wiz wiz = createWiz();

        // Missing /by tag
        String missingBy = wiz.getResponse("deadline submit report");
        assertTrue(missingBy.contains("Alas! A deadline needs a '/by' tag"));

        // Duplicate /by tag
        String duplicateBy = wiz.getResponse("deadline report /by 2026-10-10 1800 /by 2026-10-11 1800");
        assertTrue(duplicateBy.contains("Alas! The '/by' tag should only be specified once"));

        // Missing description
        String emptyDesc = wiz.getResponse("deadline /by 2026-10-10 1800");
        assertTrue(emptyDesc.contains("Alas! The deadline description cannot be empty"));

        // Missing date
        String emptyDate = wiz.getResponse("deadline report /by  ");
        assertTrue(emptyDate.contains("Alas! Please provide a date and time after '/by'"));

        // Invalid date format
        String invalidFormat = wiz.getResponse("deadline report /by tomorrow");
        assertTrue(invalidFormat.contains("Alas! Invalid date/time 'tomorrow'"));

        // Non-existent calendar date (Feb 30)
        String nonExistentDate = wiz.getResponse("deadline report /by 2026-02-30 1800");
        assertTrue(nonExistentDate.contains("Alas! Invalid date/time '2026-02-30 1800'"));

        // Valid deadline
        String success = wiz.getResponse("deadline submit report /by 2026-10-10 1800");
        assertTrue(success.contains("The hourglass is set!"));
        assertTrue(success.contains("submit report"));

        // Duplicate deadline
        String duplicate = wiz.getResponse("deadline submit report /by 2026-10-10 1800");
        assertTrue(duplicate.contains("Halt! A deadline with the same description and due time already exists"));
    }

    @Test
    public void getResponse_eventCommand_successAndErrors() {
        Wiz wiz = createWiz();

        // Missing tags
        String missingTags = wiz.getResponse("event party");
        assertTrue(missingTags.contains("Alas! An event requires both '/from' and '/to' tags"));

        // Missing description
        String emptyDesc = wiz.getResponse("event /from 2026-10-10 1000 /to 2026-10-10 1200");
        assertTrue(emptyDesc.contains("Alas! The event description cannot be empty"));

        // Duplicate tags
        String duplicateTag = wiz.getResponse("event concert /from 2026-10-10 1000 /from 2026-10-10 1100 /to 2026-10-10 1200");
        assertTrue(duplicateTag.contains("Alas! The '/from' tag should only be specified once"));

        // End before start
        String endBeforeStart = wiz.getResponse("event concert /from 2026-10-10 1400 /to 2026-10-10 1200");
        assertTrue(endBeforeStart.contains("Alas! An event cannot end before or at the same time as it begins"));

        // End equals start
        String endEqualsStart = wiz.getResponse("event concert /from 2026-10-10 1400 /to 2026-10-10 1400");
        assertTrue(endEqualsStart.contains("Alas! An event cannot end before or at the same time as it begins"));

        // Non-existent date
        String nonExistentDate = wiz.getResponse("event festival /from 2026-04-31 1000 /to 2026-04-31 1200");
        assertTrue(nonExistentDate.contains("Alas! Invalid date/time '2026-04-31 1000'"));

        // Valid event
        String success = wiz.getResponse("event magical festival /from 2026-10-10 1000 /to 2026-10-10 1600");
        assertTrue(success.contains("Marked on the celestial calendar!"));
        assertTrue(success.contains("magical festival"));

        // Duplicate event
        String duplicate = wiz.getResponse("event magical festival /from 2026-10-10 1000 /to 2026-10-10 1600");
        assertTrue(duplicate.contains("Halt! An event with the same description and time period already exists"));
    }

    @Test
    public void getResponse_markUnmarkDelete_successAndBounds() {
        Wiz wiz = createWiz();

        // Empty list bounds
        String emptyDelete = wiz.getResponse("delete 1");
        assertTrue(emptyDelete.contains("Alas! Your task list is currently empty"));

        wiz.getResponse("todo task 1");
        wiz.getResponse("todo task 2");

        // Out of bounds index
        String outOfBounds = wiz.getResponse("delete 5");
        assertTrue(outOfBounds.contains("Task number 5 does not exist"));

        // Non-numeric index
        String nonNumeric = wiz.getResponse("mark one");
        assertTrue(nonNumeric.contains("is not a valid number"));

        // Mark
        String markRes = wiz.getResponse("mark 1");
        assertTrue(markRes.contains("Splendid enchantment! I've marked this task as done"));
        assertTrue(markRes.contains("[X] task 1"));

        // Unmark
        String unmarkRes = wiz.getResponse("unmark 1");
        assertTrue(unmarkRes.contains("Spell reversed! I've marked this task as not done yet"));
        assertTrue(unmarkRes.contains("[ ] task 1"));

        // Delete
        String deleteRes = wiz.getResponse("delete 1");
        assertTrue(deleteRes.contains("Poof! Vanished into the ether"));
        assertTrue(deleteRes.contains("task 1"));
    }

    @Test
    public void getResponse_listAndFind_success() {
        Wiz wiz = createWiz();

        // Empty list
        String emptyList = wiz.getResponse("list");
        assertTrue(emptyList.contains("Your spellbook is currently pristine and empty"));

        wiz.getResponse("todo learn fireball");
        wiz.getResponse("todo learn teleport");

        // List with tasks
        String listRes = wiz.getResponse("list");
        assertTrue(listRes.contains("1.[T][ ] learn fireball"));
        assertTrue(listRes.contains("2.[T][ ] learn teleport"));

        // Empty find keyword
        String emptyFind = wiz.getResponse("find  ");
        assertTrue(emptyFind.contains("Alas! A find spell requires a keyword"));

        // Matching find
        String findRes = wiz.getResponse("find fireball");
        assertTrue(findRes.contains("1.[T][ ] learn fireball"));

        // No match find
        String noMatch = wiz.getResponse("find frostbolt");
        assertTrue(noMatch.contains("found no matching tasks for 'frostbolt'"));
    }

    @Test
    public void getResponse_clientCommands_fullCycle() {
        Wiz wiz = createWiz();

        // Empty clients list
        String emptyClients = wiz.getResponse("clients");
        assertTrue(emptyClients.contains("Your guild registry currently has no allies inscribed"));

        // Missing tags
        String missingTags = wiz.getResponse("client Alice");
        assertTrue(missingTags.contains("Alas! Please provide both '/phone' and '/email' tags"));

        // Add client with note
        String addClient = wiz.getResponse("client Alice Tan /phone 91234567 /email alice@example.com /note Archmage");
        assertTrue(addClient.contains("A new ally has been inscribed in your guild registry:"));
        assertTrue(addClient.contains("Alice Tan"));

        // Duplicate client
        String duplicateClient = wiz.getResponse("client Alice Tan /phone 90000000 /email diff@example.com");
        assertTrue(duplicateClient.contains("Halt! An ally with the same name, phone, or email already exists"));

        // List clients
        String listClients = wiz.getResponse("clients");
        assertTrue(listClients.contains("1.[C] Alice Tan (Phone: 91234567, Email: alice@example.com, Note: Archmage)"));

        // Find client
        String findClient = wiz.getResponse("findclient Alice");
        assertTrue(findClient.contains("1.[C] Alice Tan"));

        // Find client no match
        String noMatch = wiz.getResponse("findclient Merlin");
        assertTrue(noMatch.contains("No allies matching 'Merlin' were found"));

        // Delete client out of bounds
        String outOfBounds = wiz.getResponse("deleteclient 10");
        assertTrue(outOfBounds.contains("Client number 10 does not exist"));

        // Delete client
        String deleteClient = wiz.getResponse("deleteclient 1");
        assertTrue(deleteClient.contains("Ally removed from your guild registry:"));
    }

    @Test
    public void getResponse_byeCommand_success() {
        Wiz wiz = createWiz();
        String bye = wiz.getResponse("bye");
        assertTrue(bye.contains("Farewell, traveler!"));
    }

    @Test
    public void isErrorResponse_classifiesCorrectly() {
        assertTrue(Wiz.isErrorResponse("Alas! Something went wrong"));
        assertTrue(Wiz.isErrorResponse("Halt! Duplicate task"));
        assertTrue(Wiz.isErrorResponse("Please enter a valid command"));
        assertFalse(Wiz.isErrorResponse("✨ Got it!"));
        assertFalse(Wiz.isErrorResponse(null));
        assertFalse(Wiz.isErrorResponse(""));
    }
}
