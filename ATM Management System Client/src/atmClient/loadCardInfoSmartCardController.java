package atmClient;

import commonObject.communicate;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class loadCardInfoSmartCardController implements Initializable {


    @FXML
    private Label cardTypeField;

    @FXML
    private Label nameField;


    @FXML
    private Label emailField;

    @FXML
    private Label cardNumField;

    @FXML
    private TableView<PopulatingSmartCardTable> tableView;

    @FXML
    private TableColumn<PopulatingSmartCardTable, String> bankName;

    @FXML
    private TableColumn<PopulatingSmartCardTable, String> accountOwner;

    @FXML
    private TableColumn<PopulatingSmartCardTable, String> accountID;

    @FXML
    private TableColumn<PopulatingSmartCardTable, String> accountType;

    public void initialize(URL location, ResourceBundle resources)  {

        bankName.setCellValueFactory(new PropertyValueFactory<>("BankName"));
        accountOwner.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        accountID.setCellValueFactory(new PropertyValueFactory<>("accountID"));
        accountType.setCellValueFactory(new PropertyValueFactory<>("accountType"));

        try {

            communicate c=new communicate();
            c.requset="getCardInfo";
            c.cardType="SmartCard";

            main.writeToServer.writeObject(c);
            main.writeToServer.flush();

            Object object=main.readFromServer.readObject();

            if(object instanceof communicate){

                communicate cc=(communicate) object;

                cardNumField.setText("Card ID: "+cc.cardID);
                cardTypeField.setText("Card Type: "+cc.cardType);
                nameField.setText("User Name: "+cc.name);
                emailField.setText("Email: "+cc.email);

            }

            PopulatingSmartCardTable populatingSmartCardTable = new PopulatingSmartCardTable();
            populatingSmartCardTable.loadTable(tableView);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void selected() throws Exception{

        communicate c=new communicate();
        c.requset="verifySelected";
        c.index=tableView.getSelectionModel().getSelectedIndex();
        main.writeToServer.writeObject(c);
        main.writeToServer.flush();
    }
}