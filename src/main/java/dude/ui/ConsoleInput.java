package dude.ui;

import java.util.Objects;
import java.util.Scanner;

/**
 * Reads command input from a console scanner.
 */
final class ConsoleInput {
    private final Scanner scanner;

    /**
     * Creates an input adapter.
     *
     * @param scanner Source of input lines.
     */
    ConsoleInput(Scanner scanner) {
        this.scanner = Objects.requireNonNull(scanner, "scanner");
    }

    boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    String readCommand() {
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (!input.isBlank()) {
                return input;
            }
        }
        return null;
    }

    String readInputLine() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }
}
