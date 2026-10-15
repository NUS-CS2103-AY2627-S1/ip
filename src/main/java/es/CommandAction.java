package es;

/** Represents an executable chatbot command action. */
public abstract class CommandAction {
    /** Executes this action. */
    public abstract void execute();

    /** Returns whether this action terminates the chatbot. */
    public boolean isExit() {
        return false;
    }
}
