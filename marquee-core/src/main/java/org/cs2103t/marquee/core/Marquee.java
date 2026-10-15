package org.cs2103t.marquee.core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.cs2103t.marquee.core.io.CsvTable;
import org.cs2103t.marquee.core.io.FileParseException;
import org.cs2103t.marquee.core.serialization.Serializer;
import org.cs2103t.marquee.core.task.Task;
import org.cs2103t.marquee.core.task.TaskTag;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Main class for the standalone chatbot Marquee.
 */
public class Marquee {
    private final ListProperty<Task> checklist = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<Task> lastResult = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ReadOnlyListWrapper<Task> checklistReadOnly = new ReadOnlyListWrapper<>(checklist);
    private final ReadOnlyListWrapper<Task> lastResultReadOnly = new ReadOnlyListWrapper<>(lastResult);

    private Serializer.ClassNameEncoder classNameEncoder = Class::getName;
    private Serializer.ClassNameDecoder classNameDecoder = Class::forName;

    /**
     * Instantiates an instance of Marquee.
     */
    public Marquee() {}

    public ObservableList<Task> getChecklist() {
        return checklistReadOnly;
    }
    public ListProperty<Task> checklistProperty() {
        return checklistReadOnly;
    }

    public ObservableList<Task> getLastResult() {
        return lastResultReadOnly;
    }
    public ReadOnlyListProperty<Task> lastResultProperty() {
        return lastResultReadOnly;
    }

    /**
     * Sets the class name encoder and decoder for task class name.
     * Used by {@link Serializer} to serialize tasks for storing in files.
     *
     * @param classNameEncoder the class name encoder
     * @param classNameDecoder the class name decoder
     */
    public void setClassNameEncoding(
            Serializer.ClassNameEncoder classNameEncoder,
            Serializer.ClassNameDecoder classNameDecoder
    ) {
        this.classNameEncoder = classNameEncoder;
        this.classNameDecoder = classNameDecoder;
    }

