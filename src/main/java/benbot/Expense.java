package benbot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.regex.Pattern;

/** Stores an expense description and a precise monetary amount. */
final class Expense implements StorableEntity {
    /** The syntax accepted when adding an expense. */
    static final String USAGE = "expense DESCRIPTION /amount AMOUNT";

    /** Matches a conventional decimal amount with no more than two decimal places. */
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("-?\\d+(?:\\.\\d{1,2})?");

    /** The explanation of what the user paid for. */
    private final String description;

    /** The positive expense amount, represented with two decimal places. */
    private final BigDecimal amount;

    /** Creates an expense with a validated description and amount. */
    Expense(String description, BigDecimal amount) {
        assert description != null && !description.isBlank() : "An expense description must be provided";
        assert amount != null && amount.signum() > 0 : "An expense amount must be positive";
        assert amount.scale() == 2 : "An expense amount must have two decimal places";
        this.description = description;
        this.amount = amount;
    }

    /** Creates an expense from a valid expense command. */
    static Expense createFromCommand(String[] words) throws InvalidCommandException {
        int amountIndex = CommandWords.findMarker(words, "/amount");
        if (amountIndex <= 1 || amountIndex != words.length - 2) {
            throw new InvalidCommandException("Use: " + USAGE);
        }

        String description = String.join(" ", Arrays.copyOfRange(words, 1, amountIndex));
        String amountText = words[amountIndex + 1];
        if (!AMOUNT_PATTERN.matcher(amountText).matches()) {
            throw new InvalidCommandException(
                    "The expense amount must be a number with at most two decimal places.");
        }

        try {
            BigDecimal amount = new BigDecimal(amountText)
                    .setScale(2, RoundingMode.UNNECESSARY);
            if (amount.signum() <= 0) {
                throw new InvalidCommandException(
                        "The expense amount must be greater than zero.");
            }
            return new Expense(description, amount);
        } catch (NumberFormatException | ArithmeticException e) {
            throw new InvalidCommandException(
                    "The expense amount must be a number with at most two decimal places.");
        }
    }

    /** Returns this expense's monetary amount. */
    BigDecimal getAmount() {
        return amount;
    }

    /** Returns the expense in BenBot's display format. */
    @Override
    public String toString() {
        return "[$] " + description + " ($" + amount.toPlainString() + ")";
    }

    /** Returns the command used to recreate this expense from stored data. */
    @Override
    public String toStorageString() {
        return "expense " + description + " /amount " + amount.toPlainString();
    }
}
