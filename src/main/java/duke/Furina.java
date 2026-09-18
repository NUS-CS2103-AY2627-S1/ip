package duke;

/** Entry point for the Furina chatbot. */
class Furina {
    /** Starts Furina, reads commands, and coordinates task operations. */
    static void main(String[] args) {
        Ui ui = new Ui();
        CommandHandler handler = new CommandHandler();
        ui.showWelcome();

        String command;
        while ((command = ui.readCommand()) != null) {
            if (command.isBlank()) {
                continue;
            }
            if (command.trim().equalsIgnoreCase("bye")) {
                break;
            }
            ui.showLine();
            ui.showResponse(handler.handleCommand(command));
            ui.showLine();
        }

        ui.showGoodbye();
    }
}
