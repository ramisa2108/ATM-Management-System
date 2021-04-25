package atmClient;

import commonObject.communicate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;


public class CreateNewAccountController extends ToggleGroup  {

    @FXML
    private TextField textFieldName;

    @FXML
    private TextField textFieldBalance;

    @FXML
    private ToggleGroup toggleGroupAccountType;

    @FXML
    private TextField textFieldMobileNo;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private TextField textFieldWithdraw;

    @FXML
    private TextField textFieldTransfer;


    @FXML
    private TextField textFieldNationalID;
    @FXML
    private RadioButton currentButton;


    @FXML
    private VBox root;


    @FXML
    private void buttonCreateAccountClicked(ActionEvent event) throws Exception {

        communicate c=new communicate();
        c.requset="createNewAccount";

        c.name=textFieldName.getText(); c.nationalID=textFieldNationalID.getText();
        c.email=textFieldEmail.getText(); c.mobileNo=textFieldMobileNo.getText();
        c.amount=Double.parseDouble(textFieldBalance.getText()); c.activeStatus="active";
        c.transfer=Double.parseDouble(textFieldTransfer.getText());
        c.withdraw= Double.parseDouble(textFieldWithdraw.getText());

        if(toggleGroupAccountType.getSelectedToggle().equals(currentButton)){
            c.acctype="Current";
        }
        else{
            c.acctype="Savings";
        }

        MyAlertBox myAlertBox=new MyAlertBox("Confirm Request?");
        if(myAlertBox.display()) {
            try {

                main.writeToServer.writeObject(c);
                main.writeToServer.flush();
                VBox vbox=FXMLLoader.load(getClass().getResource("accInfo.fxml"));
                root.getChildren().setAll(vbox);

            } catch (Exception e) {
                System.out.println(e.getClass() + " " + e.getMessage());
            }
        }

    }

}
