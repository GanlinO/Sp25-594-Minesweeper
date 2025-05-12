import java.util.ArrayList;

// The Controller class manages communication between the view (GUI) and the model components.
// It follows the MVC (Model-View-Controller) architecture to handle user inputs and updates.
public class Controller implements ViewGUIToController {
	
	private ControllerToViewGUI myView;
	private ControllerToModel myModel;
	
// Constructor initializes the model and view components.
	// If either component fails to initialize, the program exits with an error.
	public Controller() {
		myModel = new Model();
		myView = new ViewGUI(this);
		if(myModel == null || myView == null)
			System.exit(NULL_EXIT_CODE);
	}
	// Starts the game by initializing the GUI with the list of available difficulties from the model.
	public void go() {
		if(myModel == null || myView == null)
			System.exit(NULL_EXIT_CODE);
		myView.go(myModel.getDifficulties());
	}
	// Instructs the model to set the current game difficulty to the specified value.
	public void setDifficulty(String difficulty) {
		if(myModel == null)
			System.exit(NULL_EXIT_CODE);
		myModel.setDifficulty(difficulty);
	}
		// Retrieves the list of preset difficulty levels from the model.
	public ArrayList<String> getDifficulties() {
		if(myModel == null)
			System.exit(NULL_EXIT_CODE);
		return myModel.getDifficulties();
	}
