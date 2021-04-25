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


public class BlockCardController implements Initializable {

    @FXML
    private Button block;

    @FXML
    private Label messageLabel;

    @FXML
    private TextField textFieldCardID;

    @FXML
    private VBox loadBox;

    @FXML
    private ListView<String> list;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                textFieldCardID.setText(newValue);
            }
        });
        list.setStyle("-fx-font-family: Comic Sans MS");

    }

    @FXML
    private void buttonBlockClicked(ActionEvent event) throws Exception {

        MyAlertBox myAlertBox=new MyAlertBox("Confirm changes?");
        list.setVisible(false);

        if(myAlertBox.display()){

            communicate c=new communicate();
            c.requset="blockCard";
            c.cardID=textFieldCardID.getText();

            main.writeToServer.writeObject(c);
            main.writeToServer.flush();

            messageLabel.setText("Card Blocked!");
            textFieldCardID.setText("");

            block.setVisible(false);
            loadBox.setVisible(false);
            list.setVisible(false);

        }
    }

    @FXML
    private void buttonGoClicked() throws Exception{

        list.setVisible(false);
        loadBox.setVisible(true);

        if(textFieldCardID.getText().length()!=8){

            messageLabel.setText("Invalid Card Number");
            textFieldCardID.setText("");
            loadBox.setVisible(false);
            block.setVisible(false);
            return ;

        }

        communicate c=new communicate();
        c.requset="checkForMatchCard";
        c.cardID=textFieldCardID.getText();

        main.writeToServer.writeObject(c);
        main.writeToServer.flush();

        Object object=main.readFromServer.readObject();

        if(object instanceof communicate){

            communicate cc=(communicate)object;

            if(cc.requset.equalsIgnoreCase("NO")){

                messageLabel.setText("Invalid Card Number");
                textFieldCardID.setText("");

                block.setVisible(false);
                loadBox.setVisible(false);
            }
            else {

                String fileName="loadCardInfo"+cc.cardType+".fxml";
                VBox vBox=FXMLLoader.load(getClass().getResource(fileName));

                loadBox.getChildren().setAll(vBox);
                loadBox.setVisible(true);
                block.setVisible(true);
                messageLabel.setText("");
            }

        }

    }

    @FXML
    private void search() throws  Exception{

        if(textFieldCardID.getText().length()==0){
            list.setVisible(false);
            return ;
        }

        communicate c=new communicate();
        c.requset="getSuggestion";
        c.name="user";
        c.cardID=textFieldCardID.getText();
        main.writeToServer.writeObject(c);
        main.writeToServer.flush();

        Object object=main.readFromServer.readObject();
        ObservableList<String> ListItems= FXCollections.observableArrayList();
        if(object instanceof ArrayList){

            ListItems.setAll((ArrayList)object);
            list.getItems().setAll(ListItems);
            list.setVisible(true);
        }

    }
}
