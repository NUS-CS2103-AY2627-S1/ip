package nexus;

import java.util.List;

/** Contains tasks recovered from storage and the number of unusable records. */
public record LoadResult(List<Task> tasks, int skippedRecords) {
    /** Protects the recovered collection from external mutation. */
    public LoadResult {
        tasks = List.copyOf(tasks);
    }
}
