package clover.command;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import clover.exception.CloverException;
import clover.parser.Parser;
import clover.storage.Storage;
import clover.task.TaskList;
import clover.tutoree.Tutoree;
import clover.tutoree.TutoreeList;
import clover.ui.Ui;

/**
 * Adds a tutoree with their name, tutoring address, and fee arrangement.
 */
public class AddTutoreeCommand extends Command {
    private static final String ADDRESS_MARKER = "/address";
    private static final String FEE_MARKER = "/fee";
    private static final Pattern FEE_PATTERN = Pattern.compile("\\d+(?:\\.\\d{1,2})?"
            + "(?:/(?:hour|session|lesson|month))?");

    private final String arguments;

    /** Creates a command from the text after the {@code add-tutoree} keyword. */
    public AddTutoreeCommand(String arguments) {
        this.arguments = arguments;
    }

    /** Validates, adds, and saves a tutoree. */
    @Override
    public void execute(TaskList tasks, TutoreeList tutorees, Ui ui, Storage storage) throws CloverException {
        Parser.rejectUnknownParameters(arguments, "/address", "/fee", "/hour", "/session", "/lesson", "/month");
        int addressIndex = Parser.findSingleMarker(arguments, ADDRESS_MARKER);
        int feeIndex = Parser.findSingleMarker(arguments, FEE_MARKER);
        if (addressIndex <= 0 || feeIndex <= addressIndex + ADDRESS_MARKER.length()) {
            throw invalidFormat();
        }

        String name = arguments.substring(0, addressIndex).trim();
        String address = arguments.substring(addressIndex + ADDRESS_MARKER.length(), feeIndex).trim();
        String fee = arguments.substring(feeIndex + FEE_MARKER.length()).trim();
        if (name.isEmpty() || address.isEmpty() || fee.isEmpty()) {
            throw invalidFormat();
        }
        validateName(name);
        validateFee(fee);
        if (tutorees.findExactName(name).isPresent()) {
            throw new CloverException("A learning companion named \"" + name + "\" is already in the grove.");
        }

        Tutoree tutoree = new Tutoree(name, address, fee);
        tutorees.add(tutoree);
        try {
            saveTutorees(tutorees, storage);
        } catch (CloverException exception) {
            // The newly added tutoree is always the last list element.
            tutorees.removeLast();
            throw exception;
        }
        ui.showTutoreeAdded(tutoree, tutorees.size());
    }

    @Override
    public CommandResponseStyle getResponseStyle() {
        return CommandResponseStyle.TUTOREE_ADDED;
    }

    /** Creates the shared guidance message for malformed add-tutoree commands. */
    private CloverException invalidFormat() {
        return new CloverException("To welcome a learning companion, use: add-tutoree NAME /address ADDRESS /fee FEE");
    }

    /** Ensures that a name contains at least one letter while allowing ordinary name punctuation. */
    private void validateName(String name) throws CloverException {
        if (name.codePoints().noneMatch(Character::isLetter)) {
            throw new CloverException("Enter a learning companion name containing at least one letter.");
        }
    }

    /** Ensures that the fee is a positive decimal amount with an optional supported rate unit. */
    private void validateFee(String fee) throws CloverException {
        try {
            if (!FEE_PATTERN.matcher(fee).matches() || new BigDecimal(fee.split("/", 2)[0]).signum() <= 0) {
                throw invalidFee();
            }
        } catch (NumberFormatException exception) {
            throw invalidFee();
        }
    }

    /** Creates guidance for a fee that lacks a valid positive amount or rate unit. */
    private CloverException invalidFee() {
        return new CloverException("Enter a positive fee amount, optionally followed by /hour, /session, /lesson, "
                + "or /month; for example 50 or 50/hour.");
    }
}
