
package hamtaro.task;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/** Represents a task tracked by Hamtaro. */
public class Task {
        private String description;
        private boolean isDone;
        private final Set<String> tags = new LinkedHashSet<>();
        private static final Pattern VALID_TAG = Pattern.compile("[a-z0-9_-]+");

        /** Creates an incomplete task with the given description. */
        public Task(String d){
            // Task descriptions come from validated commands or valid storage records.
            assert d != null && !d.isBlank() : "A task must have a description";
            this.description = d;
            this.isDone = false;
        }

        /** Creates a task with the given description and completion status. */
        public Task(String d, boolean i){
            // Restored tasks must satisfy the same invariant as newly created tasks.
            assert d != null && !d.isBlank() : "A task must have a description";
            this.description = d;
            this.isDone = i;
        }

        /** Marks this task as complete. */
        public void mark(){
            this.isDone = true;
        }
        /** Marks this task as incomplete. */
        public void unmark(){
            this.isDone = false;
        }

        /** Returns the symbol used to display this task's completion status. */
        public String getStatusIcon(){
            return (isDone? "X" : " ");
        }

        /** Returns the task description for persistent storage. */
        public String getDescription() {
            return description;
        }

        /** Returns whether this task has been completed. */
        public boolean isDone() {
            return isDone;
        }

        /** Adds a case-insensitive tag to this task. */
        public void addTag(String tag) {
            tags.add(normalizeTag(tag));
        }

        /** Removes a tag from this task, if it is present. */
        public void removeTag(String tag) {
            tags.remove(normalizeTag(tag));
        }

        /** Returns whether this task has the supplied tag. */
        public boolean hasTag(String tag) {
            return tags.contains(normalizeTag(tag));
        }

        /** Returns this task's tags in insertion order. */
        public Set<String> getTags() {
            return Collections.unmodifiableSet(tags);
        }

        /** Normalizes a tag and rejects unsupported tag characters. */
        public static String normalizeTag(String tag) {
            assert tag != null : "A tag must not be null";
            String normalizedTag = tag.trim().toLowerCase(Locale.ROOT);
            if (normalizedTag.startsWith("#")) {
                normalizedTag = normalizedTag.substring(1);
            }
            if (normalizedTag.isBlank() || !VALID_TAG.matcher(normalizedTag).matches()) {
                throw new IllegalArgumentException("Tags must contain only letters, numbers, '-' or '_'.");
            }
            return normalizedTag;
        }

        /** Returns a displayable representation of this task. */
        @Override
        public String toString(){
            String displayedTags = tags.stream()
                    .map(tag -> "#" + tag)
                    .reduce("", (result, tag) -> result + " " + tag);
            return "[" + this.getStatusIcon() + "] " + this.description + displayedTags;
        }
}
