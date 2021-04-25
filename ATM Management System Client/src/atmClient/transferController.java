package atmClient;

import commonObject.communicate;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class transferController  {

    @FXML
    private Label messageLabel;
    @FXML
    private TextField accountNumINput;
    @FXML
    private TextField amountInput;


    public void initial(){

        messageLabel.setText("");
        amountInput.setText("");
        accountNumINput.setText("");
    }

    public void buttonClicked(){

            if(accountNumINput.getText().length()!=8){

                initial();
                messageLabel.setText("Account Number Must Have 8 Digits");

            }
            else if(amountInput.getText().length()<1){

                initial();
                messageLabel.setText("Enter An Amount!");
            }
            else {

                try {

                    communicate c=new communicate();
                    c.requset="transfer";
                    c.amount=Double.parseDouble(amountInput.getText());
                    c.accountID=accountNumINput.getText();
                    main.writeToServer.writeObject(c);
                    main.writeToServer.flush();

                    initial();
                    String message= main.readFromServer.readUTF();

                    messageLabel.setText(message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

}







