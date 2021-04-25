package atmClient;

import commonObject.communicate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChangePasswordController implements Initializable {
    @FXML
    private PasswordField p1;
    @FXML
    private PasswordField p2;
    @FXML
    private Label messageLabel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cleanUp();
    }

    private void cleanUp() {
        p1.setText("");
        p2.setText("");
        messageLabel.setText("");
    }

    @FXML
    private void submitClicked(ActionEvent event) {
        String s1, s2;
        s1 = p1.getText();
        s2 = p2.getText();
        if (s1.equals(s2)) {
            if (s1.length() != 4) {
                cleanUp();
                messageLabel.setText("Password length should be 4");
            } else {
                communicate c = new communicate();
                c.password = s1;
                c.requset = "changePassword";
                try {
                    main.writeToServer.writeObject(c);
                    main.writeToServer.flush();
                    Object o = main.readFromServer.readObject();
                    String reply = "Not successful";
                    if (o instanceof String) {
                        reply = (String) o;
                    }
                    System.out.println(reply);
                    cleanUp();
                    messageLabel.setText(reply);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            cleanUp();
            messageLabel.setText("Two password do not match");
        }
    }
}