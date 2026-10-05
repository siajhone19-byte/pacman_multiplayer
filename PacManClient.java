import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class PacManClient {

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    public boolean connect(String hostIP) {
        try {
            socket = new Socket(hostIP, 5000);

            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            System.out.println("Connected to host!");

            String message = input.readUTF();

            System.out.println("Server: " + message);

            return true;

        } catch (IOException e) {
            System.out.println("Connection failed: " + e.getMessage());
            return false;
        }
    }

    public void send(String message) {
        try {
            output.writeUTF(message);
            output.flush();
        } catch (IOException e) {
            System.out.println("Failed to send message.");
        }
    }

    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing connection.");
        }
    }

    public static void main(String[] args) {
        System.out.println("PacManClient is ready.");
    }
}