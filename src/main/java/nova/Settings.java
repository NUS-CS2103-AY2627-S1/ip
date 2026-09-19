package nova;

import java.util.prefs.Preferences;

/**
 * Remembers the choices the user made in the GUI between runs: the theme, and
 * where the window was left. Uses the platform preference store, which is the
 * registry on Windows and a properties file elsewhere, so nothing extra is
 * written next to the save file.
 */
public class Settings {
    private static final String KEY_DARK_THEME = "darkTheme";
    private static final String KEY_WINDOW_X = "windowX";
    private static final String KEY_WINDOW_Y = "windowY";
    private static final String KEY_WINDOW_WIDTH = "windowWidth";
    private static final String KEY_WINDOW_HEIGHT = "windowHeight";

    private static final boolean DEFAULT_DARK_THEME = true;
    private static final double DEFAULT_WIDTH = 460;
    private static final double DEFAULT_HEIGHT = 720;
    private static final double NO_POSITION = -1;

    private final Preferences preferences;

    /**
     * Creates settings backed by this user's preference store.
     */
    public Settings() {
        this.preferences = Preferences.userNodeForPackage(Settings.class);
    }

    /**
     * Returns whether the user last chose the dark theme.
     *
     * @return true when the dark theme should be used
     */
    public boolean isDarkTheme() {
        return this.preferences.getBoolean(KEY_DARK_THEME, DEFAULT_DARK_THEME);
    }

    /**
     * Remembers the theme the user just chose.
     *
     * @param isDark whether the dark theme is in use
     */
    public void setDarkTheme(boolean isDark) {
        this.preferences.putBoolean(KEY_DARK_THEME, isDark);
    }

    /**
     * Returns the remembered window width.
     *
     * @return the width in pixels, or the default when nothing was remembered
     */
    public double getWindowWidth() {
        return this.preferences.getDouble(KEY_WINDOW_WIDTH, DEFAULT_WIDTH);
    }

    /**
     * Returns the remembered window height.
     *
     * @return the height in pixels, or the default when nothing was remembered
     */
    public double getWindowHeight() {
        return this.preferences.getDouble(KEY_WINDOW_HEIGHT, DEFAULT_HEIGHT);
    }

    /**
     * Returns the remembered window x position.
     *
     * @return the x coordinate, or a negative value when nothing was remembered
     */
    public double getWindowX() {
        return this.preferences.getDouble(KEY_WINDOW_X, NO_POSITION);
    }

    /**
     * Returns the remembered window y position.
     *
     * @return the y coordinate, or a negative value when nothing was remembered
     */
    public double getWindowY() {
        return this.preferences.getDouble(KEY_WINDOW_Y, NO_POSITION);
    }

    /**
     * Remembers where and how large the window was left.
     *
     * @param x the window's x coordinate
     * @param y the window's y coordinate
     * @param width the window width
     * @param height the window height
     */
    public void saveWindowBounds(double x, double y, double width, double height) {
        this.preferences.putDouble(KEY_WINDOW_X, x);
        this.preferences.putDouble(KEY_WINDOW_Y, y);
        this.preferences.putDouble(KEY_WINDOW_WIDTH, width);
        this.preferences.putDouble(KEY_WINDOW_HEIGHT, height);
    }
}
