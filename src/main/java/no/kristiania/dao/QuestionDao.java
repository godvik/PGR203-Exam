package no.kristiania.dao;

import no.kristiania.objects.Question;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionDao extends AbstractDao<Question> {

    public QuestionDao(DataSource dataSource) {
        super(dataSource);
    }

    // Insert into question table. Using the abstract method insert
    public long insert(Question question) throws SQLException {
        return insert(question, "INSERT INTO QUESTIONS (title, questionnaire_id, low_label, high_label) VALUES (?, ?, ?, ?)", "question_id");
    }

    // List all from question table. Using abstract method list
    public List<Question> list() throws SQLException {
        return list("SELECT * FROM questions");
    }

    // Delete a question based by id. Using abstract method delete
    public void delete(long id) throws SQLException {
        delete("DELETE FROM Questions WHERE Question_id = ?", id);
    }

    // Updates a question based by id. Using abstract method update.
    public void update(String title, long id) throws SQLException {
        update("UPDATE questions SET title = (?) WHERE question_id = (?)", title, id);
    }

    // Retrieves a question based by id. Using abstract method retrieve.
    public Question retrieve(long id) throws SQLException {
        return retrieve(id, "SELECT * FROM questions WHERE question_id = ?");
    }

    @Override
    protected Question readFromResultSet(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setId(rs.getLong("question_id"));
        question.setTitle(rs.getString("title"));
        question.setQuestionnaire(rs.getLong("questionnaire_id"));
        question.setHighLabel(rs.getString("high_label"));
        question.setLowLabel(rs.getString("low_label"));
        return question;
    }

    @Override
    protected void insertIntoDatabase(Question obj, PreparedStatement statement) throws SQLException {
        statement.setString(1, obj.getTitle());
        statement.setLong(2, obj.getQuestionnaire());
        statement.setString(3, obj.getLowLabel());
        statement.setString(4, obj.getHighLabel());
    }

    // Own method in question dao. Lists out questions by questionnaire id.
    public List<Question> listByQuestionnaire(long questionnaire_id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM questions WHERE questionnaire_id = ?"
            )) {
                statement.setLong(1, questionnaire_id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    ArrayList<Question> questions = new ArrayList<>();
                    while (resultSet.next()) {
                        Question question = readFromResultSet(resultSet);
                        questions.add(question);
                    }
                    return questions;
                }
            }
        }
    }
}