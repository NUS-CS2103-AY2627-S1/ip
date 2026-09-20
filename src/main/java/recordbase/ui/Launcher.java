package recordbase.ui;

import javafx.application.Application;

/**
 * Provides the executable entry point for the graphical Record application.
 *
 * <p>This class deliberately does not extend {@link Application}. Starting JavaFX from a
 * separate launcher avoids class-path detection problems that can occur when a packaged
 * application is started directly through an {@code Application} subclass. The launcher
 * delegates all lifecycle management to {@link RecordGui}.</p>
 */
public class Launcher {

    /**
     * Creates a launcher instance.
     *
     * <p>Launching does not require an instance; this constructor preserves the class's
     * original public construction contract.</p>
     */
    public Launcher() { }

    /**
     * Starts the JavaFX runtime and creates a {@link RecordGui} application instance.
     *
     * <p>Command-line arguments are forwarded unchanged to JavaFX and can subsequently be
     * obtained through the standard JavaFX application parameters API.</p>
     *
     * @param args command-line arguments supplied when Record is launched
     */
    public static void main(String[] args) {
        Application.launch(RecordGui.class, args);
    }
}
