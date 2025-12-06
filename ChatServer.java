import java.io.*;
import java.net.*;
import java.util.*;

/**
 * * This class implements the Server side of the chat application.
 * It listens for incoming connections on a specific port and assigns
 * a separate thread (Handler) to manage each connected client.
 */
public class ChatServer {

    // The port that the server listens on.
    private static final int PORT = 12345;

    // A Map to store connected users: Key=Username, Value=PrintWriter (Output Stream)
    // We use a Map instead of a Set to enable Private Messaging (finding a user by name).
    private static HashMap<String, PrintWriter> namesAndWriters = new HashMap<>();

    // The set of all the print writers for all the clients.
    // Used for broadcasting to everyone.
    private static HashSet<PrintWriter> writers = new HashSet<>();

    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running on port " + PORT);
        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                // Listen for a connection request and spawn a handler thread
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    /**
     * A handler thread class. Handlers are spawned from the listening
     * loop and are responsible for dealing with a single client
     * and broadcasting its messages.
     */
    private static class Handler extends Thread {
        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                // Create character streams for the socket.
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Request a name from this client. Keep requesting until
                // a name is submitted that is not already used.
                while (true) {
                    out.println("SUBMITNAME");
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                    // Synchronized block to prevent race conditions when checking names
                    synchronized (namesAndWriters) {
                        if (!name.isEmpty() && !namesAndWriters.containsKey(name)) {
                            namesAndWriters.put(name, out);
                            break;
                        }
                    }
                }

                // Notify the client that the name has been accepted
                out.println("NAMEACCEPTED " + name);
                
                // Broadcast to all other clients that a new user has joined
                for (PrintWriter writer : namesAndWriters.values()) {
                    writer.println("MESSAGE [Server]: " + name + " has joined the chat.");
                }

                // Accept messages from this client and broadcast them.
                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }

                    // Check for Private Message command: /w TargetUser Message...
                    if (input.startsWith("/w ")) {
                        String[] parts = input.split(" ", 3);
                        if (parts.length >= 3) {
                            String targetUser = parts[1];
                            String message = parts[2];
                            sendPrivateMessage(targetUser, message);
                        } else {
                            out.println("MESSAGE [System]: Invalid format. Use /w [user] [message]");
                        }
                    } else {
                        // Regular Broadcast Message
                        for (PrintWriter writer : namesAndWriters.values()) {
                            writer.println("MESSAGE " + name + ": " + input);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                // Client disconnected
                if (name != null) {
                    System.out.println(name + " is leaving");
                    namesAndWriters.remove(name);
                    for (PrintWriter writer : namesAndWriters.values()) {
                        writer.println("MESSAGE [Server]: " + name + " has left.");
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }

        /**
         * Helper method to send a private message to a specific user.
         */
        private void sendPrivateMessage(String targetUser, String message) {
            synchronized (namesAndWriters) {
                PrintWriter targetWriter = namesAndWriters.get(targetUser);
                if (targetWriter != null) {
                    targetWriter.println("MESSAGE [Private from " + name + "]: " + message);
                    out.println("MESSAGE [Private to " + targetUser + "]: " + message);
                } else {
                    out.println("MESSAGE [System]: User " + targetUser + " not found.");
                }
            }
        }
    }
}