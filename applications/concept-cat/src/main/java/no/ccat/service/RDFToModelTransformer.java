package no.ccat.service;

import no.ccat.model.ConceptBuilder;
import no.ccat.model.ConceptDenormalized;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.SKOSXL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class RDFToModelTransformer {

    private final Model model;
    private static final Logger logger = LoggerFactory.getLogger(RDFToModelTransformer.class);

    public RDFToModelTransformer() {
        model = ModelFactory.createDefaultModel();
    }

    public void readIntoModel(Reader r) {
        model.read(r,null, "TURTLE");//Base and lang is just untested dummy values
    }

    public List<ConceptDenormalized> getConceptsFromModel() {

        List<ConceptDenormalized> theList = new ArrayList<>();

        ResIterator conceptIterator = model.listResourcesWithProperty(RDF.type, SKOS.Concept);

        while (conceptIterator.hasNext()) {

            //Just debug output
            StmtIterator stIterator =  model.listStatements();
            while (stIterator.hasNext()) {
                logger.debug(stIterator.nextStatement().toString());
            }

            ConceptDenormalized concept = new ConceptDenormalized();
            Resource conceptResource = conceptIterator.nextResource();

            logger.debug("A concept: " + conceptResource.toString());

            concept.setPublisher(ConceptBuilder.extractPublisher(conceptResource, DCTerms.publisher));

            concept.setSubject(ConceptBuilder.extractLanguageLiteral(conceptResource, DCTerms.subject));

            concept.setPrefLabel(ConceptBuilder.extractLanguageLiteralFromLabel(conceptResource, SKOSXL.prefLabel));

            //TODO:Make the below work

            concept.setAltLabel(ConceptBuilder.extractMultipleLanguageLiterals(conceptResource, SKOS.altLabel));
            concept.setHiddenLabel(ConceptBuilder.extractMultipleLanguageLiterals(conceptResource, SKOS.hiddenLabel));
            //TODO: Betydningsbeskrivelse
            theList.add(concept);
        }
        return theList;
    }

}
