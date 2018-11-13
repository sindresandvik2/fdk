package no.acat.service;

import no.dcat.client.publisherapi.PublisherApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class PublisherApiService {
    @Value("${application.publisherApiUrl}")
    private String publisherApiUrl;

    public PublisherApiClient getClient() {
        return new PublisherApiClient(this.publisherApiUrl);
    }
}
