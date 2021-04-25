package atmClient;


import commonObject.communicate;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class IssueCardController {

    @FXML
    private ToggleGroup cardTypeGroup;

    @FXML
    private TextField accNumField;

    @FXML
    private VBox loadBox;

    @FXML
    private ToggleGroup debitCreditGroup;

    @FXML
    private TextField nameField;

    @FXML
    private HBox buttonBox;

    @FXML
    private TextField emailField;

    @FXML
    private RadioButton debit;

    @FXML
    private RadioButton regular;

    @FXML
    private RadioButton smart;


    private String type;

    @FXML private Label messageLabel;

    @FXML
    private void cardTypeSelected(){

        if(cardTypeGroup.getSelectedToggle().equals(regular)){
            type="RegularCard";
        }else{
            type="SmartCard";
        }
        messageLabel.setText("");
        cleanUP();

    }

    public void cleanUP(){

        regular.setVisible(false);
        smart.setVisible(false);

        messageLabel.setText("");
        nameField.setText("");
        emailField.setText("");
        accNumField.setText("");

        loadBox.setVisible(true);
        buttonBox.setVisible(true);
    }

    @FXML
    private void doneSelected() throws Exception{

        if(accNumField.getText().length()!=8){

            messageLabel.setText("Invalid Account Number");
            return;
        }

        communicate c=new communicate();

        c.requset="issueCard";
        c.name=nameField.getText();
        c.email=emailField.getText();

        if(debitCreditGroup.getSelectedToggle().equals(debit)){
            c.acctype="debit";
        }else{
            c.acctype="credit";
        }
        c.accountID=accNumField.getText();
        c.cardType=type;

        cleanUP();

        main.writeToServer.writeObject(c);
        main.writeToServer.flush();

        Object object=main.readFromServer.readObject();

        if(object instanceof communicate){

            if(((communicate) object).requset.equalsIgnoreCase("YES")) {

                messageLabel.setText("");
                String fileName = "loadCardInfo" + type + ".fxml";
                VBox vBox = FXMLLoader.load(getClass().getResource(fileName));
                loadBox.getChildren().setAll(vBox);
            }
            else{

                cleanUP();
                messageLabel.setText("Invalid Account Number");
            }

        }




    }


}