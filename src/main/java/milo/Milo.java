package milo;

/** Coordinates command processing for Milo's user interfaces. */
public class Milo {
    private final Logic logic = new Logic();

    /** Returns Milo's reply for the supplied user command. */
    public String getResponse(String input) {
        return logic.execute(input);
    }

    /** Returns a reply with the status needed to style errors in the GUI. */
    public Response getGuiResponse(String input) {
        return logic.executeResponse(input);
    }

    /** Preserves startup loading failures as errors in the GUI. */
    public Response getGuiGreeting() {
        return new Response(getGreeting(), logic.hasLoadingError(), "");
    }

    /** Returns the first reply displayed when the GUI opens. */
    public String getGreeting() {
        if (logic.hasLoadingError()) {
            return logic.getLoadingError();
        }
        return "Hey there! My name is Milo.\nHow can I help you today?";
    }
}
