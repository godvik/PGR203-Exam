package no.kristiania.controllers;

import no.kristiania.dao.QuestionDao;
import no.kristiania.http.HttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class EditQuestionController implements HttpController {
    private final QuestionDao questionDao;
    private static final Logger logger = LoggerFactory.getLogger(EditQuestionController.class);

    public EditQuestionController(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, IOException {
        Map<String, String> parameters = HttpMessage.parseQuery(request.messageBody, 0);
        String newName = parameters.get("text");
        Long id = (Long.parseLong(parameters.get("question")));
        questionDao.update(newName, id);
        logger.info("Question edited");
        return new HttpMessage("HTTP/1.1 303 See Other", "Post successful! Redirecting..");
    }
}
