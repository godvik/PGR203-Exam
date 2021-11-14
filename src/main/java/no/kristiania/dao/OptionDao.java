package no.kristiania.dao;

import no.kristiania.objects.Option;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OptionDao extends AbstractDao<Option>{


    public OptionDao(DataSource dataSource) {
        super(dataSource);
    }

    public long insert(Option option) throws SQLException {
        return insert(option, "INSERT INTO OPTIONS (question_id, text) VALUES (?, ?)", "option_id");
    }

    @Override
    protected Option readFromResultSet(ResultSet rs) throws SQLException {
        Option option = new Option();
        option.setId(rs.getLong("option_id"));
        option.setQuestion(rs.getLong("question_id"));
        option.setText(rs.getString("text"));
        return option;
    }

    @Override
    protected void insertIntoDatabase(Option obj, PreparedStatement statement) throws SQLException {
        statement.setLong(1, obj.getQuestion());
        statement.setString(2, obj.getText());
    }

    // List all from options. Using abstract list method.
    public List<Option> list() throws SQLException {
        return list("SELECT * FROM options");
    }

    // Own list method in option-dao. Lists out options based by question id.
    public List<Option> listByQuestion(long question_id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM options WHERE question_id = ?"
            )) {
                statement.setLong(1, question_id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    ArrayList<Option> options = new ArrayList<>();
                    while (resultSet.next()) {
                        Option option = readFromResultSet(resultSet);
                        options.add(option);
                    }
                    return options;
                }
            }
        }
    }

    // Delete options. Using delete abstract method.
    public void delete(long id) throws SQLException {
        delete("DELETE FROM Options WHERE Option_id = ?", id);
    }

    // Update options text. Using update abstract method.
    public void update(String text, long id) throws SQLException {
        update("UPDATE options SET text = (?) WHERE option_id = (?)", text, id);

    }

    // Retrieve option, based by id. Using abstract retrieve method.
    public Option retrieve(long id) throws SQLException {
        return retrieve(id, "SELECT * FROM options WHERE option_id = ?");
    }
}
