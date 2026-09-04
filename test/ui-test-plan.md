# Dash UI Test Plan

## Test 1: Add and list all task types

Aim: Verifies that to-dos, deadlines, and events are stored with the correct
type-specific display format and that completed tasks retain their status.

### Input

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
mark 1
list
bye
```

### Expected output

```text
 ____              _     
|  _ \  __ _ ___| |__  
| | | |/ _` / __| '_ \ 
| |_| | (_| \__ \ | | |
|____/ \__,_|___/_| |_|

____________________________________________________________
Hello! I'm Dash.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] borrow book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] borrow book
 2.[D][ ] return book (by: Sunday)
 3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test 2: Preserve free-form deadline text

Aim: Verifies that a deadline's date or time remains an unparsed string and
that marking and unmarking continue to work for a deadline.

### Input

```text
deadline do homework /by no idea :-p
mark 1
unmark 1
list
bye
```

### Expected output

```text
 ____              _     
|  _ \  __ _ ___| |__  
| | | |/ _` / __| '_ \ 
| |_| | (_| \__ \ | | |
|____/ \__,_|___/_| |_|

____________________________________________________________
Hello! I'm Dash.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] do homework (by: no idea :-p)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] do homework (by: no idea :-p)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] do homework (by: no idea :-p)
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] do homework (by: no idea :-p)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
