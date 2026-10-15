package bern.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import bern.logic.ScheduleEntry;
import bern.logic.ScheduleResponse;
import bern.task.Task;
import bern.task.TaskFactory;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Loads and lays out the timetable offscreen to check its FXML, styling, card placement, and scrolling.
 */
public class ScheduleFrameTest {
    private static final int NARROW_FRAME_WIDTH = 417;
    private static final int WIDE_FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 440;
    private static final LocalDate DATE = LocalDate.of(2026, 9, 15);

    /**
     * Starts JavaFX without opening a window, skipping Linux environments without a display server.
     *
     * @throws InterruptedException If interrupted while waiting for JavaFX to initialize.
     */
    @BeforeAll
    public static void startToolkit() throws InterruptedException {
        assumeFalse(System.getProperty("os.name").startsWith("Linux") && System.getenv("DISPLAY") == null,
                "JavaFX layout tests require a display server on Linux");
        CountDownLatch started = new CountDownLatch(1);
        Platform.startup(started::countDown);
        assertTrue(started.await(20, TimeUnit.SECONDS), "JavaFX should initialize without opening a window");
    }

    @Test
    public void render_unsortedOverlappingEvents_usesTwoNoncollidingColumns() throws Exception {
        Task first = makeEvent("Lecture", "15/9/2026 @ 08:00", "15/9/2026 @ 10:00");
        Task second = makeEvent("Study group", "15/9/2026 @ 09:00", "15/9/2026 @ 11:00");
        Task third = makeEvent("Project meeting", "15/9/2026 @ 10:00", "15/9/2026 @ 12:00");
        Task fourth = makeEvent("Lunch", "15/9/2026 @ 11:00", "15/9/2026 @ 13:00");
        List<Task> tasks = List.of(first, fourth, second, third);

        onFxThread(() -> {
            ScheduleResponse response = ScheduleResponse.getScheduleResponse("Today's tasks", tasks, DATE);
            for (int width : List.of(NARROW_FRAME_WIDTH, WIDE_FRAME_WIDTH)) {
                StackPane root = render(response, width);
                List<Node> cards = List.copyOf(root.lookupAll(".schedule-event"));
                assertEquals(4, cards.size());
                assertEquals(2, cards.stream().map(Node::getLayoutX).distinct().count());
                assertInstanceOf(GridPane.class, root.lookup(".schedule-grid"));
                assertNonintersectingCards(cards);
                assertOrderedTimes(root);
                assertTrue(findCard(root, first).getLayoutY() < findCard(root, second).getLayoutY());
                assertTrue(findCard(root, second).getLayoutY() < findCard(root, third).getLayoutY());
                assertTrue(findCard(root, third).getLayoutY() < findCard(root, fourth).getLayoutY());
                for (Node card : cards) {
                    assertInstanceOf(ScheduleEntry.class, card.getUserData());
                    assertFalse(((VBox) card).getBackground().getFills().isEmpty(),
                            "The FXML stylesheet must style every event card");
                }
                writeSnapshot(root, "timetable-" + width + ".png");
            }
            return null;
        });
    }

    @Test
    public void render_shortTaskNames_stretchesFrameWithoutExpandingColumns() throws Exception {
        Task first = makeEvent("A", "15/9/2026 @ 09:00", "15/9/2026 @ 10:00");
        Task second = makeEvent("B", "15/9/2026 @ 09:00", "15/9/2026 @ 10:00");

        onFxThread(() -> {
            ScheduleResponse response = ScheduleResponse.getScheduleResponse("Today's tasks",
                    List.of(first, second), DATE);
            double previousGridWidth = 0;
            for (int width : List.of(NARROW_FRAME_WIDTH, WIDE_FRAME_WIDTH)) {
                StackPane root = render(response, width);
                ScheduleFrame frame = (ScheduleFrame) root.getChildren().getFirst();
                GridPane grid = (GridPane) root.lookup(".schedule-grid");
                VBox day = (VBox) grid.getParent();
                Insets insets = day.getInsets();
                assertEquals(width, frame.getWidth(), 1,
                        "The overall frame must stretch to the available window width");
                assertEquals(day.getWidth() - insets.getLeft() - insets.getRight(), grid.getWidth(), 1,
                        "The timetable must stretch across its day's available content width");
                assertRowBackgroundsFillGrid(grid);
                assertEquals(150, getColumnWidth(findCard(root, first)), 1,
                        "Short tasks must keep their column at 150 pixels even when the frame has spare space");
                assertEquals(150, getColumnWidth(findCard(root, second)), 1);
                if (previousGridWidth > 0) {
                    assertTrue(grid.getWidth() > previousGridWidth,
                            "The timetable must grow when the window becomes wider");
                }
                previousGridWidth = grid.getWidth();
            }
            return null;
        });
    }

