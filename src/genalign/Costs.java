/*
 * -----------------------------------------------------------------------------
 *
 * GenAlign: A Gene Alignment Program
 * Copyright (c) 2007 Benjamin Gaillard
 *
 * -----------------------------------------------------------------------------
 *
 *        File: Costs.java
 *
 * Description: Cost Editing and Retrieving
 *
 * -----------------------------------------------------------------------------
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * -----------------------------------------------------------------------------
 */


package genalign;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.awt.Container;

/**
 * Operations costs.
 * @see CostTable1D
 * @see CostTable2D
 */
public final class Costs {
    /**
     * The initial default cost for insertions, deletions and substitutions if
     * the two characters are different, 0 otherwise.
     */
    public final static double DEFAULT_INS = 1.0, DEFAULT_DEL = 1.0,
			       DEFAULT_SUB_SAME = 0.0, DEFAULT_SUB_DIFF = 3.0;

    /**
     * Cost hash maps for each operation.
     */
    private HashMap insertions = new HashMap();
    private HashMap deletions = new HashMap();
    private HashMap substitutions = new HashMap();

    public double defaultIns = DEFAULT_INS, defaultDel = DEFAULT_DEL,
		  defaultSubSame = DEFAULT_SUB_SAME,
		  defaultSubDiff = DEFAULT_SUB_DIFF;

    /**
     * Input tables.
     */
    private CostTable1D insertCosts = null, deleteCosts = null;
    private CostTable2D substCosts = null;

    /**
     * Get the insertion cost for a specific character.
     *
     * @param chr the caracter.
     *
     * @return the insertion cost.
     */
    public double getIns(final char chr) {
	final Object object = insertions.get(new Character(chr));
	return object == null ? defaultIns : ((Double)object).doubleValue();
    }

    /**
     * Get the deletion cost for a specific character.
     *
     * @param chr the caracter.
     *
     * @return the deletion cost.
     */
    public double getDel(final char chr) {
	final Object object = deletions.get(new Character(chr));
	return object == null ? defaultDel : ((Double)object).doubleValue();
    }

    /**
     * Get the substitution cost for two specific characters.
     *
     * @param chr1 the first caracter.
     * @param chr2 the second caracter.
     *
     * @return the substitution cost.
     */
    public double getSub(final char chr1, final char chr2) {
	Object object = substitutions.get(new Character(chr1));
	if (object != null)
	    object = ((HashMap)object).get(new Character(chr2));
	return object == null ? (chr1 == chr2 ?
				 defaultSubSame : defaultSubDiff) :
	       ((Double)object).doubleValue();
    }

    /**
     * Make a sorted unique character array from a string.
     *
     * @param the string.
     *
     * @return the character array.
     */
    private char[] uniqueChars(final String string) {
	final HashSet set = new HashSet();
	for (int i = 0; i < string.length(); i++)
	    set.add(new Character(string.charAt(i)));

	final char[] chars = new char[set.size()];
	final Iterator iterator = set.iterator();
	for (int i = 0; i < chars.length; i++)
	    chars[i] = ((Character)iterator.next()).charValue();

	Arrays.sort(chars);
	return chars;
    }

    /**
     * Make tables for cost editing.
     *
     * @param string1    the first ("initial state") string.
     * @param string2    the second ("target") string.
     * @param insertCont AWT/Swing container for the insertions table.
     * @param deleteCont AWT/Swing container for the deletions table.
     * @param substCont  AWT/Swing container for the substitutions table.
     */
    public void makeTables(final String string1, final String string2,
			   final Container insertCont,
			   final Container deleteCont,
			   final Container substCont) {
	final char[] chars1 = uniqueChars(string1);
	final char[] chars2 = uniqueChars(string2);

	if (chars1.length > 0) {
	    insertCosts = new CostTable1D(chars1, insertions, defaultIns);
	    insertCont.add(insertCosts.makeTable());
	}
	if (chars2.length > 0) {
	    deleteCosts = new CostTable1D(chars2, deletions, defaultDel);
	    deleteCont.add(deleteCosts.makeTable());
	}
	if (chars1.length > 0 && chars2.length > 0) {
	    substCosts = new CostTable2D(chars1, chars2, substitutions,
					 defaultSubSame, defaultSubDiff);
	    substCont.add(substCosts.makeTable());
	}
    }

    /**
     * Retrieve cost values from the editing tables.
     */
    public void retrieveValues() {
	if (insertCosts != null) {
	    insertCosts.mergeValues();
	    insertCosts = null;
	}
	if (deleteCosts != null) {
	    deleteCosts.mergeValues();
	    deleteCosts = null;
	}
	if (substCosts != null) {
	    substCosts.mergeValues();
	    substCosts = null;
	}
    }

    /**
     * Initialize the insertions table to a given value.
     *
     * @param value the value.
     */
    public void initializeIns(final double value) {
	if (insertCosts != null)
	    insertCosts.initialize(value);
	defaultIns = value;
    }

    /**
     * Initialize the deletions table to a given value.
     *
     * @param value the value.
     */
    public void initializeDel(final double value) {
	if (deleteCosts != null)
	    deleteCosts.initialize(value);
	defaultDel = value;
    }

    /**
     * Initialize the substitutions table with given values.
     *
     * @param valueSame the value when characters are the same.
     * @param valueDiff the value when characters are different.
     */
    public void initializeSub(final double valueSame, final double valueDiff) {
	if (substCosts != null)
	    substCosts.initialize(valueSame, valueDiff);
	defaultSubSame = valueSame;
	defaultSubDiff = valueDiff;
    }
}

// End of File
