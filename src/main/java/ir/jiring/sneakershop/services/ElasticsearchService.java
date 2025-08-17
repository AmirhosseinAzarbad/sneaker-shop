package ir.jiring.sneakershop.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import ir.jiring.sneakershop.models.Sneaker;
import ir.jiring.sneakershop.utils.ElasticsearchUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.function.Supplier;

@Service
public class ElasticsearchService {

    private final ElasticsearchClient elasticsearchClient;

    public ElasticsearchService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public SearchResponse<Sneaker> fuzzySearch(String approximateSneakerName){
        Supplier<Query> supplier = ElasticsearchUtil.createSupplierFuzzyQuery(approximateSneakerName);
        System.out.println(supplier.get());
        try {

        SearchResponse<Sneaker> response = elasticsearchClient
                .search(s->s.index("sneakers").query(supplier.get()),Sneaker.class);
        System.out.println("Search Response :" + response.toString());
        return response;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public SearchResponse<Sneaker> autoSuggestSearch(String partialSneakerName) throws IOException {
        Supplier<Query> supplier = ElasticsearchUtil.createSupplierMatchQuery(partialSneakerName);
        return elasticsearchClient
                .search(s->s.index("sneakers").query(supplier.get()),Sneaker.class);
    }
}
