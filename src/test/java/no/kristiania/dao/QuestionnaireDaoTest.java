package no.kristiania.dao;

import no.kristiania.http.Questionnaire;
import org.junit.jupiter.api.Test;

class QuestionnaireDaoTest {

  private final QuestionnaireDao dao = new QuestionnaireDao(TestData.testDataSource());

    @Test
    void shouldListSavedQuestionnaires() {
        Questionnaire questionnaire1 = TestData.exampleQuestionnaire();
        Questionnaire questionnaire2 = TestData.exampleQuestionnaire();

        dao.save(questionnaire1);
        dao.save(questionnaire2);

        assertThat(dao.listAll())
                .extraction(Questionnaire::getId)
                .contains(questionnaire1.getId(), questionnaire2.getId());
    }
}