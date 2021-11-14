package no.kristiania.controllers;

import no.kristiania.dao.QuestionDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.objects.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class AddQuestionController implements HttpController {
    private final QuestionDao questionDao;
    private static final Logger logger = LoggerFactory.getLogger(AddQuestionController.class);

    public AddQuestionController(QuestionDao questionDao) {
        this.questionDao = questionDao;

    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, IOException {
        Map<String, String> parameters = HttpMessage.parseQuery(request.messageBody, 0);
        Question question = new Question();
        question.setQuestionnaire(Long.parseLong(parameters.get("questionnaire")));
        question.setTitle(parameters.get("title"));
        question.setLowLabel(parameters.get("low_label"));
        question.setHighLabel(parameters.get("high_label"));
        questionDao.insert(question);
        logger.info("Question inserted into database");
        return new HttpMessage("HTTP/1.1 303 See Other", "Post successful! Redirecting..");
    }
}
