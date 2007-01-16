/*
 * -----------------------------------------------------------------------------
 *
 * GenAlign: A Gene Alignment Program
 * Copyright (c) 2007 Benjamin Gaillard
 *
 * -----------------------------------------------------------------------------
 *
 *        File: Application.java
 *
 * Description: Main Application Class
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

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main application launcher
 */
public final class Application {
    /**
     * Wether to pack a frame at creation (see Application() method).
     */
    private boolean packFrame = false;

    /**
     * Construct and show the application.
     */
    public Application() {
	MainFrame frame = new MainFrame();

	// Validate frames that have preset sizes
	// Pack frames that have useful preferred size info,
	// e.g. from their layout
	if (packFrame)
	    frame.pack();
	else
	    frame.validate();

	// Center the window
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	Dimension frameSize = frame.getSize();
	if (frameSize.height > screenSize.height)
	    frameSize.height = screenSize.height;
	if (frameSize.width > screenSize.width)
	    frameSize.width = screenSize.width;
	frame.setLocation((screenSize.width - frameSize.width) / 2,
			  (screenSize.height - frameSize.height) / 2);

	// Eventually show the window
	frame.setVisible(true);
    }

    /**
     * Application entry point.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		try {
		    UIManager.setLookAndFeel(UIManager.
					     getSystemLookAndFeelClassName());
		} catch (Exception exception) {
		    exception.printStackTrace();
		}
		new Application();
	    }
	});
    }
}

// End of File