    @Test
    public void render_mediumTaskName_expandsItsColumnBeforeWrapping() throws Exception {
        Task task = makeEvent("Prepare project proposal",
                "15/9/2026 @ 09:00", "15/9/2026 @ 10:00");

        onFxThread(() -> {
            StackPane root = render(
                    ScheduleResponse.getScheduleResponse("Today's tasks", List.of(task), DATE), WIDE_FRAME_WIDTH);
            Node card = findCard(root, task);
            double columnWidth = getColumnWidth(card);
            assertTrue(columnWidth > 150 && columnWidth < 250,
                    "A medium task name must grow its column only as far as its text requires");
            Label description = (Label) card.lookup(".schedule-task-name");
            assertTrue(description.getWidth() >= description.prefWidth(-1) - 1,
                    "A description that fits within 250 pixels must have enough width for a single line");
            assertEquals(description.prefHeight(-1), description.getHeight(), 1);
            assertCardTextFits(card);
            return null;
        });
    }

    @Test
    public void render_longShortEvent_capsColumnAndExpandsSharedRow() throws Exception {
        Task longTask = makeEvent("Review project requirements and prepare the implementation proposal. ".repeat(4),
                "15/9/2026 @ 09:00", "15/9/2026 @ 09:01");
        Task shortTask = makeEvent("B", "15/9/2026 @ 09:00", "15/9/2026 @ 09:01");
        Task laterTask = makeEvent("C", "15/9/2026 @ 09:01", "15/9/2026 @ 09:02");

        onFxThread(() -> {
            ScheduleResponse response = ScheduleResponse.getScheduleResponse("Today's tasks",
                    List.of(longTask, shortTask, laterTask), DATE);
            for (int width : List.of(NARROW_FRAME_WIDTH, WIDE_FRAME_WIDTH)) {
                StackPane root = render(response, width);
                GridPane grid = (GridPane) root.lookup(".schedule-grid");
                assertRowBackgroundsFillGrid(grid);
                Node longCard = findCard(root, longTask);
                Node shortCard = findCard(root, shortTask);
                Node laterCard = findCard(root, laterTask);
                double columnWidth = getColumnWidth(longCard);
                assertEquals(250, columnWidth, 1, "Long task names must stop expanding their column at 250 pixels");
                assertEquals(150, getColumnWidth(shortCard), 1,
                        "A neighboring column with a short task must keep its minimum width");
                assertEquals(GridPane.getColumnIndex(longCard), GridPane.getColumnIndex(laterCard));
                assertEquals(columnWidth, getColumnWidth(laterCard), 1,
                        "Tasks in the same column must share the width required by the longest task");
                assertWrappedRowAlignment(grid, longCard, shortCard, laterCard);
                List<Node> cards = List.copyOf(root.lookupAll(".schedule-event"));
                assertNonintersectingCards(cards);
                cards.forEach(this::assertCardTextFits);
                if (width == NARROW_FRAME_WIDTH) {
                    ScrollPane scroll = (ScrollPane) root.lookup(".schedule-scroll");
                    assertHorizontalScrollingAvailable(scroll);
                }
                writeSnapshot(root, "timetable-content-width-" + width + ".png");
            }
            return null;
        });
    }

