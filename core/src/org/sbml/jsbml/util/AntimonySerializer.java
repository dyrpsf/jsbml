package org.sbml.jsbml.util;

import org.sbml.jsbml.SBase;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.Compartment;

/**
 * Utility class to serialize SBML models and components into the Antimony scripting language.
 * This provides a token-efficient, human-readable format optimized for Large Language Models (LLMs)
 * and bidirectional text-editor plugins.
 * 
 * @author Deepak Yadav
 */
public class AntimonySerializer {

    /**
     * Generic router for UI plugins (e.g., Eclipse, IntelliJ, VSCode). 
     * Allows dynamic serialization of any selected SBML component without knowing its specific type.
     * * @param element The generic SBase element to serialize.
     * @return An Antimony-formatted string, or a comment if the element is unsupported/null.
     */
    public static String toAntimony(SBase element) {
        if (element == null) return "// Error: Element is null.";
        
        if (element instanceof Model) {
            return toAntimony((Model) element);
        } else if (element instanceof Compartment) {
            return toAntimony((Compartment) element);
        } else if (element instanceof Species) {
            return toAntimony((Species) element);
        }
        
        return "// Unsupported SBML component for Antimony serialization.";
    }

    /**
     * Converts an entire SBML Model into a basic Antimony script string.
     * @param model The SBML Model to serialize.
     * @return An Antimony-formatted string representation of the model.
     */
    public static String toAntimony(Model model) {
        if (model == null) return "// Error: Model is null.";

        StringBuilder ant = new StringBuilder();
        
        String modelName = model.isSetName() ? model.getName() : model.getId();
        ant.append("model ").append(modelName).append("()\n\n");

        ant.append("  // Compartments\n");
        for (Compartment c : model.getListOfCompartments()) {
            ant.append("  ").append(toAntimony(c)).append("\n");
        }
        ant.append("\n");

        ant.append("  // Species\n");
        for (Species s : model.getListOfSpecies()) {
            ant.append("  ").append(toAntimony(s)).append("\n");
        }
        ant.append("\n");

        ant.append("  // Reactions and Advanced Rules serialization to be implemented...\n\n");
        ant.append("end\n");

        return ant.toString();
    }

    /**
     * Converts an individual SBML Compartment into an Antimony string.
     */
    public static String toAntimony(Compartment c) {
        if (c == null) return "";
        StringBuilder ant = new StringBuilder();
        ant.append("compartment ").append(c.getId());
        if (c.isSetSize()) {
            ant.append(" = ").append(c.getSize());
        }
        ant.append(";");
        return ant.toString();
    }

    /**
     * Converts an individual SBML Species into an Antimony string.
     * Implements rigorous checking for substance units and boundary conditions.
     */
    public static String toAntimony(Species s) {
        if (s == null) return "";
        StringBuilder ant = new StringBuilder();

        boolean hOSU = s.getHasOnlySubstanceUnits();
        boolean boundary = s.getBoundaryCondition();

        // 1. Handle Substance Units
        if (hOSU) {
            ant.append("substanceOnly species ");
        } else {
            ant.append("species ");
        }

        // 2. Handle Boundary Condition
        if (boundary) {
            ant.append("$");
        }

        ant.append(s.getId());

        // Compartment assignment
        if (s.isSetCompartment()) {
            ant.append(" in ").append(s.getCompartment());
        }

        // 3. Handle Initial Values based on Concentration vs Amount assumptions
        String comp = s.isSetCompartment() ? s.getCompartment() : "1";

        if (hOSU) {
            if (s.isSetInitialAmount()) {
                ant.append(" = ").append(s.getInitialAmount());
            } else if (s.isSetInitialConcentration()) {
                ant.append(" = ").append(s.getInitialConcentration()).append(" * ").append(comp);
            }
        } else {
            if (s.isSetInitialAmount()) {
                ant.append(" = ").append(s.getInitialAmount()).append(" / ").append(comp);
            } else if (s.isSetInitialConcentration()) {
                ant.append(" = ").append(s.getInitialConcentration());
            }
        }

        ant.append(";");
        return ant.toString();
    }
}