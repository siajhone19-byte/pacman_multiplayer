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
            socket = new Socket();
            socket.connect(
                    new java.net.InetSocketAddress(hostIP, 5000),
                    5000
            );

            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            System.out.println("Connected to host!");

            String message = input.readUTF();

            System.out.println("Server: " + message);

            return message.equals("CONNECTED");

        } catch (IOException e) {
            System.out.println("Connection failed: " + e.getMessage());
            return false;
        }
    }

    public void send(String message) {
        if (output == null) {
            return;
        }

        try {
            output.writeUTF(message);
            output.flush();

        } catch (IOException e) {
            System.out.println("Failed to send message: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (input != null) {
                input.close();
            }

            if (output != null) {
                output.close();
            }

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

        } catch (IOException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return socket != null
                && socket.isConnected()
                && !socket.isClosed();
    }

    public static void main(String[] args) {
        System.out.println("PacManClient is ready.");
    }
}