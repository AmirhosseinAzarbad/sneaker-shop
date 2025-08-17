package ir.jiring.sneakershop.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "ir.jiring.sneakershop.repositories.elasticsearch")
public class ElasticsearchConfig {
}
