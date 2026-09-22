package mono.tool;

import mono.MonoBot;
import mono.exception.MonoException;
import mono.exception.WrongFormatException;

/**
 * Ends the Mono session.
 */
public class ByeTool implements Tool {
    /** Creates a tool that ends a Mono session. */
    public ByeTool() {
    }

    /**
     * Displays Mono's farewell when no arguments are supplied.
     *
     * @param arguments text following the tool name
     * @param bot chatbot used to display the farewell
     * @return {@link ToolSignal#EXIT}
     * @throws MonoException if arguments are supplied
     */
    @Override
    public ToolSignal invoke(String arguments, MonoBot bot) throws MonoException {
        if (arguments == null || !arguments.isEmpty()) {
            throw new WrongFormatException("bye does not accept arguments.");
        }
        bot.exit();
        return ToolSignal.EXIT;
    }
}
