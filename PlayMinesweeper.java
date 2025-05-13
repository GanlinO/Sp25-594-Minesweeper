/**
 * The entry point for the Minesweeper application.
 * Creates a Controller instance and starts the game.
 */
public class PlayMinesweeper {

    /**
     * Main method to launch the Minesweeper game.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args)
    {
        Controller myController = new Controller();
        if(myController!=null)
            myController.go();
    }
}

