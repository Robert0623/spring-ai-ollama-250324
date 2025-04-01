//package com.example.springaiollama.config;
//
//import jakarta.annotation.PostConstruct;
//import org.springframework.ai.document.Document;
//import org.springframework.ai.reader.ExtractedTextFormatter;
//import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
//import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
//import org.springframework.ai.transformer.splitter.TokenTextSplitter;
//import org.springframework.ai.vectorstore.VectorStore;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.Resource;
//import org.springframework.jdbc.core.simple.JdbcClient;
//
//import java.util.List;
//
//@Configuration
//public class PdfLoader {
//
//    private final VectorStore vectorStore;
//    private final JdbcClient jdbcClient;
//
//    @Value("classpath:/SPRi AI Brief_11월호_산업동향_F.pdf")
//    private Resource pdfResource;
//
//    public PdfLoader(VectorStore vectorStore, JdbcClient jdbcClient) {
//        this.vectorStore = vectorStore;
//        this.jdbcClient = jdbcClient;
//    }
//
//    @PostConstruct
//    public void init() {
//        Integer count = jdbcClient.sql("select count(*) from hotel_vector")
//                .query(Integer.class)
//                .single();
//        System.out.println("Records count in pgvector = " + count);
//
//        if (count == 0) {
//            System.out.println("Loading...");
//
//            // PDF Reader
//            PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder() // 빌더 생성
//                    .withPageTopMargin(0) // 상단 여백
//                    .withPageExtractedTextFormatter(ExtractedTextFormatter.builder() // 텍스트 포매팅
//                            .withNumberOfTopTextLinesToDelete(0) // 페이지상단 삭제 할 텍스트 줄 수(e.g. 헤더 제거)
//                            .build())
//                    .withPagesPerDocument(1) // 한 번에 처리할 페이지 수
//                    .build();
//
//            // # 1단계: 문서 로드(Load Documents)
//            PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(pdfResource, config);
//            List<Document> documents = pdfReader.get();
//
//            // # 2단계: 문서 분할(Split Documents)
//            TokenTextSplitter splitter = new TokenTextSplitter(1000, 400, 10, 5000, true);
//            List<Document> splitDocuments = splitter.apply(documents); // 불할 된 documents
//            System.out.println("splitDocuments.size() = " + splitDocuments.size());
//            System.out.println("splitDocuments.get(0) = " + splitDocuments.get(0));
//
//            // # 3단계: 임베딩(Embedding) -> DB에 저장(벡터스토어 생성)
//            vectorStore.accept(splitDocuments); // OpenAI 임베딩을 거친다.
//            System.out.println("Embedding End");
//        }
//    }
//}
