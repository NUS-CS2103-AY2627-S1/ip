package megia.model;

import java.util.List;
import java.util.Objects;

/**
 * Represents a structured result produced by the shared command pipeline.
 */
public sealed interface CommandResult
        permits CommandResult.TaskList, CommandResult.TaskMutation,
        CommandResult.Empty, CommandResult.Help, CommandResult.Exit {
    /**
     * Represents a list, date-filtered list, or search result.
     *
     * @param entries Matching tasks with their original IDs.
     * @param query Description of the query that produced the result.
     */
    record TaskList(List<TaskEntry> entries, Query query) implements CommandResult {
        /**
         * Creates an immutable task-list result.
         */
        public TaskList {
            entries = List.copyOf(entries);
            Objects.requireNonNull(query);
        }
    }

    /**
     * Represents a successful task mutation.
     *
     * @param operation Mutation performed.
     * @param task Task affected by the mutation.
     * @param taskCount Number of tasks after the mutation.
     */
    record TaskMutation(MutationType operation, TaskEntry task, int taskCount)
            implements CommandResult {
        /**
         * Creates a validated task-mutation result.
         */
        public TaskMutation {
            Objects.requireNonNull(operation);
            Objects.requireNonNull(task);
            if (taskCount < 0) {
                throw new IllegalArgumentException("Task count cannot be negative");
            }
        }
    }

    /**
     * Identifies the task mutation represented by a result.
     */
    enum MutationType {
        /** A task was added. */
        ADD,
        /** A task was marked done. */
        MARK,
        /** A task was marked not done. */
        UNMARK,
        /** A task was deleted. */
        DELETE
    }

    /**
     * Describes the source of a task-list result.
     *
     * @param type Type of query.
     * @param value Query value, or an empty string for an unfiltered list.
     */
    record Query(QueryType type, String value) {
        /**
         * Creates a query description with an immutable value.
         */
        public Query {
            Objects.requireNonNull(type);
            Objects.requireNonNull(value);
        }
    }

    /**
     * Identifies the source of a task-list result.
     */
    enum QueryType {
        /** All tasks. */
        ALL,
        /** Tasks occurring on one date. */
        DATE,
        /** Tasks matching a description query. */
        FIND
    }

    /**
     * Represents an empty command line.
     */
    record Empty() implements CommandResult {
    }

    /**
     * Represents a request for in-app command guidance.
     */
    record Help() implements CommandResult {
    }

    /**
     * Represents a request to end the application.
     */
    record Exit() implements CommandResult {
    }
}
