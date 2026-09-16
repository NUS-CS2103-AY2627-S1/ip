package rambo;

import rambo.ui.Constants;
import rambo.ui.Ui;

/**
 * Runs a simple mode that repeats each line entered by the user.
 */
public class Echo {
    private static void showIntroduction(Ui ui) {
        ui.showDivider();
        ui.showLine("Welcome to Echo!");
        ui.showLine("Here I will echo whatever you tell me!");
        ui.showLine("Enter /exit to go back!");
        ui.showDivider();
    }

    /**
     * Repeats input until the user enters {@code /exit} or closes the input stream.
     *
     * @param ui user interface used for input and output
     */
    public static void start(Ui ui) {
        ui.showDivider("Echo");
        showIntroduction(ui);
        while (true) {
            ui.showPrompt("You: ");
            if (!ui.hasNextLine()) {
                return;
            }
            String userText = ui.readLine();

            if (userText.equals("/exit")) {
                ui.showLine("Back to home!");
                return;
            }

            ui.showPrompt(Constants.BOT_NAME + ": ");
            ui.showLine(userText);
        }
    }
}
