package no.kristiania.controllers;

import no.kristiania.dao.QuestionnaireDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.objects.Questionnaire;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;

public class AddQuestionnaireController implements HttpController {
    private final QuestionnaireDao questionnaireDao;
    private static final Logger logger = LoggerFactory.getLogger(AddQuestionnaireController.class);

    public AddQuestionnaireController(QuestionnaireDao questionnaireDao) {
        this.questionnaireDao = questionnaireDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        Map<String, String> parameters = HttpMessage.parseQuery(request.messageBody, 0);
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setName(parameters.get("questionnaire"));
        questionnaireDao.insert(questionnaire);
        logger.info("Questionnaire inserted into database");
        return new HttpMessage("HTTP/1.1 303 See Other", "Post successful! Redirecting..");
    }
}
