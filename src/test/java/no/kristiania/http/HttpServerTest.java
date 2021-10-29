package no.kristiania.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpServerTest {

    private final HttpServer server = new HttpServer(0);

    HttpServerTest() throws IOException {
    }

    @Test
    void shouldReturn404ForUnknownRequestTarget() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/does-not-exist");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldIncludeRequestTargetIn404Message() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/does-not-exist");
        assertEquals("File not found /does-not-exist", client.getMessageBody());
    }



}