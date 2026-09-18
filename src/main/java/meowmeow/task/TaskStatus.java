package meowmeow.task;

/**
 * Whether a {@link Task} is done or not done. Replaces a plain
 * {@code boolean} so call sites read as {@code TaskStatus.DONE} instead of
 * an unexplained {@code true}/{@code false}. Each constant also carries the
 * status icon shown in a task's listing (e.g. "[X]"), so both "mark" and
 * "unmark" can share one code path instead of branching on a boolean flag.
 * The confirmation message shown when a task switches status lives in
 * {@link meowmeow.ui.Ui Ui} instead, since it now has several phrasings.
 */
public enum TaskStatus {
    /** The task is finished. */
    DONE("X", "1"),
    /** The task is not finished yet. */
    NOT_DONE(" ", "0");

    private final String icon;
    private final String fileFlag;

    TaskStatus(String icon, String fileFlag) {
        this.icon = icon;
        this.fileFlag = fileFlag;
    }

    /** Returns the icon shown in a task listing, e.g. {@code "X"} for done. */
    public String getIcon() {
        return icon;
    }

    /**
     * Returns the status as it appears in the saved data file: "1" for done,
     * "0" for not done. Kept here (next to the icon) so the file encoding
     * lives in one place rather than being hardcoded in {@link Task}.
     */
    public String getFileFlag() {
        return fileFlag;
    }

    /**
     * Returns the status whose {@link #getFileFlag() file flag} is
     * {@code fileFlag}, or {@code null} if neither status uses it. The exact
     * inverse of {@link #getFileFlag()}: it lets
     * {@link meowmeow.storage.Storage Storage} recognise a saved done-flag
     * with one lookup instead of comparing against each status in turn, and
     * treat any other value as a corrupt line.
     *
     * @param fileFlag the done-flag field read from a save-file line.
     * @return the matching status, or {@code null} if the flag is unrecognised.
     */
    public static TaskStatus fromFileFlag(String fileFlag) {
        for (TaskStatus status : values()) {
            if (status.fileFlag.equals(fileFlag)) {
                return status;
            }
        }
        return null;
    }
}
