package atmClient;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import commonObject.communicate;
import java.net.URL;
import java.util.ResourceBundle;


public class balanceController implements Initializable {

    @FXML private Label nameLabel,balanceLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        main.getUpdates=true;
        Thread update = new Thread(()->{
            while(main.getUpdates){
                Platform.runLater(()->{
                    try{
                        communicate c=new communicate();
                        c.requset="checkBalance";

                        main.writeToServer.writeObject(c);
                        main.writeToServer.flush();

                        Object object=main.readFromServer.readObject();
                        if(object instanceof communicate){
                            nameLabel.setText("Name: "+((communicate) object).name);
                            balanceLabel.setText("Current Balance: "+((communicate) object).amount);

                        }


                    }catch (Exception e){
                        e.printStackTrace();
                    }

                });
                try{
                    Thread.sleep(5000);
                }catch (Exception e){
                    System.out.println("problem");

                }
            }

        });
        update.setDaemon(true);
        update.start();

    }

}