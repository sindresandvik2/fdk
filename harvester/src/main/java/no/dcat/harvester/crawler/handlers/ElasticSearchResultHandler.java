package no.dcat.harvester.crawler.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import no.dcat.admin.store.domain.DcatSource;
import no.dcat.data.store.Elasticsearch;
import no.dcat.data.store.domain.dcat.DataTheme;
import no.dcat.data.store.domain.dcat.Dataset;
import no.dcat.data.store.domain.dcat.Distribution;
import no.dcat.data.store.domain.dcat.builders.DatasetBuilder;
import no.dcat.data.store.domain.dcat.builders.DistributionBuilder;
import no.dcat.harvester.crawler.CrawlerResultHandler;
import no.dcat.harvester.crawler.client.LoadLocations;
import no.dcat.harvester.crawler.client.RetrieveCodes;
import no.dcat.harvester.crawler.client.RetrieveDataThemes;
import no.dcat.harvester.crawler.client.RetrieveModel;
import no.dcat.shared.SkosCode;
import org.apache.jena.rdf.model.Model;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;


/**
 * Handles harvesting of dcat data sources, and saving them into elasticsearch
 */
public class ElasticSearchResultHandler implements CrawlerResultHandler {

    public static final String DCAT_INDEX = "dcat";
    public static final String THEME_INDEX = "theme";
    public static final String DISTRIBUTION_TYPE = "distribution";
    public static final String DATASET_TYPE = "dataset";
    public static final String THEME_TYPE = "data-theme";
    public static final String DATA_THEME_URL = "http://publications.europa.eu/mdr/resource/authority/data-theme/skos/data-theme-skos.rdf";
    private final Logger logger = LoggerFactory.getLogger(ElasticSearchResultHandler.class);

    String hostename;
    int port;
    String clustername;


    /**
     * Creates a new elasticsearch code result handler connected to
     * a particular elasticsearch instance.
     *
     * @param hostname host name where elasticsearch cluster is found
     * @param port port for connection to elasticserach cluster. Usually 9300
     * @param clustername Name of elasticsearch cluster
     */
    public ElasticSearchResultHandler(String hostname, int port, String clustername) {
        this.hostename = hostname;
        this.port = port;
        this.clustername = clustername;

        logger.debug("ES clustername: " + this.clustername);
    }


    /**
     * Process a data catalog, represented as an RDF model
     *
     * @param dcatSource information about the source/provider of the data catalog
     * @param model RDF model containing the data catalog
     */
    @Override
    public void process(DcatSource dcatSource, Model model) {
        logger.debug("Processing results Elasticsearch: " + this.hostename +":" + this.port + " cluster: "+ this.clustername);

        try (Elasticsearch elasticsearch = new Elasticsearch(hostename, port, clustername)) {
            logger.trace("Start indexing");
            indexWithElasticsearch(dcatSource, model, elasticsearch);
        } catch (Exception e) {
            logger.error("Exception: " + e.getMessage(), e);
            throw e;
        }
        logger.trace("finished");
    }

    /**
     * Index data catalog with Elasticsearch
     * @param dcatSource information about the source/provider of the data catalog
     * @param model RDF model containing the data catalog
     * @param elasticsearch The Elasticsearch instance where the data catalog should be stored
     */
    void indexWithElasticsearch(DcatSource dcatSource, Model model, Elasticsearch elasticsearch) {
        Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").create();

        if (!elasticsearch.indexExists(DCAT_INDEX)) {
            logger.debug("Creating index: " + DCAT_INDEX);
            elasticsearch.createIndex(DCAT_INDEX);
        }else{
            logger.debug("Index exists: " + DCAT_INDEX);

        }

        logger.debug("Preparing bulkRequest");
        BulkRequestBuilder bulkRequest = elasticsearch.getClient().prepareBulk();

        // Retrieve all codes from elasticsearch.
        Map<String, DataTheme> dataThemes = new RetrieveDataThemes(elasticsearch).getAllDataThemes();

        Map<String, SkosCode> locations = new LoadLocations(elasticsearch)
                .extractLocations(model).retrieveLocTitle()
                .indexLocationsWithElasticSearch()
                .refresh()
                .getLocations();
        Map<String,Map<String, SkosCode>> codes = RetrieveCodes.getAllCodes();


        List<Distribution> distributions = new DistributionBuilder(model, locations, codes, dataThemes).build();
        logger.info("Number of distribution documents {} for dcat source {}", distributions.size(), dcatSource.getId());
        for (Distribution distribution : distributions) {

            IndexRequest indexRequest = new IndexRequest(DCAT_INDEX, DISTRIBUTION_TYPE, distribution.getId());
            indexRequest.source(gson.toJson(distribution));

            logger.debug("Add distribution document {} to bulk request", distribution.getId());
            bulkRequest.add(indexRequest);
        }


        List<Dataset> datasets = new DatasetBuilder(model, locations, codes, dataThemes).build();
        logger.info("Number of distribution documents {} for dcat source {}", datasets.size(), dcatSource.getId());
        for (Dataset dataset : datasets) {

            IndexRequest indexRequest = new IndexRequest(DCAT_INDEX, DATASET_TYPE, dataset.getId());
            indexRequest.source(gson.toJson(dataset));

            logger.debug("Add dataset document {} to bulk request", dataset.getId());
            bulkRequest.add(indexRequest);
        }

        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            //TODO: process failures by iterating through each bulk response item?
        }
    }

}
