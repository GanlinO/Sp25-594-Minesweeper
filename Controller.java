import java.util.ArrayList;
//manages communication between the view and the model components
//(not REALLY needed, but could be helpful)
public class Controller implements ViewGUIToController{
	
	private ControllerToViewGUI myView;
	private ControllerToModel myModel;
	
	public Controller()
