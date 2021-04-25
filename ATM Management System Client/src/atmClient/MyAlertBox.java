package atmClient;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

public class MyAlertBox  {

    private String message;

    MyAlertBox(String message){
        this.message=message;
    }

    public  boolean display( ) {

        Alert alert=new Alert(Alert.AlertType.INFORMATION,message, ButtonType.YES,ButtonType.NO);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("myDialogs.css").toExternalForm());

        dialogPane.getStyleClass().add("myDialog");

        alert.showAndWait();
        if(alert.getResult()==ButtonType.YES){
            return true;
        }
        else{
            return false;
        }
    }
}
