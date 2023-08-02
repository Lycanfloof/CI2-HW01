package SimpleWebServer;
import java.io.*;
import java.net.*;
import java.util.*;

public class SimpleWebServer {
   public static void main(String[] args) {
      String requestLine;
      String fileName;
      ServerSocket serverSocket;

      try {
         serverSocket = new ServerSocket(62126);

         while (true) {
            Socket clientSocket = serverSocket.accept();
   
            BufferedReader clientMessage = new BufferedReader(
               new InputStreamReader(clientSocket.getInputStream()));
   
            DataOutputStream serverMessage = new DataOutputStream(
               clientSocket.getOutputStream());
            
            requestLine =  clientMessage.readLine();
            System.out.println(requestLine);
            
            StringTokenizer separatedLine = new StringTokenizer(requestLine);
   
            if (separatedLine.nextToken().equals("GET")) {
               fileName = separatedLine.nextToken();
               System.out.println(fileName);
   
               if (fileName.startsWith("/"))
                  fileName = fileName.substring(1);
               
               File file = new File(fileName);
               FileInputStream entryFile = new FileInputStream(fileName);
   
               int byteAmount = (int) file.length();
               byte[] fileBytes = new byte[byteAmount];
               entryFile.read(fileBytes);
   
               serverMessage.writeBytes("HTTP/1.0 200 Document Follows\r\n");
               
               if (fileName.endsWith(".jpg")) {
                  serverMessage.writeBytes("Content-Type: image/jpeg\r\n");
               } else if (fileName.endsWith(".gif")) {
                  serverMessage.writeBytes("Content-Type: image/gif\r\n");
               }
   
               serverMessage.writeBytes("Content-Length: " + byteAmount + "\r\n");
               serverMessage.writeBytes("\r\n");
               serverMessage.write(fileBytes, 0, byteAmount);
   
               entryFile.close();
            } else
               System.err.println("Bad Request Message.");
            clientSocket.close();
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}