package no.acat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.core.env.Environment;

@Service
public class PublisherApiClient extends no.dcat.client.publisherapi.PublisherApiClient {
    @Autowired
    private Environment env;

    @Override
    public String getPublishersApiUrl(){
        return env.getProperty("application.publisherApiUrl");
    }
}
