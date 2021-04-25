package atmClient;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import commonObject.communicate;
import java.net.URL;
import java.util.ResourceBundle;

public class accInfoController implements Initializable {

    @FXML
    private Label accNum;

    @FXML
    private Label accName;

    @FXML
    private Label mobileNo;

    @FXML
    private Label accBalance;

    @FXML
    private Label accType;

    @FXML
    private Label email;

    @FXML
    private Label withdraw;

    @FXML
    private Label transfer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        main.getUpdates=true;

        Thread update=new Thread( ()->{
            while(main.getUpdates) {
                Platform.runLater(() -> {
                    try {


                        communicate c = new communicate();
                        c.requset = "accountInfo";
                        main.writeToServer.writeObject(c);
                        main.writeToServer.flush();

                        Object object = main.readFromServer.readObject();

                        if (object instanceof communicate) {

                            communicate in = (communicate) object;

                            accName.setText("Name: " + in.name);
                            accNum.setText("Account Number: " + in.accountID);
                            accBalance.setText("Balance: " + in.amount);
                            mobileNo.setText("Mobile No: " + in.mobileNo + "       National ID: " + in.nationalID);
                            email.setText("Email: " + in.email);
                            accType.setText("Account Type: " + in.acctype + "        Active Status: " + in.activeStatus);
                            withdraw.setText("Maximum Withdraw : " + in.withdraw);
                            transfer.setText("Maximum Transfer : " + in.transfer);

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
                try {
                    Thread.sleep(5000);
                }catch (Exception e){
                    System.out.println(e.getClass()+" "+e.getMessage());
                }
            }
        }
        );
        update.setDaemon(true);
        update.start();
    }


}