package no.kristiania.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpServerTest {

    @Test
    void shouldReturn404ForUnknownRequestTarget() throws IOException {
        HttpServer server = new HttpServer(1001);
        HttpClient client = new HttpClient("localhost", 1001, "/does-not-exist");
        assertEquals(404, client.getStatusCode());
    }

}