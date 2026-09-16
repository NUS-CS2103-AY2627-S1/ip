/**
 * The Lebron James chatbot: a command line task manager for to-dos, deadlines,
 * and events.
 *
 * <p>This package holds the entry point, {@link lebronjames.LebronJames}, and
 * {@link lebronjames.LebronJamesException}, the one checked exception the whole
 * application uses to report anything the user needs to be told about.
 *
 * <p>The exception lives here rather than in one of the sub-packages because
 * every sub-package throws or catches it; putting it in any one of them would
 * make that package a dependency of all the others.
 */
package lebronjames;
