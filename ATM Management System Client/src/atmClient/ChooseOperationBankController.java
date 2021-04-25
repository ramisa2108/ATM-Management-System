package atmClient;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ChooseOperationBankController implements Initializable {

    @FXML
    private BorderPane root;
   @FXML
   private Label WelcomeLabel;
   @FXML
   private VBox loadBox;

   private String bankName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try{

            main.writeToServer.writeObject("getBankName");
            main.writeToServer.flush();

            bankName=main.readFromServer.readUTF();
            WelcomeLabel.setText("WELCOME TO "+bankName.toUpperCase()+" SETTINGS");

        }catch (Exception e){
            System.out.println(e.getClass()+" "+e.getMessage());
        }

    }


    public void buttonCardRelatedClicked() {

        try {
            BorderPane borderPane = FXMLLoader.load(getClass().getResource("cardOption.fxml"));
            root.getChildren().setAll(borderPane);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buttonAccountRelatedClicked() {

        try {

            BorderPane borderPane = FXMLLoader.load(getClass().getResource("accountOption.fxml"));
            root.getChildren().setAll(borderPane);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void buttonDepositClicked(){

        try {

            WelcomeLabel.setText("DEPOSIT MONEY FROM "+bankName.toUpperCase());
            VBox vBox = FXMLLoader.load(getClass().getResource("Deposit.fxml"));
            loadBox.getChildren().setAll(vBox);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    public void buttonBackClicked() {

        String text="Are you sure you want to return?";
        MyAlertBox myAlertBox=new MyAlertBox(text);
        if(myAlertBox.display()) {

            try {
                BorderPane borderPane = FXMLLoader.load(getClass().getResource("ChooseBankOrCustomer.fxml"));
                root.getChildren().setAll(borderPane);
            } catch (Exception e) {

                System.out.println(e.getClass() + " " + e.getMessage());
            }
        }
    }
}