package no.dcat.client.publisherapi;

import no.dcat.shared.Publisher;
import org.springframework.web.client.RestTemplate;

public class PublisherApiClient {
    private String publishersApiUrl;

    public PublisherApiClient(String publishersApiUrl) {
        this.publishersApiUrl = publishersApiUrl;
    }

    public Publisher getByOrgNr(String orgNr) {
        RestTemplate restTemplate = new RestTemplate();

        String resourceUrl = publishersApiUrl + "/{orgNr}";

        return restTemplate.getForObject(resourceUrl, Publisher.class, orgNr);
    }
}

