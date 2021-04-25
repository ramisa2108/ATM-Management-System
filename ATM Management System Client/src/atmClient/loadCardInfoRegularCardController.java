package atmClient;

import commonObject.communicate;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;


public class loadCardInfoRegularCardController implements Initializable {

    @FXML
    private Label accNumField;

    @FXML
    private Label cardTypeField;

    @FXML
    private Label bankNameField;

    @FXML
    private Label nameField;

    @FXML
    private Label emailField;

    @FXML
    private Label cardNumField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        communicate c=new communicate();
        c.requset="getCardInfo";
        c.cardType="RegularCard";

        try{
            main.writeToServer.writeObject(c);
            main.writeToServer.flush();

            Object object=main.readFromServer.readObject();

            if(object instanceof communicate){

                communicate cc=(communicate) object;

                cardNumField.setText("Card ID: "+cc.cardID);
                cardTypeField.setText("Card Type: "+cc.cardType);
                nameField.setText("User Name: "+cc.name);
                emailField.setText("Email: "+cc.email);
                accNumField.setText("Account Number: "+cc.accountID);
                bankNameField.setText("Bank Name: "+cc.bankName);
            }


        }catch (Exception e){
            System.out.println(e.getClass()+" "+e.getMessage());
        }

    }
}
