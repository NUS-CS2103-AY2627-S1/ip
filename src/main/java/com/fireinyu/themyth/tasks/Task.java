package com.fireinyu.themyth.tasks;

import java.util.ArrayList;
import java.util.List;

import com.fireinyu.themyth.storage.CsvSerializable;
import com.fireinyu.themyth.util.MythDateTime;

/**
 * Task created by the user. Tasks can be marked or unmarked as completed.
 */
public abstract class Task implements CsvSerializable {
    private final String description;
    private boolean completed = false;
    private final String typeCode;
    private MythDateTime lastModified;
    private MythDateTime created;

    /**
     * Initializes a Task.
     *
     * @param description description of the task.
     * @param typeCode type of task.
     * @see String
     */
    public Task(String description, String typeCode) {
        created = MythDateTime.now();
        lastModified = created;
        this.description = description;
        this.typeCode = typeCode;
    }

    /**
     * Sets the creation and last-modified timestamps.
     *
     * @param created creation timestamp.
     * @param lastModified last-modified timestamp.
     */
    public void setAccessTimes(MythDateTime created, MythDateTime lastModified) {
        this.created = created;
        this.lastModified = lastModified;
    }

    /**
     * Serializes this Task into a List of String attributes.
     *
     * @return List of String attributes representing the serialized Task object.
     * @see List
     * @see String
     */
    @Override
    public List<String> extract() {
        return new ArrayList<>(List.of(
                typeCode,
                String.valueOf(completed),
                created.dump(),
                lastModified.dump(),
                description
        ));
    }

    /**
     * Returns the description of this Task.
     *
     * @return  the description of this Task.
     * @see String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this Task is completed.
     *
     * @return whether this Task is completed.
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Marks this Task as completed.
     */
    public void mark() {
        lastModified = MythDateTime.now();
        completed = true;
    }

    /**
     * Marks this Task as incomplete.
     */
    public void unmark() {
        lastModified = MythDateTime.now();
        completed = false;
    }

    /**
     * Returns the last-modified timestamp.
     *
     * @return last-modified timestamp.
     */
    public MythDateTime getLastModified() {
        return lastModified;
    }

    /**
     * Returns the creation timestamp.
     *
     * @return creation timestamp.
     */
    public MythDateTime getCreated() {
        return created;
    }

    /**
     * Gets the type code of the task.
     *
     * @return The type code string.
     */
    protected String getTypeCode() {
        return typeCode;
    }

    /**
     * Returns a detailed String representation of this Task.
     * Includes its type, whether it is completed and its description
     *
     * @return detailed String representation of this Task.
     */
    @Override
    public String toString() {
        return String.format("[%s][%s] %s", typeCode, completed ? "X" : " ", description);
    }
}
