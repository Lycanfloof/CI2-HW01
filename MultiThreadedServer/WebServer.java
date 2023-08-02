import java.io.*;
import java.net.*;

public class WebServer {
    public static void main(String[] args) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(62126);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread newThread = new Thread(new HTTPRequest(clientSocket));
                newThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
