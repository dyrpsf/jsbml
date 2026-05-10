package org.sbml.jsbml.ext.comp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the {@link ExternalModelDefinition}.
 * 
 * @author Deepak Yadav
 */
public class ExternalModelDefinitionTest {

    private ExternalModelDefinition extModelDef;

    @Before
    public void setUp() throws Exception {
        extModelDef = new ExternalModelDefinition("testExternalModel");
    }

    @After
    public void tearDown() {
        extModelDef = null;
    }

    @Test
    public void testRedirectConstants() {
        // Verify that the HTTP redirect constants are correctly defined
        assertEquals("HTTP_TEMPORARY_REDIRECT should be exactly 307", 
                     307, ExternalModelDefinition.HTTP_TEMPORARY_REDIRECT);
        assertEquals("HTTP_PERMANENT_REDIRECT should be exactly 308", 
                     308, ExternalModelDefinition.HTTP_PERMANENT_REDIRECT);
    }
    
    @Test
    public void testInitialization() {
        // Verify basic instantiation
        assertNotNull("ExternalModelDefinition should instantiate cleanly", extModelDef);
        assertEquals("ID should match the constructor parameter", "testExternalModel", extModelDef.getId());
    }
}