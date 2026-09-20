# Duchess architecture

Duchess uses a small layered design so the command behavior can be shared by
the CLI and the JavaFX application.

## Layers

| Layer | Main files | Responsibility |
| --- | --- | --- |
| User interfaces | `duchess.ui.Ui`, `duchess.gui.*`, `view/*.fxml` | Read user input and display responses |
| Application boundary | `duchess.Duchess` | Dispatch commands, update state, and format responses |
| Parsing | `duchess.parser.Parser` | Convert command text into tasks or validated indexes |
| Domain model | `duchess.task.*` | Represent task types, status, and statistics |
| Collection state | `duchess.task.TaskList` | Preserve ordering and perform list operations |
| Persistence | `duchess.storage.Storage` | Encode, save, load, and recover task records |

## Command flow

```text
CLI or JavaFX input
        |
        v
    Duchess
     /   \
 Parser  TaskList <--> Storage
        |
        v
  formatted response
```

`Duchess` is intentionally the shared application boundary. The CLI and GUI
therefore display the same validation messages and task formatting. The GUI
controller only handles presentation concerns such as dialog bubbles, status
colours, and disabling input after `bye`.

## JavaFX and FXML

`MainWindow.fxml` declares the stable page structure and composes four reusable
FXML-backed controls:

- `Header.fxml` for the title, status, and Help button;
- `QuickCommands.fxml` for common command shortcuts;
- `ConversationView.fxml` for the scrolling conversation; and
- `Composer.fxml` for the command field and Send button.

`DialogBox.fxml` is also FXML-backed, but its text, speaker, and style
are filled dynamically because they depend on each response. This division
keeps layout in FXML while leaving changing application state in Java.

## Persistence decision

`Storage` defaults to `data/duchess.txt` for normal use, but accepts a `Path`
in its constructor. The injectable path keeps tests isolated in temporary
directories without changing the production behavior.
