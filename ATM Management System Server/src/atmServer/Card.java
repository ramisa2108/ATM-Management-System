package atmServer;

import commonObject.communicate;

import java.sql.*;

public class Card {

    protected WorkingThread workingThread;
    protected String pinHash;
    protected String cardID;
    protected String userName;
    protected String cardType;
    protected String cardUserName;
    protected String email;
    protected String active;

    public Card(String cardID, WorkingThread workingThread){

        this.cardID=cardID;
        this.workingThread=workingThread;

    }
    public Card(WorkingThread workingThread){

        this.workingThread=workingThread;
    }

    public String getCardType() {
        return cardType;
    }

    public void setActive(String active) {

        this.active = active;
    }
    public String getCardID() {
        return cardID;
    }

    public String getEmail() {
        return email;
    }

    public void setPinHash(String pinHash) {
        this.pinHash = pinHash;
    }
    public String getCardUserName() {
        return cardUserName;
    }


    public String checkActive(){

        Connection connection;
        Statement statement;
        ResultSet resultSet;
        String returnValue=null;

        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", "root", "");
            statement = connection.createStatement();
            String sql="SELECT * FROM atm_machine WHERE user_id = '"+cardID+"'";
            resultSet=statement.executeQuery(sql);
            if(resultSet.next()){

                returnValue= resultSet.getString("active_status");
            }

            connection.close();
        }catch(Exception e){
            System.out.println(e.getClass()+" "+e.getMessage());
        }
        return returnValue;

    }



    public String getSmartOrRegular(String cardID){

        Connection connection;
        Statement statement;
        ResultSet resultSet;
        String returnValue=null;

        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", "root", "");
            statement = connection.createStatement();
            String sql="SELECT * FROM atm_machine WHERE user_id = '"+cardID+"'";
            resultSet=statement.executeQuery(sql);

            if(resultSet.next()){
                workingThread.getGlobalCard().setActive("active_status");
                returnValue= resultSet.getString("cardtype");
            }

            connection.close();
        }catch(Exception e){
            System.out.println(e.getClass()+" "+e.getMessage());
        }
        return returnValue;

    }

    public  void initialize() {
        try {
            String smartOrRegular = getSmartOrRegular(cardID).toLowerCase();

            if (smartOrRegular != null) {

                String sql = "SELECT * FROM " + smartOrRegular + " WHERE user_id = '" + cardID + "'";
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", "root", "");
                Statement statement = connection.createStatement();

                ResultSet resultSet = statement.executeQuery(sql);
                if (resultSet.next()) {
                    email = resultSet.getString("email");
                    cardUserName = resultSet.getString("user_name");
                } else {
                    cardID = "null";
                }
                connection.close();
            }
            else{
                cardID="null";
            }
        } catch (Exception e) {
            System.out.println(e.getClass() + " " + e.getMessage());
        }
    }

    public void deleteCard(boolean regularcard,boolean smartcard){

        Connection connection;
        Statement statement;

        String tableName;

        try{

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", "root", "");
            statement = connection.createStatement();

            tableName="atm_machine";
            String sql="DELETE  FROM "+tableName+" WHERE user_id = '"+cardID+"'";
            statement.execute(sql);

            if(regularcard) {

                tableName = "regularcard";
                sql = "DELETE  FROM " + tableName + " WHERE user_id = '" + cardID + "'";
                statement.execute(sql);
            }

            else if(smartcard) {

                tableName = "smartcard";
                sql = "DELETE  FROM " + tableName + " WHERE user_id = '" + cardID + "'";
                statement.execute(sql);
            }
            connection.close();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void blockCard(String cardID){

        Connection connection;
        Statement statement;
        String tableName;

        try{

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", "root", "");
            statement = connection.createStatement();
            tableName="atm_machine";
            String sql="UPDATE "+tableName+" SET active_status = 'inactive' WHERE user_id = '"+cardID+"'";
            statement.execute(sql);
            connection.close();

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public communicate createNewCard(String name, String email, String cardType, String smartRegular){

        communicate c=new communicate();
        c.cardID=workingThread.getAtmMachine().createNewID("Card");

        c.password=workingThread.getAtmMachine().createPinHash();
        String pinhash= ATM_Machine.generate_pinhash(c.password);

        Connection connection;
        Statement statement;
        String tableName=smartRegular.toLowerCase();

        try{

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", "root", "");

            statement=connection.createStatement();
            String sql="INSERT INTO atm_machine (user_id,active_status,pinhash,cardtype) VALUES ('"+c.cardID+"' , 'active' , '"+pinhash+"' , '"+smartRegular+"' )";
            statement.execute(sql);

            if(smartRegular.equalsIgnoreCase("regularCard")){
                sql="INSERT INTO "+tableName+" (user_id,pinhash,user_name,bank_id,cardtype,email) VALUES ('"+c.cardID+"' , '"+pinhash+"' , '"+name+"' , '"+workingThread.getCurrentBank().getBanksID()+"' , '"+cardType+"' , '"+email+"') ";
            }else{
                sql="INSERT INTO "+tableName+" (user_id,pinhash,user_name,cardtype,email) VALUES ('"+c.cardID+"' , '"+pinhash+"' , '"+name+"' , '"+cardType+"' , '"+email+"') ";
            }

            statement.execute(sql);
            connection.close();

        }catch (Exception e){
            System.out.println(e.getClass()+" "+e.getMessage());
        }

        return c;

    }

    public void changePinhash() {

        String tableName=getSmartOrRegular(this.cardID).toLowerCase();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", "root", "");
            Statement s = connection.createStatement();
            String sql = "UPDATE atm_machine SET pinhash='" + pinHash + "' WHERE user_id ='" + cardID + "'";

            s.executeUpdate(sql);
            sql = "UPDATE "+tableName+" SET pinhash='" + pinHash + "' WHERE user_id ='" + cardID + "'";
            s.executeUpdate(sql);

            connection.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}