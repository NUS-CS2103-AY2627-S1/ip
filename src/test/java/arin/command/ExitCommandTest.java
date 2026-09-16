package arin.command;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import arin.storage.Storage;
import arin.ui.Ui;

/**
 * Regression checks for the exit command.
 */
public class ExitCommandTest {
    /**
     * Verifies that the exit command displays a goodbye message and ends the loop.
     *
     * @param args command-line arguments, which are not used
     * @throws Exception if command execution fails
     */
    public static void main(String[] args) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Ui ui = new Ui(new Scanner(""), new PrintStream(buffer));
        Command command = new ExitCommand();

        command.execute(List.of(), ui, new Storage());

        if (!command.isExit()) {
            throw new AssertionError("ExitCommand must signal that Arin should exit.");
        }
        if (!buffer.toString().contains("Bye. Hope to see you again soon!")) {
            throw new AssertionError("ExitCommand did not show the goodbye message.");
        }

        System.out.println("ExitCommandTest passed");
    }
}
