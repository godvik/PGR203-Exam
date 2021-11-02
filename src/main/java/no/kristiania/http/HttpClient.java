package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;

public class HttpClient {
    private final int statusCode;
    private HttpMessage httpMessage;

    // This constructor handles GET requests.
    public HttpClient(String host, int port, String requestTarget) throws IOException {
        Socket socket = new Socket(host, port);
        HttpMessage.executeRequest(host, requestTarget, socket);
        httpMessage = new HttpMessage(socket);
        this.statusCode = Integer.parseInt(httpMessage.getStartLine().split(" ")[1]);
    }
    // This constructor handles POST requests.
    public HttpClient(String host, int port, String requestTarget, String messageBody) throws IOException {
        Socket socket = new Socket(host, port);
        HttpMessage.executeRequest(host, requestTarget, messageBody, socket);
        String responseMessage = HttpMessage.readLine(socket);
        this.statusCode = Integer.parseInt(responseMessage.split(" ")[1]);

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
