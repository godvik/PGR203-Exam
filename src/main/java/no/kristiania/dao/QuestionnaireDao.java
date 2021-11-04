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


    public long insert(Questionnaire questionnaire) throws SQLException {
        return insert(questionnaire, "INSERT INTO questionnaires (name) VALUES (?)", "questionnaire_id");
    }


    public List<Questionnaire> list() throws SQLException {
        return list("SELECT * FROM questionnaires");
    }


    @Override
    protected Questionnaire readFromResultSet(ResultSet rs) throws SQLException {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setName(rs.getString("name"));
        questionnaire.setID(rs.getLong("questionnaire_id"));
        return questionnaire;
    }

    @Override
    protected void insertIntoDatabase(Questionnaire questionnaire, PreparedStatement statement) throws SQLException {
        statement.setString(1, questionnaire.getName());
    }
}
