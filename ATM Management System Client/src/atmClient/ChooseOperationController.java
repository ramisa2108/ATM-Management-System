package atmClient;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ChooseOperationController implements Initializable {


    @FXML
    private VBox loadBox;

    @FXML
    private Label welcomeLabel;

    @FXML
    private BorderPane root;

    public void checkBalance() throws Exception {

        main.getUpdates=false;
        VBox vBox = FXMLLoader.load(getClass().getResource("balance.fxml"));
        loadBox.getChildren().setAll(vBox);
    }

    public void accountInfo() throws Exception {

        main.getUpdates=false;
        VBox vBox = FXMLLoader.load(getClass().getResource("accInfo.fxml"));
        loadBox.getChildren().setAll(vBox);

    }

    public void transferMoney() throws Exception {

        main.getUpdates=false;
        VBox vBox = FXMLLoader.load(getClass().getResource("transfer.fxml"));
        loadBox.getChildren().setAll(vBox);


    }

    public void withdrawMoney() throws Exception {

        main.getUpdates=false;
        VBox vBox = FXMLLoader.load(getClass().getResource("withdraw.fxml"));
        loadBox.getChildren().setAll(vBox);
    }

    @FXML
    private void buttonBackClicked() throws Exception{

        main.getUpdates=false;
        main.writeToServer.writeObject("getCardType");
        main.writeToServer.flush();
        String cardType=main.readFromServer.readUTF();

        if(cardType.equalsIgnoreCase("RegularCard")){

            MyAlertBox alertBox=new MyAlertBox("End session?");

            if(alertBox.display()){
                BorderPane borderPane=FXMLLoader.load(getClass().getResource("ChooseBankOrCustomer.fxml"));
                root.getChildren().setAll(borderPane);
            }

        }else{

            BorderPane borderPane=FXMLLoader.load(getClass().getResource("SmartCardBankChooser.fxml"));
            root.getChildren().setAll(borderPane);
        }


    }
    @FXML
    private void changePassword() throws  Exception{

        main.getUpdates=false;
        VBox vBox = FXMLLoader.load(getClass().getResource("ChangePassword.fxml"));
        loadBox.getChildren().setAll(vBox);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources)  {

        try {

            main.writeToServer.writeObject("getBankName");
            main.writeToServer.flush();
            String bankName = main.readFromServer.readUTF();
            welcomeLabel.setText("WELCOME TO YOUR "+bankName.toUpperCase()+" ACCOUNT!");

        }catch (Exception e){

            System.out.println(e.getClass()+" "+e.getMessage());
        }

    }
}
