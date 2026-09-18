package hermes.command;

import hermes.task.LogBook;

/** Says goodbye and ends the conversation. */
public class ByeCommand extends Command {

    @Override
    public String execute(LogBook logBook) {
        return "Fare thee well. Shouldst thou have need of me, call, and I shall come swifter than the wind.";
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
