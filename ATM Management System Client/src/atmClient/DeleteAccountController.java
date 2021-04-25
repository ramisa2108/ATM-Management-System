package atmClient;

import commonObject.communicate;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DeleteAccountController  implements Initializable {

   @FXML
    private TextField textFieldAccountID;
    @FXML
    private Button delete;
    @FXML
    Button buttonGo;
    @FXML
    private VBox root;

    @FXML
    private Label messageLabel;

    @FXML
    private ListView<String> list;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                textFieldAccountID.setText(newValue);
            }
        });
        list.setStyle("-fx-font-family: Comic Sans MS");
    }

    @FXML
    private void buttonDeleteClicked(ActionEvent event) throws Exception {

        MyAlertBox myAlertBox=new MyAlertBox("Are You Sure You Want To Delete The Account?");

        if(myAlertBox.display()){

            communicate c=new communicate();
            c.requset="deleteAccount";
            c.accountID=textFieldAccountID.getText();

            main.writeToServer.writeObject(c);
            main.writeToServer.flush();

            messageLabel.setText("      ACCOUNT DELETED!");
            textFieldAccountID.setText("");
            delete.setVisible(false);
            root.setVisible(false);
            list.setVisible(false);
            main.getUpdates=false;

        }


    }

    @FXML
    private void buttonGoClicked(ActionEvent event) throws Exception {

        list.setVisible(false);
        root.setVisible(true);

        if(textFieldAccountID.getText().length()!=8){

            textFieldAccountID.setText("");
            messageLabel.setText("Enter A Valid Account Number");
            delete.setVisible(false);
            root.setVisible(false);
            return;
        }

        communicate c=new communicate();
        c.requset="checkForMatchAccount";
        c.accountID=textFieldAccountID.getText();

        main.writeToServer.writeObject(c);
        main.writeToServer.flush();

        if(main.readFromServer.readUTF().equalsIgnoreCase("NO")){

            messageLabel.setText("NO SUCH ACCOUNT FOUND!");
            root.setVisible(false);
            textFieldAccountID.setText("");
            delete.setVisible(false);

        }else{

            try{

                VBox vBox=FXMLLoader.load(getClass().getResource("accInfo.fxml"));
                root.getChildren().setAll(vBox);
                root.setVisible(true);
                delete.setVisible(true);
                messageLabel.setText("");

            }catch (Exception e){
                System.out.println(e.getClass()+" "+e.getMessage());
            }
        }

    }
    @FXML
    private void search() throws  Exception{

        if(textFieldAccountID.getText().length()==0){
            list.setVisible(false);
            return ;
        }


        communicate c=new communicate();
        c.requset="getSuggestion";
        c.name="account";
        c.cardID=textFieldAccountID.getText();
        main.writeToServer.writeObject(c);
        main.writeToServer.flush();

        Object object=main.readFromServer.readObject();
        ObservableList<String>ListItems=FXCollections.observableArrayList();
        if(object instanceof ArrayList){

            ListItems.setAll((ArrayList)object);
            list.getItems().setAll(ListItems);
            list.setVisible(true);
        }

    }

}
