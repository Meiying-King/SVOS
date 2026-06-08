package cn.tedu.ivos.chatbot.controller;


import cn.tedu.ivos.chatbot.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class ChatController {

    @Autowired
    private ChatbotService chatbotService;

    @GetMapping("/chat")
    public Flux<ServerSentEvent<String>> chat(String userMessage) {
        return chatbotService.chatStream(userMessage)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }
}
