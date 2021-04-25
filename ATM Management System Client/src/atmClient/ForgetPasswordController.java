package atmClient;

import commonObject.communicate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ForgetPasswordController implements Initializable {
    @FXML
    private TextField cardID;
    @FXML
    private TextField email;
    @FXML
    private Label label;
    @FXML
    private  VBox root;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cleanUp();
    }

    private void cleanUp()
    {
        cardID.setText("");
        email.setText("");
        label.setText("");
    }

    @FXML void sendPin(ActionEvent event){

        communicate c=new communicate();
        c.requset="forgetPin";
        c.cardID=cardID.getText();
        c.email=email.getText();
        try {
            main.writeToServer.writeObject(c);
            Object o=main.readFromServer.readObject();

            String reply="Wrong ID-email combination ";
            if(o instanceof String ){
                reply=(String) o;
            }
            cleanUp();
            label.setText(reply);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void Back(ActionEvent e) throws IOException {
        main.logInType="customer";
        VBox loadBox= FXMLLoader.load(getClass().getResource("mainPrompt.fxml"));
        root.getChildren().setAll(loadBox);
    }


}