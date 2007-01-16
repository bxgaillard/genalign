/*
 * -----------------------------------------------------------------------------
 *
 * GenAlign: A Gene Alignment Program
 * Copyright (c) 2007 Benjamin Gaillard
 *
 * -----------------------------------------------------------------------------
 *
 *        File: ExtFileFilter.java
 *
 * Description: Extension-Based File Filter
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

import java.util.HashSet;
import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * A file filter based on file extension.
 */
public final class ExtFileFilter extends FileFilter {
    /**
     * The file filter parameters.
     */
    private final String description;
    private final HashSet extensions;

    /**
     * Constructor.
     *
     * @param desc the description of the file filter.
     * @param exts the accepted file extensions.
     */
    public ExtFileFilter(final String desc, final String[] exts) {
	String description = desc + " (";
	extensions = new HashSet(exts.length);

	for (int i = 0; i < exts.length; i++) {
	    extensions.add(exts[i]);
	    if (i > 0)
		description += ", ";
	    description += "*." + exts[i];
	}

	this.description = description + ')';
    }

    /**
     * Get the extension of a file.
     *
     * @param f the file.
     *
     * @return the file extension.
     */
    private static String getExtension(final File f) {
	String ext = null;
	final String s = f.getName();
	final int i = s.lastIndexOf('.');

	if (i > 0 && i < s.length() - 1)
	    ext = s.substring(i + 1).toLowerCase();
	return ext;
    }

    /**
     * Whether the given file is accepted by this filter.
     *
     * @param f the file.
     *
     * @return wether the file is accepted.
     */
    public boolean accept(File f) {
	return f.isDirectory() || extensions.contains(getExtension(f));
    }

    /**
     * The description of this filter.
     *
     * @return the description.
     */
    public String getDescription() {
	return description;
    }
}

// End of File
