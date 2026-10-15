package org.cs2103t.marquee.gui.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.cs2103t.marquee.core.task.TaskTag;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

/**
 * Controller for tags view.
 */
public class TagListView extends FlowPane {
    private final SetProperty<TaskTag> items =
            new SimpleSetProperty<>(this, "items", FXCollections.observableSet());

    private final ListProperty<Node> content =
            new SimpleListProperty<>(this, "content", FXCollections.observableList(new LinkedList<>()));
    private final List<TagItemView> pile = new LinkedList<>();

    private final TagListAddButton addButton;
    private final Region sentinel;

    private final HashMap<TaskTag, TagItemView> cellsByTag = new HashMap<>();

    private final BooleanProperty isAddAllowed = new SimpleBooleanProperty(this, "allowAdd", true);
    private final BooleanProperty isBroken = new SimpleBooleanProperty(this, "isBroken", false);
    private final IntegerProperty lastIndex = new SimpleIntegerProperty(this, "lastShownIndex", 0);

    private final ReadOnlyIntegerProperty lastIndexReadOnly = ReadOnlyIntegerWrapper.readOnlyIntegerProperty(lastIndex);

    private final AtomicBoolean needsLayout = new AtomicBoolean(false);
    private final ChangeListener<Bounds> layoutListener = (_, _, _) -> {
        if (!needsLayout.get()) {
            Platform.runLater(this::layoutCells);
        }
        needsLayout.set(true);
    };

    /**
     * Creates a {@code TagListView}.
     */
    public TagListView() throws IOException {
        getStylesheets().add(getClass().getResource("/style/tag-list.css").toExternalForm());

        addButton = new TagListAddButton(this);
        addButton.disableProperty().bind(items.isNull());
        getChildren().add(addButton);

        sentinel = new Region();
        sentinel.setVisible(false);
        content.add(sentinel);
        getChildren().add(sentinel);

        items.addListener((SetChangeListener.Change<? extends TaskTag> c) -> {
            if (isBroken.get()) {
                reconstructCells();
            } else if (c.wasAdded()) {
                try {
                    setCellValue(pushCell(), c.getElementAdded());
                } catch (IOException e) {
                    e.printStackTrace();
                    isBroken.set(true);
                }
            } else if (c.wasRemoved()) {
                popCell(cellsByTag.get(c.getElementRemoved()));
            }
            resetCellLayout();
        });
        lastIndex.addListener((_, _, index) -> {
            getChildren().remove(addButton);
            getChildren().add((Integer) index, addButton);
            layout();
        });
        content.addListener((ListChangeListener.Change<? extends Node> c) -> {
            getChildren().remove(addButton);
            while (c.next()) {
                if (c.wasPermutated()) {
                    for (int i = c.getFrom(); i < c.getTo(); i++) {
                        getChildren().add(c.getPermutation(i), getChildren().remove(i));
                    }
                } else if (c.wasRemoved()) {
                    getChildren().removeAll(c.getRemoved());
                } else if (c.wasAdded()) {
                    getChildren().addAll(c.getFrom(), c.getAddedSubList());
                }
            }
            getChildren().add(lastIndex.get(), addButton);
            layout();
        });
        sentinel.boundsInParentProperty().addListener(layoutListener);

        setHgap(10);
        setVgap(10);
    }

    public ObservableSet<TaskTag> getItems() {
        return items.get();
    }
    public void setItems(ObservableSet<TaskTag> newItems) {
        items.set(newItems);
    }
    public SetProperty<TaskTag> itemsProperty() {
        return items;
    }

    public boolean isAddAllowed() {
        return isAddAllowed.get();
    }
    public void setAddAllowed(boolean addAllowed) {
        isAddAllowed.set(addAllowed);
    }
    public BooleanProperty addAllowedProperty() {
        return isAddAllowed;
    }

    public int getLastIndex() {
        return lastIndex.get();
    }
    public ReadOnlyIntegerProperty lastIndexProperty() {
        return lastIndexReadOnly;
    }

    private TagItemView pushCell(int prefIndex) throws IOException {
        if (prefIndex < 0 || prefIndex > content.size() - 1) {
            throw new IndexOutOfBoundsException(prefIndex);
        }
        TagItemView newCell;
        try {
            newCell = pile.removeLast();
        } catch (NoSuchElementException _) {
            newCell = new TagItemView(this);
        }
        newCell.setVisible(true);
        newCell.setManaged(true);
        content.add(prefIndex, newCell);
        if (prefIndex <= lastIndex.get()) {
            lastIndex.set(lastIndex.get() + 1);
        }
        return newCell;
    }

    private TagItemView pushCell() throws IOException {
        return pushCell(content.size() - 1);
    }

    private void setCellValue(TagItemView cell, TaskTag item) {
        if (cell.getTag() != null) {
            cellsByTag.remove(cell.getTag(), cell);
        }
        cell.setTag(item);
        if (item != null) {
            cellsByTag.put(item, cell);
        }
    }

    private boolean popCell(TagItemView cell) {
        int i = content.indexOf(cell);
        if (i != -1) {
            content.remove(i);
            if (i <= lastIndex.get()) {
                lastIndex.set(lastIndex.get() - 1);
            }
            setCellValue(cell, null);
            pile.add(cell);
            return true;
        } else {
            return false;
        }
    }

    private void reconstructCells() {
        pile.clear();
        content.setAll(sentinel);
        cellsByTag.clear();

        for (TaskTag item : items) {
            try {
                setCellValue(pushCell(), item);
            } catch (IOException e) {
                e.printStackTrace();
                isBroken.set(true);
            }
        }
    }

    private void resetCellLayout() {
        content.forEach(cell -> {
            cell.setManaged(true);
            cell.setVisible(true);
        });
        lastIndex.set(content.size() - 1);
        layoutCells();
    }

    private void layoutCells() {
        Node curr = content.get(lastIndex.get());
        curr.boundsInParentProperty().removeListener(layoutListener);
        if (getLayoutBounds().contains(curr.getBoundsInParent())) {
            curr.setVisible(true);
            Node last;
            while (lastIndex.get() < content.size() - 1) {
                lastIndex.set(lastIndex.get() + 1);
                last = curr;
                curr = content.get(lastIndex.get());
                curr.setManaged(true);
                if (!getLayoutBounds().contains(curr.getBoundsInParent())) {
                    break;
                }
                last.setVisible(true);
            }
        } else {
            curr.setVisible(false);
            Node last;
            while (lastIndex.get() > 0) {
                lastIndex.set(lastIndex.get() - 1);
                last = curr;
                curr = content.get(lastIndex.get());
                if (getLayoutBounds().contains(curr.getBoundsInParent())) {
                    lastIndex.set(lastIndex.get() + 1);
                    break;
                }
                curr.setVisible(false);
                last.setManaged(false);
            }
        }
        content.get(lastIndex.get()).boundsInParentProperty().addListener(layoutListener);
        needsLayout.set(false);
    }
}