    @Test
    public void render_longSpanningEvent_expandsRowsWithoutClippingOrOverlap() throws Exception {
        Task longTask = makeEvent("Review project requirements and prepare the implementation proposal. ".repeat(10),
                "15/9/2026 @ 09:00", "15/9/2026 @ 09:03");
        List<Task> tasks = List.of(longTask,
                makeEvent("First", "15/9/2026 @ 09:00", "15/9/2026 @ 09:01"),
                makeEvent("Second", "15/9/2026 @ 09:01", "15/9/2026 @ 09:02"),
                makeEvent("Third", "15/9/2026 @ 09:02", "15/9/2026 @ 09:03"));

        onFxThread(() -> {
            StackPane root = render(
                    ScheduleResponse.getScheduleResponse("Today's tasks", tasks, DATE), WIDE_FRAME_WIDTH);
            GridPane grid = (GridPane) root.lookup(".schedule-grid");
            Node longCard = findCard(root, longTask);
            List<Node> cards = List.copyOf(root.lookupAll(".schedule-event"));
            assertEquals(2, cards.stream().map(Node::getLayoutX).distinct().count());
            assertEquals(250, getColumnWidth(longCard), 1);
            assertEquals(3, GridPane.getRowSpan(longCard));
            assertTrue(longCard.getLayoutBounds().getHeight() > 3 * 52,
                    "Rows spanned by a long event must provide enough combined height for its description");
            Node firstNeighbor = findCard(root, tasks.get(1));
            Node lastNeighbor = findCard(root, tasks.getLast());
            assertEquals(longCard.getBoundsInParent().getMinY(), firstNeighbor.getBoundsInParent().getMinY(), 1);
            assertEquals(longCard.getBoundsInParent().getMaxY(), lastNeighbor.getBoundsInParent().getMaxY(), 1);
            assertNonintersectingCards(cards);
            cards.forEach(this::assertCardTextFits);
            assertRowBackgroundsFillGrid(grid);
            assertOrderedTimes(root);
            writeSnapshot(root, "timetable-wrapped-spanning-event.png");
            return null;
        });
    }

    @Test
    public void render_threeConcurrentEvents_scrollsWithinNarrowFrame() throws Exception {
        List<Task> tasks = List.of(
                makeEvent("Lecture", "15/9/2026 @ 09:00", "15/9/2026 @ 10:00"),
                makeEvent("Lab", "15/9/2026 @ 09:00", "15/9/2026 @ 11:00"),
                makeEvent("Consultation", "15/9/2026 @ 09:00", "15/9/2026 @ 12:00"));

        onFxThread(() -> {
            StackPane root = render(
                    ScheduleResponse.getScheduleResponse("Today's tasks", tasks, DATE), NARROW_FRAME_WIDTH);
            List<Node> cards = List.copyOf(root.lookupAll(".schedule-event"));
            assertEquals(3, cards.size());
            assertEquals(3, cards.stream().map(Node::getLayoutX).distinct().count());
            assertNonintersectingCards(cards);

            ScheduleFrame frame = (ScheduleFrame) root.getChildren().getFirst();
            ScrollPane scroll = (ScrollPane) root.lookup(".schedule-scroll");
            assertEquals(NARROW_FRAME_WIDTH, frame.getWidth(), 1);
            assertTrue(scroll.getBoundsInParent().getMaxX() <= frame.getWidth());
            assertHorizontalScrollingAvailable(scroll);
            writeSnapshot(root, "timetable-three-columns.png");

            scroll.setHvalue(1);
            root.layout();
            Bounds viewport = scroll.lookup(".viewport").localToScene(scroll.lookup(".viewport").getBoundsInLocal());
            Node lastCard = cards.stream().max(Comparator.comparingDouble(Node::getLayoutX)).orElseThrow();
            Bounds lastBounds = lastCard.localToScene(lastCard.getBoundsInLocal());
            assertTrue(lastBounds.getMinX() >= viewport.getMinX());
            assertTrue(lastBounds.getMaxX() <= viewport.getMaxX() + 1,
                    "Horizontal scrolling should reveal the entire final column");
            return null;
        });
    }

