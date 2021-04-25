package atmServer;

import commonObject.communicate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Bank {

    private WorkingThread workingThread;
    private String BankName;
    private String banksID;
    private String tableName;

    public Bank(WorkingThread workingThread, String bankName, String banksID, String tableName) {
        this.workingThread = workingThread;
        BankName = bankName;
        this.banksID = banksID;
        this.tableName = tableName;
    }

    public Bank(String banksID, WorkingThread workingThread) {

        this.workingThread=workingThread;
        this.banksID = banksID;
        workingThread.getAtmMachine().getBankInfo(this);

    }

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getBanksID() {
        return banksID;
    }


    public BankAccount createNewAccount(communicate c){

        String ID=workingThread.getAtmMachine().createNewID("bankAccount");

        Connection connection;
        Statement statement;

        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_database", "root", "");

            statement=connection.createStatement();
            String sql="INSERT INTO "+tableName+"account (account_id,balance,account_type,email,mobile_no,national_ID,active_status,account_user_name,max_withdraw,max_transfer) VALUES "+"('"+ID+"',"+c.amount+",'"+c.acctype+"','"+c.email+"','"+c.mobileNo+"','"+c.nationalID+"','"+c.activeStatus+"','"+c.name+"',"+c.withdraw+","+c.transfer+")";

            statement.execute(sql);
            connection.close();

        }catch (Exception e){
            System.out.println(e.getClass()+" "+e.getMessage());
        }

        BankAccount returnValue=new BankAccount(c.name,c.amount,c.acctype,ID,c.activeStatus,c.email,c.nationalID,this,workingThread,c.mobileNo,banksID,c.withdraw,c.transfer);
        return  returnValue;

    }

    public BankAccount checkForMatch(String accNumber) {

        BankAccount returnValue = null;

        Connection connection;
        Statement statement;
        ResultSet resultSet;
        try {

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_database", "root", "");
            statement = connection.createStatement();

            String accountTable = tableName + "account";
            String sql = "SELECT * FROM " + accountTable + " WHERE account_id='" + accNumber + "'";

            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {

                String accType = resultSet.getString("account_type");
                Double balance = resultSet.getDouble("balance");
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                String balanceString = decimalFormat.format(balance);
                balance = Double.parseDouble(balanceString);
                String email=resultSet.getString("email");
                String mobile_no=resultSet.getString("mobile_no");
                String national_ID=resultSet.getString("national_ID");
                String activeStatus=resultSet.getString("active_status");
                String user_name=resultSet.getString("account_user_name");
                Double max_withdraw=resultSet.getDouble("max_withdraw");
                Double max_transfer=resultSet.getDouble("max_transfer");
                returnValue = new BankAccount(user_name,balance,accType,accNumber,activeStatus,email,national_ID,this,workingThread,mobile_no,this.banksID,max_transfer,max_withdraw);

            }
            connection.close();


        } catch (Exception e) {
            System.out.println(e.getClass() + " " + e.getMessage());
        } finally {
            return returnValue;
        }

    }


    ArrayList<BankAccount> checkForMatch(String CardID, String type) {

        ArrayList<BankAccount> returnValue=new ArrayList<>();

        Connection connection;
        Statement statement;
        ResultSet resultSet;

        try {

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_database", "root", "");
            statement = connection.createStatement();

            String userTable = tableName + "user";
            String accNumber, user_name;

            String sql = "SELECT * FROM " + userTable + " WHERE user_id= '" + CardID + "'";
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {

                accNumber = resultSet.getString("account_id");
                BankAccount bankAccount=checkForMatch(accNumber);

                if(bankAccount!=null){
                    returnValue.add(bankAccount);
                }
            }
            connection.close();

        } catch (Exception e) {
            System.out.println(e.getClass() + "-_- " + e.getMessage());
        } finally {

            return returnValue;
        }
    }

    public void changeBalanceInfo( Bank bank,String accountID,double change) {

        Connection connection;
        Statement statement;
        ResultSet resultSet;


        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_database", "root", "");

            statement = connection.createStatement();
            String accountTable = bank.getTableName() + "account";

            String sql = "SELECT * FROM " + accountTable + " WHERE account_id= " + accountID;
                    resultSet = statement.executeQuery(sql);
            double balance;

            if (resultSet.next()) {
                balance = resultSet.getDouble("balance");
            } else {
                return;
            }

            double newBalance = balance + change;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            String balanceString = decimalFormat.format(newBalance);
            newBalance = Double.parseDouble(balanceString);

            sql = "UPDATE " + accountTable + " SET balance=" + newBalance + " WHERE account_id= " + accountID;
            statement.executeUpdate(sql);
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public  void relateCardToAccount(String cardID,String userName,String accountID) {

        Connection connection;
        Statement statement;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_database", "root", "");
            statement = connection.createStatement();

            String sql = "INSERT INTO " + tableName + "user" + " VALUES ('" + cardID + "','" + userName + "','" + accountID + "')";
            statement.executeUpdate(sql);
            connection.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean removeCard(String cardID,String AccountID){

        Connection connection;
        Statement statement;
        ResultSet resultSet;
        boolean result=false;

        try{

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_database", "root", "");
            statement = connection.createStatement();

            String sql="SELECT *  FROM "+tableName+"user WHERE user_id = '"+cardID+"' AND account_id = '"+AccountID+"'";
            resultSet=statement.executeQuery(sql);

            if(resultSet.next()){

                result=true;
                sql="DELETE  FROM "+tableName+"user WHERE user_id = '"+cardID+"' AND account_id= '"+AccountID+"'";
                statement.execute(sql);
            }
            connection.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    ArrayList<String> getSuggestion(String ID,String type){

        ArrayList<String> retValue=new ArrayList<>();
        String table=tableName+type;
        String search;

        if(type.equalsIgnoreCase("account")) search="account_id";
        else search="user_id";

        Connection connection;
        ResultSet resultSet;
        Statement statement;

        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_database", "root", "");
            statement = connection.createStatement();

            String sql="SELECT "+search+" FROM "+table+" WHERE "+search+" LIKE '%"+ID+"%'";
            resultSet=statement.executeQuery(sql);

            while(resultSet.next()){

                retValue.add(resultSet.getString(search));
                if(retValue.size()>=10){
                    break;
                }
            }
        }catch (Exception e){
            System.out.println(e.getClass()+" "+e.getMessage());
        }
        return retValue;

    }

}

