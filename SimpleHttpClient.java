import java.io.*;
import java.net.Socket;

public class SimpleHttpClient {
    private String serverHost;
    private int serverPort;

    public SimpleHttpClient(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void sendRequest() {
        try (Socket socket = new Socket(serverHost, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send HTTP GET request
            out.println("GET / HTTP/1.1");
            out.println("Host: " + serverHost);
            out.println("Connection: close");
            out.println(); // Blank line to indicate end of request

            // Read and print the server's response
            String responseLine;
            while ((responseLine = in.readLine()) != null) {
                System.out.println(responseLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java SimpleHttpClient <server host> <server port>");
            return;
        }

        String serverHost = args[0];
        int serverPort = Integer.parseInt(args[1]);

        SimpleHttpClient client = new SimpleHttpClient(serverHost, serverPort);
        client.sendRequest();
    }
}
