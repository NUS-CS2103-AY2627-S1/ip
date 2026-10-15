package orangutan.chatlist;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stores information on a single item in the list.
 */
public class ListItem {
    public static final String DATE_TIME_INPUT_FORMAT = "yyyyMMdd HHmm"; // exposed for error messages
    private static final DateTimeFormatter DATE_TIME_INPUT_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_INPUT_FORMAT);
    private static final DateTimeFormatter DATE_TIME_OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String COMPLETED_ICON = "[X]";
    private static final String NOT_COMPLETED_ICON = "[ ]";

    private final ItemType type;
    private final String title;
    private boolean isCompleted; // not final so we can mark and unmark a task
    private final LocalDateTime from;
    private final LocalDateTime to;
    private final LocalDateTime by;

    /**
     * Constructs an ListItem.
     *
     * @param type ItemType corresponding to the type of the item, can be TODO, DEADLINE or EVENT.
     * @param title Title of the list item (e.g. dinner).
     * @param isCompleted Whether the item has already been completed.
     * @param params Time parameters, formatted "yyyy-MM-dd HH:mm" (e.g. "2026-08-31 15:00").
     *     Params required are as follows: to-do - no extra params. deadline - param "by". event - params "from", "to".
     */
    public ListItem(ItemType type, String title, boolean isCompleted, String... params) {
        assert type != null : "ListItem says: type should not be null!!!";
        assert title != null : "ListItem says: title should not be null!!!";

        this.type = type;
        this.title = title;
        this.isCompleted = isCompleted;

        switch (type) { // correct number of parameters is checked by the calling class
            case TODO:
                from = null;
                to = null;
                by = null;
                break;
            case DEADLINE:
                assert params.length >= 1 : "ListItem says: params should be length 1!!!";

                from = null;
                to = null;
                by = LocalDateTime.parse(params[0], DATE_TIME_INPUT_FORMATTER);
                break;
            case EVENT:
                assert params.length >= 2 : "ListItem says: params should be length 2!!!";

                from = LocalDateTime.parse(params[0], DATE_TIME_INPUT_FORMATTER);
                to = LocalDateTime.parse(params[1], DATE_TIME_INPUT_FORMATTER);
                by = null;
                break;
            default:
                from = null;
                to = null;
                by = null;
                break;
        }
    }

    /**
     * Constructs a ListItem from a comma-separated string.
     *
     * @param fromFileLine Comma-separated string "[icon],[isCompleted],[title],[from / by],[to]".
     *     (e.g. "[E],true,Dinner,20260831 1700,20260831 1900").
     */
    public static ListItem parseLine(String fromFileLine) {
        String[] data = fromFileLine.split(",");

        ItemType type = ItemType.getTypeFromIcon(data[0]);
        boolean isCompleted = Boolean.parseBoolean(data[1]);
        String item = data[2];

        String[] params = {};
        if (data.length > 3) { // correct number of parameters is checked by the calling class
            params = Arrays.copyOfRange(data, 3, data.length);
        }

        return new ListItem(type, item, isCompleted, params);
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public LocalDateTime getStartDateTime() {
        return switch (type) {
            case TODO -> LocalDateTime.MIN;
            case DEADLINE -> by;
            case EVENT -> from;
            default -> null;
        };
    }

    /**
     * Marks item as completed.
     */
    public void mark() {
        isCompleted = true;
    }

    /**
     * Unmarks item as to be completed.
     */
    public void unmark() {
        isCompleted = false;
    }

    /**
     * Returns a comma-separated string representation of the data in this object to be written to a file.
     *
     * @return String "[icon],[isCompleted],[title],[from/by],[to]" (e.g. "[D],true,report,20260831 1900").
     */
    public String toFile() {
        String icon = type.getIcon();
        String isCompleted = Boolean.toString(this.isCompleted);

        // here we use the input formatter because these lines need to be parsed in the future
        String by = paramToString("", this.by, "", "", DATE_TIME_INPUT_FORMATTER);
        String from = paramToString("", this.from, "", "", DATE_TIME_INPUT_FORMATTER);
        String to = paramToString("", this.to, "", "", DATE_TIME_INPUT_FORMATTER);
        return Stream.of(icon, isCompleted, title, by, from, to)
                .filter(s -> s != null)
                .collect(Collectors.joining(","));
    }

    /**
     * Returns item parameters (by, from, to) as a String.
     *
     * @param paramName Parameter name ("by", "from", "to").
     * @param param Parameter value (e.g. "Monday 5pm").
     * @param prefix Any String to be added to the beginning of the result.
     * @param suffix Any String to be added after the end of the result.
     * @param formatter Format time params to this.
     * @return Null if the parameter is of null value, else a String of format "prefix paramName param suffix".
     *     (e.g. "(by Monday 5pm)", prefix is "(", suffix is ")" and paramName is "by ")
     */
    private static String paramToString(String paramName, LocalDateTime param, String prefix, String suffix,
            DateTimeFormatter formatter) {
        return param == null ? null : prefix + paramName + param.format(formatter) + suffix;
    }

    /**
     * Prints this item as a String.
     *
     * @return String of format "[icon] [isCompleted ([X] or [ ])] title, (paramName: paramValue)".
     *     (e.g. "[T] [X] Dinner (from: 6pm to: 7pm)")
     */
    @Override
    public String toString() {
        String icon = type.getIcon();
        String isCompleted = this.isCompleted ? COMPLETED_ICON : NOT_COMPLETED_ICON;
        String item = this.title;
        String by = paramToString("by ", this.by, "(", ")", DATE_TIME_OUTPUT_FORMATTER);
        String from = paramToString("from ", this.from, "(", "", DATE_TIME_OUTPUT_FORMATTER);
        String to = paramToString("to ", this.to, "", ")", DATE_TIME_OUTPUT_FORMATTER);

        return Stream.of(icon, isCompleted, item, by, from, to)
                .filter(s -> s != null)
                .collect(Collectors.joining(" "));
    }
}
