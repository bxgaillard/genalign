/*
 * -----------------------------------------------------------------------------
 *
 * GenAlign: A Gene Alignment Program
 * Copyright (c) 2007 Benjamin Gaillard
 *
 * -----------------------------------------------------------------------------
 *
 *        File: CostTable1D.java
 *
 * Description: Editable 1D Cost Table Generator
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
 * One-dimensional cost table editor.
 */
public final class CostTable1D extends AbstractTableModel {
    /**
     * Cost editor parameters.
     */
    private char[] chars;
    private double[] costs;
    private final HashMap map;
    private JTable table = null;

    /**
     * Not used, but eliminates a warning.
     */
    private final static long serialVersionUID = 1L;

    /**
     * Constructor
     *
     * @param chars       the characters.
     * @param init        the initial cost hast table.
     * @param defaultCost the default cost.
     */
    public CostTable1D(final char[] chars, final HashMap init,
		       final double defaultCost) {
	this.chars = chars;
	costs = new double[chars.length];
	map = init;

	for (int i = 0; i < chars.length; i++) {
	    final Object object = map.get(new Character(chars[i]));
	    costs[i] = object == null ? defaultCost :
		       ((Double)object).doubleValue();
	}
    }

    /**
     * Initialize the table to a given value.
     *
     * @param value the value.
     */
    public void initialize(final double value) {
	for (int i = 0; i < chars.length; i++)
	    costs[i] = value;
	if (table != null)
	    table.repaint();
    }

    /**
     * Merge input values with the cost hash table.
     */
    public void mergeValues() {
	for (int i = 0; i < chars.length; i++)
	    map.put(new Character(chars[i]), new Double(costs[i]));
    }

    /**
     * Return the number of columns in the model.
     *
     * @return the number of columns in the model.
     */
    public int getColumnCount() {
	return chars.length;
    }

    /**
     * Return the number of rows in the model.
     *
     * @return the number of rows in the model.
     */
    public int getRowCount() {
	return 1;
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
	return new Double(costs[columnIndex]);
    }

    /**
     * Set the value for the cell at (columnIndex, rowIndex).
     *
     * @param aValue      the value to store.
     * @param rowIndex    the row whose value is to be stored.
     * @param columnIndex the column whose value is to be stored.
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	costs[columnIndex] = ((Double)aValue).doubleValue();
    }

    /**
     * Return a column name.
     *
     * @param columnIndex the column number.
     *
     * @return the column name.
     */
    public String getColumnName(int columnIndex) {
	return String.valueOf(chars[columnIndex]);
    }

    /**
     * Return a column class.
     *
     * @param columnIndex the column number.
     *
     * @return the column class.
     */
    public Class getColumnClass(int columnIndex) {
	return new Double(0.0).getClass();
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
	return true;
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
	final Double data = new Double(999);
	final Component comp = table.getDefaultRenderer(data.getClass()).
				   getTableCellRendererComponent(table, data,
								 false, false,
								 0, 1);
	final int cellWidth = comp.getPreferredSize().width + 2;

	for (int i = 0; i < getColumnCount(); i++)
	    table.getColumnModel().getColumn(i).setPreferredWidth(cellWidth);

	table.setPreferredScrollableViewportSize(new Dimension(
		table.getPreferredScrollableViewportSize().width,
		table.getRowHeight(0)
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

	final JScrollPane scroll = new JScrollPane(table);
	scroll.setHorizontalScrollBarPolicy(JScrollPane.
					    HORIZONTAL_SCROLLBAR_ALWAYS);
	scroll.setVerticalScrollBarPolicy(JScrollPane.
					  VERTICAL_SCROLLBAR_NEVER);
	    return scroll;
    }
}

// End of File
