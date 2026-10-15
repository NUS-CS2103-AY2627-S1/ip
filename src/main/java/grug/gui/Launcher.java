package grug.gui;

import javafx.application.Application;

/**
 * Launcher class to get around the "Error: JavaFX runtime components are
 * missing, and are required to run this application" error.
 * <p>
 * The workaround is to make this separate Launcher class which can either
 * simply call {@code Main.main(args)} or
 * {@code Application.launch(Main.class, args)}.
 * <p>
 *
 * See:
 * <ul>
 * <li>https://stackoverflow.com/questions/56894627/</li>
 * <li>
 * https://github.com/se-edu/addressbook-level3/commit/12bb91903e71ea1109e04f7369c2169f1c7be39a
 * </li>
 * </ul>
 *
 * for more details.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(GrugApp.class, args);
    }
}
