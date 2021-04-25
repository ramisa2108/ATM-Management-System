package atmServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class SmartCard extends Card {

    private ArrayList<BankAccount> bankAccountArrayList = new ArrayList<>();

    public SmartCard(String cardID, WorkingThread workingThread){

        super(cardID,workingThread);
        checkForMatch(cardID);
        getDataArraylistFromDatabase();
    }

    public int getSize(){
        return bankAccountArrayList.size();
    }


    public void checkForMatch(String cardID){

        Connection connection;
        Statement statement;
        ResultSet resultSet;

        try {

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database","root","");
            statement = connection.createStatement();

            String sql = "SELECT * FROM smartcard WHERE user_id= '" + cardID + "' " ;
            resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {

                cardType = resultSet.getString("cardtype");
                cardUserName = resultSet.getString("user_name");
                email=resultSet.getString("email");
            }

            connection.close();

        } catch (Exception e) {
            System.out.println(e.getClass() + " " + e.getMessage());
        }
    }

    public void getDataArraylistFromDatabase() {

        Connection connection;
        Statement statement;
        ResultSet resultSet;
        String sql;

        bankAccountArrayList.clear();

        try {

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database","root","");
            statement = connection.createStatement();
            sql = "SELECT * FROM banknames";

            String bankID;
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {

                bankID = resultSet.getString("bank_id");
                Bank tempBank=new Bank(bankID,workingThread);
                ArrayList<BankAccount> tempBankAccounts=tempBank.checkForMatch(this.cardID,"SmartCard");

                if(tempBankAccounts!=null) {
                    bankAccountArrayList.addAll(tempBankAccounts);
                }

            }
            connection.close();
            resultSet.close();

        }catch (Exception e) {
            System.out.println(e.getClass()+" "+e.getMessage());
        }

    }
    public void selectedBankandBankAccount(int index ){

        workingThread.setCurrentBank(bankAccountArrayList.get(index).getOwnerBank());
        workingThread.setCurrentBankAccount(bankAccountArrayList.get(index).checkIfUpdated());
    }

    public boolean verifySelected(int index){

        BankAccount bankAccount=bankAccountArrayList.get(index);

        if(bankAccount.getOwnerBank().getBankName().equalsIgnoreCase(workingThread.getCurrentBank().getBankName())){

            workingThread.setCurrentBankAccount(bankAccount);
            return true;
        }
        else {
            return false;
        }
    }


    public ArrayList<String> getBankAccountsForClient(){

        ArrayList<String> retValue=new ArrayList<>();

        for(BankAccount ba:bankAccountArrayList){

            retValue.add(ba.getOwnerBank().getBankName()+"#"+ba.getFullName()+"#"+ba.getAccountID()+"#"+ba.getAccountType());
        }
        return retValue;

    }
    public boolean alreadyAdded(String accountID){

        boolean result=false;
        for(BankAccount bankAccount: bankAccountArrayList){

            if(bankAccount.getAccountID().equalsIgnoreCase(accountID)){
                result=true;
                break;
            }
        }
        return result;
    }

}

