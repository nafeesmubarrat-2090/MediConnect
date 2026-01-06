package myProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerFX {
    private static final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws Exception {
        int port = 6001;
        System.out.println("Help chat server starting on port " + port + "...");
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                Socket client = server.accept();
                ClientHandler handler = new ClientHandler(client);
                clients.add(handler);
                new Thread(handler).start();
            }
        }
    }

    private static void broadcast(String message, ClientHandler from) {
        synchronized (clients) {
            for (ClientHandler handler : clients) {
                if (handler != from) {
                    handler.send(message);
                }
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            String remote = socket.getRemoteSocketAddress().toString();
            System.out.println("Client connected: " + remote);
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                this.out = writer;
                send("Connected to help desk as " + remote);

                String line;
                while ((line = in.readLine()) != null) {
                    // Preserve explicit labels; default anything else to "User:".
                    boolean labeledAgent = line.startsWith("Agent:");
                    boolean labeledUser = line.startsWith("User:");
                    String payload = (labeledAgent || labeledUser) ? line : "User: " + line;

                    // Log with remote for server visibility, but broadcast only the clean payload.
                    System.out.println(remote + " " + payload);
                    broadcast(payload, this);
                }
            } catch (IOException e) {
                System.out.println("Client disconnected: " + remote);
            } finally {
                clients.remove(this);
            }
        }

        void send(String message) {
            if (out != null) {
                out.println(message);
            }
        }
    }
}
