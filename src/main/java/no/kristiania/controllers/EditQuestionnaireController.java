package no.kristiania.controllers;

import no.kristiania.dao.QuestionnaireDao;
import no.kristiania.http.HttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class EditQuestionnaireController implements HttpController {
    private final QuestionnaireDao questionnaireDao;
    private static final Logger logger = LoggerFactory.getLogger(EditQuestionnaireController.class);

    public EditQuestionnaireController(QuestionnaireDao questionnaireDao) {
        this.questionnaireDao = questionnaireDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, IOException {
        Map<String, String> parameters = HttpMessage.parseQuery(request.messageBody, 0);
        String newName = parameters.get("questionnaire");
        Long id = (Long.parseLong(parameters.get("questionnaire_id")));
        questionnaireDao.update(newName, id);
        logger.info("Questionnaire edited");
        return new HttpMessage("HTTP/1.1 303 See Other", "Post successful! Redirecting..");
    }
}
