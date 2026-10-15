package apollo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ApolloTest {
    @TempDir
    Path tempDir;

    @Test
    public void getResponse_addTodo_returnsResponseAndPersistsTask() {
        String storagePath = tempDir.resolve("tasks.txt").toString();
        Apollo apollo = new Apollo(storagePath);

        String addResponse = apollo.getResponse("todo read book");
        Apollo reloadedApollo = new Apollo(storagePath);
        String listResponse = reloadedApollo.getResponse("list");

        Assertions.assertTrue(addResponse.contains("read book"));
        Assertions.assertEquals(ResponseType.ADD, apollo.getResponseType());
        Assertions.assertTrue(listResponse.contains("read book"));
    }

    @Test
    public void getResponse_bye_requestsExit() {
        Apollo apollo = new Apollo(tempDir.resolve("tasks.txt").toString());

        String response = apollo.getResponse("bye");

        Assertions.assertTrue(apollo.isExitRequested());
        Assertions.assertTrue(response.contains("Apollo awaits your return"));
    }

    @Test
    public void getResponse_undoAdd_removesTaskAndPersistsChange() {
        String storagePath = tempDir.resolve("tasks.txt").toString();
        Apollo apollo = new Apollo(storagePath);
        Assertions.assertFalse(apollo.canUndo());

        apollo.getResponse("todo read book");
        Assertions.assertTrue(apollo.canUndo());

        String undoResponse = apollo.getResponse("undo");
        Apollo reloadedApollo = new Apollo(storagePath);

        Assertions.assertTrue(undoResponse.contains("undone"));
        Assertions.assertFalse(apollo.canUndo());
        Assertions.assertFalse(reloadedApollo.getResponse("list").contains("read book"));
    }

    @Test
    public void getResponse_undoDelete_restoresTaskAtOriginalPosition() {
        Apollo apollo = new Apollo(tempDir.resolve("tasks.txt").toString());
        apollo.getResponse("todo first");
        apollo.getResponse("todo second");
        apollo.getResponse("todo third");
        apollo.getResponse("delete 2");

        apollo.getResponse("undo");
        String listResponse = apollo.getResponse("list");

        Assertions.assertTrue(listResponse.indexOf("first") < listResponse.indexOf("second"));
        Assertions.assertTrue(listResponse.indexOf("second") < listResponse.indexOf("third"));
    }

    @Test
    public void getResponse_undoMark_restoresPreviousCompletionState() {
        Apollo apollo = new Apollo(tempDir.resolve("tasks.txt").toString());
        apollo.getResponse("todo read book");
        apollo.getResponse("mark 1");

        apollo.getResponse("undo");
        String listResponse = apollo.getResponse("list");

        Assertions.assertTrue(listResponse.contains("[T] [ ] read book"));
    }

    @Test
    public void getResponse_sixAdds_onlyFiveNewestActionsCanBeUndone() {
        Apollo apollo = new Apollo(tempDir.resolve("tasks.txt").toString());
        for (int i = 1; i <= 6; i++) {
            apollo.getResponse("todo task " + i);
        }

        for (int i = 0; i < 5; i++) {
            apollo.getResponse("undo");
        }
        String emptyHistoryResponse = apollo.getResponse("undo");

        Assertions.assertTrue(emptyHistoryResponse.contains("nothing for Apollo to undo"));
        Assertions.assertEquals(ResponseType.ERROR, apollo.getResponseType());

        String listResponse = apollo.getResponse("list");
        Assertions.assertTrue(listResponse.contains("task 1"));
        Assertions.assertFalse(listResponse.contains("task 2"));
    }

    @Test
    public void getResponse_markWithoutStateChange_doesNotConsumeUndoHistory() {
        Apollo apollo = new Apollo(tempDir.resolve("tasks.txt").toString());
        apollo.getResponse("todo read book");
        apollo.getResponse("mark 1");
        apollo.getResponse("mark 1");

        apollo.getResponse("undo");
        apollo.getResponse("undo");
        String emptyHistoryResponse = apollo.getResponse("undo");

        Assertions.assertTrue(emptyHistoryResponse.contains("nothing for Apollo to undo"));
    }

    @Test
    public void getResponse_markAndUnmark_persistsStateAcrossRestart() {
        String storagePath = tempDir.resolve("tasks.txt").toString();
        Apollo apollo = new Apollo(storagePath);
        apollo.getResponse("todo read book");

        apollo.getResponse("mark 1");
        Apollo afterMark = new Apollo(storagePath);
        Assertions.assertTrue(afterMark.getResponse("list").contains("[T] [X] read book"));

        afterMark.getResponse("unmark 1");
        Apollo afterUnmark = new Apollo(storagePath);
        Assertions.assertTrue(afterUnmark.getResponse("list").contains("[T] [ ] read book"));
    }

    @Test
    public void getResponse_invalidMutation_doesNotAlterUndoHistory() {
        Apollo apollo = new Apollo(tempDir.resolve("tasks.txt").toString());
        apollo.getResponse("todo valid task");

        String invalidResponse = apollo.getResponse("delete 99");
        apollo.getResponse("undo");

        Assertions.assertTrue(invalidResponse.contains("valid task number"));
        Assertions.assertFalse(apollo.getResponse("list").contains("valid task"));
        Assertions.assertFalse(apollo.canUndo());
    }

    @Test
    public void getResponse_multipleDifferentActions_undoesInLifoOrderAndPersists() {
        String storagePath = tempDir.resolve("tasks.txt").toString();
        Apollo apollo = new Apollo(storagePath);
        apollo.getResponse("todo first");
        apollo.getResponse("todo second");
        apollo.getResponse("mark 1");
        apollo.getResponse("delete 2");

        apollo.getResponse("undo");
        String afterUndoDelete = new Apollo(storagePath).getResponse("list");
        Assertions.assertTrue(afterUndoDelete.contains("[T] [X] first"));
        Assertions.assertTrue(afterUndoDelete.contains("second"));

        apollo.getResponse("undo");
        String afterUndoMark = new Apollo(storagePath).getResponse("list");
        Assertions.assertTrue(afterUndoMark.contains("[T] [ ] first"));

        apollo.getResponse("undo");
        String afterUndoSecondAdd = new Apollo(storagePath).getResponse("list");
        Assertions.assertTrue(afterUndoSecondAdd.contains("first"));
        Assertions.assertFalse(afterUndoSecondAdd.contains("second"));
    }

    @Test
    public void constructor_reloadedApplication_hasNoUndoHistory() {
        String storagePath = tempDir.resolve("tasks.txt").toString();
        Apollo apollo = new Apollo(storagePath);
        apollo.getResponse("todo persisted task");

        Apollo reloadedApollo = new Apollo(storagePath);

        Assertions.assertFalse(reloadedApollo.canUndo());
        Assertions.assertTrue(reloadedApollo.getResponse("undo").contains("nothing for Apollo to undo"));
    }

    @Test
    public void getResponse_dueThisDate_filtersAndNumbersMatchingDeadlines() {
        Apollo apollo = new Apollo(tempDir.resolve("tasks.txt").toString());
        apollo.getResponse("deadline morning submission /by 25/8/2026 0900");
        apollo.getResponse("todo unrelated todo");
        apollo.getResponse("deadline other date /by 26/8/2026 0900");
        apollo.getResponse("deadline evening submission /by 25/8/2026 1800");

        String response = apollo.getResponse("dueThisDate 25/8/2026");

        Assertions.assertEquals(ResponseType.LIST, apollo.getResponseType());
        Assertions.assertTrue(response.contains("1. [D] [ ] morning submission"));
        Assertions.assertTrue(response.contains("2. [D] [ ] evening submission"));
        Assertions.assertFalse(response.contains("unrelated todo"));
        Assertions.assertFalse(response.contains("other date"));
    }

    @Test
    public void getResponse_findNoMatches_isListResponseButEmptySearchIsError() {
        Apollo apollo = new Apollo(tempDir.resolve("tasks.txt").toString());
        apollo.getResponse("todo read book");

        String noMatchesResponse = apollo.getResponse("find assignment");
        Assertions.assertEquals("My light reveals no task bearing those words.", noMatchesResponse);
        Assertions.assertEquals(ResponseType.LIST, apollo.getResponseType());

        apollo.getResponse("find   ");
        Assertions.assertEquals(ResponseType.ERROR, apollo.getResponseType());
    }

    @Test
    public void getResponse_saveFailure_warnsUserButKeepsMutationUndoable() throws IOException {
        Path parentFile = tempDir.resolve("not-a-directory");
        Files.writeString(parentFile, "blocking file");
        Apollo apollo = new Apollo(parentFile.resolve("tasks.txt").toString());

        String addResponse = apollo.getResponse("todo in-memory task");

        Assertions.assertTrue(addResponse.contains("could not save your tasks"));
        Assertions.assertTrue(apollo.getResponse("list").contains("in-memory task"));
        Assertions.assertTrue(apollo.canUndo());

        String undoResponse = apollo.getResponse("undo");
        Assertions.assertTrue(undoResponse.contains("could not save your tasks"));
        Assertions.assertFalse(apollo.getResponse("list").contains("in-memory task"));
    }

    @Test
    public void constructor_unreadableStorage_setsLoadingErrorWithoutPreventingUse() {
        Apollo apollo = new Apollo(tempDir.toString());

        Assertions.assertNotNull(apollo.getLoadingError());
        String addResponse = apollo.getResponse("todo available in memory");
        Assertions.assertTrue(addResponse.contains("available in memory"));
        Assertions.assertTrue(apollo.getResponse("list").contains("available in memory"));
    }

    @Test
    public void constructor_malformedStorage_resetsFileAndReportsLoadingError() throws IOException {
        Path storageFile = tempDir.resolve("tasks.txt");
        Files.write(storageFile, List.of(
                "T | 0 | valid task before corruption",
                "D | broken"));

        Apollo apollo = Assertions.assertDoesNotThrow(() -> new Apollo(storageFile.toString()));

        Assertions.assertNotNull(apollo.getLoadingError());
        Assertions.assertTrue(apollo.getLoadingError().contains("line 2"));
        Assertions.assertTrue(apollo.getLoadingError().contains("Storage has been reset"));
        Assertions.assertTrue(Files.readAllLines(storageFile).isEmpty());

        Apollo reloadedApollo = new Apollo(storageFile.toString());
        Assertions.assertNull(reloadedApollo.getLoadingError());
    }

    @Test
    public void getResponse_afterError_resetsResponseTypeForNextValidCommand() {
        Apollo apollo = new Apollo(tempDir.resolve("tasks.txt").toString());
        apollo.getResponse("not-a-command");
        Assertions.assertEquals(ResponseType.ERROR, apollo.getResponseType());

        apollo.getResponse("list");

        Assertions.assertEquals(ResponseType.LIST, apollo.getResponseType());
    }
}
