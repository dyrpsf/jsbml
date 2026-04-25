package org.sbml.jsbml.ext.arrays.validator.constraints;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBase;
import org.sbml.jsbml.ext.arrays.ArraysConstants;
import org.sbml.jsbml.ext.arrays.ArraysSBasePlugin;
import org.sbml.jsbml.ext.comp.SBaseRef;
import org.sbml.jsbml.util.ResourceManager;
import org.sbml.jsbml.validator.offline.factory.SBMLErrorCodes;

/**
 * Checks that if an SBaseRef points to an array (an object with dimensions),
 * it must have index objects to dereference it to a scalar.
 * @author Deepak Yadav
 */
public class SBaseRefIndicesCheck extends ArraysConstraint {

  private static final transient ResourceBundle bundle = ResourceManager.getBundle("org.sbml.jsbml.ext.arrays.validator.constraints.Messages");

  private final SBase sbase;

  public SBaseRefIndicesCheck(Model model, SBase sbase) {
    super(model);
    this.sbase = sbase;
  }

  @Override
  public void check() {
    // 1. Check if the object we are validating is an SBaseRef
    if (sbase instanceof SBaseRef) {
      SBaseRef ref = (SBaseRef) sbase;
      
      // 2. Get the object it is pointing to by looking up its ID or MetaID in the Model
      SBase referencedObject = null;
      Model m = sbase.getModel();
      
      if (m != null) {
        if (ref.isSetIdRef()) {
          referencedObject = m.getSBaseById(ref.getIdRef());
        } else if (ref.isSetMetaIdRef()) {
          referencedObject = m.getElementByMetaId(ref.getMetaIdRef()); // Corrected method name
        }
      }

      if (referencedObject != null) {
        // 3. Check if the referenced object is an array
        ArraysSBasePlugin targetPlugin = (ArraysSBasePlugin) referencedObject.getExtension(ArraysConstants.shortLabel);
        
        if (targetPlugin != null && targetPlugin.isSetListOfDimensions()) {
          // The target is an array! Now check if our reference has indices.
          ArraysSBasePlugin ourPlugin = (ArraysSBasePlugin) sbase.getExtension(ArraysConstants.shortLabel);
          
          if (ourPlugin == null || !ourPlugin.isSetListOfIndices()) {
            // Missing indices! Throw the error.
            logMissingIndicesError();
          }
        }
      }
    }
  }

  private void logMissingIndicesError() {
    int code = SBMLErrorCodes.ARRAYS_20116; // The missing rule!
    int severity = 2, category = 0, line = -1, column = -1;

    String pkg = ArraysConstants.packageName;
    // Note: We use the existing bundle key if available, or fallback to the code
    String msg = bundle.getString("SBaseWithDimensionCheck.logDimensionError"); // We will need to update the properties file eventually
    String shortMsg = "SBaseRef points to an array but does not have index objects.";

    logFailure(code, severity, category, line, column, pkg, msg, shortMsg);
  }
}