package atmClient;

import commonObject.communicate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class withdrawController implements Initializable {

    @FXML
    private Label messageLabel;
    @FXML
    private TextField amountInput;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initial();
    }

    public void initial() {

        amountInput.setText("");
        messageLabel.setText("");

    }

    public void buttonClicked(ActionEvent event) throws Exception {

        communicate c=new communicate();
        c.requset="withdraw";
        if(amountInput.getText().equalsIgnoreCase("")) c.amount=0.0;
        else c.amount=Double.parseDouble(amountInput.getText());

        main.writeToServer.writeObject(c);
        main.writeToServer.flush();
        String message=main.readFromServer.readUTF();

        messageLabel.setText(message);
        amountInput.setText("");

    }

}
