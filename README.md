# Minesweeper Project Documentation

## Overview
This project implements a classic Minesweeper game using Java Swing for the graphical user interface. The application follows the Model-View-Controller (MVC) architectural pattern to separate concerns and improve maintainability.

## Project Structure

### Main Components

#### Model
The `Model` class represents the game state and logic, maintaining the locations of mines, player actions, and game status.

#### View
The `ViewGUI` class and its supporting components handle the user interface, displaying the game grid, timer, mine counter, and other UI elements.

#### Controller
The `Controller` class mediates between the model and view, processing user input from the view and updating the model accordingly.

## Class Documentation

### Controller Classes

#### Controller.java
```java
/**
 * Controls communication between the Model and View components.
 * Implements the ViewGUIToController interface to handle requests from the View
 * and communicates with the Model through the ControllerToModel interface.
 */
public class Controller implements ViewGUIToController {
    // Class implementation
}
```
#### ControllerToModel.java
```java
/**
* Interface implemented by the Model and used by the Controller.
* Defines methods for modifying the game state and retrieving information
* about the current game.
  */
  public interface ControllerToModel {
  // Interface methods
  }
  ```


  #### ControllerToViewGUI.java
  ```java
  /**
* Interface implemented by the ViewGUI and used by the Controller.
* Defines methods for updating the UI and handling user input.
  */
  public interface ControllerToViewGUI {
  // Interface methods
  }
  ```

  #### ViewGUIToController.java
  ```java
  /**
* Interface implemented by the Controller for communication from the ViewGUI.
* Defines methods that allow the View to request changes to the game state
* and retrieve information.
  */
  public interface ViewGUIToController {
  // Interface methods
  }
  ```

  ### Model Classes
  #### Model.java
  ``` java
  /**
* Maintains the game state and implements game logic.
* Handles the creation of the mine grid, processes player actions,
* and determines game outcomes.
* Implements the ControllerToModel interface to communicate with the Controller.
  */
  public class Model implements ControllerToModel {
  // Class implementation
  }
  ```

  ### View Classes
  #### ViewGUI.java
  ``` java
  /**
* Main View class that manages all UI components.
* Creates and manages frames for game start, gameplay, and end game screens.
* Implements ControllerToViewGUI to receive updates from the Controller.
  */
  public class ViewGUI implements ControllerToViewGUI {
  // Class implementation
  }
  ```

  #### ViewStartFrame.java
  ```java
  /**
* Initial frame displayed when starting the game.
* Allows the player to select difficulty level, customize game settings,
* and toggle options like extra lives and logical mode.
  */
  public class ViewStartFrame extends JFrame {
  // Class implementation
  }
  ```
  #### ViewGameTilesFrame.java
  ```java
  /**
* Main game frame displaying the Minesweeper grid.
* Contains the mine grid as buttons, timer, mine counter, and menu bar.
* Handles the visual representation of the game during play.
  */
  public class ViewGameTilesFrame extends JFrame {
  // Class implementation
  }
  ```

  #### ViewEndFrame.java
  ``` java
  /**
* Frame displayed at the end of a game.
* Shows game results, statistics, best times, and options to play again
* or exit the game.
  */
  public class ViewEndFrame extends JFrame {
  // Class implementation
  }
  ```

  #### ViewPopupHelp.java
  ``` java
  /**
* Creates popup windows to display help text or messages to the user.
* Used for showing game rules and error messages.
  */
  public class ViewPopupHelp extends JFrame {
  // Class implementation
  }
  ```

  ### Event Listeners
  #### ViewButtonClickListener.java
  ```java
  /**
* ActionListener for button clicks in the view.
* Handles user interactions with buttons such as Play, Exit, and Play Again.
  */
  public class ViewButtonClickListener implements ActionListener {
  // Class implementation
  }
  ```

  #### ViewCheckBoxListener.java
  ```java
  /**
* ItemListener for checkbox state changes.
* Handles toggling of options like extra lives and logical mode.
  */
  public class ViewCheckBoxListener implements ItemListener {
  // Class implementation
  }
  ```

  #### ViewMenuListener.java
  ```java
  /**
* ActionListener for the main game frame's menu bar.
* Handles menu selections like Exit, Play Different Game, and Display Rules.
  */
  public class ViewMenuListener implements ActionListener {
  // Class implementation
  }
  ```

  #### ViewMouseListener.java
  ```java
  /**
* MouseAdapter for handling mouse clicks on game tiles.
* Differentiates between left clicks (reveal tile) and right clicks (place flag).
  */
  public class ViewMouseListener extends MouseAdapter {
  // Class implementation
  }
  ```

  #### ViewRadioButtonListener.java
  ```java
  /**
* ActionListener for radio button changes.
* Handles selection of difficulty levels and extra lives options.
  */
  public class ViewRadioButtonListener implements ActionListener {
  // Class implementation
  }
  ```

  #### ViewSpinnerListener.java
  ```java
  /**
* ChangeListener for spinner value changes in the custom difficulty settings.
* Updates the model with new row, column, and mine count values.
  */
  public class ViewSpinnerListener implements ChangeListener {
  // Class implementation
  }
  ```
  
  #### ViewTimerActionListener.java
  ```java
  /**
* ActionListener for the game timer.
* Triggers time increments every second during gameplay.
  */
  public class ViewTimerActionListener implements ActionListener {
  // Class implementation
  }
  ```

  #### ViewHintListener.java
  ```java
  /**
* ActionListener for the hint button.
* Activates the hint feature to help the player by suggesting a mine location.
  */
  public class ViewHintListener implements ActionListener {
  // Class implementation
  }
  ```

  ### Main Application
  #### PlayMinesweeper.java
  ```java
  /**
* Entry point for the Minesweeper application.
* Creates a Controller instance and starts the game.
  */
  public class PlayMinesweeper {
  // Class implementation
  }
  ```

  ## Features
  ### Game Difficulties

