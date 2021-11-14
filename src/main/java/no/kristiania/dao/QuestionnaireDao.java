package no.kristiania.dao;

import no.kristiania.objects.Questionnaire;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class QuestionnaireDao extends AbstractDao<Questionnaire> {

    public QuestionnaireDao(DataSource dataSource) {
        super(dataSource);
    }

    // Insert into questionnaire table. Using the abstract method insert
    public long insert(Questionnaire questionnaire) throws SQLException {
        return insert(questionnaire, "INSERT INTO questionnaires (name) VALUES (?)", "questionnaire_id");
    }

    // List all from questionnaire table. Using abstract method list
    public List<Questionnaire> list() throws SQLException {
        return list("SELECT * FROM questionnaires");
    }

    // Delete a questionnaire based by id. Using abstract method delete
    public void delete(long id) throws SQLException {
        delete("DELETE FROM Questionnaires WHERE Questionnaire_id = ?", id);
    }

    // Retrieves a questionnaire based by id. Using abstract method retrieve.
    public Questionnaire retrieve(long id) throws SQLException {
        return retrieve(id, "SELECT * FROM Questionnaires WHERE Questionnaire_id = ?");
    }

    // Updates a questionnaire based by id. Using abstract method update.
    public void update(String name, long id) throws SQLException {
        update("UPDATE questionnaires SET name = (?) WHERE questionnaire_id = (?)", name, id);
    }

    @Override
    protected Questionnaire readFromResultSet(ResultSet rs) throws SQLException {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setName(rs.getString("name"));
        questionnaire.setId(rs.getLong("questionnaire_id"));
        return questionnaire;
    }

    @Override
    protected void insertIntoDatabase(Questionnaire questionnaire, PreparedStatement statement) throws SQLException {
        statement.setString(1, questionnaire.getName());
    }
}
