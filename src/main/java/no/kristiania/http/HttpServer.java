package no.kristiania.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpServer {


    private ServerSocket serverSocket;
    private Path rootDirectory;

    public HttpServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);

        new Thread(this::handleClients).start();
    }

    private void handleClients() {
        try {
            Socket clientSocket = serverSocket.accept();

            String[] requestLine = HttpMessage.readLine(clientSocket).split(" ");
            String requestTarget = requestLine[1];
            String contentType = "text/plain";

            if (requestTarget.endsWith(".html")) {
                contentType = "text/html";
            } else if (requestTarget.endsWith(".css")) {
                contentType = "text/css";
            }


            if (requestTarget.equals("/does-not-exist")) {
                String responseText = "File not found " + requestTarget;
                String response = "HTTP/1.1 404 Not found" + "\r\n" +
                        "Content-Length: " + responseText.getBytes().length + "\r\n" +
                        "Content-Type: " + contentType + "\r\n" +
                        "Connection: close" + "\r\n" +
                        "\r\n" +
                        responseText;
                clientSocket.getOutputStream().write(response.getBytes());
            } else if (rootDirectory != null && Files.exists(rootDirectory.resolve(requestTarget.substring(1)))) {
                String responseText = Files.readString(rootDirectory.resolve(requestTarget.substring(1)));
                String response = "HTTP/1.1 200 OK" + "\r\n" +
                        "Content-Length: " + responseText.getBytes().length + "\r\n" +
                        "Content-Type: " + contentType + "\r\n" +
                        "\r\n" +
                        responseText;
                clientSocket.getOutputStream().write(response.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void setRoot(Path rootDirectory) {
        this.rootDirectory = rootDirectory;
    }
}
