package ir.jiring.sneakershop.controllers;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import ir.jiring.sneakershop.dto.sneaker.SneakerResponse;
import ir.jiring.sneakershop.mapper.SneakerMapper;
import ir.jiring.sneakershop.models.Sneaker;
import ir.jiring.sneakershop.services.ElasticsearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/sneakers")
public class ElasticsearchController {

    private final ElasticsearchService elasticsearchService;

    public ElasticsearchController(ElasticsearchService elasticsearchService) {
        this.elasticsearchService = elasticsearchService;
    }

    @GetMapping("/fuzzySearch/{approximateProductName}")
    public List<SneakerResponse> fuzzySearch(@PathVariable String approximateProductName) {
        SearchResponse<Sneaker> searchResponse = elasticsearchService.fuzzySearch(approximateProductName);
        return createSneakerResponseList(searchResponse);
    }

    @GetMapping("/autoSuggest/{partialSneakerName}")
    public List<SneakerResponse> autoSuggest(@PathVariable String partialSneakerName) throws IOException {
        SearchResponse<Sneaker> searchResponse = elasticsearchService.autoSuggestSearch(partialSneakerName);
        return createSneakerResponseList(searchResponse);
    }

    private List<SneakerResponse> createSneakerResponseList(SearchResponse<Sneaker> searchResponse) {
        List<Hit<Sneaker>> hitList = searchResponse.hits().hits();
        List<Sneaker> sneakerResponseList = new ArrayList<>();
        for (Hit<Sneaker> hit : hitList) {
            sneakerResponseList.add(hit.source());
        }
        return sneakerResponseList.stream().map(SneakerMapper::toSneakerResponseDTO).collect(Collectors.toList());
    }
}
