package no.kristiania.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpClientTest {

    @Test
    void shouldRecieveStatus200() {
        HttpClient client = new HttpClient("httpbin.org", 80, "status/200");
        assertEquals(200, client.getStatusCode());
    }
}