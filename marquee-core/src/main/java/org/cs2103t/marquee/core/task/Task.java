package org.cs2103t.marquee.core.task;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import org.cs2103t.marquee.core.serialization.FieldGetter;
import org.cs2103t.marquee.core.serialization.FieldSetter;
import org.cs2103t.marquee.core.serialization.Optional;
import org.cs2103t.marquee.core.serialization.PreprocessWith;
import org.cs2103t.marquee.core.serialization.Serializable;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

/**
 * Base class for all tasks.
 * <p>
 * Provide fields for task type, description, starting time, ending time and completion status.
 *
 * @see TaskTag
 */
@Serializable
public class Task {
    private final StringProperty description = new SimpleStringProperty(this, "description");
    private final BooleanProperty isMarked = new SimpleBooleanProperty(this, "isMarked");
    private final ObjectProperty<LocalDateTime> start = new SimpleObjectProperty<>(this, "start");
    private final ObjectProperty<LocalDateTime> end = new SimpleObjectProperty<>(this, "end");
    private final SetProperty<TaskTag> tags =
            new SimpleSetProperty<>(this, "tags", FXCollections.observableSet());

    /**
     * Creates a new {@code Task} with the given description, starting time
     * and ending time, then mark it as either completed or incomplete.
     *
     * @param description the description of the task
     * @param start when the task starts
     * @param end when the task ends
     * @param isMarked whether the task has been completed or not
     * @throws NullPointerException if tag or description is {@code null}
     * @throws IllegalArgumentException if description is empty
     */
    public Task(String description, boolean isMarked, LocalDateTime start, LocalDateTime end, TaskTag... tags)
            throws NullPointerException, IllegalArgumentException {
        this.tags.addListener((SetChangeListener<? super TaskTag>) change -> {
            if (change.wasAdded()) {
                change.getElementAdded().onTagAdded(this);
            }
            if (change.wasRemoved()) {
                change.getElementRemoved().onTagRemoved(this);
            }
        });

        this.setDescription(description);
        this.setMark(isMarked);
        this.setStart(start);
        this.setEnd(end);
        this.setTags(FXCollections.observableSet(tags));
    }

    /**
     * Creates a copy of the given {@code Task}.
     *
     * @param task the task to copy
     */
    public Task(Task task) {
        this(
                task.getDescription(),
                task.isMarked(),
                task.getStart(),
                task.getEnd(),
                task.getTags().toArray(TaskTag[]::new)
        );
    }

    public Task() {
        this("New Task", false, null, null);
    }

    @FieldGetter("tags")
    private String getTagsAsString() {
        return tags.get().stream().map(TaskTag::toString).collect(Collectors.joining(","));
    }

