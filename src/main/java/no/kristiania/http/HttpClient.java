package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;

public class HttpClient {
    private final int statusCode;
    private final HttpMessage httpMessage;

    public HttpClient(String host, int port, String requestTarget) throws IOException {
        Socket socket = new Socket(host, port);
        HttpMessage.executeRequest(host, requestTarget, socket);
        httpMessage = new HttpMessage(socket);
        this.statusCode = Integer.parseInt(httpMessage.getStartLine().split(" ")[1]);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getHeader(String headerName) {
        return httpMessage.getHeader(headerName);
    }

    public String getMessageBody() {
        return httpMessage.getMessageBody();
    }
}
