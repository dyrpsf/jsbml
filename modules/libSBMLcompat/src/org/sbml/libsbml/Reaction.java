/*
 * ----------------------------------------------------------------------------
 * This file is part of JSBML. Please visit <http://sbml.org/Software/JSBML>
 * for the latest version of JSBML and more information about SBML.
 * ----------------------------------------------------------------------------
 */

package org.sbml.libsbml;

/**
 * Wrapper for the Reaction class in the libSBML compatibility module.
 */
public class Reaction extends org.sbml.jsbml.Reaction {

	private static final long serialVersionUID = 1L;

	public Reaction() {
		super();
	}

	public Reaction(int level, int version) {
		super(level, version);
	}

	public Reaction(Reaction reaction) {
		super(reaction);
	}
	
	public Reaction(org.sbml.jsbml.Reaction reaction) {
		super(reaction);
	}

	public Reaction cloneObject() {
		return new Reaction(this);
	}
	
	@Override
	public Reaction clone() {
		return new Reaction(this);
	}
	
	/**
	 * Returns the libSBML type code for this object.
	 * @return the libSBML type code for this object.
	 */
	public int getTypeCode() {
		return libsbmlConstants.SBML_REACTION;
	}
}