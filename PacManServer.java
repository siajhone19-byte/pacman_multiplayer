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

            System.out.println("=================================");
            System.out.println("       PAC-MAN SERVER STARTED");
            System.out.println("=================================");
            System.out.println("Port: 5000");
            System.out.println("Waiting for Player 2...");

            clientSocket = serverSocket.accept();

            System.out.println("Player 2 connected!");

            input = new DataInputStream(clientSocket.getInputStream());
            output = new DataOutputStream(clientSocket.getOutputStream());

            output.writeUTF("CONNECTED");
            output.flush();

            while (!clientSocket.isClosed()) {
                try {
                    String message = input.readUTF();

                    System.out.println("Player 2: " + message);

                    output.writeUTF(message);
                    output.flush();

                } catch (EOFException e) {
                    System.out.println("Player 2 disconnected.");
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());

        } finally {
            stopServer();
        }
    }

    public void stopServer() {
        try {
            if (input != null) {
                input.close();
            }

            if (output != null) {
                output.close();
            }

            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

        } catch (IOException e) {
            System.out.println("Error closing server: " + e.getMessage());
        }
    }
}