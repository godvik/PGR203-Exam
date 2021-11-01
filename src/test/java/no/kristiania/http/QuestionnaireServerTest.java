package no.kristiania.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.net.URLEncoder.encode;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuestionnaireServerTest {
    private final HttpServer server = new HttpServer(0);

    public QuestionnaireServerTest() throws IOException {
    }

    @Test
    void shouldPostNewQuestionnaire() throws IOException {
        PostHttpClient client = new PostHttpClient("localhost", server.getPort(), "/api/questionnaire", "questionnaire=Education");
        assertEquals(200, client.getResponseCode());
        Questionnaire questionnaire = server.getQuestionnaire().get(0);
        assertEquals("Education", questionnaire.getName());
    }

    @Test
    void shouldReturnQuestionnairesFromServer() throws IOException {
        Questionnaire educationQuestionnaire = new Questionnaire();
        educationQuestionnaire.setName("Education");
        Questionnaire healthQuestionnaire = new Questionnaire();
        healthQuestionnaire.setName("Health");

        server.setQuestionnaire(List.of(educationQuestionnaire, healthQuestionnaire));
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
        server.setQuestionnaire(List.of(educationQuestionnaire, healthQuestionnaire));
        PostHttpClient client = new PostHttpClient("localhost", server.getPort(), "/api/questions",
                "questionnaire=Education&title=Happy&text=" + encodedQuestion);
        assertEquals(200, client.getResponseCode());
        Question question = server.getQuestion().get(0);
        assertEquals("Education", question.getQuestionnaire());
        assertEquals("Happy", question.getTitle());
        assertEquals("On a scale from 1-5, how happy are you?", question.getText());
    }
}
