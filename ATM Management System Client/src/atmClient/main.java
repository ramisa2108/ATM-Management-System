package atmClient;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class main extends Application{

    public static Socket socket;
    public static ObjectInputStream readFromServer;
    public static ObjectOutputStream writeToServer;
    public static String logInType="customer";//customer,bank,atm
    public static Stage window;
    public static boolean getUpdates=false;

    public static void main(String[] args) {

        String serveraddress="localhost"; // replace localhost by server address

        try{

            System.out.println("The address of client is : " + InetAddress.getLocalHost().getHostAddress());
            socket=new Socket(serveraddress,5582);
            writeToServer=new ObjectOutputStream(socket.getOutputStream());
            readFromServer=new ObjectInputStream(socket.getInputStream());

        }catch (Exception e){
            System.out.println(e.getClass()+" "+e.getMessage());
            System.exit(0);
        }
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        try {
            if (readFromServer.readUTF().equals("BYE BYE")) {
                System.out.println("INVALID CONNECTION");
            }
        }catch (Exception e){
            if(main.socket.isConnected()) main.socket.close();
            System.exit(0);
        }

        Parent root = FXMLLoader.load(getClass().getResource("ChooseBankOrCustomer.fxml"));
        Scene mainPromptScene=new Scene(root,850,550);

        mainPromptScene.getStylesheets().add(getClass().getResource("buttonEffects.css").toExternalForm());
        window=primaryStage;
        window.setScene(mainPromptScene);
        window.setResizable(false);
        window.sizeToScene();
        window.show();
        window.setOnCloseRequest(e->{
            e.consume();
            try{
                writeToServer.writeObject("EXIT");
                writeToServer.flush();
                socket.close();
                window.close();

            }catch (Exception ex){
                ex.printStackTrace();
            }
        });

    }

}
