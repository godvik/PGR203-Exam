package no.kristiania.controllers;

import no.kristiania.dao.OptionDao;
import no.kristiania.http.HttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class EditOptionController implements HttpController {
    private final OptionDao optionDao;
    private static final Logger logger = LoggerFactory.getLogger(EditOptionController.class);

    public EditOptionController(OptionDao optionDao) {
        this.optionDao = optionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, IOException {
        Map<String, String> parameters = HttpMessage.parseQuery(request.messageBody, 0);
        String newOption = parameters.get("text");
        Long id = (Long.parseLong(parameters.get("option")));
        optionDao.update(newOption, id);
        logger.info("Option edited");
        return new HttpMessage("HTTP/1.1 303 See Other", "Post successful! Redirecting..");
    }
}
