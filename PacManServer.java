import java.io.*;
import java.net.*;

public class PacManServer {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataInputStream input;
    private DataOutputStream output;

    public void startServer() {
        try {
            serverSocket = new ServerSocket(5000);

            System.out.println("PAC-MAN SERVER STARTED");
            System.out.println("Port: 5000");
            System.out.println("Waiting for Player 2...");

            clientSocket = serverSocket.accept();

            System.out.println("Player 2 connected!");

            input = new DataInputStream(clientSocket.getInputStream());
            output = new DataOutputStream(clientSocket.getOutputStream());

            output.writeUTF("CONNECTED");
            output.flush();

            while (true) {
                String message = input.readUTF();
                System.out.println("Player 2: " + message);

                output.writeUTF(message);
                output.flush();
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    public void stopServer() {
        try {
            if (clientSocket != null) {
                clientSocket.close();
            }

            if (serverSocket != null) {
                serverSocket.close();
            }

        } catch (IOException e) {
            System.out.println("Error closing server.");
        }
    }
}