* Beginner: 10 mines on a 9×9 grid
* Intermediate: 40 mines on a 16×16 grid
* Expert: 99 mines on a 16×30 grid
* Custom: User-defined grid size and mine count

### Gameplay

* Left-click to reveal a tile
* Right-click to place or remove a flag
* Numbers indicate adjacent mines
* Empty tiles automatically reveal connected empty areas

### Additional Features

* Extra Lives: Option to have 1-3 extra lives, allowing the player to survive hitting mines
* Logical Mode: Ensures the game is solvable through logical deduction without requiring guessing
* Hint System: Provides suggestions by highlighting a mine's location
* AI Solver: Automatically solves the game using logical deduction

### Statistics

* Time tracking
* Games played/won counter
* Best times for each difficulty level

### Usage Guide
#### Starting a Game

1. Launch the application
2. Select a difficulty level or customize your own settings
3. Toggle extra lives or logical mode if desired
4. Click "Play" to begin

#### During Gameplay
* Left-click tiles to reveal them
* Right-click to place flags on suspected mines
* The top panel shows mines remaining, time elapsed, and lives (if enabled)
* Use the hint button for assistance when stuck
* In logical mode, use the AI solver to automatically complete the game

#### Game End

* Upon winning or losing, stats are displayed
* Choose to play again or exit

### Design Patterns

#### Model-View-Controller (MVC)
| Role | Classes | Responsibility |
|------|---------|----------------|
| **Model** | `Model` | Stores the board, mines, timers, best-time stats; enforces all game rules. |
| **View**  | `ViewGUI` plus helper frames (`ViewStartFrame`, `ViewGameTilesFrame`, `ViewEndFrame`, `ViewPopupHelp`) | Renders the game state and collects user input via Swing widgets. |
| **Controller** | `Controller` | Receives UI events, calls the model, then updates the view through `ControllerToViewGUI` |

MVC decouples game logic from Swing, making head-less unit testing and future UI swaps (CLI, web) straightforward.

---

#### Strategy
The project varies certain behaviours at runtime rather than with `if/else` trees:

* **Difficulty strategy** – `Model#setDifficulty("beginner" | "intermediate" | "expert" | "custom")` plugs different constants (rows, columns, mines) into a single board-generation algorithm.
* **Extra-lives & logical-solver modes** – `Model#setExtraLives(int)` and `Model#setLogicalMode(boolean)` toggle alternative rule paths (life consumption, deterministic solver) without changing the click engine.

Treating these rule sets as strategies keeps the core algorithms reusable and makes it easy to add new game variants.

## Implementation Notes
### Logical Mode
The logical mode feature ensures that every game is solvable through pure logic without requiring guesswork. This is achieved by:
1. Generating a random board
2. Testing if the board can be solved logically
3. Regenerating the board if logical solving is not possible
4. Pre-revealing a connected area of empty cells to start the game

### AI Solver
The AI solver uses deterministic logical rules to:
1. Identify certain mine locations
2. Flag those mines
3. Propagate consequences of each flagged mine
4. Continue until the board is solved or no more logical deductions can be made

