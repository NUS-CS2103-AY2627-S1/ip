package bonbon.exception;

import bonbon.parser.Parser;

public class BonBonDateTimeParseException extends BonBonException {
    /**
     * Creates a BonBonDateTimeParseException.
     */
    public BonBonDateTimeParseException() {
        super(String.format("Date is not in the correct format!\nCorrect format is %s.", Parser.DATE_FORMAT_PATTERN));
    }
}
