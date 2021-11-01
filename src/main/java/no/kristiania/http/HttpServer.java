package no.kristiania.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                Map<String, String> parameters = HttpMessage.parseQuery(new HttpMessage(clientSocket).getMessageBody());
                Question question = new Question();
                question.setQuestionniare(parameters.get("questionnaire"));
                question.setTitle(parameters.get("title"));
                question.setText(parameters.get("text"));

                questions.add(question);

                HttpMessage.response200(clientSocket, contentType, "Question added.");
        }
            if ((rootDirectory != null && Files.exists(rootDirectory.resolve(requestTarget.substring(1))))) {
                String responseText = Files.readString(rootDirectory.resolve(requestTarget.substring(1)));

                HttpMessage.response200(clientSocket, contentType, responseText);
            } else {
                HttpMessage.response404(clientSocket, requestTarget, contentType);
            }
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
