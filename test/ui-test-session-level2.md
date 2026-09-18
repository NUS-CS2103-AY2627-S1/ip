# Historical verification

**Tested tag:** Level-2 (`ee72dd6`)
# UI test session

## 1. Store and list user-entered task text

**Aim:** Confirm that entered task text is kept in memory and shown in order when the user requests `list`.

**Command:**
```sh
javac -d /tmp/oreo-level2-classes src/main/java/*.java && java -cp /tmp/oreo-level2-classes Oreo
```

**Console input:**
```text
read book
return book
list
bye
```

**Expected output:**
```text
____________________________________________ 
  OOO   RRRR   EEEEE  OOO  
 O   O  R   R  E     O   O 
 O   O  RRRR   EEEE  O   O 
 O   O  R R    E     O   O 
  OOO   R  RR  EEEEE  OOO  

Hello! I'm Oreo. 
Let's get started shall we? 
____________________________________________
____________________________________________ 
added: read book 
____________________________________________ 

____________________________________________ 
added: return book 
____________________________________________ 

____________________________________________
1. read book
2. return book
____________________________________________
____________________________________________ 
Good work. See you next time! 
____________________________________________ 


```

**Actual output:**
```text
____________________________________________ 
  OOO   RRRR   EEEEE  OOO  
 O   O  R   R  E     O   O 
 O   O  RRRR   EEEE  O   O 
 O   O  R R    E     O   O 
  OOO   R  RR  EEEEE  OOO  

Hello! I'm Oreo. 
Let's get started shall we? 
____________________________________________
____________________________________________ 
added: read book 
____________________________________________ 

____________________________________________ 
added: return book 
____________________________________________ 

____________________________________________
1. read book
2. return book
____________________________________________
____________________________________________ 
Good work. See you next time! 
____________________________________________ 


```

**Exit status:** `0`

**Result:** PASS

All 1 test case(s) passed.
