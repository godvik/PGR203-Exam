package no.kristiania.dao;

import no.kristiania.objects.Questionnaire;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionnaireDao {

    private final DataSource datasource;

    public QuestionnaireDao(DataSource dataSource) {
        this.datasource = dataSource;

    }

    public void save(Questionnaire questionnaire) {
        try (Connection connection = datasource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO questionnaires (name) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, questionnaire.getName());
                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    questionnaire.setID(rs.getLong("questionnaire_id"));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public List<Questionnaire> listAll() throws SQLException {
        try (Connection connection = datasource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM questionnaires")) {
                try (ResultSet rs = statement.executeQuery()) {
                    ArrayList<Questionnaire> questionnaires = new ArrayList<>();
                    while (rs.next()) {
                        Questionnaire questionnaire = new Questionnaire();
                        questionnaire.setName(rs.getString("name"));
                        questionnaire.setID(rs.getLong("questionnaire_id"));
                        questionnaires.add(questionnaire);
                    }
                    return questionnaires;
                }
            }
        }

    }
}
