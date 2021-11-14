package no.kristiania.controllers;

import no.kristiania.dao.QuestionnaireDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.objects.Questionnaire;

import java.io.IOException;
import java.sql.SQLException;

public class ListQuestionnaireController implements HttpController {
    private final QuestionnaireDao questionnaireDao;

    public ListQuestionnaireController(QuestionnaireDao questionnaireDao) {
        this.questionnaireDao = questionnaireDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, IOException {
        String responseText = "";
        for (Questionnaire questionnaires : questionnaireDao.list()) {
            responseText += "<option value=" + questionnaires.getId() + ">" + questionnaires.getName() + "</option>";
        }
        return new HttpMessage("HTTP/1.1 200 OK", responseText);
    }
}
