package no.kristiania.dao;

import no.kristiania.objects.Question;
import no.kristiania.objects.Questionnaire;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class QuestionDaoTest {

    private final QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
    private final QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());

    @Test
    void shouldListSavedQuestions() throws SQLException {
        Questionnaire questionnaire = TestData.exampleQuestionnaire();
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
        questionnaire.setId(questionnaireDao.insert(questionnaire));
        Question question = TestData.exampleQuestion(questionnaire);
        question.setId(questionDao.insert(question));

        assertThat(questionDao.list())
                .extracting(Question::getId)
                .contains(question.getId());
    }

    @Test
    void shouldListQuestionsByQuestionnaire() throws SQLException {
//        Create 2 questionnaires
        Questionnaire questionnaire = TestData.exampleQuestionnaire();
        Questionnaire anotherQuestionnaire = TestData.exampleQuestionnaire();
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
        questionnaire.setId(questionnaireDao.insert(questionnaire));
        anotherQuestionnaire.setId(questionnaireDao.insert(anotherQuestionnaire));

//        Create 3 questions and add them to 2 different questionnaires
        Question question = TestData.exampleQuestion(anotherQuestionnaire);
        Question anotherQuestion = TestData.exampleQuestion(questionnaire);
        Question yetAnotherQuestion = TestData.exampleQuestion(questionnaire);
        question.setId(questionDao.insert(question));
        anotherQuestion.setId(questionDao.insert(anotherQuestion));
        yetAnotherQuestion.setId(questionDao.insert(yetAnotherQuestion));

        assertThat(questionDao.listByQuestionnaire(anotherQuestion.getQuestionnaire()))
                .extracting(Question::getId)
                .contains(anotherQuestion.getId(), yetAnotherQuestion.getId())
                .doesNotContain(question.getId());
    }

    @Test
    void shouldDeleteQuestions() throws SQLException {
        Questionnaire questionnaire = TestData.exampleQuestionnaire();
        questionnaireDao.insert(questionnaire);
        questionnaire.setId(questionnaireDao.insert(questionnaire));

        Question question = TestData.exampleQuestion(questionnaire);
        Question question2 = TestData.exampleQuestion(questionnaire);

        questionDao.insert(question);
        questionDao.insert(question2);
        question.setId(questionDao.insert(question));
        question2.setId(questionDao.insert(question2));

        questionDao.delete(question.getId());

        assertThat(questionDao.list())
                .extracting(Question::getId)
                .contains(question2.getId())
                .doesNotContain(question.getId());
    }

    @Test
    void shouldUpdateQuestionTitle() throws SQLException {
        Questionnaire questionnaire = TestData.exampleQuestionnaire();
        questionnaireDao.insert(questionnaire);
        questionnaire.setId(questionnaireDao.insert(questionnaire));

        Question question = TestData.exampleQuestion(questionnaire);
        questionDao.insert(question);
        question.setId(questionDao.insert(question));
        questionDao.update("Hva synes du om avansert java?", question.getId());

        assertEquals("Hva synes du om avansert java?", questionDao.retrieve(question.getId()).getTitle());
    }
}