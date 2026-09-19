package nudge;

import java.util.List;

/**
 * Represents one response from Nudge, including its main message and optional details.
 */
public final class NudgeResponse {
    private final String header;
    private final List<String> details;
    private final String footer;
    private final boolean shouldExit;
    private final boolean isError;

    private NudgeResponse(String header, List<String> details, String footer,
                          boolean shouldExit, boolean isError) {
        assert header != null : "Response header should not be null";
        assert details != null : "Response details should not be null";
        assert hasNoNullDetails(details) : "Response details should not contain null lines";
        this.header = header;
        this.details = List.copyOf(details);
        this.footer = footer;
        this.shouldExit = shouldExit;
        this.isError = isError;
    }

    private static boolean hasNoNullDetails(List<String> details) {
        for (String detail : details) {
            if (detail == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a response containing only a main message.
     *
     * @param message main response message.
     * @return response containing the message.
     */
    public static NudgeResponse message(String message) {
        return new NudgeResponse(message, List.of(), null, false, false);
    }

    /**
     * Creates a response that describes an error caused by a command or the environment.
     *
     * @param message error message to display.
     * @return response marked for prominent error styling.
     */
    public static NudgeResponse error(String message) {
        return new NudgeResponse(message, List.of(), null, false, true);
    }

    /**
     * Creates a response that tells the user the application should close.
     *
     * @param message final response message.
     * @return response containing the final message.
     */
    public static NudgeResponse exitMessage(String message) {
        return new NudgeResponse(message, List.of(), null, true, false);
    }

    /**
     * Creates a response with a main message and supporting detail lines.
     *
     * @param message main response message.
     * @param details supporting detail lines.
     * @return response containing the message and details.
     */
    public static NudgeResponse withDetails(String message, List<String> details) {
        return new NudgeResponse(message, details, null, false, false);
    }

    /**
     * Creates a response from a variable number of supporting detail lines.
     *
     * @param message main response message.
     * @param details supporting detail lines.
     * @return response containing the message and details.
     */
    public static NudgeResponse withDetails(String message, String... details) {
        return new NudgeResponse(message, List.of(details), null, false, false);
    }

    /**
     * Creates a response with a main message, details, and a concluding message.
     *
     * @param message main response message.
     * @param details supporting detail lines.
     * @param footer concluding response message.
     * @return response containing all supplied lines.
     */
    public static NudgeResponse withDetails(String message, List<String> details, String footer) {
        return new NudgeResponse(message, details, footer, false, false);
    }

    public String getHeader() {
        return header;
    }

    public List<String> getDetails() {
        return details;
    }

    public boolean hasFooter() {
        return footer != null;
    }

    public String getFooter() {
        return footer;
    }

    public boolean shouldExit() {
        return shouldExit;
    }

    public boolean isError() {
        return isError;
    }

    /**
     * Formats the complete response for display in a graphical dialog box.
     *
     * @return response lines joined with line breaks.
     */
    public String getDisplayText() {
        StringBuilder responseText = new StringBuilder(header);
        for (String detail : details) {
            responseText.append(System.lineSeparator()).append(detail);
        }
        if (hasFooter()) {
            responseText.append(System.lineSeparator()).append(footer);
        }
        return responseText.toString();
    }
}
