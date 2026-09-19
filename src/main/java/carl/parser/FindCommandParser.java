package carl.parser;

import carl.commands.FindCommand;
import carl.exceptions.CarlCommandException;
import carl.exceptions.CarlException;

/**
 * Parses input arguments and creates a new FindCommand object.
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given input string to extract the search keyword.
     *
     * @param input Raw input string containing the keyword to search for.
     * @return Find command initialized with the parsed keyword.
     * @throws CarlException If the input format is incorrect (e.g., empty keyword).
     */
    @Override
    public FindCommand parse(String input) throws CarlException {
        if (input.isEmpty()) {
            throw new CarlCommandException("find <keyword>");
        }
        if (input.indexOf('|') >= 0) {
            throw new CarlCommandException("Search keywords cannot contain the `|` character.");
        }

        return new FindCommand(input.strip().replaceAll("\\s+", " "));
    }
}
