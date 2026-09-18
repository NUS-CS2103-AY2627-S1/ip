package oreo.command;

import oreo.core.AppContext;
import oreo.core.OreoException;
import oreo.core.Parser;

/** Command that searches task descriptions for a keyword. */
public class FindCommand extends Command {
    private final String keyword;

    /** Creates a search command for the supplied keyword. */
    public FindCommand(String keyword, Parser parser) {
        super(parser);
        this.keyword = keyword;
    }

    /** Validates the keyword and displays all matching tasks. */
    @Override
    public void execute(AppContext context) throws OreoException {
        if (keyword.isEmpty()) {
            throw new OreoException("Use: find KEYWORD");
        }
        context.getUi().showMatchingTasks(context.getTasks().find(keyword));
    }
}
