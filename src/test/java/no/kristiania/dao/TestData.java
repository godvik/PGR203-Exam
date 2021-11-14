package no.kristiania.dao;

import no.kristiania.objects.Option;
import no.kristiania.objects.Question;
import no.kristiania.objects.Questionnaire;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Random;

public class TestData {
    public static DataSource testDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:questionnairedb;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }

    public static Random random = new Random();

    public static Questionnaire exampleQuestionnaire() {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setName(pickOne("Education", "Health", "Environment", "Economy"));
        return questionnaire;
    }

    public static String pickOne(String... alternatives) {
        return alternatives[random.nextInt(alternatives.length)];
    }

    public static Question exampleQuestion(Questionnaire questionnaire) throws SQLException {
        Question question = new Question();
        question.setQuestionnaire(questionnaire.getId());
        question.setTitle(TestData.pickOne("On a scale from 1-5, how happy are you with your...", "On a scale from 1-5, would you say you agree with.."));
        question.setLowLabel("Bad");
        question.setHighLabel("Excellent");
        return question;
    }

    public static Option exampleOption(Question question) {
        Option option = new Option();
        option.setText(pickOne("How do you like school?", "You are often stressed", "You stress eat", "Have trouble sleeping due to stress"));
        option.setQuestion(question.getId());
        return option;
    }
}
