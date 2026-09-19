# unmark

Marks a task incomplete

## Synopsis

unmark [taskname or number]

## Example usage

```
unmark sleep
```
marks the first occurence of `sleep` (exact match) as incomplete

Expected output:
```
Nyon! (Unmarked [E][ ] sleep (from: 01 Jan 1900 0000 to: 01 Jan 1900 0800))
```

---

```
unmark 1
```
unmarks the task at index 1, as specified by [list](https://nje14.github.io/ip/LIST)

Expected output:
Similar to above
