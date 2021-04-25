package atmClient;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import commonObject.communicate;
import java.net.URL;
import java.util.ResourceBundle;

public class addAccountController implements Initializable {

    @FXML
    private TextField cardInput,accountInput;
    @FXML
    private Label welcomeLabel,messageLabel;
    @FXML
    private VBox loadBox;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        welcomeLabel.setText("Add Existing Accounts To Card");
    }

    @FXML
    private void addClicked() throws Exception{

        if(accountInput.getText().length()!=8 || cardInput.getText().length()!=8){

            messageLabel.setText("Invalid Input");
            accountInput.setText(""); cardInput.setText("");
            return ;
        }

        communicate c=new communicate();
        c.requset="addAccountToCard";
        c.accountID=accountInput.getText();
        c.cardID=cardInput.getText();

        main.writeToServer.writeObject(c);
        main.writeToServer.flush();

        Object object=main.readFromServer.readObject();

        if(object instanceof communicate){

            communicate cc=(communicate) object;

            if(cc.requset.equalsIgnoreCase("YES")){

                messageLabel.setText("Account Added To Card");
                welcomeLabel.setText("");

                String fileName="loadCardInfo"+cc.cardType+".fxml";
                VBox vBox= FXMLLoader.load(getClass().getResource(fileName));
                loadBox.getChildren().setAll(vBox);

            }
            else{

                messageLabel.setText(cc.requset);
                accountInput.setText(""); cardInput.setText("");
            }
        }


    }
}
