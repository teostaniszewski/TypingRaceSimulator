# TypingRaceSimulator

Object Oriented Programming Project — ECS414U

## Project Structure

```
TypingRaceSimulator/
├── Part1/    # Textual simulation (Java, command-line)
│   ├── Main.java
│   ├── TypingRace.java
│   ├── Typist.java
│   └── TypistTest.java
└── Part2/    # GUI simulation (Java Swing)
    ├── TypingRaceGUI.java
    └── Typist.java
```

## Part 1 — Textual Simulation

### How to compile

```bash
cd Part1
javac *.java
```

### How to run

The race is started through `Main.java`, which creates a `TypingRace` object, adds three typists, and calls `startRace()`.

```java
public static void main(String[] args) {
    TypingRace race = new TypingRace(40);
    race.addTypist(new Typist('1', "TURBOFINGERS", 0.85), 1);
    race.addTypist(new Typist('2', "QWERTY_QUEEN",  0.60), 2);
    race.addTypist(new Typist('3', "HUNT_N_PECK",   0.30), 3);
    race.startRace();
}
```

Then run:

```bash
java Main
```

### How to test

`TypistTest.java` can be used to test the `Typist` class separately from the full race.

It tests:
- `slideBack()` so progress does not go below 0
- `burnOut()` and `recoverFromBurnout()`
- `resetToStart()`
- `setAccuracy()` clamping between 0.0 and 1.0
- `typeCharacter()` forward movement
- getter methods such as `getProgress()`, `getAccuracy()`, and `isBurntOut()`

Run the tests with:

```bash
java TypistTest
```

## Part 2 — GUI Simulation

Part 2 contains the graphical version of the typing race simulator. It is implemented using Java Swing.

The graphical version allows the user to:
- Choose a passage or enter a custom passage
- Select the number of racers
- Customise typists using typing styles, keyboard types, symbols, colours, sponsors, and accessories
- Enable difficulty modifiers such as Autocorrect, Caffeine Mode, and Night Shift
- Start an animated race
- View race statistics, leaderboard data, race history, comparisons, and charts
- Earn coins and buy upgrades in the shop

### How to compile

Because the Part 2 files use the package declaration `package Part2;`, compile Part 2 from the root `TypingRaceSimulator` folder:

```bash
javac Part2/*.java
```

### How to run

From the root `TypingRaceSimulator` folder, run:

```bash
java Part2.TypingRaceGUI
```

If you are already inside the `Part2` folder, go back to the root first:

```bash
cd ..
javac Part2/*.java
java Part2.TypingRaceGUI
```

The GUI can also be started through the `main` method inside `TypingRaceGUI.java`:

```java
public static void main(String[] args) {
    new TypingRaceGUI();
}
```

## Core Race Mechanics

- Typists move forward when they type correctly.
- Mistypes cause typists to slide backwards.
- Burnout temporarily stops a typist from typing.
- Accuracy affects the chance of typing successfully.
- The race finishes when a typist reaches or passes the end of the passage.
- Winners receive a small accuracy improvement.

## Dependencies

- Java Development Kit (JDK) 11 or higher
- Java Swing, included in the standard JDK
- No external libraries required

## Git Integration

The project was managed using Git.

- `main` contains the final stable version.
- `gui-development` was used for Part 2 GUI development.
- After completing the GUI, `gui-development` was merged back into `main`.

## Notes

- All code should compile and run using standard command-line tools without any IDE-specific configuration.
- The starter code in Part 1 was originally written by Ty Posaurus and contained issues that needed to be fixed.
- The completed version includes slide-back behaviour, burnout recovery, accuracy clamping, race winner detection, GUI configuration, statistics, rewards, and analytics.

## Author

Teo Staniszewski
