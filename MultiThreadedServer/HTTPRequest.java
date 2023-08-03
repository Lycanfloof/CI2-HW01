import java.io.*;
import java.net.*;
import java.util.*;

public class HTTPRequest implements Runnable {
    Socket clientSocket;
    String requestLine;
    String fileName;

    public HTTPRequest(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            processRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void processRequest() throws IOException {
        BufferedReader clientMessage = new BufferedReader(
            new InputStreamReader(clientSocket.getInputStream()));

        DataOutputStream serverMessage = new DataOutputStream(clientSocket.getOutputStream());

        String firstLine = clientMessage.readLine();
        StringTokenizer tokenizedLine = new StringTokenizer(firstLine);

        if (tokenizedLine.nextToken().equals("GET")) {
            try {
                String fileName = tokenizedLine.nextToken();

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
                } else if (fileName.endsWith(".html")) {
                serverMessage.writeBytes("Content-Type: text/html\r\n");
                }

                serverMessage.writeBytes("Content-Length: " + byteAmount + "\r\n");
                serverMessage.writeBytes("\r\n");
                serverMessage.write(fileBytes, 0, byteAmount);

                entryFile.close();
            } catch (Exception e) {
                serverMessage.writeBytes("HTTP/1.0 404 Not Found\r\n");
                serverMessage.writeBytes("Content-Type: text/html\r\n");
                
                String message = "<html>" +
                    "<head><title>404 Not Found</title></head>" +
                    "<body>404 Not Found</body>" +
                    "</html>";

                serverMessage.writeBytes("Content-Length: " + message.length() + "\r\n");
                serverMessage.writeBytes("\r\n");
                serverMessage.writeBytes(message);
            }
            serverMessage.close();
            clientMessage.close();
        } else {
            System.err.println("Bad Request Message.");
        }
        clientSocket.close();
    }
}
