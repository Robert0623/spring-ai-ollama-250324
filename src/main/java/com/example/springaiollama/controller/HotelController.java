package com.example.springaiollama.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
public class HotelController {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    @Value("classpath:prompt.txt")
    Resource resource;

    public HotelController(VectorStore vectorStore, ChatClient.Builder chatClient) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClient.build();
    }

    @GetMapping("/question")
    public Flux<String> question(@RequestParam("question") String question, Model model) throws Exception {
        System.out.println("question = " + question);

        List<Document> results = vectorStore.similaritySearch(SearchRequest.builder()
                .query(question)
                .similarityThreshold(0.5) // 0 ~ 1, 유사도
                .topK(1)
                .build());

        System.out.println("results.size() = " + results.size());
        results.stream().map(Document::toString).forEach(System.out::println);

        return chatClient.prompt()
                .user(userSp -> userSp.text(resource)
                        .param("context", results)
                        .param("question", question))
                .stream()
                .content();
    }
}
