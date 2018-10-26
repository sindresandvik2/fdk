package no.acat.harvester;

import no.acat.model.ApiDocument;
import no.acat.service.ApiDocumentBuilderService;
import no.acat.service.ElasticsearchService;
import no.acat.service.RegistrationApiService;
import no.dcat.client.registrationapi.ApiRegistrationPublic;
import no.dcat.client.registrationapi.RegistrationApiClient;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/*
The purpose of the harvester is to ensure that search index is synchronized to registrations.
 */
@Service
public class ApiHarvester {
    private static final Logger logger = LoggerFactory.getLogger(ApiHarvester.class);

    private ElasticsearchService elasticsearchService;
    private ApiDocumentBuilderService apiDocumentBuilderService;
    private RegistrationApiClient registrationApiClient;

    @Autowired
    public ApiHarvester(ElasticsearchService elasticsearchService, ApiDocumentBuilderService apiDocumentBuilderService, RegistrationApiService registrationApiService) {
        this.elasticsearchService = elasticsearchService;
        this.registrationApiClient = registrationApiService.getClient();
        this.apiDocumentBuilderService = apiDocumentBuilderService;
    }

    public void harvestAll() {

        logger.info("harvestAll");

        List<ApiRegistrationPublic> apiRegistrations = getApiRegistrations();
        int registrationCount = apiRegistrations != null ? apiRegistrations.size() : 0;
        logger.info("Extracted {} api-registrations", registrationCount);

        List<String> idsHarvested = new ArrayList<>();

        for (ApiRegistrationPublic apiRegistration : apiRegistrations) {
            String harvestSourceUri = registrationApiClient.getPublicApisUrlBase() + '/' + apiRegistration.getId();
            try {
                logger.debug("Indexing from source uri: {}", harvestSourceUri);
                ApiDocument apiDocument = apiDocumentBuilderService.createFromApiRegistration(apiRegistration, harvestSourceUri);
                elasticsearchService.createOrReplaceApiDocument(apiDocument);
                idsHarvested.add(apiDocument.getId());
            } catch (Exception e) {
                logger.error("Error importing API record. ErrorClass={} message={}", e.getClass().getName(), e.getMessage());
                logger.debug("Error stacktrace", e);
            }
        }
        try {
            List<String> idsToDelete = elasticsearchService.getApiDocumentIdsNotHarvested(idsHarvested);
            elasticsearchService.deleteApiDocumentByIds(idsToDelete);
        } catch (Exception e) {
            logger.error("Error deleting {}", e.getMessage());
            logger.debug("Error stacktrace", e);
        }
    }

    List<ApiRegistrationPublic> getApiRegistrations() {
        List<ApiRegistrationPublic> apiRegistrationsFromCsv = getApiRegistrationsFromCsv();
        logger.info("Loaded registrations from csv {}", apiRegistrationsFromCsv.size());

        Collection<ApiRegistrationPublic> apiRegistrationsFromRegistrationApi = registrationApiClient.getPublished();
        logger.info("Loaded registrations from registration-api {}", apiRegistrationsFromRegistrationApi.size());

        // concatenate lists
        List<ApiRegistrationPublic> result = new ArrayList<>();
        result.addAll(apiRegistrationsFromCsv);
        result.addAll(apiRegistrationsFromRegistrationApi);
        logger.info("Total registrations {}", result.size());

        return result;
    }

    List<ApiRegistrationPublic> getApiRegistrationsFromCsv() {
        List<ApiRegistrationPublic> result = new ArrayList<>();

        org.springframework.core.io.Resource apiCatalogCsvFile = new ClassPathResource("apis.csv");
        Iterable<CSVRecord> records;

        try (
            Reader input =
                new BufferedReader(new InputStreamReader(apiCatalogCsvFile.getInputStream()))
        ) {
            records = CSVFormat.EXCEL.withHeader().withDelimiter(';').parse(input);

            for (CSVRecord line : records) {
                ApiRegistrationPublic apiRegistration = new ApiRegistrationPublic();

                apiRegistration.setId(line.get("Id"));
                apiRegistration.setCatalogId(line.get("OrgNr"));
                apiRegistration.setApiSpecUrl(line.get("ApiSpecUrl"));
                apiRegistration.setApiDocUrl(line.get("ApiDocUrl"));
                apiRegistration.setNationalComponent("true".equals(line.get("NationalComponent")));
                List<String> datasetReferences = Arrays.asList(line.get("DatasetRefs").split(","));
                apiRegistration.setDatasetReferences(datasetReferences);

                result.add(apiRegistration);
            }

            logger.debug("Read {} api catalog records.", result.size());

        } catch (
            IOException e) {
            logger.error("Could not read api catalog records: {}", e.getMessage());
        }
        return result;
    }


}
