package atmClient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class accountOptionController {

    @FXML
    private BorderPane root;

    @FXML
    private VBox loadBox;


    @FXML
    void buttonBackClicked(ActionEvent event) throws Exception{

        main.getUpdates=false;
        BorderPane borderPane = FXMLLoader.load(getClass().getResource("ChooseOperationBank.fxml"));
        root.getChildren().setAll(borderPane);

    }


    @FXML
    void createAccount(ActionEvent event) throws Exception {

        main.getUpdates=false;
        VBox vBox = FXMLLoader.load(getClass().getResource("CreateNewAccount.fxml"));
        loadBox.getChildren().setAll(vBox);

    }

    @FXML
    void deleteAccount(ActionEvent event) throws Exception{

        main.getUpdates=false;
        VBox vBox = FXMLLoader.load(getClass().getResource("DeleteAccount.fxml"));
        loadBox.getChildren().setAll(vBox);

    }

    @FXML
    void changeInfo(ActionEvent event) throws Exception{

        main.getUpdates=false;
        VBox vBox = FXMLLoader.load(getClass().getResource("ChangeAccountInfo.fxml"));
        loadBox.getChildren().setAll(vBox);

    }

    @FXML
    void blockAccount(ActionEvent event) throws Exception {

        main.getUpdates=false;
        VBox vBox = FXMLLoader.load(getClass().getResource("BlockAccount.fxml"));
        loadBox.getChildren().setAll(vBox);

    }

}

