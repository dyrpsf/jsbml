/*
 * ----------------------------------------------------------------------------
 * This file is part of JSBML. Please visit <http://sbml.org/Software/JSBML>
 * for the latest version of JSBML and more information about SBML.
 * ----------------------------------------------------------------------------
 */

package org.sbml.libsbml;

/**
 * Wrapper for the Parameter class in the libSBML compatibility module.
 */
public class Parameter extends org.sbml.jsbml.Parameter {

	private static final long serialVersionUID = 1L;

	public Parameter() {
		super();
	}

	public Parameter(int level, int version) {
		super(level, version);
	}

	public Parameter(Parameter parameter) {
		super(parameter);
	}
	
	public Parameter(org.sbml.jsbml.Parameter parameter) {
		super(parameter);
	}

	public Parameter cloneObject() {
		return new Parameter(this);
	}
	
	@Override
	public Parameter clone() {
		return new Parameter(this);
	}
	
	/**
	 * Returns the libSBML type code for this object.
	 * @return the libSBML type code for this object.
	 */
	public int getTypeCode() {
		return libsbmlConstants.SBML_PARAMETER;
	}
}