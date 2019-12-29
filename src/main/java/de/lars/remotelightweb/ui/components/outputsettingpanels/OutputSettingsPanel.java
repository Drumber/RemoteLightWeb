package de.lars.remotelightweb.ui.components.outputsettingpanels;

import com.vaadin.flow.component.formlayout.FormLayout;
import de.lars.remotelightclient.devices.Device;

public abstract class OutputSettingsPanel extends FormLayout {
	
	private Device device;
	private boolean setup;

	public OutputSettingsPanel(Device device, boolean setup) {
		this.device = device;
		this.setup = setup;
		setMinWidth(null);
	}
	
	public Device getDevice() {
		return device;
	}

	public boolean isSetup() {
		return setup;
	}

	/**
	 * 
	 * @return returns false if ID is empty
	 */
	public abstract boolean save();
	
	public abstract String getIdField();
	
}
