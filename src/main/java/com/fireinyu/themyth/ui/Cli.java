package com.fireinyu.themyth.ui;

import java.util.Scanner;

import com.fireinyu.themyth.Defaults;
import com.fireinyu.themyth.TheMyth;
import com.fireinyu.themyth.responses.ExceptionResponse;
import com.fireinyu.themyth.responses.FatalResponse;
import com.fireinyu.themyth.responses.Response;

/**
 * Runs the command-line conversation with The Myth.
 */
public class Cli {

    private final TheMyth model;
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Initializes the command-line interface with its application logic.
     *
     * @param model Application logic used to handle requests.
     */
    public Cli(TheMyth model) {
        this.model = model;
    }

    /**
     * Runs the CLI.
     */
    public void run() {
        showWelcome();
        while (true) {
            String input = scanner.nextLine();
            Response response = model.handleInput(input);
            if (response instanceof ExceptionResponse) {
                say(Defaults.TWEAKPROMPT, response.getBody());
            } else if (response instanceof FatalResponse) {
                say(Defaults.DEATHPROMPT, response.getBody());
            } else {
                say(response.getBody());
            }
            if (response.doExit()) {
                break;
            }
        }
    }

    /**
     * Displays the banner, starts the model, and prints the initial greeting.
     */
    private void showWelcome() {
        String banner = """
                        ▄▄▄█████▓ ██░ ██ ▓█████     ███▄ ▄███▓▓██   ██▓▄▄▄█████▓ ██░ ██\s
                        ▓  ██▒ ▓▒▓██░ ██▒▓█   ▀    ▓██▒▀█▀ ██▒ ▒██  ██▒▓  ██▒ ▓▒▓██░ ██▒
                        ▒ ▓██░ ▒░▒██▀▀██░▒███      ▓██    ▓██░  ▒██ ██░▒ ▓██░ ▒░▒██▀▀██░
                        ░ ▓██▓ ░ ░▓█ ░██ ▒▓█  ▄    ▒██    ▒██   ░ ▐██▓░░ ▓██▓ ░ ░▓█ ░██\s
                          ▒██▒ ░ ░▓█▒░██▓░▒████▒   ▒██▒   ░██▒  ░ ██▒▓░  ▒██▒ ░ ░▓█▒░██▓
                          ▒ ░░    ▒ ░░▒░▒░░ ▒░ ░   ░ ▒░   ░  ░   ██▒▒▒   ▒ ░░    ▒ ░░▒░▒
                            ░     ▒ ░▒░ ░ ░ ░  ░   ░  ░      ░ ▓██ ░▒░     ░     ▒ ░▒░ ░
                          ░       ░  ░░ ░   ░      ░      ░    ▒ ▒ ░░    ░       ░  ░░ ░
                                  ░  ░  ░   ░  ░          ░    ░ ░               ░  ░  ░
                                                               ░ ░                     \s
                """;
        System.out.println(banner);
        model.start();
        say("""
                Haaaay superstar! The Myth has entered the room! ✨
                What fabulous wonders are we creating today, honey? 💅
                """);
    }

    /**
     * Prints a response with separators and the next input prompt.
     */
    private void say(String botPrompt, String message) {
        System.out.println(new StringBuilder().repeat('—', Defaults.LINEWIDTH).toString());
        System.out.println(botPrompt + '\n' + message.indent(4));
        System.out.println(new StringBuilder().repeat('—', Defaults.LINEWIDTH).toString());
        System.out.print(Defaults.USERPROMPT);
    }

    private void say(String message) {
        say(Defaults.BOTPROMPT, message);
    }

    /**
     * Entry-point to run CLI.
     */
    public static void main(String... args) {
        new Cli(new TheMyth()).run();
    }
}
