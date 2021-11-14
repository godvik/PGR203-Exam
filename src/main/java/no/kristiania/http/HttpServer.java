package no.kristiania.http;

import no.kristiania.controllers.*;
import no.kristiania.dao.AnswerDao;
import no.kristiania.dao.OptionDao;
import no.kristiania.dao.QuestionDao;
import no.kristiania.dao.QuestionnaireDao;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

public class HttpServer {
    private final ServerSocket serverSocket;
    private final HashMap<String, HttpController> controllers = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public HttpServer(int serverPort) throws IOException {
        serverSocket = new ServerSocket(serverPort);
        new Thread(this::handleClients).start();
    }

    private void handleClients() {
        try {
            while (true) {
                handleClient();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        DataSource dataSource = createDataSource();
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(dataSource);
        QuestionDao questionDao = new QuestionDao(dataSource);
        OptionDao optionDao = new OptionDao(dataSource);
        AnswerDao answerDao = new AnswerDao(dataSource);
        HttpServer server = new HttpServer(9080);
        runControllers(questionnaireDao, questionDao, optionDao, answerDao, server);
    }

    private static void runControllers(QuestionnaireDao questionnaireDao, QuestionDao questionDao, OptionDao optionDao, AnswerDao answerDao, HttpServer server) {
        server.addController("/api/listQuestionnaires", new ListQuestionnaireController(questionnaireDao));
        server.addController("/api/questionnaire", new AddQuestionnaireController(questionnaireDao));
        server.addController("/api/questions", new AddQuestionController(questionDao));
        server.addController("/api/listOutQuestions", new ListOutQuestionsController(questionnaireDao, questionDao, optionDao));
        server.addController("/api/questionOptions", new ListQuestionsToOptionController(questionDao));
        server.addController("/api/addOptions", new AddOptionController(optionDao));
        server.addController("/api/editQuestionnaire", new EditQuestionnaireController(questionnaireDao));
        server.addController("/api/editQuestion", new EditQuestionController(questionDao));
        server.addController("/api/optionsList", new ListOptionsController(optionDao));
        server.addController("/api/editOption", new EditOptionController(optionDao));
        server.addController("/api/deleteQuestionnaire", new DeleteQuestionnaireController(questionnaireDao));
        server.addController("/api/deleteQuestion", new DeleteQuestionController(questionDao));
        server.addController("/api/deleteOption", new DeleteOptionsController(optionDao));
        server.addController("/api/answers", new ListAnswersController(questionnaireDao, questionDao, optionDao, answerDao));
        server.addController("/api/submitQuestionnaire", new SubmitQuestionnaireController(questionDao, optionDao, answerDao));
        server.addController("/redirect.html", new RedirectController(server.serverSocket));
        logger.info("Starting server at: http://localhost:{}/index.html", server.getPort());
    }

    private static DataSource createDataSource() throws IOException {
        logger.info("Creating dataSource and running flyway migration");
        Properties properties = new Properties();
        try (FileReader reader = new FileReader("pgr203.properties")) {
            properties.load(reader);
        }
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(properties.getProperty("dataSource.url"));
        dataSource.setUser(properties.getProperty("dataSource.user"));
        dataSource.setPassword(properties.getProperty("dataSource.password"));
        Flyway.configure().dataSource(dataSource).load().migrate();
        logger.info("Flyway migration complete. Tables created.");
        return dataSource;
    }

    private void handleClient() throws IOException, SQLException {
        Socket clientSocket = serverSocket.accept();

        HttpMessage httpMessage = new HttpMessage(clientSocket);
        String[] requestLine = httpMessage.startLine.split(" ");
        String requestTarget = requestLine[1];
        String fileTarget = getFileTarget(requestTarget);

        if (controllers.containsKey(fileTarget)) {
            HttpMessage response = controllers.get(fileTarget).handle(httpMessage);
            if (requestLine[0].equals("POST")) {
                response.redirect(clientSocket, "http://localhost:" + serverSocket.getLocalPort() + "/redirect.html");
            } else {
                response.write(clientSocket);
            }
        } else {
            if (readFileResource(clientSocket, fileTarget)) return;
            String responseText = "File not found: " + requestTarget;
            HttpMessage.response404(clientSocket, responseText);
            logger.info("RequestTarget not found on server");
        }
    }

    // This method returns the filetarget. In this method we handle "/" issue.
    private String getFileTarget(String requestTarget) {
        int questionPos = requestTarget.indexOf('?');
        String fileTarget;
        if (questionPos != -1) {
            fileTarget = requestTarget.substring(0, questionPos);
        } else if (requestTarget.equals("/")) {
            fileTarget = "/index.html";
            getContentType(fileTarget);
        } else {
            fileTarget = requestTarget;
        }
        return fileTarget;
    }

    // This boolean methods checks if file exist. Used in handleClient.
    private boolean readFileResource(Socket clientSocket, String fileTarget) throws IOException {
        InputStream fileResource = getClass().getResourceAsStream(fileTarget);
        if (fileResource != null) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            fileResource.transferTo(buffer);
            String contentType = getContentType(fileTarget);
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: " + buffer.toByteArray().length + "\r\n" +
                    "Connection: close\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "\r\n";
            clientSocket.getOutputStream().write(response.getBytes());
            clientSocket.getOutputStream().write(buffer.toByteArray());
            return true;
        }
        return false;
    }

    // This method returns the content-type. Used in handleClient.
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

    public void addController(String path, HttpController controller) {
        controllers.put(path, controller);
    }
}
