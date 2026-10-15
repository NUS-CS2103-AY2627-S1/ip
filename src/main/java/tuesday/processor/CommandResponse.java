package tuesday.processor;

import tuesday.ui.Strings;

/**
 * Carries response text and whether the interface should offer the user guide.
 *
 * @param text the message without a rendered hyperlink.
 * @param showUserGuide whether to display the user-guide link with this message.
 */
public record CommandResponse(String text, boolean showUserGuide) {
    /**
     * Includes the guide address for callers that display plain text.
     *
     * @return the complete response with a guide address when requested.
     */
    public String toPlainText() {
        return showUserGuide ? text + "\nUser guide: " + Strings.USER_GUIDE_URL : text;
    }
}
