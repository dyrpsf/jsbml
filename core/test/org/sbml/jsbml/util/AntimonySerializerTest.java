package org.sbml.jsbml.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Species;

/**
 * Tests for the {@link AntimonySerializer} Phase 1 LLM utility.
 *
 *  @author Deepak Yadav
 */
public class AntimonySerializerTest {

    private Model model;

    @Before
    public void setUp() {
        model = new Model(3, 1);
        model.setId("TestModel");
    }

    @After
    public void tearDown() {
        model = null;
    }

    @Test
    public void testNullModel() {
        String result = AntimonySerializer.toAntimony(null);
        assertEquals("Null model should return error string", "// Error: Model is null.", result);
    }

    @Test
    public void testEmptyModel() {
        String result = AntimonySerializer.toAntimony(model);
        assertTrue("Should contain model start", result.contains("model TestModel()"));
        assertTrue("Should contain model end", result.contains("end\n"));
    }

    @Test
    public void testCompartmentSerialization() {
        Compartment c1 = model.createCompartment("cytosol");
        c1.setSize(2.5);
        
        Compartment c2 = model.createCompartment("nucleus");
        // No size set for c2

        String result = AntimonySerializer.toAntimony(model);
        
        assertTrue("Should serialize compartment with size", result.contains("compartment cytosol = 2.5;"));
        assertTrue("Should serialize compartment without size", result.contains("compartment nucleus;"));
    }

    @Test
    public void testSpeciesSerialization() {
        model.createCompartment("cell");
        
        Species s1 = model.createSpecies("Glucose");
        s1.setCompartment("cell");
        s1.setInitialConcentration(10.5);

        Species s2 = model.createSpecies("ATP");
        // No compartment, no initial amount

        String result = AntimonySerializer.toAntimony(model);

        assertTrue("Should serialize species with compartment and init amount", result.contains("species Glucose in cell = 10.5;"));
        assertTrue("Should serialize simple species", result.contains("species ATP;"));
    }
}