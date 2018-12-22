package va.vt.cbil;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

/*
 * A class used to help layout.
 */
class GridBagPut{
	GridBagConstraints gbc = null;	// Layout
	
	public GridBagPut(Container container) {
		container.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		
	}
	/**
	 * This method is convenient to put the component into the GridBag.
	 * @param ob The object you want to put.
	 * @param container The container you put into.
	 * @param x The gridx of the GridBagConstraints.
	 * @param y The gridy of the GridBagConstraints.
	 * @param gridwidth The gridwidth of the GridBagConstraints.
	 * @param gridheight The gridheight of the GridBagConstraints.
	 */
	public void putGridBag(Component ob, Container container, int x, int y) {
		gbc.gridx = x;
		gbc.gridy = y;
		container.add(ob,gbc);
	}
	
	public void putGridBag(Component ob, Container container, int x, int y, int gridwidth, int gridheight) {
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = gridwidth;
		gbc.gridheight = gridheight;
		container.add(ob,gbc);
	}
	public void setAnchorNorthWest() {
		gbc.anchor = GridBagConstraints.NORTHWEST;
	}
	public void setAnchorCenter() {
		gbc.anchor = GridBagConstraints.CENTER;
	}
	public void fillBoth() {
		gbc.fill = GridBagConstraints.BOTH;
	}
}

