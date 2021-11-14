package no.kristiania.controllers;

import no.kristiania.dao.OptionDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.objects.Option;

import java.io.IOException;
import java.sql.SQLException;

public class ListOptionsController implements HttpController {
    private final OptionDao optionDao;

    public ListOptionsController(OptionDao optionDao) {
        this.optionDao = optionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, IOException {
        String responseText = "";
        for (Option options : optionDao.list()) {
            responseText += "<option value=" + options.getId() + ">" + options.getText() + "</option>";
        }
        return new HttpMessage("HTTP/1.1 200 OK", responseText);
    }
}
