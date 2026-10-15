package orangutan.command;

import orangutan.OrangutanException;

/**
 * Command to set sorting method of the list.
 */
public class SortCommand {
    private final String param;

    SortCommand(String param) {
        this.param = param.toLowerCase();
    }

    /**
     * Sets list sorting method:
     *      "off" means no sorting
     *      "completion" means sorting by completion status, then alphabetically for ties.
     *      "datetime" means sorting by time (start time for events, due date for deadlines),
     *              then alphabetically for ties. To-dos are placed at the top.
     */
    public String run(Context context) throws OrangutanException {
        try {
            context.getList().setSorting(SortMethod.valueOf(param.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw CommandError.unknownSortMethodError();
        }
        if (param.equals("none")) {
            return "Any additions to your list shall henceforth take its place at the end of the list.";
        } else {
            return String.format("Your list shall henceforth be ordered by %s.\n%s", param, context.getList().toString());
        }
    }
}
