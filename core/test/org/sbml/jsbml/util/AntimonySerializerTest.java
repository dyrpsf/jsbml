package org.sbml.jsbml.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sbml.jsbml.SBase;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Species;

/**
 * Tests for the {@link AntimonySerializer} Phase 1 LLM utility.
 * 
 * @author Deepak Yadav
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
    public void testNullHandling() {
        String result = AntimonySerializer.toAntimony((Model) null);
        assertEquals("Null model should return error string", "// Error: Model is null.", result);
        
        String cResult = AntimonySerializer.toAntimony((Compartment) null);
        assertEquals("Null compartment should return empty string", "", cResult);

        String sResult = AntimonySerializer.toAntimony((Species) null);
        assertEquals("Null species should return empty string", "", sResult);
        
        String baseResult = AntimonySerializer.toAntimony((SBase) null);
        assertEquals("Null SBase should return error string", "// Error: Element is null.", baseResult);
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
    
    @Test
    public void testIndividualCompartmentSerialization() {
        Compartment c = model.createCompartment("c1");
        c.setSize(5.0);
        
        String result = AntimonySerializer.toAntimony(c);
        assertEquals("Should serialize individual compartment correctly", "compartment c1 = 5.0;", result);
    }

    @Test
    public void testStandardSpeciesSerialization() {
        Species s = model.createSpecies("S1");
        s.setCompartment("c1");
        s.setHasOnlySubstanceUnits(false);
        s.setBoundaryCondition(false);
        s.setInitialConcentration(3.0);
        
        String result = AntimonySerializer.toAntimony(s);
        assertEquals("Should serialize standard concentration species", "species S1 in c1 = 3.0;", result);
    }

    @Test
    public void testAdvancedSpeciesSerialization() {
        Species s = model.createSpecies("S1");
        s.setCompartment("C");
        
        // Case: hOSU=false, initialAmount, boundary=false -> (Amount / Compartment)
        s.setHasOnlySubstanceUnits(false);
        s.setBoundaryCondition(false);
        s.setInitialAmount(3.0);
        assertEquals("species S1 in C = 3.0 / C;", AntimonySerializer.toAntimony(s));

        // Case: hOSU=true, initialConcentration, boundary=false -> (Concentration * Compartment)
        s.unsetInitialAmount();
        s.setHasOnlySubstanceUnits(true);
        s.setInitialConcentration(3.0);
        assertEquals("substanceOnly species S1 in C = 3.0 * C;", AntimonySerializer.toAntimony(s));

        // Case: hOSU=true, initialAmount, boundary=true -> ($S1)
        s.unsetInitialConcentration();
        s.setBoundaryCondition(true);
        s.setInitialAmount(3.0);
        assertEquals("substanceOnly species $S1 in C = 3.0;", AntimonySerializer.toAntimony(s));
    }

    @Test
    public void testGenericSBaseRouting() {
        Compartment c = model.createCompartment("c1");
        c.setSize(5.0);
        
        // Pass it as a generic SBase to simulate a UI plugin click
        SBase genericElement = c;
        String result = AntimonySerializer.toAntimony(genericElement);
        
        assertEquals("SBase router should dynamically identify and serialize the compartment", "compartment c1 = 5.0;", result);
    }
}