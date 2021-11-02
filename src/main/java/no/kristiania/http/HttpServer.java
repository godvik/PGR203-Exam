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
    public final List<Questionnaire> questionnaires = new ArrayList<>();
    private Path rootDirectory;
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

        String contentType = getContentType(requestTarget);


        switch (requestTarget) {
            case "/api/questions": {
                Map<String, String> parameters = HttpMessage.parseQuery(new HttpMessage(clientSocket).getMessageBody());
                Question question = new Question();
                question.setQuestionnaire(parameters.get("questionnaire"));
                question.setTitle(parameters.get("title"));
                question.setText(parameters.get("text"));

                questions.add(question);

                HttpMessage.response200(clientSocket, contentType, "Question added.");
                break;
            }
            case "/api/questionnaire": {
                Map<String, String> parameters = HttpMessage.parseQuery(new HttpMessage(clientSocket).getMessageBody());
                Questionnaire newQuestionnaire = new Questionnaire();
                newQuestionnaire.setName(parameters.get("questionnaire"));
                questionnaires.add(newQuestionnaire);
                HttpMessage.response200(clientSocket, contentType, "Questionnaire added.");

                break;
            }
            case "/api/listQuestionnaires":
                String responstext = "";
                for (Questionnaire questionnaires : questionnaires) {
                    responstext += "<option value=" + questionnaires.getName() + ">" + questionnaires.getName() + "</option>";
                }
                HttpMessage.response200(clientSocket, contentType, responstext);
                break;


            default:
                if (requestTarget.equals("/")) {
                    requestTarget = "/index.html";
                   contentType = getContentType(requestTarget);
                }
            if ((rootDirectory != null && Files.exists(rootDirectory.resolve(requestTarget.substring(1))))) {
                String responseText = Files.readString(rootDirectory.resolve(requestTarget.substring(1)));

                HttpMessage.response200(clientSocket, contentType, responseText);
            } else {
                HttpMessage.response404(clientSocket, requestTarget, contentType);
            }
        }
    }

    private String getContentType(String requestTarget) {
        String contentType = "text/plain";
        if (requestTarget.endsWith(".html")) {
            contentType = "text/html";
        } else if (requestTarget.endsWith(".css")) {
            contentType = "text/css";
        }
        return contentType;
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
//    public void setQuestionnaires(List<Questionnaire> questionnaires) {
//        this.questionnaires = questionnaires;
//    }
}
