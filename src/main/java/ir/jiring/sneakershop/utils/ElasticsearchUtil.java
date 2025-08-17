package ir.jiring.sneakershop.utils;

import co.elastic.clients.elasticsearch._types.query_dsl.FuzzyQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;
@Component
public class ElasticsearchUtil {

    public static Supplier<Query> createSupplierFuzzyQuery(String approximateSneakerName) {
        return ()->Query.of(q->q.fuzzy(createFuzzyQuery(approximateSneakerName)));
    }

    public static FuzzyQuery createFuzzyQuery(String approximateSneakerName) {
        val fuzzyQuery = new FuzzyQuery.Builder();
        return fuzzyQuery.field("name").value(approximateSneakerName).fuzziness("2").build();
    }


    public static Supplier<Query> createSupplierMatchQuery(String partialSneakerName) {
        return ()->Query.of(q->q.match(createAutoSuggestMatchQuery(partialSneakerName)));
    }
    public static MatchQuery createAutoSuggestMatchQuery(String partialSneakerName) {
        val autoSuggestQuery = new MatchQuery.Builder();
        return autoSuggestQuery.field("name").query(partialSneakerName).analyzer("standard").build();

    }
}
