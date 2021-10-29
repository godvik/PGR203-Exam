package no.kristiania.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

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

    @Test
    void shouldReadFileFromDisk() throws IOException {
        String fileContent = "A file created at " + LocalDateTime.now();
        Files.write(Paths.get("target/test-classes/example-file.txt"), fileContent.getBytes());
        server.setRoot(Paths.get("target/test-classes"));
        HttpClient client = new HttpClient("localhost", server.getPort(), "/example-file.txt");
        assertEquals(fileContent, client.getMessageBody());
    }



}