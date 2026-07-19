package hello.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.nio.charset.StandardCharsets;

@Component
public class TextHandler implements HandlerFunction<ServerResponse> {

    private static final byte[] TEXT_BODY = "Hello, World!".getBytes(StandardCharsets.UTF_8);

    private static final String TEXT_BODY_LENGTH = String.valueOf(TEXT_BODY.length);

    @Override
    public ServerResponse handle(ServerRequest request) {
        return ServerResponse.ok()
                .header(HttpHeaders.CONTENT_LENGTH, TEXT_BODY_LENGTH)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .body(TEXT_BODY);
    }
}



    @RestController
    public final class PlaintextController {

        @GetMapping(path = "/plaintext", produces = MediaType.TEXT_PLAIN_VALUE)
        public String plaintext() {
            return "Hello, World!";
        }
    }
