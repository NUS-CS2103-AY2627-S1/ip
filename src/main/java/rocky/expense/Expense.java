package rocky.expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import rocky.RockyException;

/**
 * One recorded expense: what the money was spent on, how much, which
 * category it falls under (e.g. "food", "books", "transport"), and on
 * which day, e.g. "[food] lunch: $12.50 (on: Sep 11 2026)".
 */
public class Expense {
    /** How the date is shown to the user, e.g. "Sep 11 2026". */
    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    private final String description;
    private final BigDecimal amount;
    private final String category;
    private final LocalDate date;

    /**
     * Creates an expense from values that have already been validated,
     * e.g. by {@link #parseAmount}, {@link #parseCategory}, and {@link #parseDate}.
     *
     * @param description what the money was spent on, e.g. "lunch".
     * @param amount how much was spent; positive, with at most 2 decimal places.
     * @param category the lowercase, single-word category, e.g. "food".
     * @param date the day the money was spent.
     */
    public Expense(String description, BigDecimal amount, String category, LocalDate date) {
        // Callers validate user input and saved data first, so these can only fail through a bug.
        assert description != null && !description.isEmpty() : "an expense needs a description";
        assert amount != null && amount.signum() > 0 : "an expense's amount should be positive";
        assert category != null && category.equals(category.toLowerCase()) : "category should be lowercase";
        assert date != null : "an expense needs a date";

        this.description = description;
        this.amount = amount.setScale(2);
        this.category = category;
        this.date = date;
    }

    /**
     * Parses an amount of money such as "12.50" or "3".
     *
     * @param text the amount as typed, without a currency symbol.
     * @return the amount, with exactly 2 decimal places.
     * @throws RockyException if it isn't a positive number with at most 2 decimal places.
     */
    public static BigDecimal parseAmount(String text) throws RockyException {
        BigDecimal amount;
        try {
            amount = new BigDecimal(text.trim());
        } catch (NumberFormatException e) {
            throw new RockyException("\"" + text.trim() + "\" isn't an amount I understand. Try e.g. 12.50.");
        }

        if (amount.signum() <= 0) {
            throw new RockyException("An expense's amount must be more than 0.");
        }
        if (amount.stripTrailingZeros().scale() > 2) {
            throw new RockyException("An amount can have at most 2 decimal places, e.g. 12.50.");
        }
        return amount.setScale(2);
    }

    /**
     * Parses a category such as "food" or "Books", which must be a single word.
     *
     * @param text the category as typed.
     * @return the category in lowercase, so "Food" and "food" count as the same one.
     * @throws RockyException if the category is empty or has more than one word.
     */
    public static String parseCategory(String text) throws RockyException {
        String category = text.trim().toLowerCase();
        if (category.isEmpty() || category.contains(" ") || category.contains("|")) {
            throw new RockyException("A category should be a single word, e.g. food, books, or transport.");
        }
        return category;
    }

    /**
     * Parses a date given in ISO format (yyyy-mm-dd), e.g. "2026-09-11".
     *
     * @param text the date as typed.
     * @return the parsed date.
     * @throws RockyException if the date isn't in ISO format.
     */
    public static LocalDate parseDate(String text) throws RockyException {
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException e) {
            throw new RockyException("Please give the date as yyyy-mm-dd, e.g. 2019-10-15.");
        }
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    /** Returns the category, description, amount, and date, e.g. "[food] lunch: $12.50 (on: ...)". */
    @Override
    public String toString() {
        return "[" + category + "] " + description + ": $" + amount.toPlainString()
                + " (on: " + date.format(OUTPUT_FORMAT) + ")";
    }
}
