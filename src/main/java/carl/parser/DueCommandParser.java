package carl.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import carl.commands.DueCommand;
import carl.exceptions.CarlCommandException;
import carl.exceptions.CarlException;
import carl.util.DateParser;

/**
 * Parses input arguments and creates a new DueCommand object.
 */
public class DueCommandParser implements Parser<DueCommand> {

    /**
     * Parses the given input string to extract the query date.
     *
     * @param input Raw input string containing the target date.
     * @return DueCommand initialized with the specified or current date.
     */
    @Override
    public DueCommand parse(String input) throws CarlException {
        if (input.isEmpty()) {
            return new DueCommand(LocalDate.now());
        }

        try {
            return new DueCommand(DateParser.parseDate(input));
        } catch (DateTimeParseException e) {
            throw new CarlCommandException(DateParser.getDateErrorMessage());
        }
    }
}
