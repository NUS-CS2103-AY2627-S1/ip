package zinc.javafx;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import zinc.ui.BackgroundType;

/** Represents one of the main window's background styles and its time period. */
enum BackgroundStyle {
    /** The morning background style. */
    MORNING(LocalTime.of(6, 0), "background-morning", "sidebar-morning"),

    /** The evening background style. */
    EVENING(LocalTime.of(18, 0), "background-sunset", "sidebar-sunset"),

    /** The nighttime background style. */
    NIGHT(LocalTime.of(22, 0), "background-night", "sidebar-night");

    /** The first time at which this style applies. */
    private final LocalTime startTime;

    /** The CSS class applied to the conversation pane. */
    private final String backgroundStyleClass;

    /** The CSS class applied to the sidebar. */
    private final String sidebarStyleClass;

    /** Creates a background style with its time and CSS classes. */
    BackgroundStyle(LocalTime startTime, String backgroundStyleClass, String sidebarStyleClass) {
        this.startTime = startTime;
        this.backgroundStyleClass = backgroundStyleClass;
        this.sidebarStyleClass = sidebarStyleClass;
    }

    /** Returns the style selected by the supplied time. */
    static BackgroundStyle fromTime(LocalTime time) {
        BackgroundStyle selectedStyle = NIGHT;
        for (BackgroundStyle style : values()) {
            if (!time.isBefore(style.startTime)) {
                selectedStyle = style;
            }
        }
        return selectedStyle;
    }

    /** Returns the style selected by the user's background preference. */
    static BackgroundStyle fromSelection(LocalTime time, BackgroundType backgroundType) {
        return switch (backgroundType) {
            case AUTO -> fromTime(time);
            case MORNING -> MORNING;
            case EVENING -> EVENING;
            case NIGHT -> NIGHT;
        };
    }

    /** Returns all conversation-pane CSS classes managed by the main window. */
    static List<String> getBackgroundStyleClasses() {
        return Arrays.stream(values()).map(BackgroundStyle::getBackgroundStyleClass).toList();
    }

    /** Returns all sidebar CSS classes managed by the main window. */
    static List<String> getSidebarStyleClasses() {
        return Arrays.stream(values()).map(BackgroundStyle::getSidebarStyleClass).toList();
    }

    /** Returns the conversation-pane CSS class for this style. */
    String getBackgroundStyleClass() {
        return backgroundStyleClass;
    }

    /** Returns the sidebar CSS class for this style. */
    String getSidebarStyleClass() {
        return sidebarStyleClass;
    }
}
