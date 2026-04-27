package org.sbml.jsbml.util;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.Compartment;

/**
 * Utility class to serialize SBML models into the Antimony scripting language.
 * This provides a token-efficient, human-readable format that removes XML overhead,
 * making it highly optimized for Large Language Model (LLM) context windows.
 * 
 * @author Deepak Yadav
 */
public class AntimonySerializer {

    /**
     * Converts an SBML Model into a basic Antimony script string.
     * @param model The SBML Model to serialize.
     * @return An Antimony-formatted string representation of the model.
     */
    public static String toAntimony(Model model) {
        if (model == null) return "// Error: Model is null.";

        StringBuilder ant = new StringBuilder();
        
        // Model Header
        String modelName = model.isSetName() ? model.getName() : model.getId();
        ant.append("model ").append(modelName).append("()\n\n");

        // Compartments
        ant.append("  // Compartments\n");
        for (Compartment c : model.getListOfCompartments()) {
            String cId = c.getId();
            ant.append("  compartment ").append(cId);
            if (c.isSetSize()) {
                ant.append(" = ").append(c.getSize());
            }
            ant.append(";\n");
        }
        ant.append("\n");

        // Species
        ant.append("  // Species\n");
        for (Species s : model.getListOfSpecies()) {
            String sId = s.getId();
            ant.append("  species ").append(sId);
            
            if (s.isSetCompartment()) {
                ant.append(" in ").append(s.getCompartment());
            }
            
            double initialAmt = s.isSetInitialAmount() ? s.getInitialAmount() : s.getInitialConcentration();
            if (!Double.isNaN(initialAmt)) {
                ant.append(" = ").append(initialAmt);
            }
            ant.append(";\n");
        }
        ant.append("\n");

        // TODO: Implement Reactions, Rate Rules, Algebraic Rules, and Events mapping
        // Planned for GSoC 2026: gsoc-sysbio-llm-tools pipeline expansion.
        ant.append("  // Reactions and Advanced Rules serialization to be implemented...\n\n");

        // Close Model
        ant.append("end\n");

        return ant.toString();
    }
}