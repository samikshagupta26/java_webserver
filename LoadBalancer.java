import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class LoadBalancer {
    private ServerSocket serverSocket;
    private List<InetSocketAddress> serverList = new ArrayList<>();
    private int currentServer = 0;

    public LoadBalancer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverList.add(new InetSocketAddress("localhost", 8081));
        serverList.add(new InetSocketAddress("localhost", 8082));
        serverList.add(new InetSocketAddress("localhost", 8083));
    }

    public void start() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                InetSocketAddress server = getNextServer();
                Socket serverSocket = new Socket(server.getHostName(), server.getPort());

                Thread clientToServer = new Thread(() -> {
                    try {
                        proxy(clientSocket.getInputStream(), serverSocket.getOutputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                Thread serverToClient = new Thread(() -> {
                    try {
                        proxy(serverSocket.getInputStream(), clientSocket.getOutputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                clientToServer.start();
                serverToClient.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void proxy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
            out.flush();
        }
    }

    private synchronized InetSocketAddress getNextServer() {
        InetSocketAddress server = serverList.get(currentServer);
        currentServer = (currentServer + 1) % serverList.size();
        return server;
    }

    public static void main(String[] args) throws IOException {
        LoadBalancer loadBalancer = new LoadBalancer(8080);
        loadBalancer.start();
    }
}
