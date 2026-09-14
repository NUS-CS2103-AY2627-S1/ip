# Saved-data UI Test Plan

Run this plan in a separate temporary repository. Before launching Groot, create
`data/groot.txt` containing exactly `D | 0 | broken | invalid` followed by a newline.
The JUnit storage tests also exercise this case with platform-native file paths.

## SC1: Invalid saved deadline date

**Aim:** Verify that an invalid saved date produces the line-numbered startup error, without processing commands or modifying the saved file.

### Input

```text
todo ignored
bye
```

### Expected output

```text
____________________________________________________________
       \  |  /
     ___\_|_/___
    /   /   \   \
   /   | o o |    |
  |    |  ^  |    |
  |    \ \_/ /    |
   \    '---'    /
    \  |||||||  /
     | ||||||| |
  ___|_|||||||_|___
 /     |||||||     \
/      |||||||      \
       |||||||
      /||| |||\
     /_||| |||_\
Hello! I'm Groot.
What can I do for you?
____________________________________________________________
 Oops! I couldn't load your tasks because line 1 in data/groot.txt is invalid.
____________________________________________________________
```
