package no.kristiania.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpServer {

    private final ServerSocket serverSocket;
    private Path rootDirectory;
    public final List<Questionnaire> questionnaires = new ArrayList<>();
    private final List<Question> questions = new ArrayList<>();

    public HttpServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        new Thread(this::handleClients).start();
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer(9090);
        Questionnaire educationQuestionnaire = new Questionnaire();
        educationQuestionnaire.setName("Education");
        Questionnaire healthQuestionnaire = new Questionnaire();
        healthQuestionnaire.setName("Health");
        server.questionnaires.add(educationQuestionnaire);
        server.questionnaires.add(healthQuestionnaire);
        server.setRoot(Paths.get("src/main/resources/"));
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
        String contentType = "";

        switch (requestTarget) {
            case "/api/questions": {
                Map<String, String> parameters = HttpMessage.parseQuery(new HttpMessage(clientSocket).getMessageBody());
                Question question = new Question();
                question.setQuestionnaire(parameters.get("questionnaire"));
                question.setTitle(parameters.get("title"));
                question.setText(parameters.get("text"));
                questions.add(question);

                contentType = HttpMessage.getContentType(requestTarget);
                HttpMessage.response200(clientSocket, contentType, "");
                break;
            }
            case "/api/questionnaire": {
                Map<String, String> parameters = HttpMessage.parseQuery(new HttpMessage(clientSocket).getMessageBody());
                Questionnaire newQuestionnaire = new Questionnaire();
                newQuestionnaire.setName(parameters.get("questionnaire"));
                questionnaires.add(newQuestionnaire);

                contentType = HttpMessage.getContentType(requestTarget);
                HttpMessage.response200(clientSocket, contentType, "Questionnaire added.");
                break;
            }
            case "/api/listQuestionnaires": {
                String responseText = "";
                for (Questionnaire questionnaires : questionnaires) {
                    responseText += "<option value=" + questionnaires.getName() + ">" + questionnaires.getName() + "</option>";
                }

                contentType = HttpMessage.getContentType(requestTarget);
                HttpMessage.response200(clientSocket, contentType, responseText);
                break;
            }
            default:
                if (requestTarget.equals("/")) {
                    requestTarget = "/index.html";
                }
                if ((rootDirectory != null && Files.exists(rootDirectory.resolve(requestTarget.substring(1))))) {
                    String responseText = Files.readString(rootDirectory.resolve(requestTarget.substring(1)));

                    contentType = HttpMessage.getContentType(requestTarget);
                    HttpMessage.response200(clientSocket, contentType, responseText);
                } else {
                    contentType = HttpMessage.getContentType(requestTarget);
                    HttpMessage.response404(clientSocket, requestTarget, contentType);
                }
        }
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void setRoot(Path rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public List<Question> getQuestion() {
        return questions;
    }

    public List<Questionnaire> getQuestionnaires() {
        return questionnaires;
    }
}
