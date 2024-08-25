import java.io.*;
import java.net.*;

public class SimpleHttpServer {
    private ServerSocket serverSocket;
    private boolean isStopped = false;

    public SimpleHttpServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void start() {
        while (!isStopped) {
            try {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            } catch (IOException e) {
                if (isStopped) {
                    System.out.println("Server stopped.");
                    break;
                }
                throw new RuntimeException("Error accepting client connection", e);
            }
        }
    }

    public void stop() {
        isStopped = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String requestLine;
                while ((requestLine = in.readLine()) != null) {
                    if (requestLine.isEmpty()) {
                        break;
                    }
                    System.out.println(requestLine);
                }

                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: text/plain");
                out.println("Content-Length: 38");
                out.println();
                out.println("Hello! This is a simple web server.");

                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
// Same as above
public static void main(String[] args) throws IOException {
    SimpleHttpServer server = new SimpleHttpServer(Integer.parseInt(args[0]));
    new Thread(server::start).start();
}
}
