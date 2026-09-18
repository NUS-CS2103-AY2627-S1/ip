package oreo.command;

import oreo.core.AppContext;
import oreo.core.OreoException;
import oreo.core.Parser;

/** Searches merchandise details without searching task descriptions. */
public class FindMerchandiseCommand extends Command {
    private final String keyword;

    /** Creates a merchandise-search command. */
    public FindMerchandiseCommand(String keyword, Parser parser) {
        super(parser);
        this.keyword = keyword;
    }

    /** Displays tasks whose merchandise matches the keyword. */
    @Override
    public void execute(AppContext context) throws OreoException {
        if (keyword.isEmpty()) {
            throw new OreoException("Use: find-merchandise KEYWORD");
        }
        context.getUi().showMatchingMerchandise(context.getTasks().findMerchandise(keyword));
    }
}
