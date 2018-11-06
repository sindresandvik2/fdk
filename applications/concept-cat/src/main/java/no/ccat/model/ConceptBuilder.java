package no.ccat.model;

import no.dcat.shared.Publisher;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.SKOSXL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* This class contains code for reading the jena document structure and translating it to our domain objects, DenormalizedConcepts.
 * Code originally copied from AbstractBuilder in no.dcat.datastore */

public class ConceptBuilder {
    public static final String defaultLanguage = "no";
    private static final Logger logger = LoggerFactory.getLogger(ConceptBuilder.class);


    public static List<Map<String, String>> extractMultipleLanguageLiterals(Resource resource, Property property) {
        Map<String, List<String>> map = new HashMap<>();
        StmtIterator iterator = resource.listProperties(property);

        while (iterator.hasNext()) {
            Statement statement = iterator.next();
            String language = statement.getLanguage();
            if (language == null || language.isEmpty()) {
                language = defaultLanguage;
            }
            if (statement.getString() != null && !statement.getString().isEmpty()) {
                List<String> x = map.get(language);
                if (x == null) {
                    x = new ArrayList<>();
                    map.put(language, x);
                }
                x.add(statement.getString());
            }
        }

        if (map.keySet().size() > 0) {
            List<Map<String, String>> result = new ArrayList<>();

            for (String language : map.keySet()) {
                for (String value : map.get(language)) {
                    Map<String, String> x = new HashMap<>();
                    x.put(language, value);
                    result.add(x);
                }
            }
            return result;
        }

        return null;
    }

    public static Publisher extractPublisher(Resource resource, Property property) {
        try {
            Publisher publisher = new Publisher();
            Statement propertyStmnt = resource.getProperty(property);
            if (propertyStmnt != null) {
                Resource object = resource.getModel().getResource(propertyStmnt.getObject().asResource().getURI());
                extractPublisherFromStmt(publisher, object);

                return publisher;
            }
        } catch (Exception e) {
            logger.warn("Error when extracting property {} from resource {}", DCTerms.publisher, resource.getURI(), e);
        }

        return null;
    }

    protected static void extractPublisherFromStmt(Publisher publisher, Resource object) {
        publisher.setUri(object.getURI());
        publisher.setId(extractAsString(object, DCTerms.identifier));

        String name = extractAsString(object, FOAF.name);
        if (name != null) {
            publisher.setName(name);
        }

        Map<String, String> preferredName = extractLanguageLiteral(object, SKOS.prefLabel);
        if (preferredName != null && preferredName.size() > 0) {
            publisher.setPrefLabel(preferredName);
        }

        // publisher.setValid(extractAsBoolean(object, DCTerms.valid));
        // publisher.setOrgPath(extractAsString(object, DCATNO.organizationPath));

        //publisher.setOrganisasjonsform(extractAsString(object, EnhetsregisteretRDF.organisasjonsform));
        //publisher.setOverordnetEnhet(extractAsString(object, EnhetsregisteretRDF.overordnetEnhet));

        /*Statement hasProperty = object.getProperty(EnhetsregisteretRDF.naeringskode);
        if (hasProperty != null) {
            publisher.setNaeringskode(extractBRCode(hasProperty, "http://www.ssb.no/nace/sn2007/"));
        }*/

        /*hasProperty = object.getProperty(EnhetsregisteretRDF.institusjonellSektorkode);
        if (hasProperty != null) {
            publisher.setSektorkode(extractBRCode(hasProperty, "http://www.brreg.no/sektorkode/"));
        }*/
    }

    public static String extractAsString(Resource resource, Property property) {
        try {
            Statement statement = resource.getProperty(property);
            String x = getStringFromStatement(statement);
            if (x != null) return x;
        } catch (Exception e) {
            logger.warn("Error when extracting property {} from resource {}. Reason {}", property, resource.getURI(), e.getLocalizedMessage());
        }
        return null;
    }

    public static Map<String, String> extractLanguageLiteralFromLabel(Resource resource, Property property) {
        //Get the substructure of the property (pref label in this case)
        Statement stmt = resource.getProperty(property);
        RDFNode node =  stmt.getObject();
        Resource subResource = node.asResource();

        return extractLanguageLiteral(subResource,  SKOSXL.literalForm);
    }

    public static Map<String, String> extractLanguageLiteral(Resource resource, Property property) {
        Map<String, String> map = new HashMap<>();

        StmtIterator iterator = resource.listProperties(property);

        while (iterator.hasNext()) {
            Statement statement = iterator.next();
            String language = statement.getLanguage();
            if (language == null || language.isEmpty()) {
                language = defaultLanguage;
            }
            if (statement.getString() != null && !statement.getString().isEmpty()) {
                map.put(language, statement.getString());
            }
        }

        if (map.keySet().size() > 0) {
            return map;
        }

        return null;
    }

    private static String getStringFromStatement(Statement statement) {
        if (statement != null) {
            if (statement.getObject().isLiteral()) {
                return statement.getString();
            } else {
                if (statement.getObject().isResource()) {
                    String x = statement.getObject().asResource().getURI();
                    return x;
                    //return removeDefaultBaseUri(statement.getModel(), x);
                } else {
                    return statement.getObject().asLiteral().getValue().toString();
                }

            }
        }
        return null;
    }
}
