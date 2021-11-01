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
    void shouldPostNewQuestion() throws IOException {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setName("Education");

        Questionnaire questionnaire2 = new Questionnaire();
        questionnaire2.setName("Health");

        String questionQuery = "On a scale from 1-5, how happy are you?";
        String encodedQuestion = encode(questionQuery, StandardCharsets.UTF_8);
        server.setQuestionnaire(List.of(questionnaire, questionnaire2));
        PostHttpClient client = new PostHttpClient("localhost", server.getPort(), "/api/questions",
                "questionnaire=Education&title=Happy&text=" + encodedQuestion);
        assertEquals(200, client.getResponseCode());
        Question question = server.getQuestion().get(0);
        assertEquals("Health", question.getQuestionniare());
        assertEquals("Happy", question.getTitle());
        assertEquals("On a scale from 1-5, how happy are you?", question.getText());
    }
}
