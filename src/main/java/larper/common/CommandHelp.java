package larper.common;

/**
 * Stores command examples used in exception messages.
 */
public class CommandHelp {
    /** Supported command formats shown when task input is invalid. */
    public static final String INPUT_FORMATS = " Please use one of these formats:\n"
            + " todo DESCRIPTION\n"
            + " deadline DESCRIPTION /by DATE TIME\n"
            + " event DESCRIPTION /from START_DATE START_TIME /to END_DATE END_TIME\n"
            + " Dates: 2019-10-15, 2/12/2019, Aug 6, August 6th, or Monday.\n"
            + " Times: 2pm, 2:30pm, 1400, 14:00, or no time.";

    /** Supported date examples shown when a required date is missing or invalid. */
    public static final String DATE_EXAMPLES = " Dates can look like 2019-10-15, 2/12/2019, Aug 6, "
            + "August 6th, or Monday.\n"
            + " Try: deadline DESCRIPTION /by 2019-10-15 1400\n"
            + " Or: event DESCRIPTION /from Aug 6 2pm /to Aug 6 4pm";

    /** Supported optional time examples shown when a time confirmation is needed. */
    public static final String TIME_EXAMPLES = " Time is optional: add 2pm, 2:30pm, 1400, 14:00, "
            + "or type no time.";

    private CommandHelp() {
    }
}
