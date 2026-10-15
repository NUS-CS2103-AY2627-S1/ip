package carlo.persona;

/**
 * How Carlo is feeling about the user's tasks. The window shows the mood as
 * the colour of the ring around Carlo's photo and the border of his speech
 * bubble, so a single photo can still express different states.
 */
public enum CarloMood {
    /** Everything is on track (gold). */
    CHEERFUL("mood-cheerful"),
    /** Something is due soon, or Carlo needs the user's attention (amber). */
    HEADS_UP("mood-heads-up"),
    /** Something is overdue (soft red). */
    CONCERNED("mood-concerned");

    private final String styleClass;

    CarloMood(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * Returns the CSS style class that colours the UI for this mood.
     *
     * @return the style class name defined in {@code carlo.css}
     */
    public String getStyleClass() {
        return styleClass;
    }
}
