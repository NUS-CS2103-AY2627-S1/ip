# mark

Marks a task complete

## Synopsis

mark \<taskname or number\>

## Example usage

```
mark sleep
```
marks the first occurence of `sleep` (exact match) as completed

Expected output:
```
Nyon! (Marked [E][X] sleep (from: 01 Jan 1900 0000 to: 01 Jan 1900 0800) as completed)
```

---

```
mark 1
```
marks the task at index 1, as specified by [list](https://nje14.github.io/ip/LIST)

Expected output:
Similar to above
