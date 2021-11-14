package no.kristiania.dao;

import no.kristiania.controllers.AddOptionController;
import no.kristiania.controllers.EditOptionController;
import no.kristiania.http.HttpServer;
import no.kristiania.objects.Option;
import no.kristiania.objects.Question;
import no.kristiania.objects.Questionnaire;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OptionDaoTest {

    private final OptionDao optionDao = new OptionDao(TestData.testDataSource());
    private final QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
    private final QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
    private final HttpServer server = new HttpServer(0);

    public OptionDaoTest() throws IOException {
    }

    @Test
    void shouldCreateAndListOptions() throws SQLException {
        Questionnaire questionnaire = TestData.exampleQuestionnaire();
        questionnaire.setId(questionnaireDao.insert(questionnaire));
        Question question = TestData.exampleQuestion(questionnaire);
        question.setId(questionDao.insert(question));

        Option option = TestData.exampleOption(question);
        option.setId(optionDao.insert(option));

        assertThat(optionDao.list())
                .extracting(Option::getId)
                .contains(option.getId());
    }

    @Test
    void shouldListOptionsByQuestion() throws SQLException {
        Questionnaire questionnaire = TestData.exampleQuestionnaire();
        questionnaire.setId(questionnaireDao.insert(questionnaire));
        Question question = TestData.exampleQuestion(questionnaire);
        Question anotherQuestion = TestData.exampleQuestion(questionnaire);
        question.setId(questionDao.insert(question));
        anotherQuestion.setId(questionDao.insert(anotherQuestion));

        Option firstOption = TestData.exampleOption(question);
        Option secondOption = TestData.exampleOption(question);
        Option thirdOption = TestData.exampleOption(anotherQuestion);
        firstOption.setId(optionDao.insert(firstOption));
        secondOption.setId(optionDao.insert(secondOption));
        thirdOption.setId(optionDao.insert(thirdOption));

        assertThat(optionDao.listByQuestion(firstOption.getQuestion()))
                .extracting(Option::getId)
                .contains(firstOption.getId(), secondOption.getId())
                .doesNotContain(thirdOption.getId());
    }

    @Test
    void shouldDeleteOption() throws SQLException {
//        Create questionnaire and attach a question
        Questionnaire questionnaire = TestData.exampleQuestionnaire();
        questionnaire.setId(questionnaireDao.insert(questionnaire));
        Question question = TestData.exampleQuestion(questionnaire);
        question.setId(questionDao.insert(question));
//        Create and attach 3 options to question
        Option firstOption = TestData.exampleOption(question);
        Option secondOption = TestData.exampleOption(question);
        Option thirdOption = TestData.exampleOption(question);
        firstOption.setId(optionDao.insert(firstOption));
        secondOption.setId(optionDao.insert(secondOption));
        thirdOption.setId(optionDao.insert(thirdOption));
//        Delete an option
        optionDao.delete(firstOption.getId());

        assertThat(optionDao.list())
                .extracting(Option::getId)
                .contains(secondOption.getId(), thirdOption.getId())
                .doesNotContain(firstOption.getId());
    }

    @Test
    void shouldCascadeDeleteWhenDeletingQuestionnaire() throws SQLException {
        //        Create questionnaire and attach a question
        Questionnaire questionnaire = TestData.exampleQuestionnaire();
        questionnaire.setId(questionnaireDao.insert(questionnaire));
        Question question = TestData.exampleQuestion(questionnaire);
        question.setId(questionDao.insert(question));
//        Create and attach 3 options to question
        Option firstOption = TestData.exampleOption(question);
        Option secondOption = TestData.exampleOption(question);
        Option thirdOption = TestData.exampleOption(question);
        firstOption.setId(optionDao.insert(firstOption));
        secondOption.setId(optionDao.insert(secondOption));
        thirdOption.setId(optionDao.insert(thirdOption));
// Delete the questionnaire
        questionnaireDao.delete(questionnaire.getId());
// Assert that related rows in other tables are also deleted
        assertThat(questionnaireDao.list())
                .extracting(Questionnaire::getId)
                .doesNotContain(questionnaire.getId());
        assertThat(questionDao.list())
                .extracting(Question::getId)
                .doesNotContain(question.getId());
        assertThat(optionDao.list())
                .extracting(Option::getId)
                .doesNotContain(firstOption.getId(), secondOption.getId(), thirdOption.getId());
    }

    @Test
    void shouldUpdateOptionText() throws SQLException {
        // Create questionnaire and attach a question
        Questionnaire questionnaire = TestData.exampleQuestionnaire();
        questionnaire.setId(questionnaireDao.insert(questionnaire));
        Question question = TestData.exampleQuestion(questionnaire);
        question.setId(questionDao.insert(question));
        // Create an option to a question
        Option firstOption = TestData.exampleOption(question);
        firstOption.setId(optionDao.insert(firstOption));
        // Update option text
        optionDao.update("Luften på skolen er bra", firstOption.getId());

        assertEquals("Luften på skolen er bra", optionDao.retrieve(firstOption.getId()).getText());

    }
}
