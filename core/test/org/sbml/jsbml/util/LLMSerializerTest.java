package org.sbml.jsbml.util;

import org.junit.Test;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.xml.XMLNode;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LLMSerializerTest {

    @Test
    public void testToMarkdown() throws Exception {
        // 1. Create a dummy model
        Model model = new Model(3, 1); // SBML Level 3, Version 1
        model.setId("Test_LLM_Model");
        model.setName("Simple A to B Reaction");

        // 2. Add a Compartment
        Compartment c = model.createCompartment("cytosol");
        c.setSize(1.0);

        // 3. Add Species A and B
        Species sA = model.createSpecies("A", c);
        sA.setInitialConcentration(10.0);
        
        Species sB = model.createSpecies("B", c);
        sB.setInitialConcentration(0.0);

        // 4. Add a Reaction: A -> B
        Reaction r = model.createReaction("r1");
        r.setName("Conversion of A to B");
        
        SpeciesReference reactant = r.createReactant();
        reactant.setSpecies("A");
        
        SpeciesReference product = r.createProduct();
        product.setSpecies("B");

        // 5. Add a simple Kinetic Law: k1 * A
        KineticLaw kl = r.createKineticLaw();
        ASTNode math = ASTNode.parseFormula("k1 * A");
        kl.setMath(math);

        // 6. Run the Serializer!
        String markdownOutput = LLMSerializer.toMarkdown(model);

        // 7. Print the output so we can see it
        System.out.println("=== LLM SERIALIZER OUTPUT ===");
        System.out.println(markdownOutput);
        System.out.println("=============================");

        // 8. Basic Assertions
        assertNotNull(markdownOutput);
        assertTrue(markdownOutput.contains("Simple A to B Reaction"));
        assertTrue(markdownOutput.contains("k1*A"));
    }
}