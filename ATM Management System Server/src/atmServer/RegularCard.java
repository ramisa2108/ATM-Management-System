package atmServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class RegularCard extends Card {

     Bank ownerBank;
     BankAccount bankAccount;

    public RegularCard(String cardID, WorkingThread workingThread){
        super(cardID,workingThread);
        checkForMatch(cardID);

    }
    public Bank getOwnerBank() {
        return ownerBank;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    private void checkForMatch(String cardID) {

        Connection connection;
        Statement statement;
        ResultSet resultSet;

        try {

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database","root","");
            statement = connection.createStatement();

            String sql = "SELECT * FROM regularcard WHERE user_id= '" + cardID + "'";
            resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {

                cardType = resultSet.getString("cardtype");
                cardUserName = resultSet.getString("user_name");
                email=resultSet.getString("email");

                String bankID = resultSet.getString("bank_id");
                ownerBank = new Bank(bankID,workingThread);
                workingThread.setCurrentBank(ownerBank);

                bankAccount = ownerBank.checkForMatch(cardID,"Card").get(0);
                workingThread.setCurrentBankAccount(bankAccount);
                connection.close();
            }

        } catch (Exception e) {
            System.out.println(e.getClass() + " " + e.getMessage());
        }
    }


}

