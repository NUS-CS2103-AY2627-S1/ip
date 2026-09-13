package nelson.command;

/** Represents a parsed command that searches task descriptions for a keyword. */
public class FindCommand extends Parser.Command {
    /** The keyword to search for. */
    private final String keyword;

    /**
     * Creates a find command.
     *
     * @param keyword non-empty keyword to search for.
     */
    public FindCommand(String keyword) {
        super(Parser.Type.FIND, keyword);
        this.keyword = keyword;
    }

    /**
     * Returns the keyword being searched for.
     *
     * @return the search keyword.
     */
    public String getKeyword() {
        return keyword;
    }
}
