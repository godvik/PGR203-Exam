package no.kristiania.dao;

import no.kristiania.objects.Answer;
import no.kristiania.objects.Option;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AnswerDao extends AbstractDao<Answer> {
    public AnswerDao(DataSource dataSource) {
        super(dataSource);
    }

    // Lists all answers from answer table
    public List<Answer> list() throws SQLException {
        return list("SELECT * FROM answers");
    }

    // Inserts answer into the answers table
    public long insert(Answer answer) throws SQLException {
        return insert(answer, "INSERT INTO ANSWERS (questionnaire_id, question_id, option_id, answer_value) VALUES (?, ?, ?, ?)", "answer_id");
    }

    // Retrieve answer, based by id. Using abstract retrieve method.
    public Answer retrieve(long id) throws SQLException {
        return retrieve(id, "SELECT * FROM Answers WHERE answer_id = ?");
    }

    @Override
    protected Answer readFromResultSet(ResultSet rs) throws SQLException {
        Answer answer = new Answer();
        answer.setId(rs.getLong("answer_id"));
        answer.setQuestionnaire(rs.getLong("questionnaire_id"));
        answer.setQuestion(rs.getLong("question_id"));
        answer.setOption(rs.getLong("option_id"));
        answer.setValue(rs.getString("answer_value"));
        return answer;
    }

    @Override
    protected void insertIntoDatabase(Answer obj, PreparedStatement statement) throws SQLException {
        statement.setLong(1, obj.getQuestionnaire());
        statement.setLong(2, obj.getQuestion());
        statement.setLong(3, obj.getOption());
        statement.setString(4, obj.getValue());
    }
}
