package duchess.ui.gui.controller;

import javafx.application.Application;

/**
 * Acts as a workaround for classpath issues in JDK11,
 * as specified in
 * <a href="https://github.com/se-edu/addressbook-level3/commit/12bb91903e71ea1109e04f7369c2169f1c7be39a">
 *     this commit</a>.
 */
public class Launcher {
    public static void main(String... args) {
        Application.launch(Main.class, args);
    }
}
