package ir.jiring.sneakershop.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "ir.jiring.sneakershop.repositories.jpa")
public class JpaConfig {
}
