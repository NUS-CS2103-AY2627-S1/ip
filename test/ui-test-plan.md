# Console UI Test Plan

## Dialog font

Aim: Verify that dialog labels render with the bundled JetBrains Mono font.

Inputs: Launch the JavaFX application and submit a message.

Expected result: The user and reply dialog labels use JetBrains Mono; console output is unchanged.

## Exit after a final message

Aim: Verify that the application displays the farewell message and exits after the `bye` command.

Inputs: `bye`

Expected output pattern: `^So long, and thanks for all the fish\.$`

## Add and remove a command alias

Aim: Verify that alias commands store and remove an alias mapping.

Inputs: `alias add /from abc /to todo`, `alias list`, `alias remove /name abc`, `bye`

Expected output patterns: `(?s)^.*\r?\n\r?\nalias added:\r?\nabc -> todo$`,
`(?s)^.*\r?\n\r?\naliases:\r?\nabc -> todo$`,
`(?s)^.*\r?\n\r?\nalias removed:\r?\nabc$`, `^So long, and thanks for all the fish\.$`
