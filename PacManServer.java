import java.io.*;
import java.net.*;

public class PacManServer {
    private ServerSocket serverSocket;
    private volatile Socket clientSocket;
    private DataInputStream input;
    private DataOutputStream output;
    private volatile PacMan game;
    private volatile boolean running = true;

    public void attachGame(PacMan game) { this.game = game; }

    public void startServer() {
        try (ServerSocket server = new ServerSocket(5000)) {
            serverSocket = server;
            System.out.println("PAC-MAN SERVER STARTED on port 5000. Waiting for Player 2...");
            clientSocket = server.accept();
            input = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            output = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
            send("CONNECTED");
            System.out.println("Player 2 connected!");
            while (running && !clientSocket.isClosed()) {
                String message;
                try { message = input.readUTF(); }
                catch (EOFException e) { break; }
                if (message.startsWith("INPUT|")) {
                    String parts = message.substring(6);
                    if (parts.length() == 1 && "UDLR".indexOf(parts.charAt(0)) >= 0) {
                        PacMan current = game;
                        if (current != null) current.acceptRemoteDirection(parts.charAt(0));
                    }
                }
            }
        } catch (IOException e) {
            if (running) System.out.println("Server error: " + e.getMessage());
        } finally { stopServer(); }
    }

    public synchronized void broadcastState(String state) { send("STATE|" + state); }
    private synchronized void send(String message) {
        try { if (output != null && clientSocket != null && !clientSocket.isClosed()) { output.writeUTF(message); output.flush(); } }
        catch (IOException e) { System.out.println("Could not send update: " + e.getMessage()); }
    }
    public void stopServer() {
        running = false;
        try { if (clientSocket != null) clientSocket.close(); } catch (IOException ignored) {}
        try { if (serverSocket != null) serverSocket.close(); } catch (IOException ignored) {}
    }
    public static void main(String[] args) { new PacManServer().startServer(); }
}
