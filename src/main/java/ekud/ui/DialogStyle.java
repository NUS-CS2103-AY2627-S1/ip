package ekud.ui;

/**
 * CSS style applied to an Ekud reply bubble.
 * {@link #NONE} keeps the default reply color.
 * {@link #ERROR} is used when the last input was invalid.
 */
public enum DialogStyle {
    ADD("add-label"),
    CHANGE_MARK("marked-label"),
    DELETE("delete-label"),
    ERROR("error-label"),
    NONE("");

    private final String cssClass;

    DialogStyle(String cssClass) {
        this.cssClass = cssClass;
    }

    /**
     * Returns the JavaFX style class for this reply, or empty if unstyled.
     *
     * @return a CSS class name, or an empty string
     */
    public String getCssClass() {
        return cssClass;
    }
}
