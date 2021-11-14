package no.kristiania.controllers;

import no.kristiania.dao.OptionDao;
import no.kristiania.dao.QuestionDao;
import no.kristiania.dao.QuestionnaireDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.objects.Option;
import no.kristiania.objects.Question;
import no.kristiania.objects.Questionnaire;

import java.io.IOException;
import java.sql.SQLException;

public class ListOutQuestionsController implements HttpController {
    private final QuestionDao questionDao;
    private final QuestionnaireDao questionnaireDao;
    private final OptionDao optionDao;

    public ListOutQuestionsController(QuestionnaireDao questionnaireDao, QuestionDao questionDao, OptionDao optionDao) {
        this.questionnaireDao = questionnaireDao;
        this.questionDao = questionDao;
        this.optionDao = optionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, IOException {
        String responseText = "";
        for (Questionnaire questionnaire : questionnaireDao.list()) {
            responseText += "<h1>" + questionnaire.getName() + "</h1>";
            responseText += "<form method=\"POST\" action=\"/api/submitQuestionnaire\">\n";
            for (Question question : questionDao.listByQuestionnaire(questionnaire.getId())) {
                responseText +=
                        "<fieldset>\n" +
                            "<legend>" + question.getTitle() + "</legend>\n";

                for (Option option : optionDao.listByQuestion(question.getId())) {
                    responseText +=
                            "<div class=\"form-options\">" +
                                "<p>" + option.getText() + "</p>" +
                                "<div class=\"option-wrapper\">" +
                                    "<label for=\"1\">" + question.getLowLabel() + "</label>\n" +
                                    "<input type=\"radio\" value=\"1\" id=\"1\" name=\"option" + option.getId() + "\"/>\n" +
                                    "<input type=\"radio\" value=\"2\" id=\"2\" name=\"option" + option.getId() + "\"/>\n" +
                                    "<input type=\"radio\" value=\"3\" id=\"3\" name=\"option" + option.getId() + "\"/>\n" +
                                    "<input type=\"radio\" value=\"4\" id=\"4\" name=\"option" + option.getId() + "\"/>\n" +
                                    "<input type=\"radio\" value=\"5\" id=\"5\" name=\"option" + option.getId() + "\"/>\n" +
                                    "<label for=\"5\">" + question.getHighLabel() + "</label>" +
                                "</div>" +
                            "</div>";
                }
                responseText +=
                        "<br />\n" +
                        "</fieldset>\n";
            }
            responseText +=
                        "<input type=\"submit\" value=\"Submit\" />\n" +
                    "</form>";
        }
        return new HttpMessage("HTTP/1.1 200 OK", responseText);
    }
}
