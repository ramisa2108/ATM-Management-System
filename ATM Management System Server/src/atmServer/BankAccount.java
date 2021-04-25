package atmServer;

import commonObject.communicate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;


public class BankAccount {

    private String UserName;
    private double balance,max_transfer,max_withdraw;
    private String AccountID, activeStatus, email, nationalID;
    private Bank ownerBank;
    private WorkingThread workingThread;
    private String mobileNo;
    private String accountType;
    private String bankID;


    public BankAccount(String userName, double balance, String accountType, String accountID, String activeStatus, String email, String nationalID, Bank ownerBank, WorkingThread workingThread, String mobileNo, String bankID, double transfer, double withdraw) {

        UserName = userName;
        this.balance = balance;
        this.AccountID = accountID;
        this.activeStatus = activeStatus;
        this.email = email;
        this.nationalID = nationalID;
        this.ownerBank = ownerBank;
        this.workingThread = workingThread;
        this.mobileNo = mobileNo;
        this.accountType = accountType;
        this.bankID = bankID;
        this.max_transfer=transfer;
        this.max_withdraw=withdraw;

    }
    public double getMax_transfer() {
        return max_transfer;
    }

    public double getMax_withdraw() {
        return max_withdraw;
    }

    public String getEmail() {
        return email;
    }

    public String getActiveStatus() {
        return activeStatus;
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountID() {
        return AccountID;
    }

    public String getFullName() {
        return UserName;
    }


    public String getUserName() {
        return UserName;
    }

    public void setBalance(Double amount){
        this.balance=amount;
    }

    public String getAccountType() {
        return accountType;
    }

    public Bank getOwnerBank() {
        return ownerBank;
    }

    public BankAccount checkIfUpdated(){

        BankAccount updatedCurrent=workingThread.getCurrentBank().checkForMatch(this.getAccountID());
        return updatedCurrent;
    }

    public communicate getAllInfo(){

        communicate c=new communicate();
        c.name=UserName; c.accountID=AccountID; c.acctype=accountType; c.email=email;
        c.mobileNo=mobileNo; c.nationalID=nationalID; c.activeStatus=activeStatus; c.amount=balance;
        c.transfer=max_transfer; c.withdraw=max_withdraw;
        return c;
    }

    public void changeInfo(communicate c){

        Connection connection;
        Statement statement;
        String tableName=ownerBank.getTableName()+"account";

        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_database", "root", "");

            statement=connection.createStatement();
            String sql="UPDATE "+tableName+" SET account_type = '"+c.acctype+"' , email= '"+c.email+"' , mobile_no = '"+c.mobileNo+"' , national_ID = '"+c.nationalID+"' , active_status = '"+c.activeStatus+"' , account_user_name= '"+c.name+"' ,max_withdraw= "+c.withdraw+" ,max_transfer = "+c.transfer+" WHERE account_id= '"+this.AccountID+"'";

            statement.execute(sql);
            Email.sendMail(c.email,"Account Information Changed","Hi "+c.name+",\n"+"Information of your account holding ID number:"+this.AccountID+" under "+ownerBank.getBankName()+" has been changed.");

            connection.close();
        }catch (Exception e){
            System.out.println(e.getClass()+" "+e.getMessage());
        }

    }

    public void deleteAccount() {

        String table1=ownerBank.getTableName()+"account";
        String table2=ownerBank.getTableName()+"user";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_database", "root", "");
            Statement statement = connection.createStatement();

            String sql = "DELETE FROM " + table1 + " WHERE account_id ='" + AccountID + "'";
            statement.execute(sql);

            sql = "DELETE FROM " + table2 + " WHERE account_id ='" + AccountID + "'";
            statement.execute(sql);
            connection.close();

            Email.sendMail(email,"Account deleted","Hi "+getUserName()+",\n"+"Your account holding ID number:"+getAccountID()+" under "+ownerBank.getBankName()+" has been deleted.");
            workingThread.setCurrentBankAccount(null);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void blockAccount(){

        try {

            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_database","root","");

            Statement statement = connection.createStatement();
            String table=ownerBank.getTableName()+"account";
            String sql = "UPDATE " + table + " SET active_status= \"inactive\"  WHERE account_id ='" + AccountID + "'";
            Email.sendMail(email,"Account blocked","Hi "+getUserName()+",\n"+"Your account holding ID number: "+getAccountID()+" under "+ownerBank.getBankName()+" has been blocked ");

            statement.executeUpdate(sql);
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}