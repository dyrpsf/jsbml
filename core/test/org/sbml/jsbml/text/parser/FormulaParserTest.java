package org.sbml.jsbml.text.parser;

import org.junit.Assert;
import org.junit.Test;
import org.sbml.jsbml.ASTNode;

public class FormulaParserTest {
    @Test
    public void testUnaryPrecedenceIssue249() throws Exception {
        // Verify exponentiation happens BEFORE unary minus
        ASTNode node1 = ASTNode.parseFormula("-x^y");
        Assert.assertEquals(ASTNode.Type.MINUS, node1.getType());
        Assert.assertEquals(1, node1.getChildCount());
        Assert.assertEquals(ASTNode.Type.POWER, node1.getChild(0).getType());
        
        // Verify that unary operators wrapped in parentheses do not throw a ParseException
        ASTNode node2 = ASTNode.parseFormula("(konm * (-1.0) / koffm)");
        Assert.assertNotNull(node2);
    }
}
