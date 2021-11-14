package no.kristiania.dao;

import no.kristiania.objects.Questionnaire;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class QuestionnaireDaoTest {

  private final QuestionnaireDao dao = new QuestionnaireDao(TestData.testDataSource());

    @Test
    void shouldListSavedQuestionnaires() throws SQLException {
        Questionnaire questionnaire1 = TestData.exampleQuestionnaire();
        Questionnaire questionnaire2 = TestData.exampleQuestionnaire();

        dao.insert(questionnaire1);
        dao.insert(questionnaire2);
        questionnaire1.setId(dao.insert(questionnaire1));
        questionnaire2.setId(dao.insert(questionnaire2));

        assertThat(dao.list())
                .extracting(Questionnaire::getId)
                .contains(questionnaire1.getId(), questionnaire2.getId());
    }

    @Test
    void shouldDeleteQuestionnaire() throws SQLException {
        Questionnaire questionnaire1 = TestData.exampleQuestionnaire();
        Questionnaire questionnaire2 = TestData.exampleQuestionnaire();

        dao.insert(questionnaire1);
        dao.insert(questionnaire2);
        questionnaire1.setId(dao.insert(questionnaire1));
        questionnaire2.setId(dao.insert(questionnaire2));

        dao.delete(questionnaire2.getId());

        assertThat(dao.list())
                .extracting(Questionnaire::getId)
                .contains(questionnaire1.getId())
                .doesNotContain(questionnaire2.getId());
    }

    @Test
    void shouldUpdateQuestionnaire() throws SQLException {
        Questionnaire q1 = TestData.exampleQuestionnaire();
        dao.insert(q1);
        q1.setId(dao.insert(q1));
        dao.update("Ny undersøkelse", q1.getId());
        assertEquals("Ny undersøkelse", dao.retrieve(q1.getId()).getName());
    }
}