package de.lars.remotelightweb.ui.components.outputsettingpanels;

import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;

import de.lars.remotelightclient.devices.remotelightserver.RemoteLightServer;

public class RLServerSettingsPanel extends OutputSettingsPanel {
	
	private RemoteLightServer rlServer;
	private TextField fieldName;
	private TextField fieldIP;
	private IntegerField fieldPixels;

	public RLServerSettingsPanel(RemoteLightServer rlServer, boolean setup) {
		super(rlServer, setup);
		this.rlServer = rlServer;
		
		fieldName = new TextField();
		addFormItem(fieldName, "Name / ID");
		
		fieldIP = new TextField();
		addFormItem(fieldIP, "IP / Hostname");
		
		fieldPixels = new IntegerField();
		fieldPixels.setMin(1);
		fieldPixels.setStep(1);
		addFormItem(fieldPixels, "Pixels");
		
		add(new OutputPatchPanel(rlServer, fieldPixels), 3);
		
		setValues();
	}
	
	
	private void setValues() {
		if(rlServer.getId() != null) {
			fieldName.setValue(rlServer.getId());
		}
		if(rlServer.getIp() != null) {
			fieldIP.setValue(rlServer.getIp());
		}
		if(rlServer.getPixels() <= 0) {
			rlServer.setPixels(1);
		}
		fieldPixels.setValue(rlServer.getPixels());
	}

	
	@Override
	public boolean save() {
		if(fieldName.getValue().isEmpty()) {
			return false;
		}
		rlServer.setId(fieldName.getValue());
		rlServer.setIp(fieldIP.getValue());
		rlServer.setPixels(fieldPixels.getValue());
		return true;
	}

	@Override
	public String getIdField() {
		return fieldName.getValue();
	}

}
