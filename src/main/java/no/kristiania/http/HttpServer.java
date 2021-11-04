package no.kristiania.http;

import no.kristiania.objects.Option;
import no.kristiania.objects.Question;
import no.kristiania.objects.Questionnaire;

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
    private final List<Option> options = new ArrayList<>();

    public HttpServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        new Thread(this::handleClients).start();
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer(9090);
//        Questionnaire educationQuestionnaire = new Questionnaire();
//        educationQuestionnaire.setName("Education");
//        Questionnaire healthQuestionnaire = new Questionnaire();
//        healthQuestionnaire.setName("Health");
//        server.questionnaires.add(educationQuestionnaire);
//        server.questionnaires.add(healthQuestionnaire);
//
//        Question dummyQuestion = new Question();
//        dummyQuestion.setText("How are you?");
//        server.questions.add(dummyQuestion);
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
                question.setText(parameters.get("text"));
                questions.add(question);

                contentType = HttpMessage.getContentType(requestTarget);
                String responseText = "Question added to " + question.getQuestionnaire();
                HttpMessage.response200(clientSocket, contentType, responseText);
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
            case  "/api/listOutQuestions": {
                String responseText = "";
                for (Question question : questions) {
                    responseText += "<h1>" + question.getQuestionnaire() + "</h1>" +
                            "<form action=\"POST\">\n" +
                            "        <fieldset>\n" +
                            "          <legend>" + question.getText() + "</legend>\n" +
                                            listOptionText(question) +
                            "          <br />\n" +
                            "        </fieldset>\n" +
                            "        <input type=\"submit\" value=\"Submit\" />\n" +
                            "      </form>";

                }


                HttpMessage.response200(clientSocket, contentType, responseText);
                break;
            }
            case "/api/questionOptions": {
                String responseText = "";
                for (Question question : questions) {
                    responseText += "<option value=" + question.getText() + ">" + question.getText() + "</option>";
                }

                contentType = HttpMessage.getContentType(requestTarget);
                HttpMessage.response200(clientSocket, contentType, responseText);
                break;
            }
            case "/api/alternativeAnswers": {
                Map<String, String> parameters = HttpMessage.parseQuery(new HttpMessage(clientSocket).getMessageBody());
                Option newOption = new Option();
                newOption.setQuestion(parameters.get("question"));
                newOption.setText(parameters.get("option"));
                options.add(newOption);

                contentType = HttpMessage.getContentType(requestTarget);
                HttpMessage.response200(clientSocket, contentType, "Option added");
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

    private String listOptionText(Question question) {
        String optionText = "";
        for (Option option :
                options) {
            if (question.getText().equals(option.getQuestion())) {
                optionText += "<div class=\"form-options\">\n" +
                        "            <p>" + option.getText() + "</p>\n" +
                        "\n" +
                        "            <div class=\"option-wrapper\">\n" +
                        "              <label for=\"1\">Helt uenig</label>\n" +
                        "              <input type=\"radio\" value=\"1\" id=\"1\" name=\"question1\" />\n" +
                        "              <input type=\"radio\" value=\"2\" id=\"2\" name=\"question1\" />\n" +
                        "              <input type=\"radio\" value=\"3\" id=\"3\" name=\"question1\" />\n" +
                        "              <input type=\"radio\" value=\"4\" id=\"4\" name=\"question1\" />\n" +
                        "              <input type=\"radio\" value=\"5\" id=\"5\" name=\"question1\" />\n" +
                        "              <label for=\"5\">Helt enig</label>\n" +
                        "            </div>\n" +
                        "          </div>\n";
            }
        }
        return optionText;
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

    public List<Option> getOptions() {
        return options;
    }
}
