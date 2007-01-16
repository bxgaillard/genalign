/*
 * -----------------------------------------------------------------------------
 *
 * GenAlign: A Gene Alignment Program
 * Copyright (c) 2007 Benjamin Gaillard
 *
 * -----------------------------------------------------------------------------
 *
 *        File: CostDefaultTableCellRenderer.java
 *
 * Description: Table Cell Renderer for the Cost Table
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

import java.text.NumberFormat;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Table cell renderer for the representation of a cost data table.
 */
public final class CostTableCellRenderer extends DefaultTableCellRenderer {
    /**
     * Some standard colors.
     */
    private final static Color NORMAL_BG =
	UIManager.getColor("Table.background");
    private final static Color NORMAL_FG =
	UIManager.getColor("Table.foreground");
    private final static Color SELECTED_BG =
	UIManager.getColor("Table.selectionBackground");
    private final static Color SELECTED_FG =
	UIManager.getColor("Table.selectionForeground");
    private final static Color HEADER_BG =
	UIManager.getColor("TableHeader.background");
    private final static Color HEADER_FG =
	UIManager.getColor("TableHeader.foreground");

    /**
     * Common type classes.
     */
    private final Class numberClass, doubleClass;

    /**
     * Bevel for the vertical header cells.
     */
    private final BevelBorder bevel;

    /**
     * List of the highlighted cells positions.
     */
    private final Point[] hlCells;
    private final int hlCellCount;

    /**
     * The maximum value of the table.
     */
    private final double max;

    /**
     * Not used, but eliminates a warning.
     */
    private final static long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * @param highlighted the positions of the highlighted cells.
     * @param max         the maximum value of the table.
     */
    public CostTableCellRenderer(final Point[] highlighted, final double max) {
	Class numberClass = null, doubleClass = null;

	try {
	    numberClass = Class.forName("java.lang.Number");
	    doubleClass = Class.forName("java.lang.Double");
	} catch (ClassNotFoundException exception) {}

	this.numberClass = numberClass;
	this.doubleClass = doubleClass;

	final BevelBorder temp = new BevelBorder(BevelBorder.RAISED);
	bevel = new BevelBorder(BevelBorder.RAISED,
				temp.getHighlightOuterColor(), HEADER_BG,
				temp.getShadowOuterColor(), HEADER_BG);

	int i;
	for (i = 0; i < highlighted.length; i++)
	    if (highlighted[i] == null)
		break;
	hlCells = highlighted;
	hlCellCount = i;

	this.max = max;
    }

    /**
     * Constructor for a renderer without maximum value.
     */
    public CostTableCellRenderer(final Point[] highlighted) {
	this(highlighted, Double.NaN);
    }

    /**
     * Constructor for a renderer without highlighted cells and maximum value.
     */
    public CostTableCellRenderer() {
	this(new Point[0]);
    }

    /**
     * Blend two colors.
     *
     * @param color1 the first color.
     * @param color2 the second color.
     * @param ratio  the blending ratio.
     *
     * @return the resulting color.
     */
    private static Color blendColors(final Color color1, final Color color2,
				     double ratio) {
	if (ratio < 0.0)
	    ratio = 0.0;
	else if (ratio > 1.0)
	    ratio = 1.0;

	return new Color((int)(color1.getRed() * ratio +
			       color2.getRed() * (1.0 - ratio) + 0.5),
			 (int)(color1.getGreen() * ratio +
			       color2.getGreen() * (1.0 - ratio) + 0.5),
			 (int)(color1.getBlue() * ratio +
			       color2.getBlue() * (1.0 - ratio) + 0.5));
    }

    /**
     * Blend two colors given a value within an interval.
     *
     * @param color1 the first color.
     * @param color2 the second color.
     * @param min    the minimum the value can get.
     * @param max    the maximum the value can get.
     * @param value  the value.
     *
     * @return the resulting color.
     */
    private static Color blendColors(final Color color1, final Color color2,
				     final double min, final double max,
				     final double value) {
	return blendColors(color1, color2, (value - min) / (max - min));
    }

    /**
     * Get the background color for a normal cell.
     *
     * @param value the value contained in the cell.
     *
     * @return the resulting color.
     */
    private Color getBlendedBackground(final double value) {
	if (max < 0.0)
	    return NORMAL_BG;
	return blendColors(NORMAL_BG.darker(), NORMAL_BG, 0.0, max, value);
    }

    /**
     * Get the background color for a highlighted cell.
     *
     * @param position the position of the cell in the highlighted cells list.
     *
     * @return the resulting color.
     */
    private Color getBlendedHighlight(final int position) {
	return blendColors(SELECTED_BG, SELECTED_BG.darker(),
			   (double)position / (double)hlCellCount);
    }

    /**
     * Returns the default table cell renderer.
     *
     * @param table      the JTable.
     * @param value      the value to assign to the cell at [row, column].
     * @param isSelected true if cell is selected.
     * @param hasFocus   true if cell has focus.
     * @param row        the row of the cell to render.
     * @param column     the column of the cell to render.
     *
     * @return the default table cell renderer.
     */
    public Component getTableCellRendererComponent(JTable table,
						   Object value,
						   boolean isSelected,
						   boolean hasFocus,
						   int row,
						   int column) {
	super.getTableCellRendererComponent(table, value, isSelected,
					    hasFocus, row, column);

	if (numberClass.isInstance(value))
	    setHorizontalAlignment(JLabel.RIGHT);
	if (doubleClass.isInstance(value) && value != null)
	    setText(NumberFormat.getInstance().format(value));

	if (column == 0) {
	    setBackground(HEADER_BG);
	    setForeground(HEADER_FG);
	    setBorder(bevel);
	} else {
	    boolean isHighlighted = false;
	    int i;

	    for (i = 0; i < hlCellCount; i++)
		if (column == hlCells[i].x + 1 && row == hlCells[i].y) {
		    isHighlighted = true;
		    break;
		}

	    if (isHighlighted) {
		setBackground(getBlendedHighlight(i));
		setForeground(SELECTED_FG);
	    } else {
		if (doubleClass.isInstance(value) && value != null)
		    setBackground(getBlendedBackground(((Double)value).doubleValue()));
		else
		    setBackground(NORMAL_BG);
		setForeground(NORMAL_FG);
	    }
	}

	return this;
    }
}

// End of File
