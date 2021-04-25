package atmClient;


import commonObject.communicate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class mainPromptController implements Initializable {

    @FXML
    private TextField cardIDInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private Label wrongInput;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Button forgetButton;
    @FXML
    private VBox root;

    private String cardID, password;

    @FXML
   public void buttonClicked(ActionEvent event){

            wrongInput.setText("");
            cardID=cardIDInput.getText();
            password=passwordInput.getText();

            if (password.length() == 4 && cardID.length() == 8) {

                communicate c=new communicate();
                c.requset="Login";
                c.cardID=cardID;
                c.loginType=main.logInType;
                c.password=password;

                try{

                    main.writeToServer.writeObject(c);
                    main.writeToServer.flush();

                    Object object=main.readFromServer.readObject();

                    if(object instanceof communicate && ((communicate) object).requset.equalsIgnoreCase("Logged in")){

                        try {
                            changeScene(((communicate) object).cardType);
                        } catch (Exception e) {
                            System.out.println(e.getClass() + " " + e.getMessage());
                        }

                    } else {
                        wrongInput.setText("Wrong Card Number Or Pin");
                    }
                }catch (Exception e){
                    System.out.println("problem in reading");
                }

                cardIDInput.setText("");
                passwordInput.setText("");
                cardID = "";
                password = "";

            } else if (cardID.length() != 8) {

                wrongInput.setText("Card Number Must Have 8 Digits");
                cardIDInput.setText("");
                cardID = "";
                passwordInput.setText("");
                password = "";

            } else  {

                wrongInput.setText("Pin Number Must Have 4 Digits");
                passwordInput.setText("");
                password = "";

            }




    }
    public  void changeScene(String cardType) throws Exception {


        if (main.logInType.equalsIgnoreCase("customer")) {


            if (cardType.equals("RegularCard")) {

                BorderPane choosePane=FXMLLoader.load(getClass().getResource("ChooseOperation.fxml"));
                Scene scene =new Scene(choosePane,850,550);
                scene.getStylesheets().add(getClass().getResource("buttonEffects.css").toExternalForm());
                main.window.setScene(scene);

            } else if (cardType.equals("SmartCard")) {

                BorderPane borderPane=FXMLLoader.load(getClass().getResource("SmartCardBankChooser.fxml"));
                Scene scene=new Scene(borderPane,850,550);
                scene.getStylesheets().add(getClass().getResource("buttonEffects.css").toExternalForm());
                main.window.setScene(scene);


            }
        } else if (main.logInType.equalsIgnoreCase("bank")) {

            BorderPane borderPane=FXMLLoader.load(getClass().getResource("ChooseOperationBank.fxml"));
            Scene scene=new Scene(borderPane,850,550);
            scene.getStylesheets().add(getClass().getResource("buttonEffects.css").toExternalForm());
            main.window.setScene(scene);

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        welcomeLabel.setText("WELCOME TO "+main.logInType.toUpperCase()+" SERVICE");
        if(main.logInType.equalsIgnoreCase("customer")){
            forgetButton.setVisible(true);

        }
    }
    @FXML
    private void buttonForgetClicked(ActionEvent event){
        try {
            VBox vb=FXMLLoader.load(getClass().getResource("ForgetPassword.fxml"));
            root.getChildren().setAll(vb);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}