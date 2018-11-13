package no.dcat.client.publisherapi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.dcat.shared.Publisher;
import org.springframework.web.client.RestTemplate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PublisherApiClient {
    private String publishersApiUrl;

    public Publisher getByOrgNr(String orgNr) {
        RestTemplate restTemplate = new RestTemplate();

        String resourceUrl = getPublishersApiUrl() + "/{orgNr}";

        return restTemplate.getForObject(resourceUrl, Publisher.class, orgNr);
    }
}

