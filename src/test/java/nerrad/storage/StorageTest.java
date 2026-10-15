package nerrad.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nerrad.loan.Loan;
import nerrad.loan.LoanType;
import nerrad.task.Deadline;
import nerrad.task.Event;
import nerrad.task.Task;
import nerrad.task.Todo;

/**
 * Tests persistence of tasks through {@link Storage}.
 */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void loadTasks_missingSaveFile_returnsEmptyList() throws IOException {
        Storage storage = createStorage();

        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    void loadLoans_missingSaveFile_returnsEmptyList() throws IOException {
        Storage storage = createStorage();

        assertTrue(storage.loadLoans().isEmpty());
    }

    @Test
    void saveTasksAndLoadTasks_mixedTasks_roundTripsAllDetailsAndStatuses() throws IOException {
        List<Task> tasks = new ArrayList<>();
        Todo completedTodo = new Todo("read book");
        completedTodo.markAsDone();
        tasks.add(completedTodo);
        tasks.add(new Deadline("return book", LocalDate.of(2019, 12, 2)));
        tasks.add(new Event("project meeting", "Mon 2pm", "4pm"));

        Storage storage = createStorage();
        storage.saveTasks(tasks);
        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        assertEquals("[T][X] read book", loadedTasks.get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 02 2019)", loadedTasks.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Mon 2pm to: 4pm)", loadedTasks.get(2).toString());
    }

    @Test
    void loadTasks_corruptedSaveData_throwsIOException() throws IOException {
        Path saveFile = temporaryDirectory.resolve("data").resolve("nerrad.txt");
        Files.createDirectories(saveFile.getParent());
        Storage storage = new Storage(saveFile.toString());

        for (String invalidTaskLine : List.of(
                "Z | 0 | unknown task",
                "T | 2 | invalid status",
                "D | 0 | invalid date | tomorrow")) {
            Files.writeString(saveFile, invalidTaskLine);

            assertThrows(IOException.class, storage::loadTasks);
        }
    }

    @Test
    void saveLoansAndLoadLoans_roundTripsDetailsAndSettlementState() throws IOException {
        Loan settledLoan = new Loan(LoanType.LENT, "Alex", new BigDecimal("12.50"), "lunch");
        settledLoan.markAsSettled();
        Loan outstandingLoan = new Loan(LoanType.BORROWED, "Ben", new BigDecimal("5"), "bus");
        Storage storage = createStorage();

        storage.saveLoans(List.of(settledLoan, outstandingLoan));
        List<Loan> loadedLoans = storage.loadLoans();

        assertEquals(2, loadedLoans.size());
        assertEquals("[LENT][SETTLED] Alex: S$12.50 (lunch)", loadedLoans.get(0).toString());
        assertEquals("[BORROWED][OUTSTANDING] Ben: S$5.00 (bus)", loadedLoans.get(1).toString());
    }

    @Test
    void loadLoans_corruptedSaveData_throwsIOException() throws IOException {
        Path loanFile = temporaryDirectory.resolve("data").resolve("loans.txt");
        Files.createDirectories(loanFile.getParent());
        Storage storage = createStorage();

        for (String invalidLoanLine : List.of(
                "T | 0 | task saved in the wrong file",
                "L | GIFTED | 0 | Alex | 10 | lunch",
                "L | LENT | 2 | Alex | 10 | lunch",
                "L | LENT | 0 | Alex | 0 | lunch",
                "L | LENT | 0 | Alex | unknown | lunch")) {
            Files.writeString(loanFile, invalidLoanLine);

            assertThrows(IOException.class, storage::loadLoans);
        }
    }

    /**
     * Creates storage with a fresh nested data path, exercising directory creation on save.
     *
     * @return storage for this test's temporary save file
     */
    private Storage createStorage() {
        return new Storage(temporaryDirectory.resolve("data").resolve("nerrad.txt").toString());
    }
}
