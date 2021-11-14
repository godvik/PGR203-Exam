package no.kristiania.controllers;

import no.kristiania.http.HttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;

public class RedirectController implements HttpController{
    private final ServerSocket serverSocket;
    private static final Logger logger = LoggerFactory.getLogger(RedirectController.class);

    public RedirectController(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, IOException {
        String responseText =
                "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                    "<meta charset=\"UTF-8\" />\n" +
                    "<meta http-equiv=\"refresh\" content=\"5; URL=http://localhost:" + serverSocket.getLocalPort() + "\">\n" +
                    "<title>Kristiania Questionnaire</title>\n" +
                    "<link rel=\"stylesheet\" href=\"/css/style.css\" />\n" +
                "</head>\n" +
                "<body>\n" +
                    "<h1>Success!</h1>\n" +
                    "<p>If you are not redirected in five seconds, <a href=\"index.html\">click here</a></p>\n" +
                "</body>\n" +
                "</html>\n";
        logger.info("Redirecting after successful POST");
        return new HttpMessage("HTTP/1.1 200 OK",responseText);
    }
}
