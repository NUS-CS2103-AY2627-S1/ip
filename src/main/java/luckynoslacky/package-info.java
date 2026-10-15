/**
 * Contains the LuckyNoSlacky application entry point and top-level
 * application coordination.
 *
 * <p>The application flow is:</p>
 *
 * <ol>
 * <li>the user interface receives user input</li>
 * <li>the parser creates an executable command</li>
 * <li>the command delegates to {@code TaskMaster}</li>
 * <li>the task manager updates tasks and storage</li>
 * </ol>
 */
package luckynoslacky;