    @Test
    public void render_shortAndZeroDurationEvents_keepsReadableCards() throws Exception {
        Task first = makeEvent("Quick check", "15/9/2026 @ 09:00", "15/9/2026 @ 09:01");
        Task second = makeEvent("Next check", "15/9/2026 @ 09:01", "15/9/2026 @ 09:02");
        Task zeroDuration = makeEvent("Reminder", "15/9/2026 @ 09:01", "15/9/2026 @ 09:01");

        onFxThread(() -> {
            StackPane root = render(ScheduleResponse.getScheduleResponse("Today's tasks",
                    List.of(first, second, zeroDuration), DATE), NARROW_FRAME_WIDTH);
            List<Node> cards = List.copyOf(root.lookupAll(".schedule-event"));
            assertEquals(2, cards.size());
            assertEquals(3, root.lookupAll(".schedule-card").size());
            assertNonintersectingCards(cards);
            assertOrderedTimes(root);
            List<Label> timeLabels = getTimeLabels(root);
            assertEquals(List.of("09:00", "10:00"), timeLabels.stream().map(Label::getText).toList());
            for (Node card : cards) {
                assertTrue(card.getBoundsInParent().getHeight() >= 40,
                        "A one-minute event must still have room for its title and time");
                assertTrue(timeLabels.getLast().getBoundsInParent().getMinY()
                        >= card.getBoundsInParent().getMaxY(),
                        "The final hourly label must appear below both short events");
            }
            assertFalse(findCard(root, zeroDuration).getStyleClass().contains("schedule-event"));
            writeSnapshot(root, "timetable-short-events.png");
            return null;
        });
    }

    @Test
    public void render_multipleDays_preservesTodosDeadlinesAndAllDayEvents() throws Exception {
        Task overnight = makeEvent("Overnight trip", "14/9/2026 @ 23:00", "15/9/2026 @ 01:00");
        Task allDay = makeEvent("Conference", "15/9/2026", "15/9/2026");
        Task deadline = TaskFactory.makeTaskFromData(new String[] {"D", "1", "Submit report", "16/9/2026"});
        Task todo = TaskFactory.makeTaskFromData(new String[] {"T", "0", "Buy groceries"});
        List<Task> tasks = List.of(deadline, todo, allDay, overnight);

        onFxThread(() -> {
            ScheduleResponse response = ScheduleResponse.getScheduleResponse("All tasks", tasks).withTaskNumbers(tasks);
            StackPane root = render(response, WIDE_FRAME_WIDTH);
            List<Node> days = root.lookupAll(".schedule-day").stream()
                    .sorted(Comparator.comparingDouble(Node::getLayoutY)).toList();
            assertEquals(List.of(DATE.minusDays(1), DATE, DATE.plusDays(1)),
                    days.stream().map(Node::getUserData).toList());
            assertEquals(List.of("23:00", "24:00"),
                    getTimeLabels(days.getFirst()).stream().map(Label::getText).toList());
            assertEquals(List.of("00:00", "01:00"),
                    getTimeLabels(days.get(1)).stream().map(Label::getText).toList());
            assertEquals(5, root.lookupAll(".schedule-card").size());
            Set<String> shownTasks = root.lookupAll(".schedule-card").stream()
                    .map(Node::getAccessibleText).collect(Collectors.toSet());
            assertEquals(tasks.stream().map(Task::toString).collect(Collectors.toSet()), shownTasks);
            assertEquals(2, root.lookupAll(".schedule-event").size());
            assertTrue(findCard(root, deadline).getStyleClass().contains("schedule-completed"));
            Label deadlineName = (Label) findCard(root, deadline).lookup(".schedule-task-name");
            Label todoName = (Label) findCard(root, todo).lookup(".schedule-task-name");
            Label overnightName = (Label) findCard(root, overnight).lookup(".schedule-task-name");
            assertEquals("1. [X] Submit report", deadlineName.getText());
            assertEquals("2. [ ] Buy groceries", todoName.getText());
            assertEquals("4. [ ] Overnight trip", overnightName.getText());
            assertNotSame(root.getChildren().getFirst(), response.getResponseNodes().get(1),
                    "Each response rendering needs fresh nodes that can belong to a new scene");
            return null;
        });
    }

