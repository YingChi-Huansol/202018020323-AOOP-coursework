import Controller.NumberleController;
import Model.Interface.INumberleModel;
import Model.NumberleModel;
import View.NumberleView;

public class GUIApp {
    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(
                () -> createAndShowGUI()
        );
    }

    public static void createAndShowGUI() {
        INumberleModel model = new NumberleModel();
        NumberleController controller = new NumberleController(model);
        NumberleView view = new NumberleView(model, controller);
    }
}