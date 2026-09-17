package kia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import javafx.application.Application;

/**
 * Unit tests for the JavaFX application entry point.
 */
class MainTest {
    /**
     * Verifies that the Hello World view is a JavaFX application.
     */
    @Test
    void main_extendsApplication_isConfiguredForJavaFx() {
        assertEquals(Application.class, Main.class.getSuperclass());
    }

    /** Verifies that the separate launcher entry point required by JavaFX exists. */
    @Test
    void launcher_hasMainMethod_forwardsToJavaFx() throws NoSuchMethodException {
        assertNotNull(Launcher.class.getDeclaredMethod("main", String[].class));
    }

    /** Verifies that GUI commands are processed by the chatbot logic. */
    @Test
    void getResponse_todoCommand_confirmsTask() throws Exception {
        Path taskFile = Path.of("data", "kia.txt");
        Files.deleteIfExists(taskFile);
        try {
            Kia kia = new Kia();

            String response = kia.getResponse("todo read book");

            assertTrue(response.contains("Gotcha! I've added this task:"));
            assertTrue(response.contains("[T][ ] read book"));
        } finally {
            Files.deleteIfExists(taskFile);
        }
    }

    /** Verifies that a task description can be updated in place. */
    @Test
    void getResponse_updateDescription_updatesExistingTask() throws Exception {
        Path taskFile = Path.of("data", "kia.txt");
        Files.deleteIfExists(taskFile);
        try {
            Kia kia = new Kia();
            kia.getResponse("todo read book");

            String response = kia.getResponse("update 1 /description read reference book");

            assertTrue(response.contains("Got it! I've updated this task:"));
            assertTrue(response.contains("[T][ ] read reference book"));

            String literalResponse = kia.getResponse("update 1 /description read /unknown chapter");

            assertTrue(literalResponse.contains("[T][ ] read /unknown chapter"));
        } finally {
            Files.deleteIfExists(taskFile);
        }
    }

    /** Verifies that a deadline date is validated and displayed using the existing format. */
    @Test
    void getResponse_updateDeadlineDate_updatesDate() throws Exception {
        Path taskFile = Path.of("data", "kia.txt");
        Files.deleteIfExists(taskFile);
        try {
            Kia kia = new Kia();
            kia.getResponse("deadline return book /by 2026-10-01");

            String response = kia.getResponse("update 1 /by 2026-10-15");

            assertTrue(response.contains("[D][ ] return book (by: Oct 15 2026)"));
        } finally {
            Files.deleteIfExists(taskFile);
        }
    }

    /** Verifies that extra spacing around a deadline marker is accepted. */
    @Test
    void getResponse_deadlineWithExtraSpacing_acceptsDate() throws Exception {
        Path taskFile = Path.of("data", "kia.txt");
        Files.deleteIfExists(taskFile);
        try {
            Kia kia = new Kia();

            String response = kia.getResponse("deadline submit report   /by    2026-10-12");

            assertTrue(response.contains("[D][ ] submit report (by: Oct 12 2026)"));
        } finally {
            Files.deleteIfExists(taskFile);
        }
    }

    /** Verifies that multiple event fields can be changed in one command. */
    @Test
    void getResponse_updateMultipleEventFields_updatesAllFields() throws Exception {
        Path taskFile = Path.of("data", "kia.txt");
        Files.deleteIfExists(taskFile);
        try {
            Kia kia = new Kia();
            kia.getResponse("event meeting /from 2pm /to 3pm");

            String response = kia.getResponse("update 1 /description project meeting /from 4pm /to 6pm");

            assertTrue(response.contains("[E][ ] project meeting (from: 4pm to: 6pm)"));
        } finally {
            Files.deleteIfExists(taskFile);
        }
    }

    /** Verifies type conversion and preservation of the completion status. */
    @Test
    void getResponse_updateType_preservesDoneStatus() throws Exception {
        Path taskFile = Path.of("data", "kia.txt");
        Files.deleteIfExists(taskFile);
        try {
            Kia kia = new Kia();
            kia.getResponse("todo read book");
            kia.getResponse("mark 1");

            String response = kia.getResponse("update 1 /type deadline /by 2026-10-15");

            assertTrue(response.contains("[D][X] read book (by: Oct 15 2026)"));
        } finally {
            Files.deleteIfExists(taskFile);
        }
    }

    /** Verifies that an update with identical values reports a no-op. */
    @Test
    void getResponse_updateSameValues_reportsNothingChanged() throws Exception {
        Path taskFile = Path.of("data", "kia.txt");
        Files.deleteIfExists(taskFile);
        try {
            Kia kia = new Kia();
            kia.getResponse("todo read book");

            String response = kia.getResponse("update 1 /description read book");

            assertTrue(response.contains("Nothing changed. This task is already up to date:"));
        } finally {
            Files.deleteIfExists(taskFile);
        }
    }

    /** Verifies that invalid update input does not modify the task. */
    @Test
    void getResponse_invalidUpdate_doesNotChangeTask() throws Exception {
        Path taskFile = Path.of("data", "kia.txt");
        Files.deleteIfExists(taskFile);
        try {
            Kia kia = new Kia();
            kia.getResponse("todo read book");

            String response = kia.getResponse("update 1 /by 2026-10-15");
            String listResponse = kia.getResponse("list");

            assertTrue(response.contains("The /by field is only valid for deadlines."));
            assertTrue(listResponse.contains("1.[T][ ] read book"));
        } finally {
            Files.deleteIfExists(taskFile);
        }
    }

    /** Verifies malformed update commands report all primary input errors. */
    @Test
    void getResponse_malformedUpdate_reportsInputErrors() throws Exception {
        Path taskFile = Path.of("data", "kia.txt");
        Files.deleteIfExists(taskFile);
        try {
            Kia kia = new Kia();

            assertTrue(kia.getResponse("update").contains("The update command must include a task number."));
            kia.getResponse("todo read book");
            assertTrue(kia.getResponse("update 1").contains("The update command must include at least one field."));
            assertTrue(kia.getResponse("update 1 /description").contains("The description cannot be empty."));
            assertTrue(kia.getResponse("update 1 /unknown value").contains("Unknown update field: /unknown."));
            assertTrue(kia.getResponse("update 1 /description first /description second")
                    .contains("Each update field may appear only once."));
            assertTrue(kia.getResponse("update 1 /type deadline")
                    .contains("Converting to a deadline requires a /by date or time."));
            kia.getResponse("deadline submit report /by 2026-10-01");
            assertTrue(kia.getResponse("update 2 /by 2026-02-30")
                    .contains("The /by date or time must use yyyy-MM-dd format."));
            assertTrue(kia.getResponse("update 3 /description another task")
                    .contains("The task number is invalid."));
        } finally {
            Files.deleteIfExists(taskFile);
        }
    }

    /** Verifies that updated data is available after creating a new Kia instance. */
    @Test
    void getResponse_updateTask_persistsAcrossReload() throws Exception {
        Path taskFile = Path.of("data", "kia.txt");
        Files.deleteIfExists(taskFile);
        try {
            Kia kia = new Kia();
            kia.getResponse("todo read book");
            kia.getResponse("update 1 /description read reference book");

            Kia reloadedKia = new Kia();

            assertTrue(reloadedKia.getResponse("list").contains("1.[T][ ] read reference book"));
        } finally {
            Files.deleteIfExists(taskFile);
        }
    }

    /** Verifies that the FXML view is included in the application resources. */
    @Test
    void mainWindow_fxmlViewExists_inApplicationResources() {
        assertNotNull(Main.class.getResource("/view/MainWindow.fxml"));
    }
}
