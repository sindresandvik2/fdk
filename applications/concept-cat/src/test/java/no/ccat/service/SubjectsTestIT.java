package no.ccat.service;

import no.ccat.model.ConceptDenormalized;
import no.ccat.model.Definition;
import no.dcat.shared.testcategories.IntegrationTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.HashMap;
import java.util.List;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Integration Test class for SubjectsService
 */
@ActiveProfiles("unit-integration")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@Category(IntegrationTest.class)
public class SubjectsTestIT {
    private static Logger logger = LoggerFactory.getLogger(SubjectsTestIT.class);
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void CreateAndDeleteDocument() {
        //Manually make hardcoded doc
        ConceptDenormalized concept = new ConceptDenormalized();
        concept.setId("1");

        //Try to save the thing
       // conceptRepository.save(concept);
    }

    @Test
    public void ClassExistsAndCanStoreData() {
        ConceptDenormalized concept = new ConceptDenormalized();

        Assert.assertNotNull(concept);
        Definition d = new Definition();
        HashMap defText = new HashMap<String,String>();
        defText.put("nb","Samboer er en person som bor sammen med en annen person.");
        d.setText(defText);

        concept.setDefinition(d);
        String theValue =(String)concept.getDefinition().getText().values().toArray()[0];
        Assert.assertEquals("This is just a test".length(), theValue.length());
    }

    @Test
    public void UseTheDcatStoreBuilderCodeToBuild(){

        RDFToModelTransformer transformer = new RDFToModelTransformer();
        transformer.readIntoModel(resourceAsReader("jira-example-result.ttl"));
        List s = transformer.getConceptsFromModel();
        logger.debug("We loaded {} subject(s)",s.size());
    }

    @Test
    public void TestReadIntoJenaAndGetModelsOut() {
            RDFToModelTransformer transformer = new RDFToModelTransformer();
            transformer.readIntoModel(resourceAsReader("jira-example-result.ttl"));
            List s = transformer.getConceptsFromModel();
            Assert.assertEquals ("We load {"+s.size()+"} subject(s)",s.size(),1);
    }

    private Reader resourceAsReader(final String resourceName) {
        return new InputStreamReader(getClass().getClassLoader().getResourceAsStream(resourceName), StandardCharsets.UTF_8);
    }
}