    /**
     * Attempts to load the checklist from the save file.
     * <p>
     * If an I/O error or file format error occurs, no change is made to the checklist.
     * <p>
     * If a row cannot be deserialized, that row will be silently skipped.
     *
     * @param savePath the path to the CSV file Marquee will save its checklist to
     * @return whether some items were skipped
     * @throws NoSuchFileException if the file doesn't exist
     * @throws IOException if an I/O error occurred during reading
     * @throws FileParseException if the file doesn't follow the right format
     */
    public boolean load(Path savePath)
            throws NoSuchFileException, IOException, FileParseException {
        CsvTable csv = CsvTable.readFile(savePath);
        boolean lossless = true;
        List<Task> tempList = new ArrayList<>();
        for (CsvTable.Record record : csv.getValues()) {
            try {
                tempList.add((Task) Serializer.deserialize(record.getAllFields(), classNameDecoder));
            } catch (ClassNotFoundException e) {
                lossless = false;
                System.out.println("Cannot find class " + record.getField(Serializer.CLASS_NAME_FIELD_NAME));
            } catch (IllegalStateException e) {
                lossless = false;
                System.out.println("Class is not marked for deserialization");
            } catch (InstantiationException e) {
                lossless = false;
                System.out.println("Class " + record.getField(Serializer.CLASS_NAME_FIELD_NAME)
                        + " cannot be instantiated");
            } catch (NoSuchElementException e) {
                lossless = false;
                System.out.println("Task is missing attribute " + e.getMessage());
            } catch (NoSuchMethodException e) {
                lossless = false;
                System.out.println("Preprocessor " + e.getMessage() + " not found");
            } catch (ClassCastException e) {
                lossless = false;
                System.out.println("Preprocessor return type mismatch: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                lossless = false;
                System.out.println("Setter doesn't have 1 argument");
            } catch (InvocationTargetException e) {
                lossless = false;
                System.out.println("Setter threw an exception: " + e.getMessage());
            }
        }
        checklist.clear();
        checklist.addAll(tempList);
        return lossless;
    }

    /**
     * Saves the checklist into the save file.
     * <p>
     * If the operation fails, the original save file will not be changed.
     * <p>
     * If a row cannot be serialized, that row will be silently skipped.
     *
     * @param savePath the path to the CSV file Marquee will read the checklist from
     * @return whether some items were skipped
     * @throws IOException if an unexpected I/O error occurs
     */
    public boolean save(Path savePath) throws IOException {
        CsvTable csv = new CsvTable();
        boolean lossless = true;
        for (Task task : checklist) {
            try {
                Map<String, String> fields = Serializer.serialize(task, classNameEncoder);
                for (String fieldName : fields.keySet()) {
                    if (!csv.getColumns().contains(fieldName)) {
                        csv.newColumn(fieldName, "");
                    }
                }
                csv.add(csv.createRecord(fields));
            } catch (IllegalStateException e) {
                lossless = false;
                System.out.println("Class is not marked for deserialization");
            } catch (NoSuchElementException e) {
                lossless = false;
                System.out.println("Task is missing attribute " + e.getMessage());
            } catch (NoSuchMethodException e) {
                lossless = false;
                System.out.println("Preprocessor " + e.getMessage() + " not found");
            } catch (ClassCastException e) {
                lossless = false;
                System.out.println("Preprocessor return type mismatch: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                lossless = false;
                System.out.println("Getter doesn't have a return value");
            } catch (InvocationTargetException e) {
                lossless = false;
                System.out.println("Getter threw an exception: " + e.getMessage());
            }
        }
        CsvTable.writeFile(savePath, csv);
        return lossless;
    }

    /**
     * Returns the tasks in the checklist.
     * <p>
     * If there are none, output a different message clarifying that the checklist is empty.
     *
     * @return an unmodifiable view of the current checklist
     */
    public final List<Task> list() {
        List<Task> tempList = Collections.unmodifiableList(checklist);
        lastResult.setAll(tempList);
        return tempList;
    }

    /**
     * Searches for tasks in the checklist satisfying the search conditions.
     * <p>
     * If a parameter is null (or empty for {@code description}), no constraint is imposed on that property.
     * If description is empty or {@code null}, and all other parameters are {@code null}, no result is returned.
     *
     * @param description match tasks containing this substring in its description
     * @param start match tasks starting after this time
     * @param end match tasks ending before this time
     * @param isMarked match tasks with this mark status
     * @param tags match tasks that has all these tags
     * @param inheritLastResult whether to search only from the last result set
     * @return the tasks matching the filters
     */
    public final List<Task> find(String description, LocalDateTime start, LocalDateTime end,
                           Boolean isMarked, Set<TaskTag> tags, boolean inheritLastResult) {
        List<Task> matchingItems = (description == null || description.isEmpty())
                && isMarked == null && start == null && end == null && tags == null
                ? Collections.emptyList()
                : (inheritLastResult ? lastResult.stream() : checklist.stream())
                .filter(isMarked == null
                        ? _ -> true
                        : task -> task.isMarked() == isMarked
                )
                .filter(tags == null
                        ? _ -> true
                        : task -> task.getTags().containsAll(tags)
                )
                .filter(start == null
                        ? _ -> true
                        : task -> task.startsAfter(start)
                )
                .filter(end == null
                        ? _ -> true
                        : task -> task.endsBefore(end)
                )
                .filter(description == null || description.isEmpty()
                        ? _ -> true
                        : task -> task.getDescription().contains(description)
                )
                .toList();
        lastResult.setAll(matchingItems);
        return matchingItems;
    }

    /**
     * Adds tasks to the checklist, then returns the added tasks.
     *
     * @param tasks the tasks to be added to the checklist
     * @return the added tasks
     */
    public final List<Task> addTasks(Task... tasks) {
        List<Task> newTasks = List.of(tasks);
        checklist.addAll(newTasks);
        lastResult.setAll(newTasks);
        return newTasks;
    }

    /**
     * Deletes tasks from the last search/list result by index, then returns the deleted tasks.
     *
     * @param inheritLastResult whether to perform operations on last result set
     * @param indices the indices of the tasks to be deleted
     * @return the deleted tasks
     * @throws IndexOutOfBoundsException if an index is out of the last task list's bounds
     */
    public final List<Task> deleteTasks(boolean inheritLastResult, int... indices) throws IndexOutOfBoundsException {
        int limit = inheritLastResult ? lastResult.size() : checklist.size();
        List<Task> deletedTasks = IntStream.of(indices)
                .peek(i -> {
                    if (i < 0 || i >= limit) {
                        throw new IndexOutOfBoundsException(i);
                    }
                })
                .mapToObj(inheritLastResult ? lastResult::get : checklist::get)
                .peek(checklist::remove)
                .toList();
        lastResult.setAll(deletedTasks);
        return deletedTasks;
    }

    /**
     * Deleted all tasks from the last search/list result, then returns the deleted tasks.
     *
     * @param inheritLastResult whether to perform operations on last result set
     * @return the deleted tasks
     */
    public final List<Task> deleteAllTasks(boolean inheritLastResult) {
        List<Task> deletedTasks = (inheritLastResult ? lastResult : checklist.stream().toList()).stream()
                .peek(checklist::remove)
                .toList();
        lastResult.setAll(deletedTasks);
        return deletedTasks;
    }

    /**
     * Marks tasks from the last search/list result by index, then returns the marked tasks.
     *
     * @param inheritLastResult whether to perform operations on last result set
     * @param indices the indices of the tasks to be marked
     * @return the marked tasks
     * @throws IndexOutOfBoundsException if an index is out of the last task list's bounds
     */
    public final List<Task> markTasks(boolean inheritLastResult, int... indices) throws IndexOutOfBoundsException {
        int limit = inheritLastResult ? lastResult.size() : checklist.size();
        List<Task> markedTasks = IntStream.of(indices)
                .peek(i -> {
                    if (i < 0 || i >= limit) {
                        throw new IndexOutOfBoundsException(i);
                    }
                })
                .mapToObj(inheritLastResult ? lastResult::get : checklist::get)
                .peek(Task::mark)
                .toList();
        lastResult.setAll(markedTasks);
        return markedTasks;
    }

    /**
     * Marks all tasks from the last search/list result, then returns the marked tasks.
     *
     * @param inheritLastResult whether to perform operations on last result set
     * @return the marked tasks
     */
    public final List<Task> markAllTasks(boolean inheritLastResult) {
        List<Task> markedTasks = (inheritLastResult ? lastResult : checklist).stream()
                .peek(Task::mark)
                .toList();
        lastResult.setAll(markedTasks);
        return markedTasks;
    }

    /**
     * Unmarks tasks from the last search/list result by index, then returns the unmarked tasks.
     *
     * @param inheritLastResult whether to perform operations on last result set
     * @param indices the indices of the tasks to be unmarked
     * @return the unmarked tasks
     * @throws IndexOutOfBoundsException if an index is out of the last task list's bounds
     */
    public final List<Task> unmarkTasks(boolean inheritLastResult, int... indices) throws IndexOutOfBoundsException {
        int limit = inheritLastResult ? lastResult.size() : checklist.size();
        List<Task> unmarkedTasks = IntStream.of(indices)
                .peek(i -> {
                    if (i < 0 || i >= limit) {
                        throw new IndexOutOfBoundsException(i);
                    }
                })
                .mapToObj(inheritLastResult ? lastResult::get : checklist::get)
                .peek(Task::unmark)
                .toList();
        lastResult.setAll(unmarkedTasks);
        return unmarkedTasks;
    }

    /**
     * Unmarks all tasks from the last search/list result, then returns the unmarked tasks.
     *
     * @param inheritLastResult whether to perform operations on last result set
     * @return the unmarked tasks
     */
    public final List<Task> unmarkAllTasks(boolean inheritLastResult) {
        List<Task> unmarkedTasks = (inheritLastResult ? lastResult : checklist).stream()
                .peek(Task::unmark)
                .toList();
        lastResult.setAll(unmarkedTasks);
        return unmarkedTasks;
    }

    /**
     * Performs an action on tasks from the last search/list result by index, then returns the list of action result.
     *
     * @param <T> the return type of the action
     * @param action the action to perform on the target tasks
     * @param inheritLastResult whether to perform operations on last result set
     * @param indices the indices of the tasks to be operated on
     * @return the action result
     * @throws IndexOutOfBoundsException if an index is out of the last task list's bounds
     */
    public final <T> List<T> forTasks(Function<Task, T> action, boolean inheritLastResult, int... indices)
            throws IndexOutOfBoundsException {
        return IntStream.of(indices)
                .peek(i -> {
                    if (i < 0 || i >= lastResult.size()) {
                        throw new IndexOutOfBoundsException(i);
                    }
                })
                .mapToObj(inheritLastResult ? lastResult::get : checklist::get)
                .map(action)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Performs an action on all tasks from the last search/list result, then returns the list of action result.
     *
     * @param <T> the return type of the action
     * @param action the action to perform on the target tasks
     * @param inheritLastResult whether to perform operations on last result set
     * @return the action result
     */
    public final <T> List<T> forAllTasks(Function<Task, T> action, boolean inheritLastResult)
            throws IndexOutOfBoundsException {
        return (inheritLastResult ? lastResult : checklist.stream().toList()).stream()
                .map(action)
                .filter(Objects::nonNull)
                .toList();
    }
}
