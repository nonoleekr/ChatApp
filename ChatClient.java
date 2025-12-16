import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * * This class implements the Client side of the chat application.
 * It connects to the server and uses two threads:
 * 1. Main Thread: Reads user input and sends it to the server.
 * 2. Listener Thread: Listens for incoming messages from the server.
 */
public class ChatClient {
    
    private String serverAddress;
    private Scanner scanner = new Scanner(System.in);

    public ChatClient(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    private void run() throws IOException {
        // Connect to the server on localhost port 12345
        Socket socket = new Socket(serverAddress, 12345);
        
        // Setup input and output streams
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // --- Thread 1: Listen for incoming messages ---
        Thread listenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String line = in.readLine();
                        if (line == null) break;

                        // Protocol handling
                        if (line.startsWith("SUBMITNAME")) {
                            System.out.print("Enter your username: ");                            
                        } else if (line.startsWith("NAMEACCEPTED")) {
                            System.out.println("Connected! Type a message or use '/w [user] [message]' for private chat.");
                        } else if (line.startsWith("MESSAGE")) {
                            // Display the message content (skipping the protocol header "MESSAGE ")
                            System.out.println(line.substring(8));
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Connection to server lost.");
                }
            }
        });
        listenerThread.start();

        // --- Thread 2: Main thread handles user input ---
        while (true) {
            String input = scanner.nextLine();
            out.println(input);
        }
    }

    public static void main(String[] args) throws Exception {
        // Default to localhost (127.0.0.1)
        ChatClient client = new ChatClient("127.0.0.1");
        client.run();
    }
}