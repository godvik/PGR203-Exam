package no.kristiania.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.net.URLEncoder.encode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuestionnaireServerTest {
    private final HttpServer server = new HttpServer(0);

    public QuestionnaireServerTest() throws IOException {
    }

    @Test
    void shouldPostNewQuestionnaire() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/questionnaire", "questionnaire=Education");
        assertEquals(200, client.getStatusCode());
        Questionnaire questionnaire = server.getQuestionnaires().get(0);
        assertEquals("Education", questionnaire.getName());
    }

    @Test
    void shouldReturnQuestionnairesFromServer() throws IOException {
        Questionnaire educationQuestionnaire = new Questionnaire();
        educationQuestionnaire.setName("Education");
        Questionnaire healthQuestionnaire = new Questionnaire();
        healthQuestionnaire.setName("Health");
        server.questionnaires.add(educationQuestionnaire);
        server.questionnaires.add(healthQuestionnaire);

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/listQuestionnaires");
        assertEquals("<option value=Education>Education</option>" +
                        "<option value=Health>Health</option>",
                client.getMessageBody());
    }

    @Test
    void shouldPostNewQuestion() throws IOException {
        Questionnaire educationQuestionnaire = new Questionnaire();
        educationQuestionnaire.setName("Education");
        Questionnaire healthQuestionnaire = new Questionnaire();
        healthQuestionnaire.setName("Health");

        String questionQuery = "On a scale from 1-5, how happy are you?";
        String encodedQuestion = encode(questionQuery, StandardCharsets.UTF_8);
        server.questionnaires.add(educationQuestionnaire);
        server.questionnaires.add(healthQuestionnaire);
        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/questions",
                "questionnaire=Education&text=" + encodedQuestion);
        assertEquals(200, client.getStatusCode());
        Question question = server.getQuestion().get(0);
        assertEquals("Education", question.getQuestionnaire());
        assertEquals("On a scale from 1-5, how happy are you?", question.getText());
    }

    @Test
    void shouldReturnQuestionsFromServer() throws IOException {
        Questionnaire educationQuestionnaire = new Questionnaire();
        educationQuestionnaire.setName("Education");

        server.getQuestionnaires().add(educationQuestionnaire);
        String questionQuery = "On a scale from 1-5, how happy are you?";
        Question question = new Question();
        question.setQuestionnaire(server.getQuestionnaires().get(0).getName());;
        question.setText(questionQuery);
        server.getQuestion().add(question);

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/listOutQuestions");
        assertThat(client.getMessageBody()).contains("<legend>On a scale from 1-5, how happy are you?</legend>");
    }

    @Test
    void listQuestionsWhenAddingOption() throws IOException {
        Question firstDummyQuestion = new Question();
        firstDummyQuestion.setText("On a scale from 1-5, how happy are you?");
        Question secondDummyQuestion = new Question();
        secondDummyQuestion.setText("On a scale from 1-5, how good is our exam?");
        server.getQuestion().add(firstDummyQuestion);
        server.getQuestion().add(secondDummyQuestion);

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/questionOptions");
        assertEquals("<option value=On a scale from 1-5, how happy are you?>On a scale from 1-5, how happy are you?</option>" +
                        "<option value=On a scale from 1-5, how good is our exam?>On a scale from 1-5, how good is our exam?</option>",
                client.getMessageBody());
    }

    @Test
    void shouldPostOptionsToQuestion() throws IOException {
        Question firstDummyQuestion = new Question();
        firstDummyQuestion.setText("On a scale from 1-5, how happy are you?");
        Question secondDummyQuestion = new Question();
        secondDummyQuestion.setText("Test fra 1 - 5 hvor noob er Aastralis?");
        server.getQuestion().add(firstDummyQuestion);
        server.getQuestion().add(secondDummyQuestion);
        String optionText = encode( "The education is very good", StandardCharsets.UTF_8);

        HttpClient client = new HttpClient("localhost", server.getPort(), "/api/alternativeAnswers",
                "question=On+a+scale+from+1-5%2C+how+happy+are+you%3F&option=" + optionText);

        assertEquals(200, client.getStatusCode());
        Option option = server.getOptions().get(0);
        assertEquals("On a scale from 1-5, how happy are you?", option.getQuestion());
        assertEquals("The education is very good", option.getText());

    }
}
