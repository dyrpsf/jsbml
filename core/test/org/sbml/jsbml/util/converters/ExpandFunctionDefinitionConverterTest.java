package org.sbml.jsbml.util.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.FunctionDefinition;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;

/**
 * Tests for the {@link ExpandFunctionDefinitionConverter}.
 * 
 * @author Deepak Yadav
 */
public class ExpandFunctionDefinitionConverterTest {

    private SBMLDocument doc;
    private Model model;

    @Before
    public void setUp() throws Exception {
        doc = new SBMLDocument(3, 2);
        model = doc.createModel("testModel");
    }

    @After
    public void tearDown() {
        doc = null;
        model = null;
    }

    @Test
    public void testBasicExpansion() throws Exception {
        // Create a function: f(x, y) = x * y
        FunctionDefinition fd = model.createFunctionDefinition("f");
        fd.setMath(ASTNode.parseFormula("lambda(x, y, x * y)"));

        // The math we want to expand: f(2, 3)
        ASTNode math = ASTNode.parseFormula("f(2, 3)");

        ASTNode expanded = ExpandFunctionDefinitionConverter.expandFunctionDefinition(model, math);

        assertNotNull(expanded);
        // Should successfully expand to 2*3 (JSBML formats without spaces)
        assertEquals("Should expand basic function", "2*3", ASTNode.formulaToString(expanded));
    }

    @Test(timeout = 2000) 
    public void testInfiniteLoopPrevention() throws Exception {
        // Create a malicious recursive function: f(x) = f(x) + 1
        FunctionDefinition fd = model.createFunctionDefinition("f");
        fd.setMath(ASTNode.parseFormula("lambda(x, f(x) + 1)"));

        // The math we want to expand: f(2)
        ASTNode math = ASTNode.parseFormula("f(2)");

        // This should hit your iteration limit and return without timing out or crashing!
        ASTNode expanded = ExpandFunctionDefinitionConverter.expandFunctionDefinition(model, math);

        assertNotNull(expanded);
        // Verify that it successfully escaped the loop
        assertTrue("Math string should exist after escaping loop", ASTNode.formulaToString(expanded).contains("f(2)"));
    }
}