package ff15.command;

import ff15.FF15Exception;
import ff15.Storage;
import ff15.Ui;
import ff15.ai.AiAssistant;
import ff15.contact.ContactList;
import ff15.task.TaskList;

/** Asks the AI a question about FF15's features, and shows its answer. */
public class AiAskCommand extends Command {
    private final AiAssistant assistant;
    private final String question;

    /**
     * Creates a command that puts {@code question} to the AI.
     *
     * @param assistant what knows how to ask the AI about FF15.
     * @param question what the user wants to know.
     */
    public AiAskCommand(AiAssistant assistant, String question) {
        this.assistant = assistant;
        this.question = question;
    }

    /** Shows the AI's answer, one line of output per line it wrote. Nothing is changed or saved. */
    @Override
    public void execute(TaskList tasks, ContactList contacts, Ui ui, Storage storage) throws FF15Exception {
        String answer = assistant.answerQuestion(question);
        ui.showMessage(answer.lines()
                .filter(line -> !line.isBlank())
                .toArray(String[]::new));
    }
}
