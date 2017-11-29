package no.dcat.portal.query;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.MetricsAggregationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class HarvestQueryService extends ElasticsearchService {
    private static Logger logger = LoggerFactory.getLogger(HarvestQueryService.class);
    public static final String INDEX_DCAT = "dcat";
    public static final String TYPE_DATA_PUBLISHER = "publisher";
    public static final String QUERY_PUBLISHER = "/publisher";

    /**
     * Finds all harvest catalog records for a given orgpath
     * <p/>
     *
     * @return The complete elasticsearch response on Json-format is returned..
     */
    @CrossOrigin
    @RequestMapping(value = "/harvest/catalog", produces = "application/json")
    public ResponseEntity<String> listCatalogHarvestRecords(@RequestParam(value = "q", defaultValue = "") String query) {
        logger.info("/harvest query: {}", query);

        ResponseEntity<String> jsonError = initializeElasticsearchTransportClient();
        if (jsonError != null) return jsonError;

        QueryBuilder search;

        if ("".equals(query)) {
            search = QueryBuilders.matchAllQuery();
        } else {
            search = QueryBuilders.termQuery("publisher.orgPath", query);
        }

        MetricsAggregationBuilder agg1 = AggregationBuilders.sum("inserts").field("changeInformation.inserts");
        MetricsAggregationBuilder agg2 = AggregationBuilders.sum("updates").field("changeInformation.updates");
        MetricsAggregationBuilder agg3 = AggregationBuilders.sum("deletes").field("changeInformation.deletes");

        long now = new Date().getTime();
        long DAY_IN_MS = 1000 * 3600 *24;

        RangeQueryBuilder range1 = QueryBuilders.rangeQuery("date").from(now -   7*DAY_IN_MS).to(now).format("epoch_millis");
        RangeQueryBuilder range2 = QueryBuilders.rangeQuery("date").from(now -  30*DAY_IN_MS).to(now).format("epoch_millis");
        RangeQueryBuilder range3 = QueryBuilders.rangeQuery("date").from(now - 365*DAY_IN_MS).to(now).format("epoch_millis");

        AggregationBuilder last7 = AggregationBuilders.filter("last7days").filter(range1).subAggregation(agg1).subAggregation(agg2).subAggregation(agg3);
        AggregationBuilder last30 = AggregationBuilders.filter("last30days").filter(range2).subAggregation(agg1).subAggregation(agg2).subAggregation(agg3);
        AggregationBuilder last365 = AggregationBuilders.filter("last365days").filter(range3).subAggregation(agg1).subAggregation(agg2).subAggregation(agg3);

        SearchRequestBuilder searchQuery = getClient()
                .prepareSearch("harvest").setTypes("catalog")
                .setQuery(search)
                .addAggregation(last7)
                .addAggregation(last30)
                .addAggregation(last365)
                .setSize(20)
                ;

        logger.trace("SEARCH: {}", searchQuery.toString());
        SearchResponse response = searchQuery.execute().actionGet();

        int totNumberOfCatalogRecords = (int) response.getHits().getTotalHits();
        logger.debug("Found total number of catalog records: {}", totNumberOfCatalogRecords);

        return new ResponseEntity<String>(response.toString(), HttpStatus.OK);
    }


}