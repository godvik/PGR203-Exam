package no.kristiania.dao;

import no.kristiania.objects.Questionnaire;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionnaireDaoTest {

  private final QuestionnaireDao dao = new QuestionnaireDao(TestData.testDataSource());

    @Test
    void shouldListSavedQuestionnaires() throws SQLException {
        Questionnaire questionnaire1 = TestData.exampleQuestionnaire();
        Questionnaire questionnaire2 = TestData.exampleQuestionnaire();

        dao.save(questionnaire1);
        dao.save(questionnaire2);

        assertThat(dao.listAll())
                .extracting(Questionnaire::getId)
                .contains(questionnaire1.getId(), questionnaire2.getId());
    }
}