package com.nourproject.backend.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "com.nourproject.backend.repositories")
@RequiredArgsConstructor
@Slf4j
public class MongoConfig {
    
    /**
     * Create indexes after application startup
     * This runs after MongoDB connection is established
     */
    @Bean
    public CommandLineRunner createIndexes(MongoTemplate mongoTemplate, MongoClient mongoClient) {
        return args -> {
            try {
                log.info("Creating MongoDB indexes for User collection...");
                
                MongoCollection<Document> userCollection = mongoClient
                        .getDatabase(mongoTemplate.getDb().getName())
                        .getCollection("users");
                
                // Create unique index on userName
                userCollection.createIndex(
                        Indexes.ascending("user_name"),
                        new IndexOptions().unique(true)
                );
                log.info("Created unique index on user_name");
                
                // Create unique index on email
                userCollection.createIndex(
                        Indexes.ascending("email"),
                        new IndexOptions().unique(true)
                );
                log.info("Created unique index on email");
                
                log.info("MongoDB indexes created successfully");
                
            } catch (Exception e) {
                log.warn("Could not create indexes (might already exist or no permissions): {}", 
                        e.getMessage());
            }
        };
    }
}
