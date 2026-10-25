import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class PacManClient {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private volatile boolean running;
    private Consumer<String> messageHandler = message -> {};

    public boolean connect(String hostIP) {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(hostIP, 5000), 5000);
            input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            String message = input.readUTF();
            if (!"CONNECTED".equals(message)) { disconnect(); return false; }
            running = true;
            Thread reader = new Thread(() -> {
                while (running) {
                    try { messageHandler.accept(input.readUTF()); }
                    catch (IOException e) { if (running) System.out.println("Server connection closed: " + e.getMessage()); break; }
                }
            }, "PacMan-client-reader");
            reader.setDaemon(true);
            reader.start();
            System.out.println("Connected to host!");
            return true;
        } catch (IOException e) {
            System.out.println("Connection failed: " + e.getMessage());
            disconnect(); return false;
        }
    }

    public synchronized void send(String message) {
        if (output == null || socket == null || socket.isClosed()) return;
        try { output.writeUTF(message); output.flush(); }
        catch (IOException e) { System.out.println("Failed to send message: " + e.getMessage()); }
    }
    public void setMessageHandler(Consumer<String> handler) { messageHandler = handler == null ? message -> {} : handler; }
    public void disconnect() {
        running = false;
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
    }
    public boolean isConnected() { return socket != null && socket.isConnected() && !socket.isClosed(); }
    public static void main(String[] args) { System.out.println("PacManClient is ready."); }
}
