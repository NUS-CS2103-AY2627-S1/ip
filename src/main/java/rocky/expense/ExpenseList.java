package rocky.expense;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import rocky.RockyException;

/**
 * The expenses the user has recorded, in the order they were added. The
 * list is saved to a file after every change so it survives a restart,
 * one expense per line, e.g. {@code "lunch | 12.50 | food | 2026-09-11"}.
 */
public class ExpenseList {
    private static final String FIELD_SEPARATOR = " | ";
    private static final int FIELD_COUNT = 4;

    private final List<Expense> expenses = new ArrayList<>();
    private final File file;

    /**
     * Creates an empty list that saves to, and loads from, the given file.
     * The file and its folder are created on the first save if needed.
     *
     * @param file where the expenses are saved, e.g. data/expenses.txt.
     */
    public ExpenseList(File file) {
        this.file = file;
    }

    /**
     * Loads previously saved expenses from the file. Does nothing if the
     * file doesn't exist yet (e.g. first run). A line that can't be read
     * is skipped with a warning, without stopping the rest of the file
     * from loading.
     */
    public void load() {
        if (!file.exists()) {
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                try {
                    expenses.add(lineToExpense(line));
                } catch (RockyException e) {
                    System.out.println("     Warning: skipped an unreadable saved expense.");
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("     Warning: couldn't read saved expenses.");
        }
    }

    /**
     * Adds the expense to the end of the list, then saves the list.
     *
     * @param expense the expense to record.
     */
    public void add(Expense expense) {
        expenses.add(expense);
        save();
    }

    /**
     * Removes the expense at the given position, then saves the list.
     *
     * @param index the expense's 0-based position in the list.
     * @return the expense that was removed.
     */
    public Expense remove(int index) {
        // Rocky checks the number the user typed against size() before calling this.
        assert index >= 0 && index < expenses.size() : "index should be within the list";

        Expense removed = expenses.remove(index);
        save();
        return removed;
    }

    /**
     * Returns the expenses in the order they were added. The returned list
     * can't be modified; use {@link #add} and {@link #remove} instead, so
     * every change is saved.
     *
     * @return a read-only view of the recorded expenses.
     */
    public List<Expense> getExpenses() {
        return Collections.unmodifiableList(expenses);
    }

    /** Returns how many expenses have been recorded. */
    public int size() {
        return expenses.size();
    }

    /** Returns true if no expenses have been recorded. */
    public boolean isEmpty() {
        return expenses.isEmpty();
    }

    /**
     * Saves every expense to the file, overwriting whatever was there
     * before, and creating the file's folder first if needed.
     */
    private void save() {
        try {
            File folder = file.getParentFile();
            if (folder != null && !folder.exists()) {
                folder.mkdirs();
            }

            try (FileWriter writer = new FileWriter(file)) {
                for (Expense expense : expenses) {
                    writer.write(expenseToLine(expense) + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.out.println("     Warning: couldn't save expenses (" + e.getMessage() + ").");
        }
    }

    /**
     * Converts an expense to its one-line save-file form, e.g.
     * {@code "lunch | 12.50 | food | 2026-09-11"}.
     */
    private static String expenseToLine(Expense expense) {
        return expense.getDescription() + FIELD_SEPARATOR + expense.getAmount().toPlainString()
                + FIELD_SEPARATOR + expense.getCategory() + FIELD_SEPARATOR + expense.getDate();
    }

    /**
     * Parses one save-file line back into an Expense, validating each field
     * with the same rules used for the expense command.
     *
     * @param line a line previously produced by {@link #expenseToLine}.
     * @return the reconstructed expense.
     * @throws RockyException if the line has the wrong number of fields, an
     *     empty description, or an invalid amount, category, or date.
     */
    private static Expense lineToExpense(String line) throws RockyException {
        String[] fields = line.split("\\|");
        if (fields.length != FIELD_COUNT) {
            throw new RockyException("A saved expense should have exactly " + FIELD_COUNT + " fields.");
        }

        String description = fields[0].trim();
        if (description.isEmpty()) {
            throw new RockyException("A saved expense is missing its description.");
        }

        return new Expense(description, Expense.parseAmount(fields[1]),
                Expense.parseCategory(fields[2]), Expense.parseDate(fields[3]));
    }
}
