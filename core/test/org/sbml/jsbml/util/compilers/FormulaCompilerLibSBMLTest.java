package org.sbml.jsbml.util.compilers;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.SBMLException;

/**
 * Tests for the Formula compilers.
 * 
 * @author Deepak Yadav
 */
public class FormulaCompilerLibSBMLTest {

    @Test
    public void testIssue259OperatorPrecedence() throws SBMLException {
        // Build the tree for CCn/A1
        ASTNode divide = new ASTNode(ASTNode.Type.DIVIDE);
        divide.addChild(new ASTNode("CCn"));
        divide.addChild(new ASTNode("A1"));

        // Build the tree for (CCn/A1)^a
        ASTNode power = new ASTNode(ASTNode.Type.POWER);
        power.addChild(divide);
        power.addChild(new ASTNode("a"));

        // Compile it to a formula string
        String formula = power.toFormula();
        
        // Remove spaces for clean comparison and verify parentheses exist
        assertEquals("Division must be bracketed to preserve mathematical precedence (Issue #259)", 
                     "(CCn/A1)^a", formula.replaceAll("\\s+", ""));
    }
}