# UI Test Plan

## Test Case: Initial greeting and exit

### Aim
Verify that ubis.Ubis displays its banner and greeting, then exits with a farewell message when the user enters `bye`.

### Command
```sh
javac -d /private/tmp/ubis-ui-test src/main/java/*.java && java -cp /private/tmp/ubis-ui-test ubis.Ubis
```

### Input
```text
bye
```

### Expected Output
```text
______________________________
 _   _ ____ ___ ____  
| | | | __ )|_ _/ ___| 
| | | |  _ \ | |\___ \ 
| |_| | |_) || | ___) |
 \___/|____/|___|____/ 

Hello! I am ubis.Ubis.
What can I do for you?
______________________________
______________________________
Goodbye. See you soon!
______________________________
```
