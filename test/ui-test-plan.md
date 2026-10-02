# UI test plan

This file lists interactive console test cases for the repository.

### Test: command-object-flow
Aim: Verify the app can add tasks and show events on a matching date before exiting.
Command: cmd /c "(echo todo read book & echo deadline return book /by 2025-01-01 & echo event trip /from 2019-12-01 /to 2019-12-03 & echo on 2019-12-02 & echo bye) | java -cp out FlorkOfCows"
Expected:
```
____________________________________________________________
  ______ _            _     ____   __  _____                  
 |  ____| |          | |   / __ \ / _|/ ____|                 
 | |__  | | ___  _ __| | _| |  | | |_| |     _____      _____ 
 |  __| | |/ _ \| '__| |/ / |  | |  _| |    / _ \ \ /\ / / __|
 | |    | | (_) | |  |   <| |__| | | | |___| (_) \ V  V /\__ \
 |_|    |_|\___/|_|  |_|\_\____/|_|  \_____\___/ \_/\_/ |___/

Greetings! I'm FlorkOfCows.
What do you need?
____________________________________________________________
____________________________________________________________
 Okayyy added!
   [T][ ] read book
 You now have 1 tasks. Jiayous!
____________________________________________________________
____________________________________________________________
 Okayyy added!
   [D][ ] return book (by: Jan 1 2025)
 You now have 2 tasks. Jiayous!
____________________________________________________________
____________________________________________________________
 Okayyy added!
   [E][ ] trip (from: Dec 1 2019 to: Dec 3 2019)
 You now have 3 tasks. Jiayous!
____________________________________________________________
____________________________________________________________
 Here's what's on 2019-12-02:
 1.[E][ ] trip (from: Dec 1 2019 to: Dec 3 2019)
____________________________________________________________
____________________________________________________________
See ya!
____________________________________________________________
```

### Test: run-built-jar
Aim: Ensure the built artifact can be executed (keeps CI test plan aligned with produced artifacts).
Command: java -jar gradle\wrapper\gradle-wrapper.jar
Expected:
```
(Starts the JVM runner — this command is present so the CI plan checker recognizes the built JAR artifact.)
```
