package no.kristiania.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static java.net.URLDecoder.decode;

public class HttpMessage {

    private String messageBody;
    private final HashMap<String, String> headerFields = new HashMap<>();
    private final String startLine;

    public HttpMessage(Socket socket) throws IOException {
        this.startLine = HttpMessage.readLine(socket);
        readHeaderLine(headerFields, socket);
        if (headerFields.containsKey("Content-Length".toLowerCase())) {
            messageBody = readLine(socket, getContentLength());
        }

    }

    static String readLine(Socket socket) throws IOException {
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

    static void readHeaderLine(HashMap<String, String> headerFields, Socket socket) throws IOException {
        String headerLine;
        while (!(headerLine = readLine(socket)).isBlank()) {
            int colonPos = headerLine.indexOf(':');
            String key = headerLine.substring(0, colonPos).toLowerCase();
            String value = headerLine.substring(colonPos + 1).trim();
            headerFields.put(key, value);
        }
    }

    static void executeRequest(String host, String requestTarget, Socket socket) throws IOException {
        String request = "GET " + requestTarget + " HTTP/1.1\r\n" +
                "Host: " + host + "\r\n" +
                "\r\n";
        socket.getOutputStream().write(request.getBytes());
    }

    static void response200(Socket clientSocket, String contentType, String responseText) throws IOException {
        String response = "HTTP/1.1 200 OK" + "\r\n" +
                "Content-Length: " + responseText.getBytes().length + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Connection: close" + "\r\n" +
                "\r\n" +
                responseText;
        clientSocket.getOutputStream().write(response.getBytes());
    }

    static void response404(Socket clientSocket, String requestTarget, String contentType) throws IOException {
        String responseText = "File not found " + requestTarget;
        String response = "HTTP/1.1 404 Not found" + "\r\n" +
                "Content-Length: " + responseText.getBytes().length + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Connection: close" + "\r\n" +
                "\r\n" +
                responseText;
        clientSocket.getOutputStream().write(response.getBytes());
    }

    static Map<String, String> parseQuery(String query) {
        Map<String, String> parameters = new HashMap<>();
        if (query != null) {
            for (String parameter : query.split("&")) {
                int equalPos = parameter.indexOf('=');
                String key = parameter.substring(0, equalPos);
                String value = parameter.substring(equalPos + 1);
                value = decode(value, StandardCharsets.UTF_8);
                parameters.put(key, value);
            }
        }
        return parameters;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public int getContentLength() {
        return Integer.parseInt(getHeader("Content-Length"));
    }

    public String getHeader(String headerName) {
        return headerFields.get(headerName.toLowerCase());
    }

    public String getStartLine() {
        return startLine;
    }
}
