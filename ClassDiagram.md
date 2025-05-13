```mermaid
classDiagram
    %% ─────────── Interfaces ───────────
    class ControllerToModel
    class ControllerToViewGUI
    class ViewGUIToController
    <<interface>> ControllerToModel
    <<interface>> ControllerToViewGUI
    <<interface>> ViewGUIToController

    %% ─────────── Core classes ─────────
    class Controller {
        - ControllerToModel   myModel
        - ControllerToViewGUI myView
        + startGame() : boolean
        + tilePressed(r:int, c:int, t:long)
        + placeFlag(flag:bool, r:int, c:int)
        + reset()
        + setDifficulty(level:String)
    }
    Controller --|> ViewGUIToController        %% realises interface

    class Model {
        + startGame() : boolean
        + tilePressed(r:int, c:int, t:long) : boolean[][]
        + tileFlagged(flag:bool, r:int, c:int)
        + playerLost() : boolean
        + playerWon()  : boolean
        + setDifficulty(level:String)
    }
    Model --|> ControllerToModel

    class ViewGUI {
        - ViewStartFrame      startframe
        - ViewGameTilesFrame  gameframe
        - ViewEndFrame        endframe
        + playGame()
        + playAgain()
        + exitGame()
        + tilePressed(cmd:String)
        + showExtraLives()
        + setCustom(spinner:JSpinner)
        + hint()
    }
    ViewGUI --|> ControllerToViewGUI
    ViewGUI ..> ViewGUIToController             %% callback / uses

    %% ───────── Swing frames (views) ─────────
   class ViewStartFrame
    class ViewGameTilesFrame
    class ViewEndFrame
    class ViewPopupHelp
    <<JFrame>> ViewStartFrame
    <<JFrame>> ViewGameTilesFrame
    <<JFrame>> ViewEndFrame
    <<JFrame>> ViewPopupHelp

    ViewGUI --> ViewStartFrame
    ViewGUI --> ViewGameTilesFrame
    ViewGUI --> ViewEndFrame
    ViewGUI --> ViewPopupHelp

    ViewStartFrame      --> ViewGUI
    ViewGameTilesFrame  --> ViewGUI
    ViewEndFrame        --> ViewGUI
    ViewPopupHelp       --> ViewGUI

    %% ───────── Listener (Observer) classes ─────────
    class ViewButtonClickListener
    class ViewCheckBoxListener
    class ViewMenuListener
    class ViewMouseListener
    class ViewRadioButtonListener
    class ViewSpinnerListener
    class ViewTimerActionListener
    class ViewHintListener

    ViewButtonClickListener   --> ViewGUI
    ViewCheckBoxListener      --> ViewGUI
    ViewMenuListener          --> ViewGUI
    ViewMouseListener         --> ViewGUI
    ViewRadioButtonListener   --> ViewGUI
    ViewSpinnerListener       --> ViewGUI
    ViewHintListener          --> ViewGUI
    ViewTimerActionListener   --> ViewGameTilesFrame

    %% ─────────── Relationships ──────────
    Controller  ..>  ControllerToModel
    Controller  ..>  ControllerToViewGUI
```
