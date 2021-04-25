package atmClient;

import commonObject.communicate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class changeInfoController implements Initializable {


    @FXML
    private ToggleGroup toggleGroupAccountType;

    @FXML
    private RadioButton savingsButton;

    @FXML
    private RadioButton currentButton;

    @FXML
    private Button update;

    @FXML
    private TextField textFieldTransfer;

    @FXML
    private ToggleGroup toggleGroupActiveStatus;

    @FXML
    private TextField textFieldNationalID;

    @FXML
    private TextField textFieldName;

    @FXML
    private TextField textFieldMobileNo;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private VBox root;

    @FXML
    private TextField textFieldWithdraw;

    @FXML
    private RadioButton activeButton;

    @FXML
    private RadioButton inactiveButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        communicate cc = new communicate();
        cc.requset = "accountInfo";
        try {
            main.writeToServer.writeObject(cc);
            main.writeToServer.flush();

            Object object = main.readFromServer.readObject();

            if (object instanceof communicate) {

                communicate in = (communicate) object;
                textFieldName.setText(in.name);
                textFieldEmail.setText(in.email);
                textFieldMobileNo.setText(in.mobileNo);
                textFieldNationalID.setText(in.nationalID);
                if (in.activeStatus.equalsIgnoreCase("active")) {
                    activeButton.setSelected(true);
                } else {
                    inactiveButton.setSelected(true);
                }
                if (in.acctype.equalsIgnoreCase("current")) {
                    currentButton.setSelected(true);
                } else {
                    savingsButton.setSelected(true);
                }
                textFieldTransfer.setText(in.transfer.toString());
                textFieldWithdraw.setText(in.withdraw.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void buttonChangeClicked(ActionEvent event) throws Exception {

        communicate c=new communicate();
        c.requset="changeAccountInfo";

        c.name=textFieldName.getText();
        c.nationalID=textFieldNationalID.getText();
        c.email=textFieldEmail.getText();
        c.mobileNo=textFieldMobileNo.getText();

        if (toggleGroupAccountType.getSelectedToggle().equals(currentButton)) {
            c.acctype = "Current";
        } else {
            c.acctype = "Savings";
        }
        if(toggleGroupActiveStatus.getSelectedToggle().equals(activeButton)){
            c.activeStatus="active";
        }
        else{
            c.activeStatus="inactive";
        }
        c.withdraw= Double.parseDouble(textFieldWithdraw.getText());
        c.transfer=Double.parseDouble(textFieldTransfer.getText());

        MyAlertBox myAlertBox=new MyAlertBox("Confirm Changes?");
        if(myAlertBox.display()){

            main.writeToServer.writeObject(c);
            main.writeToServer.flush();

            VBox vBox= FXMLLoader.load(getClass().getResource("accInfo.fxml"));
            root.getChildren().setAll(vBox);
            root.setVisible(true);
            update.setVisible(false);


        }

    }
}
