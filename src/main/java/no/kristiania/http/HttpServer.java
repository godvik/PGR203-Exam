package no.kristiania.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class HttpServer {


    private ServerSocket serverSocket;
    private Path rootDirectory;
    private List<String> questionnaires;
    private final List<Question> questions = new ArrayList<>();

    public HttpServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);

        new Thread(this::handleClients).start();
    }

    private void handleClients() {
        while (true) {
            try {
                handleClient();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void handleClient() throws IOException {

            Socket clientSocket = serverSocket.accept();

            String[] requestLine = HttpMessage.readLine(clientSocket).split(" ");
            String requestTarget = requestLine[1];
            String contentType = "text/plain";

            if (requestTarget.endsWith(".html")) {
                contentType = "text/html";
            } else if (requestTarget.endsWith(".css")) {
                contentType = "text/css";
            }

            if ((rootDirectory != null && Files.exists(rootDirectory.resolve(requestTarget.substring(1))))) {
                String responseText = Files.readString(rootDirectory.resolve(requestTarget.substring(1)));
                String response = "HTTP/1.1 200 OK" + "\r\n" +
                        "Content-Length: " + responseText.getBytes().length + "\r\n" +
                        "Content-Type: " + contentType + "\r\n" +
                        "\r\n" +
                        responseText;
                clientSocket.getOutputStream().write(response.getBytes());
            } else {
                response404(clientSocket, requestTarget, contentType);
            }
    }

    private void response404(Socket clientSocket, String requestTarget, String contentType) throws IOException {
        String responseText = "File not found " + requestTarget;
        String response = "HTTP/1.1 404 Not found" + "\r\n" +
                "Content-Length: " + responseText.getBytes().length + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Connection: close" + "\r\n" +
                "\r\n" +
                responseText;
        clientSocket.getOutputStream().write(response.getBytes());
    }


    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void setRoot(Path rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public void setQuestionToQuestionnaire(List<String> question) {
        this.questionnaires = question;
    }

    public List<Question> getQuestion() {
        return questions;
    }
}
