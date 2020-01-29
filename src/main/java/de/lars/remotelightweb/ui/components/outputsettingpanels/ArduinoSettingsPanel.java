package de.lars.remotelightweb.ui.components.outputsettingpanels;

import java.util.ArrayList;
import java.util.List;

import com.fazecast.jSerialComm.SerialPort;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

import de.lars.remotelightclient.devices.arduino.Arduino;
import de.lars.remotelightclient.devices.arduino.ComPort;
import de.lars.remotelightclient.devices.arduino.RgbOrder;

public class ArduinoSettingsPanel extends OutputSettingsPanel {
	
	private Arduino arduino;
	private TextField fieldName;
	private ComboBox<String> boxComPort;
	private ComboBox<RgbOrder> boxOrder;
	private IntegerField fieldPixels;

	public ArduinoSettingsPanel(Arduino arduino, boolean setup) {
		super(arduino, setup);
		this.arduino = arduino;
		
		fieldName = new TextField();
		addFormItem(fieldName, "Name / ID");
		
		boxComPort = new ComboBox<>();
		addFormItem(boxComPort, "ComPort");
		
		fieldPixels = new IntegerField();
		fieldPixels.setMin(1);
		fieldPixels.setStep(1);
		addFormItem(fieldPixels, "Pixels");
		
		boxOrder = new ComboBox<>();
		boxOrder.setItems(RgbOrder.values());
		addFormItem(boxOrder, "RGB order");
		
		add(new OutputPatchPanel(arduino, fieldPixels), 3);
		
		setValues();
	}
	
	
	private void setValues() {
		List<String> ports = new ArrayList<String>();
		for(SerialPort port : ComPort.getComPorts()) {
			ports.add(port.getSystemPortName());
		}
		boxComPort.setItems(ports);
		
		if(arduino.getId() != null) {
			fieldName.setValue(arduino.getId());
		}
		if(arduino.getSerialPort() != null) {
			String pname = arduino.getSerialPort();
			if(ports.contains(pname)) {
				boxComPort.setValue(pname);
			}
		}
		if(arduino.getPixels() <= 0) {
			arduino.setPixels(1);
		}
		fieldPixels.setValue(arduino.getPixels());
		
		if(arduino.getRgbOrder() == null) {
			arduino.setRgbOrder(RgbOrder.GRB);
		}
		boxOrder.setValue(arduino.getRgbOrder());
	}

	
	@Override
	public boolean save() {
		if(fieldName.getValue().isEmpty()) {
			return false;
		}
		arduino.setId(fieldName.getValue());
		arduino.setSerialPort(boxComPort.getValue());
		arduino.setPixels(fieldPixels.getValue());
		return true;
	}

	@Override
	public String getIdField() {
		return fieldName.getValue();
	}

}
