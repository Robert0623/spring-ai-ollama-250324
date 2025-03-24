package com.example.springaiollama.config;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class HotelLoader {

    private final VectorStore vectorStore;
    private final JdbcClient jdbcClient;

    @Value("classpath:data.txt")
    Resource resource;

    public HotelLoader(VectorStore vectorStore, JdbcClient jdbcClient) {
        this.vectorStore = vectorStore;
        this.jdbcClient = jdbcClient;
    }

    @PostConstruct
    public void init() throws Exception {
        Integer count = jdbcClient.sql("select count(*) from hotel_vector")
                .query(Integer.class)
                .single();
        System.out.println("레코드 수 = " + count);

        if (count == 0) {
            List<Document> documents = Files.lines(resource.getFile().toPath())
                    .map(Document::new)
                    .collect(Collectors.toList());

            TextSplitter textSplitter = new TokenTextSplitter();
            for (Document document : documents) {
                List<Document> splitteddocs = textSplitter.split(document);
                vectorStore.add(splitteddocs); // 임베딩
            }
        }
        System.out.println("임베딩 완료");
    }
}
