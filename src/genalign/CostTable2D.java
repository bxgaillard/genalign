/*
 * -----------------------------------------------------------------------------
 *
 * GenAlign: A Gene Alignment Program
 * Copyright (c) 2007 Benjamin Gaillard
 *
 * -----------------------------------------------------------------------------
 *
 *        File: CostTable2D.java
 *
 * Description: Ediatble 2D Cost Table Generator
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

import java.util.HashMap;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;

/**
 * Two-dimensional cost table editor.
 */
public final class CostTable2D extends AbstractTableModel {
    /**
     * Cost editor parameters.
     */
    private char[] chars1, chars2;
    private double[][] costs;
    private final HashMap map;
    private JTable table = null;

    /**
     * Not used, but eliminates a warning.
     */
    private final static long serialVersionUID = 1L;

    /**
     * Constructor
     *
     * @param chars1      the first string characters.
     * @param chars2      the second string characters.
     * @param init        the initial cost hast table.
     * @param defaultSame the default cost for same characters.
     * @param defaultDiff the default cost for different characters.
     */
    public CostTable2D(final char[] chars1, final char[] chars2,
		       final HashMap init,
		       final double defaultSame, final double defaultDiff) {
	this.chars1 = chars1;
	this.chars2 = chars2;
	costs = new double[chars1.length][];
	map = init;

	for (int i = 0; i < chars1.length; i++) {
	    costs[i] = new double[chars2.length];
	    for (int j = 0; j < chars2.length; j++) {
		Object object = map.get(new Character(chars1[i]));
		if (object != null)
		    object = ((HashMap)object).get(new Character(chars2[j]));
		costs[i][j] = object == null ?
			      (chars1[i] == chars2[j] ?
			       defaultSame : defaultDiff) :
			      ((Double)object).doubleValue();
	    }
	}
    }

    /**
     * Initialize the table with given values.
     *
     * @param valueSame the value when characters are the same.
     * @param valueDiff the value when characters are different.
     */
    public void initialize(final double valueSame, final double valueDiff) {
	for (int i = 0; i < chars1.length; i++)
	    for (int j = 0; j < chars2.length; j++)
		costs[i][j] = chars1[i] == chars2[j] ? valueSame : valueDiff;
	if (table != null)
	    table.repaint();
    }

    /**
     * Merge input values with the cost hash table.
     */
    public void mergeValues() {
	for (int i = 0; i < chars1.length; i++) {
	    final Object object = map.get(new Character(chars1[i]));
	    final HashMap submap = object == null ? new HashMap(chars2.length) :
				   (HashMap)object;

	    for (int j = 0; j < chars2.length; j++)
		submap.put(new Character(chars2[j]), new Double(costs[i][j]));
	    map.put(new Character(chars1[i]), submap);
	}
    }

    /**
     * Return the number of columns in the model.
     *
     * @return the number of columns in the model.
     */
    public int getColumnCount() {
	return chars1.length + 1;
    }

    /**
     * Return the number of rows in the model.
     *
     * @return the number of rows in the model.
     */
    public int getRowCount() {
	return chars2.length;
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
	if (columnIndex == 0)
	    return String.valueOf(chars2[rowIndex]);
	return new Double(costs[columnIndex - 1][rowIndex]);
    }

    /**
     * Set the value for the cell at (columnIndex, rowIndex).
     *
     * @param aValue      the value to store.
     * @param rowIndex    the row whose value is to be stored.
     * @param columnIndex the column whose value is to be stored.
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	if (columnIndex > 0)
	    costs[columnIndex - 1][rowIndex] = ((Double)aValue).doubleValue();
    }

    /**
     * Return a column name.
     *
     * @param columnIndex the column number.
     *
     * @return the column name.
     */
    public String getColumnName(int columnIndex) {
	if (columnIndex == 0)
	    return new String();
	return String.valueOf(chars1[columnIndex - 1]);
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
     * Check wether a cell is editable.
     *
     * @param rowIndex    the row number.
     * @param columnIndex the column number.
     *
     * @return wether the cell is editable.
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
	return columnIndex > 0;
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

	final Double data = new Double(999);
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
     * Make an editable cost table.
     *
     * @return the table, enclosed in a scroll pane.
     */
    public Component makeTable() {
	table = new JTable(this);
	initColumnSizes(table);
	table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	table.setDefaultRenderer(table.getColumnClass(0),
				 new CostTableCellRenderer());

	return new JScrollPane(table);
    }
}

// End of File
