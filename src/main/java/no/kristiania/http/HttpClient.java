package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class HttpClient {
    private final int statusCode;
    private final HashMap<String, String> headerFields = new HashMap<>();
    private String messageBody;

    public HttpClient(String host, int port, String requestTarget) throws IOException {
        Socket socket = new Socket(host, port);
        HttpMessage.executeRequest(host, requestTarget, socket);
        String startLine = HttpMessage.readLine(socket);
        HttpMessage.readHeaderLine(headerFields, socket);

        if (headerFields.containsKey("Content-Length".toLowerCase())) {
            this.messageBody = HttpMessage.readLine(socket, getContentLength());
        }

        this.statusCode = Integer.parseInt(startLine.split(" ")[1]);


    }

    private int getContentLength() {
        return Integer.parseInt(getHeader("Content-Length"));
    }


    public int getStatusCode() {
        return statusCode;
    }

    public String getHeader(String headerName) {
        return headerFields.get(headerName.toLowerCase());
    }

    public String getMessageBody() {
        return messageBody;
    }
}
