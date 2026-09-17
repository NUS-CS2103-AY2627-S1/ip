package glennon.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import glennon.exception.GlennonException;

/**
 * Stores the missions of one Glennon session and guards every access by
 * mission number, so callers cannot reach a position outside the log.
 */
public class TaskList {
    /** Missions stored in their display order. */
    private final List<Task> missions;

    /**
     * Creates an empty mission list for a new Glennon session.
     */
    public TaskList() {
        this(Collections.emptyList());
    }

    /**
     * Creates a mission list containing missions loaded from storage.
     *
     * @param missions missions to place in the log.
     */
    public TaskList(List<Task> missions) {
        this.missions = new ArrayList<>(missions);
    }

    /**
     * Adds a mission to the end of the log.
     *
     * @param mission mission to store.
     * @throws GlennonException if the mission's details already appear in the log.
     */
    public void add(Task mission) throws GlennonException {
        for (Task existingMission : missions) {
            if (existingMission.hasSameDetails(mission)) {
                throw new GlennonException("That mission already exists in the log.");
            }
        }
        missions.add(mission);
    }

    /**
     * Returns the mission at the given position.
     *
     * @param missionIndex zero-based position of the mission.
     * @return the stored mission.
     * @throws GlennonException if the position is outside the log.
     */
    public Task get(int missionIndex) throws GlennonException {
        requireInRange(missionIndex);
        return missions.get(missionIndex);
    }

    /**
     * Marks the mission at the specified position as complete.
     *
     * @param missionIndex zero-based position of the mission.
     * @return mission whose status was changed.
     * @throws GlennonException if the position is outside the log.
     */
    public Task mark(int missionIndex) throws GlennonException {
        Task mission = get(missionIndex);
        mission.markAsDone();
        return mission;
    }

    /**
     * Marks the mission at the specified position as incomplete.
     *
     * @param missionIndex zero-based position of the mission.
     * @return mission whose status was changed.
     * @throws GlennonException if the position is outside the log.
     */
    public Task unmark(int missionIndex) throws GlennonException {
        Task mission = get(missionIndex);
        mission.markAsNotDone();
        return mission;
    }

    /**
     * Removes the mission at the given position and returns it.
     *
     * @param missionIndex zero-based position of the mission.
     * @return the mission that was removed.
     * @throws GlennonException if the position is outside the log.
     */
    public Task remove(int missionIndex) throws GlennonException {
        requireInRange(missionIndex);
        return missions.remove(missionIndex);
    }

    /**
     * Returns how many missions are currently stored.
     *
     * @return the mission count.
     */
    public int size() {
        return missions.size();
    }

    /**
     * Returns a read-only view of the missions for display.
     *
     * @return the stored missions in insertion order.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(missions);
    }

    /**
     * Returns missions whose descriptions contain the specified keyword,
     * preserving their order in the mission log.
     *
     * @param keyword case-sensitive keyword to find in mission descriptions.
     * @return matching missions in insertion order.
     */
    public List<Task> findByDescription(String keyword) {
        return missions.stream()
                .filter(mission -> mission.getDescription().contains(keyword))
                .toList();
    }

    /**
     * Returns deadlines and events occurring on the specified date, preserving
     * their order in the mission log.
     *
     * @param date date whose scheduled missions should be returned.
     * @return matching scheduled missions.
     */
    public List<Task> occurringOn(LocalDate date) {
        return missions.stream()
                .filter(mission -> mission.occursOn(date))
                .toList();
    }

    /**
     * Sorts scheduled missions chronologically and places unscheduled missions
     * afterward. Missions with equal sort times retain their relative order.
     */
    public void sortChronologically() {
        missions.sort(Comparator.comparing(
                TaskList::getSortDateTime,
                Comparator.nullsLast(Comparator.naturalOrder())));
    }

    /**
     * Returns the date-time used to place a mission chronologically.
     *
     * @param mission mission whose sort date-time is required.
     * @return deadline due time, event start time, or null for an unscheduled mission.
     */
    private static LocalDateTime getSortDateTime(Task mission) {
        if (mission instanceof Deadline deadline) {
            return deadline.getDueDateTime();
        }
        if (mission instanceof Event event) {
            return event.getStartDateTime();
        }
        return null;
    }

    /**
     * Rejects a position that no mission occupies.
     *
     * @param missionIndex zero-based position to check.
     * @throws GlennonException if the position is outside the log.
     */
    private void requireInRange(int missionIndex) throws GlennonException {
        if (missionIndex < 0 || missionIndex >= missions.size()) {
            throw new GlennonException("Please enter a valid mission number.");
        }
    }
}
