package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static java.net.URLDecoder.decode;

public class HttpMessage {
    public String startLine;
    public final Map<String, String> headerFields = new HashMap<>();
    public String messageBody;

    // HttpMessage constructor
    public HttpMessage(Socket socket) throws IOException {
        startLine = HttpMessage.readLine(socket);
        readHeaderLine(socket);
        if (headerFields.containsKey("Content-Length".toLowerCase())) {
            messageBody = HttpMessage.readLine(socket, getContentLength());
        }
    }
    // HttpMessage constructor
    public HttpMessage(String startLine, String messageBody) {
        this.startLine = startLine;
        this.messageBody = messageBody;
    }
    // Static method that is parsing all parameters into a Map.
    public static Map<String, String> parseQuery(String query, int subStringIndex) {
        Map<String, String> parameters = new HashMap<>();
        for (String queryParameter : query.split("&")) {
            int equalsPos = queryParameter.indexOf('=');
            String key = queryParameter.substring(subStringIndex, equalsPos);
            String value = decode(queryParameter.substring(equalsPos + 1), StandardCharsets.UTF_8);
            parameters.put(key, value);
        }
        return parameters;
    }




    // Responsemessage 404 Not found
    static void response404(Socket clientSocket, String responseText) throws IOException {
        String response = "HTTP/1.1 404 Not found\r\n" +
                "Content-Length: " + responseText.length() + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                responseText;
        clientSocket.getOutputStream().write(response.getBytes());
    }

    // Write method
    public void write(Socket socket) throws IOException {
        String response = startLine + "\r\n" +
                "Content-Length: " + messageBody.length() + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                messageBody;
        socket.getOutputStream().write(response.getBytes());
    }

    // Redirect method with location in header.
    public void redirect(Socket socket, String location) throws IOException {
        String response = startLine + "\r\n" +
                "Location: " + location + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";
        socket.getOutputStream().write(response.getBytes());
    }
    // Reads all lines based on the content-length into a StringBuilder.
    static String readLine(Socket socket, int contentLength) throws IOException {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < contentLength; i++) {
            result.append((char) socket.getInputStream().read());
        }
        return result.toString();
    }

    // Reads header lines and stores it in a map.
    private void readHeaderLine(Socket socket) throws IOException {
        String headerLine;
        while (!(headerLine = HttpMessage.readLine(socket)).isBlank()) {
            int colonPos = headerLine.indexOf(':');
            String headerKey = headerLine.substring(0, colonPos).toLowerCase();
            String headerValue = headerLine.substring(colonPos + 1).trim();
            headerFields.put(headerKey, headerValue);
        }
    }

    // Read lines from socket.
    static String readLine(Socket socket) throws IOException {
        StringBuilder result = new StringBuilder();
        int c;
        while ((c = socket.getInputStream().read()) != '\r') {
            result.append((char) c);
        }
        int expectedNewline = socket.getInputStream().read();
        assert expectedNewline == '\n';
        return result.toString();
    }

    public int getContentLength() {
        return Integer.parseInt(getHeader("Content-Length"));
    }

    public String getHeader(String headerName) {
        return headerFields.get(headerName.toLowerCase());
    }
}
