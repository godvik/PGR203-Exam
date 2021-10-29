package no.kristiania.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.net.URLDecoder.decode;
import static java.net.URLEncoder.encode;

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

            if (requestTarget.equals("/api/questions")) {
                Map<String, String> parameters = parseQuery(new HttpMessage(clientSocket).getMessageBody());
                Question question = new Question();
                question.setQuestionniare(parameters.get("questionnaire"));
                question.setTitle(parameters.get("title"));
                question.setText(parameters.get("text"));

                questions.add(question);

                response200(clientSocket, contentType, "Question added.");
        }
            if ((rootDirectory != null && Files.exists(rootDirectory.resolve(requestTarget.substring(1))))) {
                String responseText = Files.readString(rootDirectory.resolve(requestTarget.substring(1)));

                response200(clientSocket, contentType, responseText);
            } else {
                response404(clientSocket, requestTarget, contentType);
            }
    }

    private void response200(Socket clientSocket, String contentType, String responseText) throws IOException {
        String response = "HTTP/1.1 200 OK" + "\r\n" +
                "Content-Length: " + responseText.getBytes().length + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "\r\n" +
                responseText;
        clientSocket.getOutputStream().write(response.getBytes());
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

    private Map<String, String> parseQuery(String query) {
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
