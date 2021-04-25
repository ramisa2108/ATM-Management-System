package atmClient;

import commonObject.communicate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class DepositController   {

    @FXML
    private TextField amountInput;
    @FXML
    private Label messageLabel;

    @FXML
    public void buttonClicked(ActionEvent event) throws  Exception{

        if(amountInput.getText().equalsIgnoreCase(""))
        {
            messageLabel.setText("Enter An Amount");
            return;
        }

            String text="Are you sure you want to deposit the amount?";
            MyAlertBox myAlertBox=new MyAlertBox(text);

            if(myAlertBox.display()) {

                communicate c=new communicate();
                c.requset="changeBalance";
                c.amount=Double.parseDouble(amountInput.getText());

                main.writeToServer.writeObject(c);
                main.writeToServer.flush();

                messageLabel.setText(c.amount + " Taka Deposited To ATM");
                amountInput.setText("");

            }
            amountInput.setText("");


    }


}
