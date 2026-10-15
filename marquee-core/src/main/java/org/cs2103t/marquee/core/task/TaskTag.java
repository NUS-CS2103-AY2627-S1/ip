package org.cs2103t.marquee.core.task;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import org.cs2103t.marquee.core.DuplicateKeyException;

import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * Base class for tags used by {@code Task}.
 */
public final class TaskTag {
    private static final Set<Character> SPECIAL_CHARACTERS = Set.of('-', '_', '.', ' ');
    private static final MapProperty<String, TaskTag> DICTIONARY =
            new SimpleMapProperty<>(null, "tags", FXCollections.observableHashMap());

    private final String label;
    private final Set<Task> taggedTasks;

    /**
     * Create a new {@code TaskTag}.
     * <p>
     * Tag names can only contain alphanumeric characters {@code a-z} {@code A-Z} {@code 0-9},
     * hyphens {@code -}, underscores {@code _}, periods {@code .} and spaces <code>&nbsp;</code>.
     *
     * @param label the label of the tag, which is what
     *              would be displayed when {@link #toString()} is invoked
     * @throws NullPointerException if the label is {@code null}
     * @throws IllegalArgumentException if an invalid character is found
     */
    public TaskTag(String label) throws NullPointerException, DuplicateKeyException, IllegalArgumentException {
        if (label == null) {
            throw new NullPointerException("Tag label cannot be null");
        }
        if (label.isEmpty()) {
            throw new IllegalArgumentException("Tag label cannot be empty");
        }
        int invalidCharCodePoint = label.chars()
                .dropWhile(c -> Character.isLetterOrDigit(c) || SPECIAL_CHARACTERS.contains((char) c))
                .findAny().orElse(-1);
        if (invalidCharCodePoint != -1) {
            throw new IllegalArgumentException("Illegal character in tag name: '" + (char) invalidCharCodePoint + "'");
        }

        this.label = label;
        this.taggedTasks = new HashSet<>();
    }

    public static ObservableMap<String, TaskTag> getDictionary() {
        return DICTIONARY.get();
    }

    public static MapProperty<String, TaskTag> dictionaryProperty() {
        return DICTIONARY;
    }

    /**
     * Adds the tag to the dictionary.
     *
     * @param tag the tag to add
     * @throws DuplicateKeyException if the tag name already exist in dictionary
     * @throws IllegalArgumentException if the tag is stale
     */
    public static void addTag(TaskTag tag) throws DuplicateKeyException, IllegalArgumentException {
        if (DICTIONARY.containsKey(tag.label)) {
            throw new DuplicateKeyException("Tag name already exist", tag.label);
        } else {
            DICTIONARY.put(tag.label, tag);
        }
    }

    /**
     * Returns the tag with the given label in the dictionary, or {@code null} if there's none.
     *
     * @param label the label of the tag
     * @return the tag with the given label, or {@code null} if there's none
     */
    public static TaskTag getTag(String label) {
        return DICTIONARY.get(label);
    }

    /**
     * Remove the tag from dictionary and tasks and mark it as stale.
     * <p>
     * Stale tags will throw an exception when trying to add it to tasks.
     *
     * @param tag the tag to remove
     * @throws NoSuchElementException if the tag doesn't exist in the dictionary
     */
    public static void removeTag(TaskTag tag) throws NoSuchElementException {
        if (DICTIONARY.containsKey(tag.label)) {
            for (Task task : tag.taggedTasks) {
                tag.onTagRemoved(task);
            }
        } else {
            throw new NoSuchElementException("Tag does not exist");
        }
    }

    /**
     * If a tag with this label exist in the dictionary, returns it.
     * Otherwise, creates a new tag with the given label and add it to the dictionary.
     *
     * @param label the label of the tag, which is what
     *              would be displayed when {@link #toString()} is invoked
     * @return the tag with the given label
     */
    public static TaskTag createOrGet(String label) {
        if (label == null) {
            throw new IllegalArgumentException("Label cannot be null");
        } else if (label.isEmpty()) {
            return null;
        } else if (DICTIONARY.containsKey(label)) {
            return DICTIONARY.get(label);
        } else {
            TaskTag newTag = new TaskTag(label);
            addTag(newTag);
            return newTag;
        }
    }

    void onTagAdded(Task task) {
        this.taggedTasks.add(task);
    }

    void onTagRemoved(Task task) {
        this.taggedTasks.remove(task);
    }

    /**
     * Returns the tag's label.
     *
     * @return the label of the tag
     */
    @Override
    public String toString() {
        return this.label;
    }
}
