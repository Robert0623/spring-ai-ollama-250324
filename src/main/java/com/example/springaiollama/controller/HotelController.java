package com.example.springaiollama.controller;

import io.micrometer.core.instrument.search.Search;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HotelController {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public HotelController(VectorStore vectorStore, ChatClient.Builder chatClient) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClient.build();
    }

    @GetMapping("/question")
    public String question(@RequestParam("question") String question, Model model) throws Exception {
        System.out.println("question = " + question);

        List<Document> results = vectorStore.similaritySearch(SearchRequest.builder()
                .query(question)
                .similarityThreshold(0.5)
                .topK(1)
                .build());

        System.out.println("results.size() = " + results.size());

        String template = """
                당신은 어느 호텔 직원입니다. 문맥에 따라 고객의 질문에 정중하게 답변해 주십시오.
                컨텍스트가 질문에 대답할 수 없는 경우 '해당 내용은 정보가 부족해 답변드릴 수 없습니다'라고 대답하세요.

                컨텍스트:
                {context}
                질문:
                {question}

                답변:
                """;

        return chatClient.prompt()
                .user(userSp -> userSp.text(template)
                        .param("context", results)
                        .param("question", question))
                .call()
                .content();
    }
}
