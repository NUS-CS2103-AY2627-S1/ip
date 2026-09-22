package nico;

import java.lang.reflect.Field;
import java.util.Scanner;

/**
 * Runs the actual command and response code through stdin for scripted UI tests.
 */
public final class ConsoleTestAdapter {
    private ConsoleTestAdapter() {
    }

    /**
     * Runs commands without starting the JavaFX window.
     */
    public static void main(String[] args) throws ReflectiveOperationException, NicoException {
        Nico nico = new Nico();
        Ui ui = new Ui();
        Field responseField = Ui.class.getDeclaredField("responseBuilder");
        responseField.setAccessible(true);
        StringBuilder response = (StringBuilder) responseField.get(ui);
        nico.loadTasks();
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                response.setLength(0);
                boolean shouldExit = false;
                try {
                    shouldExit = nico.processCommand(scanner.nextLine(), ui);
                } catch (NicoException exception) {
                    ui.showMessage(exception.getMessage());
                }
                System.out.println(response);
                if (shouldExit) {
                    break;
                }
            }
        }
    }
}
