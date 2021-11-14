package no.kristiania.controllers;

import no.kristiania.dao.AnswerDao;
import no.kristiania.dao.OptionDao;
import no.kristiania.dao.QuestionDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.objects.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class SubmitQuestionnaireController implements HttpController {
    private static final Logger logger = LoggerFactory.getLogger(SubmitQuestionnaireController.class);
    private final QuestionDao questionDao;
    private final OptionDao optionDao;
    private final AnswerDao answerDao;

    public SubmitQuestionnaireController(QuestionDao questionDao, OptionDao optionDao, AnswerDao answerDao) {
        this.questionDao = questionDao;
        this.optionDao = optionDao;
        this.answerDao = answerDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, IOException {
        Map<String, String> parameters = HttpMessage.parseQuery(request.messageBody, 6);
        // Insert an answer row in db for each option answered
        for (String key : parameters.keySet()) {
            Answer answer = new Answer();
            Long option_id = Long.parseLong(key);
            Long question_id = optionDao.retrieve(option_id).getQuestion();
            Long questionnaire_id = questionDao.retrieve(question_id).getQuestionnaire();
            answer.setQuestionnaire(questionnaire_id);
            answer.setQuestion(question_id);
            answer.setOption(option_id);
            answer.setValue(parameters.get(key));
            answerDao.insert(answer);
        }
        logger.info("Answers inserted into database");
        return new HttpMessage("HTTP/1.1 303 See Other", "Post successful! Redirecting..");
    }
}
