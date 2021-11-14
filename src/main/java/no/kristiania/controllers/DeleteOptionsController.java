package no.kristiania.controllers;

import no.kristiania.dao.OptionDao;
import no.kristiania.http.HttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class DeleteOptionsController implements HttpController {
    private final OptionDao optionDao;
    private static final Logger logger = LoggerFactory.getLogger(DeleteOptionsController.class);

    public DeleteOptionsController(OptionDao optionDao) {
        this.optionDao = optionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, IOException {
        Map<String, String> parameters = HttpMessage.parseQuery(request.messageBody, 0);
        long id = (Long.parseLong(parameters.get("option")));
        optionDao.delete(id);
        logger.info("Option deleted from database");
        return new HttpMessage("HTTP/1.1 303 See Other", "Post successful! Redirecting..");
    }
}
