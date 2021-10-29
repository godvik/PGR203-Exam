package no.kristiania.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;

public class HttpMessage {


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
}
