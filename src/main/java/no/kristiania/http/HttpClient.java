package no.kristiania.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class HttpClient {
    private final int statusCode;

    public HttpClient(String host, int port, String requestTarget) throws IOException {
        Socket socket = new Socket(host, port);
        String request = "GET " + requestTarget + " HTTP/1.1\r\n" +
                "Host: " + host + "\r\n" +
                "\r\n";
        socket.getOutputStream().write(request.getBytes());

        int c;
        StringBuilder result = new StringBuilder();
        InputStream inputStream = socket.getInputStream();
        while ((c = inputStream.read()) != -1 && c != '\r') {
            result.append((char)c);
        }
        int expectedNewLine = socket.getInputStream().read();
        assert expectedNewLine == '\n';

        this.statusCode = Integer.parseInt(result.toString().split(" ")[1]);


    }

    public int getStatusCode() {
        return statusCode;
    }
}
