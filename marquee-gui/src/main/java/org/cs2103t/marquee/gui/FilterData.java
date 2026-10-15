package org.cs2103t.marquee.gui;

import java.time.LocalDateTime;
import java.util.Set;

import org.cs2103t.marquee.core.task.Task;
import org.cs2103t.marquee.core.task.TaskTag;

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

/**
 * Data class for filter criteria.
 * Filter arguments are null if unused.
 */
public final class FilterData {
    private final StringProperty description = new SimpleStringProperty(this, "description", "");
    private final ObjectProperty<Boolean> mark = new SimpleObjectProperty<>(this, "mark", null);
    private final SetProperty<TaskTag> tags = new SimpleSetProperty<>(this, "tags", FXCollections.observableSet());
    private final ObjectProperty<LocalDateTime> start = new SimpleObjectProperty<>(this, "start", null);
    private final ObjectProperty<LocalDateTime> end = new SimpleObjectProperty<>(this, "end", null);

    private final BooleanProperty active = new SimpleBooleanProperty(this, "active");

    /**
     * Creates an empty {@code FilterData}.
     */
    public FilterData() {
        this.active.bind(this.description.isEmpty()
                .and(this.mark.isNull())
                .and(this.tags.emptyProperty())
                .and(this.start.isNull())
                .and(this.end.isNull())
                .not());
    }

    /**
     * Creates a {@code FilterData}.
     *
     * @param description the string to search in task descriptions
     * @param mark        the mark status to search
     * @param tags        the tags to search for
     * @param start       the earliest time to search from
     * @param end         the latest time to search to
     */
    public FilterData(
            String description, Boolean mark, Set<TaskTag> tags,
            LocalDateTime start, LocalDateTime end
    ) {
        this();
        setDescription(description);
        setMark(mark);
        setTags(FXCollections.observableSet(tags));
        setStart(start);
        setEnd(end);
    }

    /**
     * Creates a copy from an existing {@code FilterData}.
     */
    public FilterData(FilterData other) {
        this();
        setDescription(other.getDescription());
        setMark(other.getMark());
        setTags(other.getTags());
        setStart(other.getStart());
        setEnd(other.getEnd());
    }

    /**
     * Checks if the given task satisfy the filter conditions.
     *
     * @param task the task to check
     * @return whether the task satisfy the filter conditions
     */
    public boolean apply(Task task) {
        return (getDescription().isEmpty() || task.getDescription().contains(getDescription()))
                && (getMark() == null || task.isMarked() == getMark())
                && (getTags().isEmpty() || task.getTags().containsAll(getTags()))
                && (getStart() == null || task.startsAfter(getStart()))
                && (getEnd() == null || task.endsBefore(getEnd()));
    }

    public boolean isActive() {
        return active.get();
    }
    public BooleanProperty activeProperty() {
        return active;
    }

    public String getDescription() {
        return description.get();
    }
    public void setDescription(String description) {
        this.description.set(description == null ? "" : description);
    }
    public StringProperty descriptionProperty() {
        return description;
    }

    public Boolean getMark() {
        return mark.get();
    }
    public void setMark(Boolean mark) {
        this.mark.set(mark);
    }
    public ObjectProperty<Boolean> markProperty() {
        return mark;
    }

    public ObservableSet<TaskTag> getTags() {
        return tags.get();
    }
    public void setTags(ObservableSet<TaskTag> tags) {
        if (tags == null) {
            this.tags.clear();
        } else {
            this.tags.set(tags);
        }
    }
    public SetProperty<TaskTag> tagsProperty() {
        return tags;
    }

    public LocalDateTime getStart() {
        return start.get();
    }
    public void setStart(LocalDateTime start) {
        this.start.set(start);
    }
    public ObjectProperty<LocalDateTime> startProperty() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end.get();
    }
    public void setEnd(LocalDateTime end) {
        this.end.set(end);
    }
    public ObjectProperty<LocalDateTime> endProperty() {
        return end;
    }
}
