package ir.jiring.sneakershop.repositories.elasticsearch;

import ir.jiring.sneakershop.models.Sneaker;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("sneakerRepositoryElasticsearch")
public interface SneakerRepositoryElasticsearch extends ElasticsearchRepository<Sneaker, UUID> {
    @Override
    List<Sneaker> findAll();
}