    @Test
    public void render_noTasks_showsEmptyScheduleMessage() throws Exception {
        onFxThread(() -> {
            StackPane root = render(ScheduleResponse.getScheduleResponse("All tasks", List.of()), NARROW_FRAME_WIDTH);
            assertEquals(Set.of("No tasks to display."), getVisibleLabelTexts(root));
            assertTrue(root.lookupAll(".schedule-day").isEmpty());
            assertTrue(root.lookupAll(".schedule-card").isEmpty());
            assertTrue(root.lookupAll(".schedule-grid").isEmpty());
            return null;
        });
    }

    @Test
    public void render_emptyDay_showsDateAndEmptyDayMessage() throws Exception {
        onFxThread(() -> {
            StackPane root = render(
                    ScheduleResponse.getScheduleResponse("Today's tasks", List.of(), DATE), NARROW_FRAME_WIDTH);
            String heading = DATE.format(DateTimeFormatter.ofPattern("EEE, d MMM yyyy"));
            assertEquals(Set.of(heading, "No tasks on this day."), getVisibleLabelTexts(root));
            assertEquals(1, root.lookupAll(".schedule-day").size());
            assertTrue(root.lookupAll(".schedule-card").isEmpty());
            assertTrue(root.lookupAll(".schedule-grid").isEmpty());
            return null;
        });
    }

    @Test
    public void render_onlyUndatedTasks_showsNumberedCardsWithoutTimetable() throws Exception {
        Task first = TaskFactory.makeTaskFromData(new String[] {"T", "0", "Buy groceries"});
        Task second = TaskFactory.makeTaskFromData(new String[] {"T", "1", "Read project brief"});
        List<Task> tasks = List.of(first, second);

        onFxThread(() -> {
            ScheduleResponse response = ScheduleResponse.getScheduleResponse("All tasks", tasks).withTaskNumbers(tasks);
            StackPane root = render(response, NARROW_FRAME_WIDTH);
            assertEquals(Set.of("Undated", "1. [ ] Buy groceries", "2. [X] Read project brief", "To do"),
                    getVisibleLabelTexts(root));
            assertEquals(2, root.lookupAll(".schedule-card").size());
            assertTrue(root.lookupAll(".schedule-day").isEmpty());
            assertTrue(root.lookupAll(".schedule-grid").isEmpty());
            assertTrue(findCard(root, first).getLayoutY() < findCard(root, second).getLayoutY());
            for (Task task : tasks) {
                Node card = findCard(root, task);
                Label timing = (Label) card.lookup(".schedule-task-time");
                assertEquals("To do", timing.getText());
                assertCardTextFits(card);
            }
            return null;
        });
    }

    /**
     * Creates a saved event, including equal-time legacy entries that still need to be displayed.
     *
     * @param name The event description.
     * @param start The saved start date and optional time.
     * @param end The saved end date and optional time.
     * @return The incomplete event reconstructed from saved task data.
     * @throws ParseException If either date or time cannot be parsed.
     */
    private Task makeEvent(String name, String start, String end) throws ParseException {
        return TaskFactory.makeTaskFromData(new String[] {"E", "0", name, start, end});
    }

    /**
     * Loads the response's actual FXML and stylesheets in a scene without showing a stage.
     *
     * @param response The response whose schedule is rendered.
     * @param width The scene and root width in pixels.
     * @return The laid-out root containing the schedule frame.
     */
    private StackPane render(ScheduleResponse response, double width) {
        List<Node> nodes = response.getResponseNodes();
        assertEquals(2, nodes.size());
        assertInstanceOf(DialogBox.class, nodes.getFirst());
        ScheduleFrame frame = assertInstanceOf(ScheduleFrame.class, nodes.get(1));
        StackPane root = new StackPane(frame);
        new Scene(root, width, FRAME_HEIGHT);
        root.resize(width, FRAME_HEIGHT);
        root.applyCss();
        root.layout();
        return root;
    }

