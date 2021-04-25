package atmClient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class cardOptionController {

    @FXML
    private BorderPane root;

    @FXML
    private VBox loadBox;

    @FXML
    void buttonBackClicked(ActionEvent event) throws Exception{

        BorderPane borderPane = FXMLLoader.load(getClass().getResource("ChooseOperationBank.fxml"));
        root.getChildren().setAll(borderPane);

    }


    @FXML
    void issueCard(ActionEvent event) throws Exception {

        VBox vBox = FXMLLoader.load(getClass().getResource("IssueCard.fxml"));
        loadBox.getChildren().setAll(vBox);

    }

    @FXML
    void deleteCard(ActionEvent event) throws Exception{

        VBox vBox = FXMLLoader.load(getClass().getResource("DeleteCard.fxml"));
        loadBox.getChildren().setAll(vBox);

    }

    @FXML
    void changeInfo(ActionEvent event) throws Exception{

        VBox vBox = FXMLLoader.load(getClass().getResource("addAccount.fxml"));
        loadBox.getChildren().setAll(vBox);

    }

    @FXML
    void blockCard(ActionEvent event) throws Exception {

        VBox vBox = FXMLLoader.load(getClass().getResource("BlockCard.fxml"));
        loadBox.getChildren().setAll(vBox);

    }

}

