package no.kristiania.controllers;

import no.kristiania.dao.QuestionDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.objects.Question;


import java.io.IOException;
import java.sql.SQLException;

public class ListQuestionsToOptionController implements HttpController {
    private final QuestionDao questionDao;

    public ListQuestionsToOptionController(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, IOException {
        String responseText = "";
        for (Question questions : questionDao.list()) {
            responseText += "<option value=" + questions.getId() + ">" + questions.getTitle() + "</option>";
        }
        return new HttpMessage("HTTP/1.1 200 OK", responseText);
    }
}
