package atmServer;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class ATM_Machine {

    private WorkingThread workingThread;
    private String address;
    private String location;

    ATM_Machine(WorkingThread workingThread, String address) {

        this.address = address;
        this.workingThread = workingThread;

    }

    public boolean checkBalance(double amount) {

        Connection connection;
        Statement statement;
        ResultSet resultSet;

        boolean result = false;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", "root", "");

            statement = connection.createStatement();
            String column_name = workingThread.getCurrentBank().getTableName() + "_balance";
            String sql = "SELECT * FROM atm_list WHERE ip_address= '" + address + "'";
            resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                double balance = resultSet.getDouble(column_name);
                if (balance >= amount)
                    result = true;

            }

        } catch (Exception e) {
            System.out.println(e.getClass() + " " + e.getMessage());
        }
        return result;
    }

    public void changeBalance(double change) {

        Connection connection;
        Statement statement;
        ResultSet resultSet;

        try {

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", "root", "");

            statement = connection.createStatement();
            String column_name = workingThread.getCurrentBank().getTableName() + "_balance";
            String sql = "SELECT * FROM atm_list WHERE ip_address= '" + address + "'";
            resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {

                Double currentBalance = resultSet.getDouble(column_name);
                sql = "UPDATE atm_list SET " + column_name + " = " + (currentBalance + change) + " WHERE ip_address= '" + address + "'";
                statement.execute(sql);
            }
            connection.close();

        } catch (Exception e) {
            System.out.println(e.getClass() + " " + e.getMessage());

        }

    }

    public boolean checkIfMatches() {

        boolean result = false;
        Connection connection;
        Statement statement;
        ResultSet resultSet;

        try {

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", "root", "");
            statement = connection.createStatement();
            String sql = "SELECT * FROM atm_list WHERE ip_address= '" + address + "'";
            resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                location = resultSet.getString("location");
                result = true;
            }
            connection.close();

        } catch (Exception e) {
            System.out.println("Problem loading database");
        } finally {
            return result;
        }

    }

    public static String generate_pinhash(String pinInput) {

        // using MD5 algorithm for generating pinhash from password

        byte[] pinHash;

        try {

            MessageDigest md = MessageDigest.getInstance("MD5");
            pinHash = md.digest(pinInput.getBytes());
            BigInteger bigInteger = new BigInteger(1, pinHash);
            String HashText = bigInteger.toString(16);

            while (HashText.length() < 32) {
                HashText = '0' + HashText;
            }

            return HashText;

        } catch (NoSuchAlgorithmException n) {
            throw new RuntimeException(n);
        }
    }


    public BankAccount findAccount(String accountNum) {

        Connection connection;
        Statement statement;
        ResultSet resultSet;
        BankAccount returnAccount = null;

        try {

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", "root", "");
            statement = connection.createStatement();

            String sql = "SELECT * FROM banknames";
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {

                String bankID = resultSet.getString("bank_id");
                String bankName = resultSet.getString("bank_name");
                String tableName = resultSet.getString("bankTable_name");

                Bank bank = new Bank(workingThread, bankName, bankID, tableName);
                returnAccount = bank.checkForMatch(accountNum);

                if (returnAccount != null)
                    break;
            }
            connection.close();


        } catch (Exception e) {
            e.printStackTrace();

        }
        return returnAccount;
    }


    public String createPinHash() {

        Random random = new Random();
        String value = String.valueOf(random.nextInt(8000) + 1000);
        return value;

    }

    public String createNewID(String type) {

        Random random = new Random();
        String returnValue = null;

        if (type.equalsIgnoreCase("bankAccount")) {

            do {
                returnValue = String.valueOf(random.nextInt(1000000) + 10000000); // need to implement more sophisticated technique
            } while ((findAccount(returnValue)) != null);
        } else {

            do {
                returnValue = String.valueOf(random.nextInt(1000000) + 10000000); // need to implement more sophisticated technique
            } while (CardIDExist(returnValue));

        }
        return returnValue;
    }


    public boolean checkForMatch(String CardID, String PinNumber, String logInType) {

        Connection connection;
        Statement statement;
        ResultSet resultSet;

        String pinHash = generate_pinhash(PinNumber);

        Boolean result = false;
        String id = "", pin = "", table = "";

        if (logInType.equals("bank")) {
            id = "bank_id";
            pin = "bank_pinhash";
            table = "banknames";
        } else if (logInType.equalsIgnoreCase("customer")) {
            id = "user_id";
            pin = "pinhash";
            table = "atm_machine";
        }

        try {

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", "root", "");
            statement = connection.createStatement();

            String sql = "SELECT * FROM " + table + " WHERE " + id + " = '" + CardID + "' AND " + pin + " = '" + pinHash + "'";
            resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {

                result = true;
                workingThread.setLoginType(logInType);

                if (logInType.equals("customer")) {

                    workingThread.setCardType(resultSet.getString("cardtype"));
                    workingThread.setGlobalCard(new Card(id, workingThread));
                    workingThread.getGlobalCard().setActive(resultSet.getString("active_status"));
                }
                else{
                    workingThread.setCardType("NULL");
                }

            }
            connection.close();

        } catch (Exception e) {

            System.out.println(e.getClass() + " " + e.getMessage());

        } finally {

            return result;

        }

    }

    public void getBankInfo(Bank bank) {

        Connection connection;
        Statement statement;
        ResultSet resultSet;

        try {

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", "root", "");
            statement = connection.createStatement();

            String sql = "SELECT * FROM banknames WHERE bank_id='" + bank.getBanksID() + "'";
            resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {

                String BankName = resultSet.getString("bank_name");
                bank.setBankName(BankName);
                String tableName = resultSet.getString("bankTable_name");
                bank.setTableName(tableName);
                connection.close();
            }

        } catch (Exception e) {

            System.out.println(e.getClass() + " " + e.getMessage() + "Atm_database");
        }
    }

    public boolean CardIDExist(String cardID) {

        boolean retValue = false;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", "root", "");
            Statement statement = connection.createStatement();

            String sql = "SELECT * FROM atm_machine WHERE user_id='" + cardID + "'";
            ResultSet resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                retValue = true;
            }

            connection.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return retValue;
        }

    }
    ArrayList<String> getSuggestion(String cardID){

        ArrayList<String> retValue=new ArrayList<>();
        Connection connection;
        Statement statement;
        ResultSet resultSet;
        try{

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", "root", "");
            statement = connection.createStatement();
            String sql="SELECT user_id FROM atm_machine WHERE user_id LIKE '%"+cardID+"%'";
            resultSet=statement.executeQuery(sql);
            while(resultSet.next()){

                retValue.add(resultSet.getString("user_id"));
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
