package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;

public class PostHttpClient {
    private final int statusCode;

    public PostHttpClient(String host, int port, String requestTarget, String messageBody) throws IOException {
        Socket socket = new Socket(host, port);
        HttpMessage.executeRequest(host, requestTarget, messageBody, socket);
        String responseMessage = HttpMessage.readLine(socket);
        this.statusCode = Integer.parseInt(responseMessage.split(" ")[1]);
    }

    public int getResponseCode() {
        return statusCode;
    }
}
