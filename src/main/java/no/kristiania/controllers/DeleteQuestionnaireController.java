package no.kristiania.controllers;

import no.kristiania.dao.QuestionnaireDao;
import no.kristiania.http.HttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class DeleteQuestionnaireController implements HttpController {
    private final QuestionnaireDao questionnaireDao;
    private static final Logger logger = LoggerFactory.getLogger(DeleteQuestionnaireController.class);

    public DeleteQuestionnaireController(QuestionnaireDao questionnaireDao) {
        this.questionnaireDao = questionnaireDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, IOException {
        Map<String, String> parameters = HttpMessage.parseQuery(request.messageBody, 0);
        long id = (Long.parseLong(parameters.get("questionnaire_id")));
        questionnaireDao.delete(id);
        logger.info("Questionnaire and related questions/options deleted from database");
        return new HttpMessage("HTTP/1.1 303 See Other", "Post successful! Redirecting..");
    }
}
