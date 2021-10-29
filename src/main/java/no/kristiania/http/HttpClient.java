package no.kristiania.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;

public class HttpClient {
    private final int statusCode;
    private final HashMap<String, String> headerFields = new HashMap<>();
    private String messageBody;

    public HttpClient(String host, int port, String requestTarget) throws IOException {
        Socket socket = new Socket(host, port);
        executeRequest(host, requestTarget, socket);
        String startLine = readLine(socket);
        readHeaderLine(socket);

        if (headerFields.containsKey("Content-Length".toLowerCase())) {
            this.messageBody = readLine(socket, getContentLength());
        }

        this.statusCode = Integer.parseInt(startLine.split(" ")[1]);


    }

    private void executeRequest(String host, String requestTarget, Socket socket) throws IOException {
        String request = "GET " + requestTarget + " HTTP/1.1\r\n" +
                "Host: " + host + "\r\n" +
                "\r\n";
        socket.getOutputStream().write(request.getBytes());
    }

    private void readHeaderLine(Socket socket) throws IOException {
        String headerLine;
        while (!(headerLine = readLine(socket)).isBlank()) {
            int colonPos = headerLine.indexOf(':');
            String key = headerLine.substring(0, colonPos).toLowerCase();
            String value = headerLine.substring(colonPos + 1).trim();
            headerFields.put(key, value);
        }
    }

    private int getContentLength() {
        return Integer.parseInt(getHeader("Content-Length"));
    }


    private String readLine(Socket socket) throws IOException {
        int c;
        StringBuilder result = new StringBuilder();
        InputStream inputStream = socket.getInputStream();
        while ((c = inputStream.read()) != -1 && c != '\r') {
            result.append((char) c);
        }
        int expectedNewLine = socket.getInputStream().read();
        assert expectedNewLine == '\n';
        return result.toString();
    }

    static String readLine(Socket socket, int contentLength) throws IOException {
        int c;
        StringBuilder result = new StringBuilder();
        InputStream inputStream = socket.getInputStream();
        for (int i = 0; i < contentLength; i++) {
            c = inputStream.read();
            result.append((char) c);
        }
        return result.toString();
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
