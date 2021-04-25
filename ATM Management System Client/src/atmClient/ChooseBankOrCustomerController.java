
package atmClient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ChooseBankOrCustomerController implements Initializable {

    @FXML
    private Button butttonAsACustomer;
    @FXML
    private Button buttonAsABank;
    @FXML
    private Button buttonHelp;

    @FXML
    private Button buttonExit;

    @FXML
    public static  BorderPane root;

    @FXML
    public  VBox buttonBox;

    @FXML
    public  VBox loadBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {

            main.logInType="customer";
            VBox vbox= FXMLLoader.load(getClass().getResource("mainPrompt.fxml"));
            loadBox.getChildren().setAll(vbox);

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    @FXML
    void buttonClicked(ActionEvent event){

        Button source=(Button) (event.getSource());

        if(source.equals(butttonAsACustomer)){

            try {

                main.logInType="customer";
                VBox vbox= FXMLLoader.load(getClass().getResource("mainPrompt.fxml"));
                loadBox.getChildren().setAll(vbox);

            } catch (Exception e) {

                e.printStackTrace();
            }

        }

        else if(source.equals(buttonAsABank)){

            try {
                main.logInType = "bank";
                VBox vBox = FXMLLoader.load(getClass().getResource("mainPrompt.fxml"));
                loadBox.getChildren().setAll(vBox);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else if(source.equals(buttonHelp)) {
            try {
                VBox vBox = FXMLLoader.load(getClass().getResource("Help.fxml"));
                loadBox.getChildren().setAll(vBox);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else if(source.equals(buttonExit)){

            try{
                main.writeToServer.writeObject("EXIT");
                main.writeToServer.flush();
                main.socket.close();
                System.exit(0);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }


}
