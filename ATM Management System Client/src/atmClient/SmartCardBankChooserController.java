package atmClient;


import commonObject.communicate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class SmartCardBankChooserController implements Initializable {

    @FXML
    private TableView<PopulatingSmartCardTable> tableView;
    @FXML
    private TableColumn<PopulatingSmartCardTable,String>bankName;
    @FXML
    private TableColumn<PopulatingSmartCardTable,String>accountOwner;
    @FXML
    private TableColumn<PopulatingSmartCardTable,String>accountID;
    @FXML
    private TableColumn<PopulatingSmartCardTable,String>accountType;

    @FXML
    BorderPane root;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        bankName.setCellValueFactory(new PropertyValueFactory<>("BankName"));
        accountOwner.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        accountID.setCellValueFactory(new PropertyValueFactory<>("accountID"));
        accountType.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        PopulatingSmartCardTable populatingSmartCardTable=new PopulatingSmartCardTable();
        populatingSmartCardTable.loadTable(tableView);

    }

    @FXML
    void bankSelected(MouseEvent event) throws Exception {

        communicate c=new communicate();
        c.requset="selectedBankandBankAccount";
        c.index=tableView.getSelectionModel().getSelectedIndex();

        main.writeToServer.writeObject(c);
        main.writeToServer.flush();

        BorderPane pane = FXMLLoader.load(getClass().getResource("ChooseOperation.fxml"));
        root.getChildren().setAll(pane);

    }
    @FXML
    private void buttonBackClicked(ActionEvent event){

        String text="Are you sure you want to return?";
        MyAlertBox myAlertBox=new MyAlertBox(text);

        if(myAlertBox.display()) {

            try {

                BorderPane borderPane=FXMLLoader.load(getClass().getResource("ChooseBankOrCustomer.fxml"));
                root.getChildren().setAll(borderPane);

            } catch (Exception e) {
                System.out.println(e.getClass() + " " + e.getMessage());
            }
        }
    }

}
