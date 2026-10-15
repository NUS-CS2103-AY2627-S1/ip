package orangutan.command;

import java.io.IOException;

import orangutan.OrangutanException;

/**
 * Command to save list to file and exit chatbot.
 */
class ByeCommand implements Command {
    /**
     * Saves list to file and exits chatbot.
     *
     * @param context Context item storing information on the chatbot's current internal state.
     * @return Goodbye message, after successfully writing to file.
     * @throws OrangutanException When write to file fails, in which case the bot does not exit
     *      and continues to take input.
     */
    public String run(Context context) throws OrangutanException {
        assert context != null : "ByeCommand says: context should not be null!!!";
        assert context.getFilePath() != null : "ByeCommand says: filepath should not be null!!!";
        assert context.getList() != null : "ByeCommand says: list should not be null!!!";

        try {
            context.setRun(false);
            context.getStorage().writeToFile(context.getFilePath(), context.getList());
            return ("Fare thee well, and may we meet again.");
        } catch (IOException e) {
            context.setRun(true);
            throw CommandError.fileWriteError(context.getFilePath().toString());
        }
    }
}
