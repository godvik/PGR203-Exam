package no.kristiania.controllers;

import no.kristiania.dao.OptionDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.objects.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;

public class AddOptionController implements HttpController {
    private final OptionDao optionDao;
    private static final Logger logger = LoggerFactory.getLogger(AddOptionController.class);

    public AddOptionController(OptionDao optionDao) {
        this.optionDao = optionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException {
        Map<String, String> parameters = HttpMessage.parseQuery(request.messageBody, 0);
        Option option = new Option();
        option.setQuestion(Long.parseLong(parameters.get("question")));
        option.setText(parameters.get("option"));
        optionDao.insert(option);
        logger.info("Option inserted into database");
        return new HttpMessage("HTTP/1.1 303 See Other", "Post successful! Redirecting..");
    }
}
