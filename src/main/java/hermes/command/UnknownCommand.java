package hermes.command;

import hermes.task.LogBook;

/**
 * Reports that the user typed something Hermes does not recognize.
 *
 * <p>Not recognizing a command is an ordinary outcome rather than an error, so
 * it is a Command like any other and the main loop needs no special case.
 */
public class UnknownCommand extends Command {

    private final String word;

    /**
     * @param word what the user typed in place of a command.
     */
    public UnknownCommand(String word) {
        this.word = word;
    }

    @Override
    public String execute(LogBook logBook) {
        return String.format("Alas, '%s' is a word unknown even upon Olympus. I cannot act upon it.",
                this.word);
    }
}
