package org.sbml.jsbml.util;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.ModifierSpeciesReference;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.JSBML;

/**
 * Utility class to serialize complex SBML models into a flat, 
 * token-efficient Markdown format optimized for Large Language Models (LLMs).
 * * @author Deepak Yadav
 */
public class LLMSerializer {

    /**
     * Converts an SBML Model into an LLM-friendly Markdown string.
     * @param model The SBML Model to serialize.
     * @return A Markdown-formatted string representation of the model.
     */
    public static String toMarkdown(Model model) {
        if (model == null) return "Error: Model is null.";

        StringBuilder md = new StringBuilder();
        
        // Model Header
        String modelName = model.isSetName() ? model.getName() : model.getId();
        md.append("# Model: ").append(modelName).append("\n\n");

        // Compartments
        md.append("## Compartments\n");
        for (Compartment c : model.getListOfCompartments()) {
            String name = c.isSetName() ? c.getName() : c.getId();
            md.append("* **").append(name).append("** (Size: ").append(c.getSize()).append(")\n");
        }
        md.append("\n");

        // Species (The biological entities)
        md.append("## Species\n");
        for (Species s : model.getListOfSpecies()) {
            String name = s.isSetName() ? s.getName() : s.getId();
            double initialAmt = s.isSetInitialAmount() ? s.getInitialAmount() : s.getInitialConcentration();
            md.append("* **").append(name).append("**")
              .append(" (Initial: ").append(initialAmt)
              .append(", Compartment: ").append(s.getCompartment()).append(")\n");
        }
        md.append("\n");

        // Reactions & Math (The core logic)
        md.append("## Reactions\n");
        for (Reaction r : model.getListOfReactions()) {
            String name = r.isSetName() ? r.getName() : r.getId();
            md.append("* **").append(name).append("**: ");
            
            // Build Reactants -> Products string
            md.append(buildReactionEquation(r)).append("\n");

            // Extract the Math using JSBML's native formula parser!
            KineticLaw kl = r.getKineticLaw();
            if (kl != null && kl.isSetMath()) {
                // This is the magic line that converts the ASTNode tree into a readable math string
                String mathFormula = JSBML.formulaToString(kl.getMath());
                md.append("  * Rate Law: `").append(mathFormula).append("`\n");
            }
        }

        return md.toString();
    }

    /**
     * Helper method to format Reactants -> Products using proper chemical standards
     */
    private static String buildReactionEquation(Reaction r) {
        StringBuilder eq = new StringBuilder();
        Model m = r.getModel();
        
        // Reactants
        for (int i = 0; i < r.getReactantCount(); i++) {
            SpeciesReference sr = r.getReactant(i);
            appendSpeciesReference(eq, m, sr.getSpecies(), sr.getStoichiometry());
            if (i < r.getReactantCount() - 1) eq.append(" + ");
        }
        
        // Arrow
        if (r.isReversible()) {
            eq.append(" ⇌ ");
        } else {
            eq.append(" → ");
        }
        
        // Modifiers
        if (r.getModifierCount() > 0) {
            eq.append("[");
            for (int i = 0; i < r.getModifierCount(); i++) {
                ModifierSpeciesReference msr = r.getModifier(i);
                appendSpeciesReference(eq, m, msr.getSpecies(), 1.0); // Modifiers don't use stoichiometry here
                if (i < r.getModifierCount() - 1) eq.append(" + ");
            }
            eq.append("] ");
        }
        
        // Products
        for (int i = 0; i < r.getProductCount(); i++) {
            SpeciesReference sr = r.getProduct(i);
            appendSpeciesReference(eq, m, sr.getSpecies(), sr.getStoichiometry());
            if (i < r.getProductCount() - 1) eq.append(" + ");
        }
        
        return eq.toString();
    }

    /**
     * Helper method to cleanly append stoichiometry and resolve species names
     */
    private static void appendSpeciesReference(StringBuilder eq, Model m, String speciesId, double stoichiometry) {
        // Append stoichiometry if it is not 1.0
        if (stoichiometry != 1.0 && !Double.isNaN(stoichiometry)) {
            if (stoichiometry == Math.floor(stoichiometry)) {
                eq.append((int) stoichiometry).append(" ");
            } else {
                eq.append(stoichiometry).append(" ");
            }
        }
        
        // Attempt to resolve the human-readable name of the species
        String displayName = speciesId;
        if (m != null) {
            Species s = m.getSpecies(speciesId);
            if (s != null && s.isSetName()) {
                displayName = s.getName();
            }
        }
        eq.append(displayName);
    }
}