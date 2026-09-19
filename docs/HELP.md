# help

displays help page.

pass in optional command argument to see the specific command help page.

## Synopsis

help [command]

## Example usage

```
help
```
shows all available commands

Expected output:
```
Nyon! (Available commands (use help <command> for details):
bye, exit - save tasks and close NyonBot
echo, cat - repeat the supplied text
nyon - show a Nyon message
...
```

---

```
help event
```
show the help message for the event command

Expected output:
```
Nyon! (event - add an event: event <description> --from dd/MM/yyyy HHmm --to dd/MM/yyyy HHmm)
```

## Aliases

`?`, `man`
