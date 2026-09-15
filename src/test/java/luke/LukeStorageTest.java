package luke;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests task-list loading and saving through the command-line program.
 */
final class LukeStorageTest {
    @Test
    void persistsTasksBetweenSessions() {
        Path storageFile = TestSupport.createTempStorageFile();
        try {
            TestSupport.runLuke("""
                    todo remember me
                    bye
                    """, storageFile);

            String output = TestSupport.runLuke("""
                    list
                    bye
                    """, storageFile);

            TestSupport.assertContains(output, "1. [T][ ] remember me");
        } finally {
            TestSupport.deleteTempStorageFile(storageFile);
        }
    }

    @Test
    void reportsStorageFailuresAsChatbotErrors() {
        Path storageDirectory = TestSupport.createTempStorageDirectory();
        try {
            String output = TestSupport.runLuke("""
                    todo cannot save
                    bye
                    """, storageDirectory);

            TestSupport.assertContains(output, "Failed to load task list from data:");
            TestSupport.assertContains(output, "Failed to save task list to data:");
        } finally {
            TestSupport.deleteTempStorageDirectory(storageDirectory);
        }
    }

    @Test
    void reportsMalformedStorageDataAsChatbotError() {
        Path storageFile = TestSupport.createTempStorageFile();
        try {
            TestSupport.writeString(storageFile, "not enough fields");

            String output = TestSupport.runLuke("""
                    list
                    bye
                    """, storageFile);

            TestSupport.assertContains(output, "Failed to load task list from data:");
            TestSupport.assertContains(output, "Storage line has missing fields.");
            TestSupport.assertContains(output, "No items added.");
        } finally {
            TestSupport.deleteTempStorageFile(storageFile);
        }
    }

    @Test
    void reportsInvalidStoredTaskTypeAsChatbotError() {
        Path storageFile = TestSupport.createTempStorageFile();
        try {
            TestSupport.writeString(storageFile, "NOPE\t0\tbad");

            String output = TestSupport.runLuke("""
                    list
                    bye
                    """, storageFile);

            TestSupport.assertContains(output, "Failed to load task list from data:");
            TestSupport.assertContains(output, "Storage line has invalid task type.");
            TestSupport.assertContains(output, "No items added.");
        } finally {
            TestSupport.deleteTempStorageFile(storageFile);
        }
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
            TODO\t2\tbad                 | Storage line has invalid completion value.
            DEADLINE\t0\treturn book      | Storage line has wrong number of fields.
            'DEADLINE\t0\treturn book\t'  | Storage line has a blank flag value.
            """)
    void reportsOtherMalformedStorageDataAsChatbotErrors(
            String storedData, String expectedError) {
        Path storageFile = TestSupport.createTempStorageFile();
        try {
            TestSupport.writeString(storageFile, storedData.stripLeading());

            String output = TestSupport.runLuke("list\nbye\n", storageFile);

            TestSupport.assertContains(output, "Failed to load task list from data:");
            TestSupport.assertContains(output, expectedError);
            TestSupport.assertContains(output, "No items added.");
        } finally {
            TestSupport.deleteTempStorageFile(storageFile);
        }
    }

    @Test
    void persistsCompletionChangesBetweenSessions() {
        Path storageFile = TestSupport.createTempStorageFile();
        try {
            TestSupport.runLuke("todo submit work\nmark 1\nbye\n", storageFile);

            String output = TestSupport.runLuke("list\nbye\n", storageFile);

            TestSupport.assertContains(output, "1. [T][X] submit work");
        } finally {
            TestSupport.deleteTempStorageFile(storageFile);
        }
    }
}
