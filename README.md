# FinalProject
Final Project for object oriented programming which is a program of my choosing in which case is a program for measuring the dimensions of a piece of furniture and room.

___________________________________________________
FinalProject - Room & Furniture Measurement System
_____________________________________________________


__________________________________
Problem stated
________________________________

The dimensions of a room are needed to put furniture in, that furniture dimensions are also needed.


A Java console application that allows users to:
- Input room dimensions in **feet and inches**
- Add furniture with **length, width, height** in feet/inches
- Archive furniture when no longer needed
- Check which furniture fits in the room
- save data across sessions using files


__________
Features
___________

| Feature ----------------------------------Implementation |

| `ArrayList<Furniture>` ------------------ Current furniture list (dynamic) |
| `Furniture[]` array --------------------- Archived furniture (fixed size) |
| Recursive input validation -------------- All numeric inputs |
| Try-catch ------------------------------- File I/O, parsing, array bounds |
| files save ------------------------------ Saves/loads on start/exit |
| Menu-driven UI -------------------------- Clear numbered options |


_____________
How to Run
_____________

1. Open in GitHub Codespaces
2. Run:
   ```bash
   javac src/Main.java -d bin
   java -cp bin Main
