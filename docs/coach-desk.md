# Coach's Desk

DisciTrack keeps its original logo (`src/main/resources/images/discitrack-logo.png`) and its aspect ratio.
The new layout uses a dedicated coach panel, response cards, a task board, and typed commands.

## Controls

- **Add task** collects a to-do, deadline, or event and submits the equivalent command.
- **View tasks** runs `list` and brings the updated task board into view.
- **Search** stays on the main page and runs `find`; results filter the task board while preserving full-list numbers. **Show all** clears the search.
- **Commands** opens the existing command reference in a scrollable dialog.
- **Mark done / Unmark** submits `mark` / `unmark` with the full-list task number.
- **Tag / Untag** opens an inline task editor; **Delete** removes that task through the existing command.
- Description/date editing is not currently supported by the command layer.
- The input bar continues to accept all existing commands, including tags and date searches.

Task data and validation still belong to the existing command layer. Errors are explicitly flagged
by the application, rather than guessed from response wording. The sidebar contracts below 780 px;
the application minimum window size is 660 by 580 px.

## Verification

`MainWindowTest` loads the real FXML with temporary data, adds a task through the input bar,
marks it through a task button, unmarks it through the input bar, checks error recovery and saved
state, and writes wide/compact layout captures under `build/gui-review`.

## Coach artwork

Asset: `src/main/resources/images/discitrack-coach-hype.png`.
Generated with the built-in imagegen tool; the existing logo and original coach asset are unchanged.

Generation prompt:

> Create a single polished 2D illustrated character asset for DisciTrack, a motivational task app.
> Waist-up friendly male sports coach, short dark spiky hair, dark forest-green zip-up tracksuit with
> golden yellow shoulder stripes, whistle around neck, one raised clenched fist celebrating,
> open-mouth happy cheering grin, bright encouraging eyes and relaxed eyebrows. Passionate
> 'LET'S GOOO' energy, playful and supportive, not angry or aggressive. Clean modern comic
> illustration, bold tidy outlines and soft shaded colors. Centered portrait composition 3:4,
> include full head and raised fist with safe margins. Solid uniform dark forest green background
> #174f43 to blend with app panel. NO text, NO lettering, NO logos, NO interface, NO borders.
> Main mascot asset for a desktop application.

## Latest-action feedback

The desk retains command and response history for the current session. Each command brings its newest exchange into view; scrolling up reveals earlier exchanges. Only one task board is kept, reflecting the latest command. Moving to another command clears the
search filter and shows only that command's affected tasks. Adding a task shows the full list with the new task at the end; mark, unmark, tag and untag show only the affected row; list shows all tasks; find and checkdate show matches. Empty lists congratulate the user, while a search
with no matches still explains that no results were found. Completion adds a brief coach pulse
and more energetic encouragement.


## Coach movement

Completion and an empty list briefly pulse the coach portrait. There is no applause bubble,
sound, or animation of the coach's hands.
