package atmClient;

import commonObject.communicate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.util.ArrayList;


public class PopulatingSmartCardTable {

    private String BankName;
    private String ownerName;
    private String accountID;
    private String accountType;

    public PopulatingSmartCardTable(){

    }

    public PopulatingSmartCardTable(String string){
        String splited[]=string.split("#");
        BankName=splited[0];
        ownerName=splited[1];
        accountID=splited[2];
        accountType=splited[3];

    }

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void loadTable(TableView<PopulatingSmartCardTable> tableView){

        String addAccount[];

        ObservableList<PopulatingSmartCardTable>populatingSmartCardTables= FXCollections.observableArrayList();
        try {

            communicate c=new communicate();
            c.requset="getBankAccountsForClient";
            main.writeToServer.writeObject(c);
            main.writeToServer.flush();

            Object object=main.readFromServer.readObject();

            if(object instanceof ArrayList) {

                int accounts=((ArrayList) object).size();
                addAccount=new String[accounts];

                for (int i = 0; i < accounts; i++) {
                    addAccount[i]=(String) ((ArrayList) object).get(i);
                    populatingSmartCardTables.add(new PopulatingSmartCardTable(addAccount[i]));
                }

                tableView.setItems(populatingSmartCardTables);
            }
        }catch (Exception e){

            System.out.println(e.getClass()+" "+e.getMessage());
        }
    }
}
