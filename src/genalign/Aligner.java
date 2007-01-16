/*
 * -----------------------------------------------------------------------------
 *
 * GenAlign: A Gene Alignment Program
 * Copyright (c) 2007 Benjamin Gaillard
 *
 * -----------------------------------------------------------------------------
 *
 *        File: Aligner.java
 *
 * Description: Gene Aligner
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * Implementation of the alignment algorithm.
 */
public final class Aligner extends AbstractTableModel {
    /**
     * Operation opcodes (end, insertion, deletion, substitution).
     */
    private final static byte OPR_END = 0, OPR_INS = 1,
			      OPR_DEL = 2, OPR_SUB = 3;

    /**
     * Input strings.
     */
    private final char[] string1, string2;

    /**
     * The table generated during the alignment computation.
     */
    private double[][] costTab = null;

    /**
     * The maximum value in costTab.
     */
    private double maxCost = 0.0;

    /**
     * Operation path: each cell backlinks to the previous one according to its
     * content (an opcode).
     */
    private byte[][] operations = null;

    /**
     * Not used, but eliminates a warning.
     */
    private final static long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * @param str1 the first string ("initial state").
     * @param str2 the second string ("target").
     */
    public Aligner(final String str1, final String str2) {
	string1 = str1.toCharArray();
	string2 = str2.toCharArray();
    }

    /**
     * Compute the minimum of three numbers.
     *
     * @param a the first number.
     * @param b the second number.
     * @param c the third number.
     *
     * @return the minimum of the three numbers.
     */
    private static double min(final double a, final double b, final double c) {
	return Math.min(Math.min(a, b), c);
    }

    /**
     * Compute the operations table.
     *
     * @param costs the operations costs.
     *
     * @return the minimum cost (side effect: fill the operations table).
     */
    public double compute(final Costs costs) {
	costs.retrieveValues();

	costTab = new double[string1.length + 1][];
	operations = new byte[string1.length + 1][];
	for (int i = 0; i <= string1.length; i++) {
	    costTab[i] = new double[string2.length + 1];
	    operations[i] = new byte[string2.length + 1];
	}

	costTab[0][0] = 0.0;
	operations[0][0] = OPR_END;
	maxCost = 0.0;

	for (int i = 0; i < string1.length; i++) {
	    final double value = costTab[i][0] + costs.getDel(string1[i]);
	    costTab[i + 1][0] = value;
	    operations[i + 1][0] = OPR_DEL;

	    if (value > maxCost)
		maxCost = value;
	}

	for (int j = 0; j < string2.length; j++) {
	    final double value = costTab[0][j] + costs.getIns(string2[j]);
	    costTab[0][j + 1] = value;
	    operations[0][j + 1] = OPR_INS;

	    if (value > maxCost)
		maxCost = value;

	    for (int i = 0; i < string1.length; i++) {
		final double subVal = costTab[i][j] +
				      costs.getSub(string1[i], string2[j]);
		final double delVal = costTab[i][j + 1] +
				      costs.getDel(string1[i]);
		final double insVal = costTab[i + 1][j] +
				      costs.getIns(string2[j]);
		final double minVal = min(insVal, delVal, subVal);

		if (minVal > maxCost)
		    maxCost = minVal;

		costTab[i + 1][j + 1] = minVal;
		if (minVal == insVal)
		    operations[i + 1][j + 1] = OPR_INS;
		else if (minVal == delVal)
		    operations[i + 1][j + 1] = OPR_DEL;
		else
		    operations[i + 1][j + 1] = OPR_SUB;
	    }
	}

	return costTab[string1.length][string2.length];
    }

    /**
     * Get a two-line string with the two input strings aligned.
     *
     * @return the string representation of the alignment.
     */
    public String toString() {
	if (operations == null)
	    return new String();

	final int total = string1.length + string2.length;
	final char[] str1 = new char[total + 1];
	final char[] str2 = new char[total];
	int pos = total, i = string1.length, j = string2.length;

	while (operations[i][j] != OPR_END) {
	    pos--;

	    switch (operations[i][j]) {
	    case OPR_INS:
		str1[pos] = '-';
		str2[pos] = string2[--j];
		break;

	    case OPR_DEL:
		str1[pos] = string1[--i];
		str2[pos] = '-';
		break;

	    case OPR_SUB:
		str1[pos] = string1[--i];
		str2[pos] = string2[--j];
	    }
	}

	str1[total] = '\n';
	return new String(str1, pos, total - pos + 1) +
	       new String(str2, pos, total - pos);
    }

