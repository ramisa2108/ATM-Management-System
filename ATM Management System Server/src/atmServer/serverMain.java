package atmServer;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class serverMain{

    public static void main(String[] args) {


        try {

            System.out.println("The address of server is : " + InetAddress.getLocalHost().getHostAddress());
            //Email.initializeMailSender();

            ServerSocket welcomeSocket = new ServerSocket(5582);

            while(true){

                Socket socket=welcomeSocket.accept();
                WorkingThread workingThread=new WorkingThread(socket);
                Thread thread=new Thread(workingThread);
                thread.start();

            }

        }catch (Exception e){

            System.out.println(e.getClass()+" "+e.getMessage());
        }

    }

}

