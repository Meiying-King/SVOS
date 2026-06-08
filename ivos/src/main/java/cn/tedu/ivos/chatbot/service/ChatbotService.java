package cn.tedu.ivos.chatbot.service;

import dev.langchain4j.service.SystemMessage;
import reactor.core.publisher.Flux;

public interface ChatbotService {


    @SystemMessage(fromResource = "systemDocs/system-prompt.txt")
    String chat(String userMessage);

    @SystemMessage(fromResource = "systemDocs/system-prompt.txt")
    Flux<String> chatStream(String userMessage);
}
