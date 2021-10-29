package no.kristiania.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;

public class HttpClient {
    private final int statusCode;
    private final HashMap<String, String> headerFields = new HashMap<>();

    public HttpClient(String host, int port, String requestTarget) throws IOException {
        Socket socket = new Socket(host, port);
        String request = "GET " + requestTarget + " HTTP/1.1\r\n" +
                "Host: " + host + "\r\n" +
                "\r\n";
        socket.getOutputStream().write(request.getBytes());

        String result = readLine(socket);

        String headerLine;
        while (!(headerLine = readLine(socket)).isBlank()) {
            int colonPos = headerLine.indexOf(':');
            String key = headerLine.substring(0, colonPos);
            String value = headerLine.substring(colonPos +1).trim();
            headerFields.put(key, value);
        }

        this.statusCode = Integer.parseInt(result.toString().split(" ")[1]);


    }

    private String readLine(Socket socket) throws IOException {
        int c;
        StringBuilder result = new StringBuilder();
        InputStream inputStream = socket.getInputStream();
        while ((c = inputStream.read()) != -1 && c != '\r') {
            result.append((char)c);
        }
        int expectedNewLine = socket.getInputStream().read();
        assert expectedNewLine == '\n';
        return result.toString();
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getHeader(String headerName) {
        return headerFields.get(headerName);
    }
}
