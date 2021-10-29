package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;

public class PostHttpClient {
    private final int statusCode;

    public PostHttpClient(String host, int port, String requestTarget, String messageBody) throws IOException {
        Socket socket = new Socket(host, port);
        executeRequest(host, requestTarget, messageBody, socket);
        String responsMessage = HttpMessage.readLine(socket);
        this.statusCode = Integer.parseInt(responsMessage.split(" ")[1]);
    }

    private void executeRequest(String host, String requestTarget, String messageBody, Socket socket) throws IOException {
        String request = "POST " + requestTarget + " HTTP/1.1\r\n" +
                "Host: " + host + "\r\n" +
                "Content-Length: " + messageBody.length() + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                messageBody;
        socket.getOutputStream().write(request.getBytes());
    }

    public int getResponseCode() {
        return statusCode;
    }
}
