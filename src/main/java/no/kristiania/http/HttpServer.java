package no.kristiania.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {


    private ServerSocket serverSocket;

    public HttpServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);

        new Thread(this::handleClients).start();
    }

    private void handleClients() {
        try {
            Socket clientSocket = serverSocket.accept();
            String responseText = "File not found";
            String response = "HTTP/1.1 404 " + responseText + "\r\n" +
                    "Content-Length: " + responseText.getBytes().length + "\r\n" +
                    "Connection: close" + "\r\n" +
                    "\r\n" +
                    responseText;
            clientSocket.getOutputStream().write(response.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
