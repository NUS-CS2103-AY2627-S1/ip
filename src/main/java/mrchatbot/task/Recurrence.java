package mrchatbot.task;

/**
 * Defines the supported recurrence intervals in calendar days.
 */
public enum Recurrence {
    DAY(1),
    WEEK(7);

    private final int days;

    Recurrence(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }
}
