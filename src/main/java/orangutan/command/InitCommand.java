package orangutan.command;

import java.io.IOException;
import java.nio.file.Files;
import java.time.format.DateTimeParseException;

import orangutan.OrangutanException;
import orangutan.chatlist.ChatList;
import orangutan.chatlist.ListItem;

/**
 * Command to initiate chatbot.
 * This command cannot be accessed by the user.
 */
class InitCommand implements Command {
    /**
     * Loads data from file and stores it in context, then flags the program to start collecting input.
     * If no file exists, will load an empty list into context.
     * Terminates program if read file fails, or if the stored dates and times are of the wrong format.
     *
     * @param context Context item storing information on the chatbot's current internal state.
     * @return Welcome message if successful.
     * @throws OrangutanException If read file has failed, or dates and times in file are of the wrong format,
     *      or if a user tries to run this command.
     */
    public String run(Context context) throws OrangutanException {
        // initialize list from save file if one exists. If not, initialize an empty list
        // only reachable in the initial state, before user input is queried.
        // this is only reachable when context.isRunLoop is false, since isRunLoop is initialized to false
        // if loading data is a success, isRunLoop set to true and we start querying user for input.
        String welcome = "Greetings, I am Orangutan. In what manner may I be of service today?";
        assert context != null : "InitCommand says: context should not be null!!!";

        if (Files.exists(context.getFilePath())) {
            try {
                context.setRun(true);
                context.setList(context.getStorage().readFromFile(context.getFilePath()));
                return (welcome);
            } catch (IOException e) {
                context.setRun(false);
                throw CommandError.fileReadError(context.getFilePath().toString());
            } catch (DateTimeParseException e) {
                context.setRun(false);
                throw CommandError.dateTimeLoadError(context.getFilePath().toString(),
                        ListItem.DATE_TIME_INPUT_FORMAT);
            } catch (IllegalArgumentException e) {
                context.setRun(false);
                throw CommandError.sortMethodLoadError(context.getFilePath().toString());
            }
        } else {
            context.setRun(true);
            context.setList(new ChatList());
            return (welcome);
        }
    }
}