    /**
     * Finds a task's card using its full accessible description, independent of display order.
     *
     * @param root The subtree to search.
     * @param task The task whose card is needed.
     * @return The first card with the task's accessible description.
     * @throws java.util.NoSuchElementException If no matching card exists.
     */
    private Node findCard(Node root, Task task) {
        return root.lookupAll(".schedule-card").stream()
                .filter(card -> task.toString().equals(card.getAccessibleText())).findFirst().orElseThrow();
    }

    /**
     * Returns the text of labels marked visible in the supplied subtree.
     *
     * @param root The subtree containing the labels.
     * @return The distinct text values of labels whose visible property is true.
     */
    private Set<String> getVisibleLabelTexts(Node root) {
        return root.lookupAll(".label").stream().filter(Node::isVisible).map(node -> ((Label) node).getText())
                .collect(Collectors.toSet());
    }

    /**
     * Returns a task column's actual width, including the margins around its card.
     *
     * @param card The laid-out card in a task column.
     * @return The card width plus its horizontal grid margins, in pixels.
     */
    private double getColumnWidth(Node card) {
        Insets margin = GridPane.getMargin(card);
        if (margin == null) {
            margin = Insets.EMPTY;
        }
        return card.getLayoutBounds().getWidth() + margin.getLeft() + margin.getRight();
    }

    /**
     * Checks that wrapping expands a shared row and moves subsequent events below it.
     *
     * @param grid The laid-out timetable grid.
     * @param longCard The card whose description should wrap.
     * @param shortCard A card occupying the same time range in another column.
     * @param laterCard A card in the following row.
     */
    private void assertWrappedRowAlignment(GridPane grid, Node longCard, Node shortCard, Node laterCard) {
        Label description = (Label) longCard.lookup(".schedule-task-name");
        assertTrue(description.getHeight() > description.prefHeight(-1) + 1,
                "A description wider than the column limit must wrap onto multiple lines");
        Bounds rowBounds = grid.getCellBounds(GridPane.getColumnIndex(longCard), GridPane.getRowIndex(longCard));
        assertTrue(rowBounds.getHeight() > 52,
                "A short event's row must grow beyond its default height to fit wrapped text");
        assertEquals(longCard.getBoundsInParent().getMinY(), shortCard.getBoundsInParent().getMinY(), 1);
        assertEquals(longCard.getBoundsInParent().getMaxY(), shortCard.getBoundsInParent().getMaxY(), 1,
                "Overlapping events with the same time range must retain their shared row boundaries");
        assertTrue(laterCard.getBoundsInParent().getMinY() >= longCard.getBoundsInParent().getMaxY(),
                "Growing a row must move the following event below it");
    }

    /**
     * Checks that wide content exposes a usable horizontal scrollbar.
     *
     * @param scroll The laid-out schedule scroll pane.
     */
    private void assertHorizontalScrollingAvailable(ScrollPane scroll) {
        assertTrue(scroll.getContent().getLayoutBounds().getWidth() > scroll.getViewportBounds().getWidth());
        assertTrue(scroll.lookupAll(".scroll-bar").stream().map(node -> (ScrollBar) node)
                .anyMatch(bar -> bar.getOrientation() == Orientation.HORIZONTAL && bar.isVisible()),
                "Wide task columns must remain horizontally scrollable in a narrow frame");
    }

