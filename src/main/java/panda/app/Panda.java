package panda.app;

import java.util.Scanner;

import panda.ui.Ui;

/**
 * The cool entry point for the Panda chatbot.
 */
public class Panda {
    /**
     * Runs the Panda command-line assistant.
     *
     * @param args command-line arguments; currently unused
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        PandaService service = new PandaService();
        if (service.getStartupWarning() != null) {
            ui.showDivider();
            System.out.println(service.getStartupWarning());
            ui.showDivider();
        }
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            ui.showDivider();

            String response = service.getResponse(command);
            if (response.equals(PandaService.BYE_RESPONSE)) {
                ui.showBye();
                break;
            }
            System.out.println(response);
            ui.showDivider();
        }
    }

}
