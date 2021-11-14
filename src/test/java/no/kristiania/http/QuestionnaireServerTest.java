package no.kristiania.http;

import no.kristiania.controllers.*;
import no.kristiania.dao.*;
import no.kristiania.objects.Answer;
import no.kristiania.objects.Option;
import no.kristiania.objects.Question;
import no.kristiania.objects.Questionnaire;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import static java.net.URLEncoder.encode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuestionnaireServerTest {
    private final HttpServer server = new HttpServer(0);
    private final OptionDao optionDao = new OptionDao(TestData.testDataSource());

    public QuestionnaireServerTest() throws IOException {
    }


    @BeforeEach
    void setUp() {
        Flyway.configure().dataSource(TestData.testDataSource()).load().clean();
    }


    @Test
    void shouldPostNewQuestionnaire() throws IOException, SQLException {
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
        server.addController("/api/questionnaire", new AddQuestionnaireController(questionnaireDao));

        HttpClient postClient = new HttpClient(
                "localhost",
                server.getPort(),
                "/api/questionnaire",
                "questionnaire=Education"
        );
        assertEquals(303, postClient.getStatusCode());
        assertThat(questionnaireDao.list())
                .anySatisfy(p -> {
                    assertThat(p.getId()).isEqualTo(1);
                    assertThat(p.getName()).isEqualTo("Education");

                });

    }

    @Test
    void shouldReturnQuestionnairesFromServer() throws IOException, SQLException {
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
        server.addController("/api/listQuestionnaires", new ListQuestionnaireController(questionnaireDao));

        Questionnaire educationQuestionnaire = new Questionnaire();
        educationQuestionnaire.setName("Environment");
        Questionnaire healthQuestionnaire = new Questionnaire();
        healthQuestionnaire.setName("Health");
        questionnaireDao.insert(educationQuestionnaire);
        questionnaireDao.insert(healthQuestionnaire);


        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/listQuestionnaires");
        assertEquals("<option value=1>Education</option>" +
                        "<option value=2>Environment</option>" +
                        "<option value=3>Health</option>",
                client.getMessageBody());
    }

    @Test
    void shouldPostNewQuestion() throws IOException, SQLException {
        Questionnaire educationQuestionnaire = new Questionnaire();
        educationQuestionnaire.setName("Education");

        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
        educationQuestionnaire.setId(questionnaireDao.insert(educationQuestionnaire));

        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        server.addController("/api/questions", new AddQuestionController(questionDao));

        String questionQuery = "On a scale from 1-5, how happy are you?";
        String encodedQuestion = encode(questionQuery, StandardCharsets.UTF_8);

        new HttpClient("localhost", server.getPort(), "/api/questions",
                "questionnaire=1&title=" + encodedQuestion + "&low_label=bad&high_label=good");

        assertThat(questionDao.list())
                .anySatisfy(p -> {
                    assertThat(p.getQuestionnaire()).isEqualTo(1);
                    assertThat(p.getTitle()).isEqualTo(questionQuery);
                });
    }

    @Test
    void shouldReturnQuestionsFromServer() throws IOException, SQLException {
        Questionnaire educationQuestionnaire = new Questionnaire();
        educationQuestionnaire.setName("Education");
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        educationQuestionnaire.setId(questionnaireDao.insert(educationQuestionnaire));

        Question question = TestData.exampleQuestion(educationQuestionnaire);
        server.addController("/api/listOutQuestions", new ListOutQuestionsController(questionnaireDao, questionDao, optionDao));

        question.setId(questionDao.insert(question));


        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/listOutQuestions");
        assertThat(client.getMessageBody()).contains("<legend>" + questionDao.retrieve(1).getTitle() + "</legend>");
    }


    @Test
    void listQuestionsWhenAddingOption() throws IOException, SQLException {
        Questionnaire educationQuestionnaire = new Questionnaire();
        educationQuestionnaire.setName("Education");
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        educationQuestionnaire.setId(questionnaireDao.insert(educationQuestionnaire));

        Question firstDummyQuestion = TestData.exampleQuestion(educationQuestionnaire);
        Question secondDummyQuestion = TestData.exampleQuestion(educationQuestionnaire);
        server.addController("/api/questionOptions", new ListQuestionsToOptionController(questionDao));
        firstDummyQuestion.setId(questionDao.insert(firstDummyQuestion));
        secondDummyQuestion.setQuestionnaire(questionDao.insert(secondDummyQuestion));

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/questionOptions");
        assertThat(client.getMessageBody().contains("<option value=On a scale from 1 - 5, how happy are you?>On a scale from 1 - 5, how happy are you?</option>" +
                "<option value=On a scale from 1 - 5, how good is our exam?>On a scale from 1 - 5, how good is our exam?</option>"));
    }

    @Test
    void shouldPostOptionsToQuestion() throws IOException, SQLException {
        Questionnaire educationQuestionnaire = new Questionnaire();
        educationQuestionnaire.setName("Education");
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        educationQuestionnaire.setId(questionnaireDao.insert(educationQuestionnaire));

        Question question = TestData.exampleQuestion(educationQuestionnaire);
        server.addController("/api/addOptions", new AddOptionController(optionDao));
        question.setId(questionDao.insert(question));
        Option option = TestData.exampleOption(question);
        option.setId(optionDao.insert(option));

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/addOptions",
                "question=" + option.getQuestion() + "&option=" + encode(option.getText(), StandardCharsets.UTF_8));

        assertEquals(303, client.getStatusCode());
        assertThat(optionDao.list())
                .anySatisfy(o -> {
                    assertThat(o.getQuestion()).isEqualTo(option.getQuestion());
                    assertThat(o.getText()).isEqualTo(option.getText());
                });
    }

    @Test
    void shouldDeleteOptionFromDatabase() throws SQLException, IOException {
        Questionnaire educationQuestionnaire = new Questionnaire();
        educationQuestionnaire.setName("Education");
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        educationQuestionnaire.setId(questionnaireDao.insert(educationQuestionnaire));
        OptionDao optionDao = new OptionDao(TestData.testDataSource());
        Question question = TestData.exampleQuestion(educationQuestionnaire);
        server.addController("/api/deleteOption", new DeleteOptionsController(optionDao));
        server.addController("/api/optionsList", new ListOptionsController(optionDao));
        server.addController("/api/addOptions", new AddOptionController(optionDao));
        question.setId(questionDao.insert(question));
        Option firstOption = TestData.exampleOption(question);
        Option secondOption = TestData.exampleOption(question);
        Option thirdOption = TestData.exampleOption(question);
        firstOption.setId(optionDao.insert(firstOption));
        secondOption.setId(optionDao.insert(secondOption));
        thirdOption.setId(optionDao.insert(thirdOption));

//        Add options using controller
        new HttpClient("localhost", server.getPort(), "/api/addOptions",
                "question=" + firstOption.getQuestion() + "&option=" + encode(firstOption.getText(), StandardCharsets.UTF_8));
        new HttpClient("localhost", server.getPort(), "/api/addOptions",
                "question=" + secondOption.getQuestion() + "&option=" + encode(secondOption.getText(), StandardCharsets.UTF_8));
        new HttpClient("localhost", server.getPort(), "/api/addOptions",
                "question=" + thirdOption.getQuestion() + "&option=" + encode(thirdOption.getText(), StandardCharsets.UTF_8));
        new HttpClient("localhost", server.getPort(), "/api/optionsList");
//        Delete one option
        new HttpClient("localhost", server.getPort(), "/api/deleteOption", "option=" + firstOption.getId());

        assertThat(optionDao.list())
                .extracting(Option::getId)
                .contains(secondOption.getId(), thirdOption.getId())
                .doesNotContain(firstOption.getId());
    }

    @Test
    void shouldDeleteQuestionFromDatabase() throws SQLException, IOException {
        Questionnaire educationQuestionnaire = new Questionnaire();
        educationQuestionnaire.setName("Education");
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        educationQuestionnaire.setId(questionnaireDao.insert(educationQuestionnaire));
        Question firstQuestion = TestData.exampleQuestion(educationQuestionnaire);
        Question secondQuestion = TestData.exampleQuestion(educationQuestionnaire);
        server.addController("/api/addQuestion", new AddQuestionController(questionDao));
        server.addController("/api/deleteQuestion", new DeleteQuestionController(questionDao));
        firstQuestion.setId(questionDao.insert(firstQuestion));
        secondQuestion.setId(questionDao.insert(secondQuestion));

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/questions",
                "questionnaire=" + firstQuestion.getQuestionnaire() + "&title=" + firstQuestion.getTitle() +
                        "&low_label=" + firstQuestion.getLowLabel() + "&high_label=" + firstQuestion.getHighLabel());
        HttpClient client2 = new HttpClient("localhost", server.getPort(), "/api/questions",
                "questionnaire=" + secondQuestion.getQuestionnaire() + "&title=" + secondQuestion.getTitle() +
                        "&low_label=" + secondQuestion.getLowLabel() + "&high_label=" + secondQuestion.getHighLabel());
        assertThat(questionDao.list())
                .extracting(Question::getId)
                .contains(firstQuestion.getId(), secondQuestion.getId());

        HttpClient client3 = new HttpClient("localhost", server.getPort(), "/api/deleteQuestion", "question=" + firstQuestion.getId());

        assertThat(questionDao.list())
                .extracting(Question::getId)
                .contains(secondQuestion.getId())
                .doesNotContain(firstQuestion.getId());
    }

    @Test
    void shouldDeleteQuestionnaireFromDatabase() throws SQLException, IOException {
        Questionnaire educationQuestionnaire = new Questionnaire();
        educationQuestionnaire.setName("Education");
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
        educationQuestionnaire.setId(questionnaireDao.insert(educationQuestionnaire));
        server.addController("/api/questionnaire", new AddQuestionnaireController(questionnaireDao));
        server.addController("/api/deleteQuestionnaire", new DeleteQuestionnaireController(questionnaireDao));

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/questionnaire",
                "questionnaire=" + educationQuestionnaire.getName());

        assertThat(questionnaireDao.list())
                .extracting(Questionnaire::getId)
                .contains(educationQuestionnaire.getId());

        HttpClient client3 = new HttpClient("localhost", server.getPort(), "/api/deleteQuestionnaire",
                "questionnaire_id=" + educationQuestionnaire.getId());

        assertThat(questionnaireDao.list())
                .extracting(Questionnaire::getId)
                .doesNotContain(educationQuestionnaire.getId());
    }

    @Test
    void shouldEditOptionFromDatabase() throws SQLException, IOException {
        Questionnaire educationQuestionnaire = new Questionnaire();
        educationQuestionnaire.setName("Education");
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        educationQuestionnaire.setId(questionnaireDao.insert(educationQuestionnaire));
        OptionDao optionDao = new OptionDao(TestData.testDataSource());
        Question question = TestData.exampleQuestion(educationQuestionnaire);
        server.addController("/api/editOption", new EditOptionController(optionDao));
        question.setId(questionDao.insert(question));
        Option firstOption = TestData.exampleOption(question);
        firstOption.setId(optionDao.insert(firstOption));

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/editOption", "option=" + firstOption.getId() + "&text=newOptionName");
        assertEquals(optionDao.retrieve(firstOption.getId()).getText(), "newOptionName");
    }

    @Test
    void shouldEditQuestionFromDatabase() throws SQLException, IOException {
        Questionnaire educationQuestionnaire = new Questionnaire();
        educationQuestionnaire.setName("Education");
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        educationQuestionnaire.setId(questionnaireDao.insert(educationQuestionnaire));
        Question question = TestData.exampleQuestion(educationQuestionnaire);
        server.addController("/api/editQuestion", new EditQuestionController(questionDao));
        question.setId(questionDao.insert(question));

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/editQuestion", "question=" + question.getId() + "&text=newQuestionText");
        assertEquals(questionDao.retrieve(question.getId()).getTitle(), "newQuestionText");
    }

    @Test
    void shouldEditQuestionnaireFromDatabase() throws SQLException, IOException {
        Questionnaire educationQuestionnaire = new Questionnaire();
        educationQuestionnaire.setName("Education");
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());
        educationQuestionnaire.setId(questionnaireDao.insert(educationQuestionnaire));

        server.addController("/api/editQuestionnaire", new EditQuestionnaireController(questionnaireDao));

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/editQuestionnaire", "questionnaire_id=" + educationQuestionnaire.getId() + "&questionnaire=Health");
        assertEquals(questionnaireDao.retrieve(educationQuestionnaire.getId()).getName(), "Health");
    }

    @Test
    void shouldPostAndListAnswers() throws SQLException, IOException {
        AnswerDao answerDao = new AnswerDao(TestData.testDataSource());
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());

        Questionnaire questionnaire = TestData.exampleQuestionnaire();
        questionnaire.setId(questionnaireDao.insert(questionnaire));
        Question question = TestData.exampleQuestion(questionnaire);
        question.setId(questionDao.insert(question));
        Option option = TestData.exampleOption(question);
        option.setId(optionDao.insert(option));


        server.addController("/api/submitQuestionnaire", new SubmitQuestionnaireController(questionDao, optionDao, answerDao));
        server.addController("/api/answers", new ListAnswersController(questionnaireDao, questionDao, optionDao, answerDao));

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/submitQuestionnaire","option1=1");

        assertEquals(303, client.getStatusCode());
        assertThat(answerDao.retrieve(1));

        new HttpClient("localhost", server.getPort(), "/api/answers");

        assertThat(answerDao.list())
                .extracting(Answer::getId)
                .isNotEmpty();
    }
}
