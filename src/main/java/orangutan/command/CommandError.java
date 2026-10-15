package orangutan.command;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

import orangutan.Orangutan;
import orangutan.OrangutanException;

/**
 * Utility class for throwing OrangutanExceptions with the appropriate messages.
 */
class CommandError {
    static OrangutanException fileWriteError(String path) {
        String msg = String.format("Alas! I was unable to inscribe your list in %s.\n\n"
                + "Pray grant me access to the requisite files and folders ere we bid farewell.", path);
        return new OrangutanException(msg);
    }

    static OrangutanException fileReadError(String path) {
        String msg = String.format("Alas! I could not retrieve the records preserved in %s.\n\n"
                + "Pray grant me access to the requisite files ere you return to me.", path);
        return new OrangutanException(msg);
    }

    static OrangutanException dateTimeParseError(String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String msg = String.format("Alas! I do not comprehend the dates and times you have told me.\n\n"
                + "Please ensure your dates are of format %s (e.g. %s).",
                format.toLowerCase(), LocalDateTime.now().format(formatter));
        return new OrangutanException(msg);
    }

    static OrangutanException dateTimeLoadError(String path, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String msg = String.format("Alas! The dates and times thou hast supplied are beyond my understanding %s.\n\n"
                + "Prithee provide them in the following format: %s (for example, %s).",
                path, format.toLowerCase(), LocalDateTime.now().format(formatter));
        return new OrangutanException(msg);
    }

    static OrangutanException emptyListError(String action) {
        String msg = String.format("Alas! There is naught to %s.\n\n"
                + "Pray add some items to the list first.", action);
        return new OrangutanException(msg);
    }

    static OrangutanException indexOutOfBoundsError(int length) {
        String msg = String.format("Alas! That number appeareth not in your list.\n\n"
                + "Pray choose an index between 1 and %d (inclusive).", length);
        return new OrangutanException(msg);
    }

    static OrangutanException numberParseError(int length) {
        String msg = String.format("Alas! That is no number known to me.\n\n"
                + "Please input an integer between 1 and %d (inclusive).", length);
        return new OrangutanException(msg);
    }

    static OrangutanException missingDeadlineParamError() {
        String msg = String.format("Alas! The deadline's details remain incomplete.\n\n"
                + "Pray include /by, along with the due date and time.");
        return new OrangutanException(msg);
    }

    static OrangutanException missingEventParamError() {
        String msg = String.format("Alas! The event's details remain incomplete.\n\n"
                + "pray include /from and /to in your message, along with the start and end dates and times.");
        return new OrangutanException(msg);
    }

    static OrangutanException missingSortMethodError() {
        String sortMethodNames = Arrays.stream(SortMethod.values())
                .map(Enum::name)
                .map(String::toLowerCase)
                .collect(Collectors.joining(", "));
        String msg = String.format("Alas! Thou hast not chosen a sorting method.\n\n"
                + "Pray choose one of the sorting methods I have learned: %s.", sortMethodNames);
        return new OrangutanException(msg);
    }

    static OrangutanException unknownSortMethodError() {
        String sortMethodNames = Arrays.stream(SortMethod.values())
                .map(Enum::name)
                .map(String::toLowerCase)
                .collect(Collectors.joining(", "));
        String msg = String.format("Alas! This sorting method is unknown to me.\n\n"
                + "Pray choose one of the sorting methods I have learned: %s.", sortMethodNames);
        return new OrangutanException(msg);
    }

    static OrangutanException sortMethodLoadError(String path) {
        String sortMethodNames = Arrays.stream(SortMethod.values())
                .map(Enum::name)
                .map(String::toLowerCase)
                .collect(Collectors.joining(", "));
        String msg = String.format("Alas! The sorting method preserved in %s is unknown to me.\n\n"
                        + "Pray choose one of the sorting methods I have learned: %s.", path, sortMethodNames);
        return new OrangutanException(msg);
    }

    static OrangutanException missingTitleError(String action) {
        String msg = String.format("Alas! The %s hath been given no name.\n\n"
                + "Pray provide the %s name.", action, action);
        return new OrangutanException(msg);
    }

    static OrangutanException missingIndexError(String action, int index) {
        String msg = String.format("Alas! I know not which item to %s.\n\n"
                + "Pray follow the %s command with an integer from 1 to %d, inclusive.", action, action, index);
        return new OrangutanException(msg);
    }

    static OrangutanException missingFindKeywordError() {
        String msg = "Alas! I know not what to find.\n\n"
                + "Pray include the words you wish to find in the list.";
        return new OrangutanException(msg);
    }

    static OrangutanException unknownCommandError() {
        String commandNames = Arrays.stream(CommandType.values())
                .skip(1) // we must skip the INIT command.
                .map(Enum::name)
                .map(String::toLowerCase)
                .collect(Collectors.joining(", "));
        String msg = String.format("Alas! My simian mind is unable to comprehend your instructions.\n\n"
                + "Please use instructions I understand: %s", commandNames);
        return new OrangutanException(msg);
    }

}