    /**
     * Return the number of columns in the model.
     *
     * @return the number of columns in the model.
     */
    public int getColumnCount() {
	return string1.length + 2;
    }

    /**
     * Return the number of rows in the model.
     *
     * @return the number of rows in the model.
     */
    public int getRowCount() {
	return string2.length + 1;
    }

    /**
     * Return the value for the cell at (columnIndex, rowIndex).
     *
     * @param rowIndex    the row whose value is to be queried.
     * @param columnIndex the column whose value is to be queried.
     *
     * @return the value Object at the specified cell.
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
	if (columnIndex == 0) {
	    if (rowIndex == 0)
		return new String();
	    return String.valueOf(string2[rowIndex - 1]);
	}

	return new Double(costTab[columnIndex - 1][rowIndex]);
    }

    /**
     * Return a column name.
     *
     * @param columnIndex the column number.
     *
     * @return the column name.
     */
    public String getColumnName(int columnIndex) {
	if (columnIndex < 2)
	    return new String();
	return String.valueOf(string1[columnIndex - 2]);
    }

    /**
     * Return a column class.
     *
     * @param columnIndex the column number.
     *
     * @return the column class.
     */
    public Class getColumnClass(int columnIndex) {
	if (columnIndex > 0)
	    return new Double(0.0).getClass();
	return new String().getClass();
    }

    /**
     * Initialize the column sizes of a given table.
     *
     * @param table the table.
     */
    private void initColumnSizes(final JTable table) {
	final TableCellRenderer headerRenderer =
	    table.getTableHeader().getDefaultRenderer();

	final TableColumn column = table.getColumnModel().getColumn(0);
	Component comp = headerRenderer.getTableCellRendererComponent(
			     null, column.getHeaderValue(),
			     false, false, 0, 0);

	final int headerWidth = comp.getPreferredSize().width;
	int cellWidth;

	cellWidth = 0;
	for (int j = 0; j < getRowCount(); j++) {
	    comp = table.getDefaultRenderer(new String().getClass()).
		   getTableCellRendererComponent(table,
						 getValueAt(j, 0),
						 false, false, 0, 0);
	    final int thisCellWidth = comp.getPreferredSize().width;
	    if (thisCellWidth > cellWidth)
		cellWidth = thisCellWidth;
	}
	column.setPreferredWidth(Math.max(headerWidth, cellWidth));

	final Double data = new Double(maxCost);
	comp = table.getDefaultRenderer(data.getClass()).
	       getTableCellRendererComponent(table, data,
					     false, false, 0, 1);
	cellWidth = comp.getPreferredSize().width + 2;
	for (int i = 1; i < getColumnCount(); i++)
	    table.getColumnModel().getColumn(i).setPreferredWidth(cellWidth);

	int height = 0;
	for(int row = 0; row < getRowCount(); row++)
	    height += table.getRowHeight(row);
	table.setPreferredScrollableViewportSize(new Dimension(
		table.getPreferredScrollableViewportSize().width,
		height
	));
    }

    /**
     * Make a table from the computed cost table.
     *
     * @return the table, enclosed in a scroll pane.
     */
    public Component makeTable() {
	if (costTab == null || operations == null)
	    return null;

	final JTable table = new JTable(this);
	initColumnSizes(table);
	table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	table.setDefaultRenderer(new Object().getClass(),
				 new CostTableCellRenderer());

	final Point[] cells = new Point[string1.length + string2.length + 1];
	int pos = 0, i = string1.length, j = string2.length;

	while (operations[i][j] != OPR_END) {
	    cells[pos++] = new Point(i, j);

	    switch (operations[i][j]) {
	    case OPR_INS:
		j--;
		break;

	    case OPR_DEL:
		i--;
		break;

	    case OPR_SUB:
		i--;
		j--;
	    }
	}

	cells[pos] = new Point(i, j);
	table.setDefaultRenderer(new Double(0).getClass(),
				 new CostTableCellRenderer(cells, maxCost));
	return new JScrollPane(table);
    }
}

// End of File
