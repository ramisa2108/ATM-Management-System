package atmServer;

import commonObject.communicate;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class WorkingThread implements Runnable {

    private Socket socket;
    private ObjectInputStream readFromClient;
    private ObjectOutputStream writeToClient;
    private ATM_Machine atmMachine;
    private SmartCard globalSmartCard;
    private RegularCard globalRegularCard;
    private Card globalCard;
    private Bank currentBank;
    private BankAccount currentBankAccount;
    private String cardType;
    private String loginType;//regular card or smart card

    WorkingThread(Socket socket) {

        this.socket = socket;

        try {
            this.writeToClient=new ObjectOutputStream(socket.getOutputStream());
            this.readFromClient=new ObjectInputStream(socket.getInputStream());

        } catch (Exception e) {
            System.out.println(e.getClass()+" "+e.getMessage());
        }

        this.atmMachine = new ATM_Machine(this, socket.getInetAddress().getHostAddress());

        try {

            if (!atmMachine.checkIfMatches())  writeToClient.writeUTF("BYE BYE");
            else writeToClient.writeUTF("No problem");

            writeToClient.flush();

        }catch (Exception e){
            System.out.println(e.getClass()+" "+e.getMessage());

        }

    }

    public ATM_Machine getAtmMachine() {
        return atmMachine;
    }

    public Card getGlobalCard() {
        return globalCard;
    }

    public void setGlobalCard(Card globalCard) {
        this.globalCard = globalCard;
    }

    public Bank getCurrentBank() {
        return currentBank;
    }

    public void setCurrentBank(Bank currentBank) {
        this.currentBank = currentBank;
    }

    public void setCurrentBankAccount(BankAccount currentBankAccount) {
        this.currentBankAccount = currentBankAccount;
    }
    
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public void run() {

        Object input;

        while (true) {
            try {

                input=readFromClient.readObject();
                
                if (input instanceof String && ((String) input).equalsIgnoreCase("EXIT")){  // clicking close
                    break;
                } else {
                    FindOutRequest(input);

                }
            } catch (Exception e) {
                System.out.println(e.getClass()+" "+e.getMessage());
            }
        }
        try {

            readFromClient.close();
            writeToClient.close();
            if (socket.isConnected()) socket.close();

        } catch (Exception e) {
            System.out.println(e.getClass()+" "+e.getMessage());
        }

    }

    private void FindOutRequest(Object input) throws Exception{

        if(input instanceof communicate ){
            communicate c=(communicate) input;
            communicate response=new communicate();
            switch (c.requset)
            {
                case "Login":
                {
                    boolean match=atmMachine.checkForMatch(c.cardID,c.password,c.loginType);

                    if(!match){
                        writeToClient.writeObject("invalid");
                        writeToClient.flush();

                    }else{

                        loginType=c.loginType;

                        if(c.loginType.equalsIgnoreCase("customer")){
                            if(cardType.equalsIgnoreCase("RegularCard")){

                                globalRegularCard=new RegularCard(c.cardID,this);
                                globalCard=globalRegularCard;

                            }else{
                                globalSmartCard=new SmartCard(c.cardID,this);
                                globalCard=globalSmartCard;
                            }

                        }else{
                            currentBank=new Bank(c.cardID,this);
                        }

                        response.requset="Logged In";
                        if(loginType.equalsIgnoreCase("customer"))
                        {
                            response.cardType=cardType;
                        }
                        else response.cardType="bank";

                        writeToClient.writeObject(response);
                        writeToClient.flush();

                    }
                    break;
                }
                case "getBankAccountsForClient":
                {
                    globalSmartCard.getDataArraylistFromDatabase();
                    ArrayList<String>bankAccountList=globalSmartCard.getBankAccountsForClient();

                    writeToClient.writeObject(bankAccountList);
                    writeToClient.flush();
                    break;
                }
                case "selectedBankandBankAccount":
                {
                    globalSmartCard.selectedBankandBankAccount(c.index);
                    break;
                }
                case "checkBalance":
                {
                    currentBankAccount=currentBankAccount.checkIfUpdated();

                    response.name=currentBankAccount.getFullName();
                    response.amount=currentBankAccount.getBalance();

                    writeToClient.writeObject(response);
                    writeToClient.flush();
                    break;

                }
                case "accountInfo":
                {
                    currentBankAccount=currentBankAccount.checkIfUpdated();
                    response=currentBankAccount.getAllInfo();
                    writeToClient.writeObject(response);
                    writeToClient.flush();
                    break;

                }
                case "withdraw":
                {
                    String responseString;
                    Double a=c.amount;
                    currentBankAccount=currentBankAccount.checkIfUpdated();

                    if(globalCard.checkActive().equalsIgnoreCase("inactive")){
                        responseString="Your Card Is Blocked";
                    }
                    else if(currentBankAccount.getActiveStatus().equalsIgnoreCase("inactive")){
                        responseString="Your Account Is Blocked";
                    }
                    else if(atmMachine.checkBalance(a)) {

                        if (currentBankAccount.getMax_withdraw()>=a) {

                            if (currentBankAccount.getBalance() >= a) {

                                currentBankAccount.setBalance(currentBankAccount.getBalance() - a);
                                atmMachine.changeBalance(-a);
                                currentBank.changeBalanceInfo(currentBank, currentBankAccount.getAccountID(), -a);

                                responseString = a + " Taka Withdrawn Successfully";

                                Email.sendMail(currentBankAccount.getEmail(), "Taka withdrawn.", "Taka : " + a + " has been withdrawn from your account : " + currentBankAccount.getAccountID());

                            } else {

                                responseString = "Not Enough Balance In Account";
                            }
                        } else {
                            responseString = "Exceeds Maximum Withdraw Limit";
                        }
                    }
                    else {
                        responseString = "Not Enough Cash in ATM";
                    }

                    writeToClient.writeUTF(responseString);
                    writeToClient.flush();
                    break;
                }
                case "transfer":
                {
                    String responseString;
                    BankAccount transferAccount;
                    String transferID=c.accountID;
                    Double transferAmount=c.amount;

                    currentBankAccount=currentBankAccount.checkIfUpdated();

                    if(globalCard.checkActive().equalsIgnoreCase("inactive")){
                        responseString="Your Card Is Blocked";
                    }
                    else if (currentBankAccount.getActiveStatus().equalsIgnoreCase("inactive")) {
                        responseString ="Your Account Is Blocked";
                    }
                    else if(currentBankAccount.getMax_transfer()>=transferAmount){

                        if((transferAccount=atmMachine.findAccount(transferID))!=null){

                            if(currentBankAccount.getBalance()>=transferAmount){

                                transferAccount.getOwnerBank().changeBalanceInfo(transferAccount.getOwnerBank(), transferID, transferAmount);
                                currentBank.changeBalanceInfo(currentBank, currentBankAccount.getAccountID(), -transferAmount);
                                currentBankAccount.setBalance(currentBankAccount.getBalance() - transferAmount);
                                responseString  = "Transferred " + transferAmount + " Taka Successfully";
                                Email.sendMail(currentBankAccount.getEmail(), "Taka transferred.", "Taka " + transferAmount + " has been transferred to " + transferID);

                            }
                            else{
                                responseString="Not Enough Balance In Account";
                            }
                        }
                        else
                        {
                            responseString="Invalid Account Number";
                        }
                    }
                    else{
                        responseString="Maximum Transfer Limit Exceeded";
                    }

                    writeToClient.writeUTF(responseString);
                    writeToClient.flush();
                    break;

                }

                case "changeBalance":
                {
                    atmMachine.changeBalance(c.amount);
                    break;
                }
                case "createNewAccount":
                {
                    currentBankAccount=currentBank.createNewAccount(c);
                    Email.sendMail(c.email,"Account Registered ","Hi "+c.name+",\n"+"Congratulations! New Account in "+currentBank.getBankName()+" has been registered.\nAccount ID: "+currentBankAccount.getAccountID());
                    break;
                }
                case "changeAccountInfo":
                {
                    currentBankAccount.changeInfo(c);
                    currentBankAccount=currentBankAccount.checkIfUpdated();
                    break;
                }
                case "deleteAccount":
                {
                    currentBankAccount.deleteAccount();
                    break;

                }
                case "blockAccount":
                {
                    currentBankAccount.blockAccount();
                    break;
                }
                case "checkForMatchAccount":
                {
                    BankAccount bankAccount=currentBank.checkForMatch(c.accountID);

                    if(bankAccount!=null){
                        currentBankAccount=bankAccount;
                        writeToClient.writeUTF("YES");
                    }else{
                        writeToClient.writeUTF("NO");
                    }

                    writeToClient.flush();
                    break;
                }
                case "checkForMatchCard":
                {

                    if(currentBank.checkForMatch(c.cardID,"card").size()==0){
                        response.requset="NO";
                    }
                    else{

                        globalCard=new Card(c.cardID,this);
                        cardType=response.cardType=globalCard.getSmartOrRegular(c.cardID);
                        response.requset="YES";

                        if(response.cardType.equalsIgnoreCase("SmartCard")){
                            globalCard=globalSmartCard=new SmartCard(c.cardID,this);
                        }else{
                            globalCard=globalRegularCard=new RegularCard(c.cardID,this);
                        }

                    }
                    writeToClient.writeObject(response);
                    writeToClient.flush();
                    break;
                }
                case "getCardInfo":
                {

                    response.name=globalCard.getCardUserName();
                    response.email=globalCard.getEmail();
                    response.cardType=globalCard.getCardType();
                    response.cardID=globalCard.getCardID();

                    if(c.cardType.equalsIgnoreCase("RegularCard")){

                        response.accountID=globalRegularCard.getBankAccount().getAccountID();
                        response.bankName=globalRegularCard.getOwnerBank().getBankName();
                        System.out.println(globalRegularCard.getOwnerBank().getBankName()+" "+globalRegularCard.getBankAccount().getAccountID());
                    }

                    writeToClient.writeObject(response);
                    writeToClient.flush();

                    break;
                }
                case "addAccountToCard":
                {

                    globalCard=new Card(c.cardID,this);

                    if((cardType=globalCard.getSmartOrRegular(c.cardID))==null){

                        response.requset="Invalid Card Number";

                    }else if(cardType.equalsIgnoreCase("RegularCard")){

                        response.requset="Can't Add Accounts To Regular Card";

                    }else if((currentBankAccount=currentBank.checkForMatch(c.accountID))==null){

                        response.requset="Invalid Account Number";

                    }else{

                        globalSmartCard=new SmartCard(c.cardID,this);

                        if(globalSmartCard.alreadyAdded(c.accountID)){

                            response.requset="Account Already Added To Card";

                        }else{

                            currentBank.relateCardToAccount(c.cardID,globalSmartCard.getCardUserName(),c.accountID);

                            globalSmartCard.getDataArraylistFromDatabase();
                            globalCard=globalSmartCard;


                            Email.sendMail(globalCard.getEmail(), "Account added to card", "Hi"+globalCard.getCardUserName()+",\n"+"New account had beed added to your card.\nCard Number:"+c.cardID+"\nAccount Number:"+c.accountID+"\nBank:"+currentBank.getBankName());

                            response.requset="YES";
                            response.cardType=cardType;

                        }

                    }

                    writeToClient.writeObject(response);
                    writeToClient.flush();
                    break;
                }
                case "blockCard":
                {
                    globalCard.blockCard(c.cardID);
                    Email.sendMail(globalCard.getEmail(), "Card blocked", "Hi "+globalCard.getCardUserName()+",\n"+"Your card holding ID number:"+globalCard.getCardID()+" has been blocked");

                    break;
                }
                case "issueCard":
                {

                    if(currentBank.checkForMatch(c.accountID)==null){
                        response.requset="NO";
                    }else{

                        globalCard=new Card(this);
                        cardType=c.cardType;

                        response=globalCard.createNewCard(c.name,c.email,c.acctype,c.cardType);
                        currentBank.relateCardToAccount(response.cardID,c.name,c.accountID);

                        response.requset="YES";
                        if(c.cardType.equalsIgnoreCase("RegularCard")){
                            globalCard=globalRegularCard=new RegularCard(response.cardID,this);
                            System.out.println(globalRegularCard.getOwnerBank().getBankName()+" "+globalRegularCard.getBankAccount().getAccountID());
                        }else{
                            globalCard=globalSmartCard=new SmartCard(response.cardID,this);
                        }

                    }
                    Email.sendMail(c.email, "Card issued", "Hi "+c.name+",\n"+"New card had beed issued against this email\n ID : "+response.cardID+"\nPassword :"+response.password);

                    writeToClient.writeObject(response);
                    writeToClient.flush();
                    break;

                }
                case "verifySelected":
                {
                    if(!globalSmartCard.verifySelected(c.index))
                        currentBankAccount=null;
                    break;
                }
                case "removeAccountFromCard":
                {
                    if(cardType.equalsIgnoreCase("RegularCard")){

                        currentBank.removeCard(c.cardID,globalRegularCard.getBankAccount().getAccountID());
                        globalCard.deleteCard(true,false);
                        response.index=0;
                        response.requset="YES";
                        Email.sendMail(globalCard.getEmail(), "Card Deleted", "Hi "+globalCard.getCardUserName()+",\n"+"Your "+currentBank.getBankName()+" card holding ID number: "+c.cardID+" has been deleted");

                    }
                    else{
                        if(currentBankAccount==null){
                            response.requset="NO";
                            response.index=1;
                        }
                        else{

                            currentBank.removeCard(c.cardID,currentBankAccount.getAccountID());
                            response.requset="YES";
                            globalSmartCard.getDataArraylistFromDatabase();
                            response.index=globalSmartCard.getSize();

                            if(response.index==0){
                                globalCard.deleteCard(false,true);
                                Email.sendMail(globalCard.getEmail(), "Card Deleted", "Hi "+globalCard.getCardUserName()+",\n"+"Your "+currentBank.getBankName()+" card holding ID number: "+c.cardID+" has been deleted");
                            }
                            else{
                                Email.sendMail(globalCard.getEmail(), "Account Removed from Card", "Hi "+globalCard.getCardUserName()+",\n"+"Your "+currentBank.getBankName()+" account holding ID number: "+currentBankAccount.getAccountID()+" has been removed from card No. "+c.cardID);

                            }

                        }
                    }
                    writeToClient.writeObject(response);
                    writeToClient.flush();
                    break;
                }

                case "changePassword": {

                    globalCard.setPinHash(ATM_Machine.generate_pinhash(c.password));
                    globalCard.changePinhash();

                    writeToClient.writeObject("password changed");
                    writeToClient.flush();

                    Email.sendMail(globalCard.getEmail(), "Password Changed .", "Hi " + globalCard.getCardUserName() + ",\nYour Card (ID: " + globalCard.getCardID() + " ) password was changed ! ");
                    break;
                }
                case "forgetPin": {

                    globalCard = new Card(c.cardID, this);
                    globalCard.initialize();

                    if (globalCard.cardID.equalsIgnoreCase("null") || !c.email.equals(globalCard.getEmail())) {
                        writeToClient.writeObject("Wrong combination of ID and Email.");
                        writeToClient.flush();
                    } else {

                        String pin = this.getAtmMachine().createPinHash();
                        Email.sendMail(globalCard.getEmail(), "New pin request.", "Hi " + globalCard.getCardUserName() + ",\n You requested for " +
                                "New password .\nCard ID : " + globalCard.getCardID() + "\npin : " + pin);
                        globalCard.setPinHash(ATM_Machine.generate_pinhash(pin));
                        globalCard.changePinhash();
                        writeToClient.writeObject("Please Check your email for new pin. Now use it to login");
                        writeToClient.flush();
                    }
                    break;
                }
                case "getSuggestion":
                {
                    ArrayList<String> arrayList;
                    if(c.name.equalsIgnoreCase("atmList")){
                        arrayList=atmMachine.getSuggestion(c.cardID);
                    }
                    else{
                        arrayList=currentBank.getSuggestion(c.cardID,c.name);
                    }

                    writeToClient.writeObject(arrayList);
                    writeToClient.flush();
                    break;
                }
            }

        }else if(input instanceof String){

            String value=(String) input;
            switch (value)
            {
                case "getCardType":
                {
                    writeToClient.writeUTF(cardType);
                    writeToClient.flush();
                    break;
                }
                case "getBankName":
                {
                    writeToClient.writeUTF(currentBank.getBankName());
                    writeToClient.flush();
                    break;
                }

            }

        }

    }
}
