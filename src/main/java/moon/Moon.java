package moon;

/** Runs Moon, a chatbot that manages a list of tasks through the console. */
public class Moon {
    /**
     * Starts the chatbot and reads commands from the user.
     *
     * @param args command-line arguments, which are not used by this program
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        MoonEngine engine = new MoonEngine();

        ui.showWelcome();
        if (engine.hasLoadingError()) {
            ui.showLoadingError();
        }

        String command;
        while ((command = ui.readCommand()) != null) {
            ui.showLine();
            if (command.equals("bye")) {
                ui.showGoodbye();
                ui.showLine();
                return;
            }
            System.out.print(engine.getResponse(command));
            ui.showLine();
        }
    }
}
