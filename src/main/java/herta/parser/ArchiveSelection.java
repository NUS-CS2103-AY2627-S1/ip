package herta.parser;

import java.util.List;
import java.util.Objects;

/**
 * Represents the validated selectors supplied to an archive command.
 */
public final class ArchiveSelection {
    private final List<ArchiveRange> ranges;
    private final boolean isAllSelected;

    /**
     * Creates an archive selection.
     *
     * @param ranges the inclusive, one-based ranges selected by the user.
     * @param isAllSelected whether the selection targets every completed active task.
     */
    public ArchiveSelection(List<ArchiveRange> ranges, boolean isAllSelected) {
        this.ranges = List.copyOf(Objects.requireNonNull(ranges,
                "Archive selection ranges cannot be null."));
        this.isAllSelected = isAllSelected;
    }

    /**
     * Indicates whether this selection represents {@code archive all}.
     *
     * @return {@code true} when every completed active task is selected.
     */
    public boolean isAllSelected() {
        return isAllSelected;
    }

    /**
     * Returns the selected task-number ranges.
     *
     * @return an immutable list of selected ranges.
     */
    public List<ArchiveRange> getRanges() {
        return ranges;
    }
}
