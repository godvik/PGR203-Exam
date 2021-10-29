package no.kristiania.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.net.URLDecoder.decode;
import static java.net.URLEncoder.encode;


//import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuestionnaireServerTest {
    private final HttpServer server = new HttpServer(0);

    public QuestionnaireServerTest() throws IOException {
    }

    @Test
    void shouldPostNewQuestion() {
        String question = "On a scale from 1-5, how happy are you?";
        String encodedQuestion = encode(question, StandardCharsets.UTF_8);
        server.setQuestionToQuestionnaire(List.of("Environment", "Education"));
        PostHttpClient client = new PostHttpClient("localhost", server.getPort(), "/api/questions",
                "questionnaire=Education&title=Happy&text=" + encodedQuestion);
        assertEquals(200, client.getResponseCode());
        Question question = server.getQuestion().get(0);
        assertEquals("Education", question.getQuestionniare());
        assertEquals("Happy", question.getTitle());
        assertEquals("On a scale from 1-5, how happy are you?", question.getText());
    }
}