    @FieldSetter("tags")
    private void setTagsFromString(String tags) {
        setTags(Arrays.stream(tags.split(","))
                .map(TaskTag::createOrGet)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(FXCollections::observableSet)));
    }

    /**
     * Gets the tag set of this task.
     *
     * @return the tag set of this task
     */
    public ObservableSet<TaskTag> getTags() {
        return tags.get();
    }

    /**
     * Sets the tag set of this task.
     * <p>
     * To set all tags instead, use {@link #getTags()}.{@link ObservableSet#clear() clear()}
     * then {@link #getTags()}.{@link ObservableSet#addAll(Collection) addAll(newTags)}
     *
     * @param newTags the new tag set for this task
     */
    public void setTags(ObservableSet<TaskTag> newTags) {
        if (newTags == null) {
            throw new NullPointerException("Tag list cannot be null");
        } else {
            tags.forEach(t -> t.onTagRemoved(this));
            tags.set(newTags);
            newTags.forEach(t -> t.onTagAdded(this));
        }
    }

    /**
     * Gets the tag set property of this task.
     *
     * @return the tag set property
     */
    public SetProperty<TaskTag> tagsProperty() {
        return tags;
    }

    /**
     * Adds a tag to this task.
     *
     * @param newTag the new tag for this task
     * @return whether the task already has the given tag
     * @throws NullPointerException if the given tag is {@code null}
     * @throws IllegalArgumentException if the given tag is stale
     */
    public boolean addTag(TaskTag newTag) {
        if (newTag == null) {
            throw new NullPointerException("Tag cannot be null");
        } else {
            newTag.onTagAdded(this);
            return tags.add(newTag);
        }
    }

    /**
     * Removes a tag from this task.
     *
     * @param oldTag the old tag to remove from this task
     * @return whether the task didn't have the given tag
     * @throws NullPointerException if the given tag is {@code null}
     */
    public boolean removeTag(TaskTag oldTag) {
        if (oldTag == null) {
            throw new NullPointerException("Tag cannot be null");
        } else {
            oldTag.onTagRemoved(this);
            return tags.remove(oldTag);
        }
    }

    /**
     * Gets the description of this task.
     *
     * @return the task description
     */
    @FieldGetter("description")
    public String getDescription() {
        return this.description.get();
    }

    /**
     * Sets the description of this task.
     *
     * @param newDescription the new task description
     */
    @FieldSetter("description")
    public void setDescription(String newDescription) throws NullPointerException {
        if (newDescription == null) {
            throw new NullPointerException("Description cannot be null");
        }
        if (newDescription.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        this.description.set(newDescription);
    }

    /**
     * Gets the description property of this task.
     *
     * @return the description property
     */
    public StringProperty descriptionProperty() {
        return this.description;
    }

    /**
     * Gets the starting time of this event, or {@code null} if not applicable.
     *
     * @return the starting time, or {@code null} if not applicable
     */
    @FieldGetter("start")
    @Optional
    @PreprocessWith(method = "toString")
    public LocalDateTime getStart() {
        return start.get();
    }

    /**
     * Sets the starting time of this event.
     *
     * @param newStart the new starting time, or {@code null} if not applicable
     */
    @FieldSetter("start")
    @Optional
    @PreprocessWith(clazz = LocalDateTime.class, method = "parse")
    public void setStart(LocalDateTime newStart) {
        start.set(newStart);
    }

    /**
     * Gets the starting time property of this event.
     *
     * @return the starting time property
     */
    public ObjectProperty<LocalDateTime> startProperty() {
        return start;
    }

    /**
     * Checks if the task starts strictly after the given time.
     *
     * @param startTime the reference time
     * @return whether the task starts after the given time
     */
    public boolean startsAfter(LocalDateTime startTime) {
        return this.start.get() != null && this.start.get().isAfter(startTime);
    }

    /**
     * Checks if the task starts after or at the given time.
     *
     * @param startTime the reference time
     * @return whether the task starts after or at the given time
     */
    public boolean startsAfterInclusive(LocalDateTime startTime) {
        return this.start.get() != null && !this.start.get().isBefore(startTime);
    }

    /**
     * Gets the ending time of this event, or {@code null} if not applicable.
     *
     * @return the ending time, or {@code null} if not applicable
     */
    @FieldGetter("end")
    @Optional
    @PreprocessWith(method = "toString")
    public LocalDateTime getEnd() {
        return end.get();
    }

    /**
     * Sets the ending time of this event.
     *
     * @param newEnd the new ending time, or {@code null} if not applicable
     */
    @FieldSetter("end")
    @Optional
    @PreprocessWith(clazz = LocalDateTime.class, method = "parse")
    public void setEnd(LocalDateTime newEnd) {
        end.set(newEnd);
    }

    /**
     * Gets the ending time of this event, or {@code null} if not applicable.
     *
     * @return the ending time, or {@code null} if not applicable
     */
    public ObjectProperty<LocalDateTime> endProperty() {
        return end;
    }

    /**
     * Checks if the task ends strictly before the given time.
     *
     * @param endTime the reference time
     * @return whether the task ends before the given time
     */
    public boolean endsBefore(LocalDateTime endTime) {
        return this.end.get() != null && this.end.get().isBefore(endTime);
    }

    /**
     * Checks if the task ends before or at the given time.
     *
     * @param endTime the reference time
     * @return whether the task ends before or at the given time
     */
    public boolean endsBeforeInclusive(LocalDateTime endTime) {
        return this.end.get() != null && !this.end.get().isAfter(endTime);
    }

    /**
     * Gets the mark status of this task.
     *
     * @return whether this task is marked
     */
    @FieldGetter("mark")
    @PreprocessWith(method = "toString")
    public boolean isMarked() {
        return isMarked.get();
    }

    @FieldSetter("mark")
    private void setMarkFromString(String newMark) {
        setMark(Boolean.parseBoolean(newMark));
    }

    /**
     * Sets the task mark status.
     *
     * @param newMark the new mark status for the task
     * @return whether the mark status was changed
     */
    public boolean setMark(boolean newMark) {
        if (isMarked.get() != newMark) {
            isMarked.set(newMark);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the marked property of this task.
     *
     * @return the marked property
     */
    public BooleanProperty markProperty() {
        return isMarked;
    }

    /**
     * Marks the task as completed.
     *
     * @return whether the task was incomplete before
     */
    public boolean mark() {
        return setMark(true);
    }

    /**
     * Marks the task as incomplete.
     *
     * @return whether the task was completed before
     */
    public boolean unmark() {
        return setMark(false);
    }

    @Override
    public String toString() {
        return this.getTags().stream().map(tag -> "[" + tag + "]").collect(Collectors.joining(" "))
                + (this.isMarked() ? " [x] " : " [ ] ") + this.getDescription();
    }
}
