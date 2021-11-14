package no.kristiania.controllers;

import no.kristiania.dao.AnswerDao;
import no.kristiania.dao.OptionDao;
import no.kristiania.dao.QuestionDao;
import no.kristiania.dao.QuestionnaireDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.objects.Answer;

import java.io.IOException;
import java.sql.SQLException;

public class ListAnswersController implements HttpController {
    private final QuestionnaireDao questionnaireDao;
    private final QuestionDao questionDao;
    private final OptionDao optionDao;
    private final AnswerDao answerDao;

    public ListAnswersController(QuestionnaireDao questionnaireDao, QuestionDao questionDao, OptionDao optionDao, AnswerDao answerDao) {
        this.questionnaireDao = questionnaireDao;
        this.questionDao = questionDao;
        this.optionDao = optionDao;
        this.answerDao = answerDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, IOException {
        String responseText =
                "<table>\n" +
                    "<tr>\n" +
                        "<th>Questionnaire</th>\n" +
                        "<th>Question</th>\n" +
                        "<th>Option</th>\n" +
                        "<th>Answer on a scale from 1-5</th>\n" +
                    "</tr>\n";
        for (Answer answer :
                answerDao.list()) {
            responseText +=
                        "<tr>\n" +
                            "<td>" + questionnaireDao.retrieve(answer.getQuestionnaire()).getName() + "</td>\n" +
                            "<td>" + questionDao.retrieve(answer.getQuestion()).getTitle() + "</td>\n" +
                            "<td>" + optionDao.retrieve(answer.getOption()).getText() + "</td>\n" +
                            "<td>" + answer.getValue() + "</td>\n" +
                        "</tr>\n";
        }
        responseText += "</table>";
        return new HttpMessage("HTTP/1.1 200 OK", responseText);
    }
}
