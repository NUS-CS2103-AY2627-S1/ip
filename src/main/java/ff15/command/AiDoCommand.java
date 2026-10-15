package ff15.command;

import ff15.CommandWord;
import ff15.FF15Exception;
import ff15.Parser;
import ff15.Storage;
import ff15.Ui;
import ff15.ai.AiAssistant;
import ff15.contact.ContactList;
import ff15.task.TaskList;

/**
 * Asks the AI to turn a request in plain words, such as "remind me to call Pam
 * tomorrow", into an FF15 command, and suggests that command to the user.
 *
 * <p>The command is suggested, not run. The AI can misread a request, and a
 * wrong {@code delete} cannot be taken back, so the user sees exactly what
 * would be run and sends it themselves to go ahead. In the window, the
 * suggestion is put in the text box ready to send or edit.
 */
public class AiDoCommand extends Command {
    private final AiAssistant assistant;
    private final String request;

    /**
     * Creates a command that asks the AI to carry out {@code request}.
     *
     * @param assistant what knows how to ask the AI about FF15.
     * @param request what the user wants done, in their own words.
     */
    public AiDoCommand(AiAssistant assistant, String request) {
        this.assistant = assistant;
        this.request = request;
    }

    /**
     * Shows the command the AI came up with, once it is known to be one FF15 can
     * run. Nothing is changed or saved until the user sends it.
     *
     * @throws FF15Exception if the AI could not be reached, found no command
     *     that fits, or came up with one that FF15 cannot run.
     */
    @Override
    public void execute(TaskList tasks, ContactList contacts, Ui ui, Storage storage) throws FF15Exception {
        String suggestion = assistant.suggestCommand(request);
        requireRunnable(suggestion);
        ui.showSuggestion(suggestion);
    }

    /**
     * Rejects a suggestion FF15 could not run as it stands. Parsing it here
     * catches a malformed command now, while the user can still rephrase, rather
     * than after they have sent it. Suggesting another AI command is refused too,
     * as it would only send the user round in a circle.
     */
    private static void requireRunnable(String suggestion) throws FF15Exception {
        CommandWord word = CommandWord.match(suggestion);
        if (word == CommandWord.AI || word == CommandWord.DO) {
            throw new FF15Exception("The AI told me to ask the AI. I'm not doing that. "
                    + "Try asking another way.");
        }
        try {
            Parser.parse(suggestion);
        } catch (FF15Exception e) {
            throw new FF15Exception("The AI came up with '" + suggestion
                    + "', and I can't run that. Try asking another way.");
        }
    }
}
