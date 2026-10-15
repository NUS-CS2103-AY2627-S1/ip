package zinc.ui;

/** Selects how Zinc chooses the main-window background. */
public enum BackgroundType {
    /** Use the current local time to choose the background. */
    AUTO,

    /** Use the morning background. */
    MORNING,

    /** Use the evening background. */
    EVENING,

    /** Use the nighttime background. */
    NIGHT
}