    /**
     * Checks that each label has room for all wrapped lines and remains inside its card's padding.
     *
     * @param card The laid-out task card to inspect.
     */
    private void assertCardTextFits(Node card) {
        VBox box = (VBox) card;
        Insets insets = box.getInsets();
        for (String styleClass : List.of(".schedule-task-name", ".schedule-task-time")) {
            Label label = (Label) card.lookup(styleClass);
            assertTrue(label.getHeight() >= label.prefHeight(label.getWidth()) - 1,
                    "Every label must have enough height to display its complete text at the available width");
            Bounds bounds = label.getBoundsInParent();
            assertTrue(bounds.getMinX() >= insets.getLeft() - 1);
            assertTrue(bounds.getMaxX() <= box.getWidth() - insets.getRight() + 1);
            assertTrue(bounds.getMinY() >= insets.getTop() - 1);
            assertTrue(bounds.getMaxY() <= box.getHeight() - insets.getBottom() + 1,
                    "Wrapped descriptions and timing labels must stay inside their card");
        }
    }

    /**
     * Checks that every row background fills the grid width after the time axis.
     *
     * @param grid The laid-out timetable grid.
     */
    private void assertRowBackgroundsFillGrid(GridPane grid) {
        Set<Node> backgrounds = grid.lookupAll(".schedule-row");
        assertFalse(backgrounds.isEmpty());
        double timeAxisEnd = grid.getCellBounds(0, 0).getMaxX();
        for (Node background : backgrounds) {
            Bounds bounds = background.getBoundsInParent();
            assertEquals(timeAxisEnd, bounds.getMinX(), 1,
                    "Row backgrounds must begin immediately after the time axis");
            assertEquals(grid.getWidth(), bounds.getMaxX(), 1,
                    "Row backgrounds must extend to the timetable's right edge");
        }
    }

    /**
     * Checks actual laid-out card rectangles rather than trusting the logical column assignments.
     *
     * @param cards The cards sharing a timetable grid.
     */
    private void assertNonintersectingCards(List<Node> cards) {
        for (int i = 0; i < cards.size(); i++) {
            Bounds bounds = cards.get(i).getBoundsInParent();
            assertTrue(bounds.getWidth() > 0 && bounds.getHeight() > 0);
            for (int j = i + 1; j < cards.size(); j++) {
                assertFalse(bounds.intersects(cards.get(j).getBoundsInParent()),
                        "Timetable cards must not cover one another");
            }
        }
    }

    /**
     * Checks that time labels appear in increasing order down the shared vertical axis.
     *
     * @param root The subtree containing one timetable's time axis.
     */
    private void assertOrderedTimes(Node root) {
        List<String> times = getTimeLabels(root).stream().map(Label::getText).toList();
        assertFalse(times.isEmpty());
        assertEquals(times.stream().sorted().toList(), times);
    }

    /**
     * Returns the time-axis labels in display order within the supplied subtree.
     *
     * @param root The subtree containing one timetable's time axis.
     * @return The time-axis labels sorted by their vertical layout positions.
     */
    private List<Label> getTimeLabels(Node root) {
        return root.lookupAll(".schedule-time").stream().map(node -> (Label) node)
                .sorted(Comparator.comparingDouble(Node::getLayoutY)).toList();
    }

    /**
     * Saves a rendered preview for visual inspection without adding a javafx-swing dependency.
     *
     * @param root The laid-out node to capture.
     * @param filename The PNG filename within {@code build/reports/timetable}.
     * @throws IOException If the output directory cannot be created or the PNG cannot be written.
     */
    private void writeSnapshot(Node root, String filename) throws IOException {
        WritableImage snapshot = root.snapshot(null, null);
        int width = (int) snapshot.getWidth();
        int height = (int) snapshot.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, snapshot.getPixelReader().getArgb(x, y));
            }
        }
        Path directory = Path.of("build", "reports", "timetable");
        Files.createDirectories(directory);
        assertTrue(ImageIO.write(image, "png", directory.resolve(filename).toFile()));
    }

    /**
     * Executes JavaFX work on its application thread and propagates assertion or loading failures to JUnit.
     *
     * @param <T> The action's result type.
     * @param action The work to execute on the JavaFX application thread.
     * @return The action's result.
     * @throws Exception If waiting is interrupted, the action fails, or the 20-second timeout expires.
     */
    private static <T> T onFxThread(Callable<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action);
        Platform.runLater(task);
        return task.get(20, TimeUnit.SECONDS);
    }
}
