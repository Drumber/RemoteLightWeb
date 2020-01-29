package de.lars.remotelightweb.ui.components.outputsettingpanels;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;

import de.lars.remotelightclient.devices.artnet.Artnet;

public class ArtnetSettingsPanel extends OutputSettingsPanel {
	
	private final String END_UNIVERSE_PREFIX = "End Universe: ";
	private Artnet artnet;
	private TextField fieldName;
	private IntegerField fieldPixels;
	private TextField fieldIP;
	private Checkbox boxBroadcast;
	private Label lblEndUniverse;
	private IntegerField fieldStartUniverse;
	private IntegerField fieldSubnet;

	public ArtnetSettingsPanel(Artnet artnet, boolean setup) {
		super(artnet, setup);
		this.artnet = artnet;
		
		fieldName = new TextField();
		addFormItem(fieldName, "Name / ID");
		
		fieldIP = new TextField();
		FormItem addressItem = addFormItem(fieldIP, "IP Address");
		
		boxBroadcast = new Checkbox("Broadcast");
		boxBroadcast.addValueChangeListener(e -> {
			if(boxBroadcast.getValue()) {
				fieldIP.setEnabled(false);
			} else {
				fieldIP.setEnabled(true);
			}
		});
		addressItem.add(boxBroadcast);
		boxBroadcast.getStyle().set("padding", "8px");
		
		fieldSubnet = new IntegerField();
		fieldSubnet.setStep(1);
		fieldSubnet.setMin(0);
		fieldSubnet.setHasControls(true);
		addFormItem(fieldSubnet, "Subnet");
		
		fieldStartUniverse = new IntegerField();
		fieldStartUniverse.setStep(1);
		fieldStartUniverse.setMin(0);
		fieldStartUniverse.setValue(0);
		fieldStartUniverse.setHasControls(true);
		fieldStartUniverse.addValueChangeListener(e -> {
			lblEndUniverse.setText(END_UNIVERSE_PREFIX + artnet.getEndUniverse(fieldStartUniverse.getValue(), fieldPixels.getValue()));
		});
		FormItem universeItem = addFormItem(fieldStartUniverse, "Start Universe");
		
		lblEndUniverse = new Label(END_UNIVERSE_PREFIX + "0");
		universeItem.add(lblEndUniverse);
		lblEndUniverse.getStyle().set("padding", "8px");
		
		fieldPixels = new IntegerField();
		fieldPixels.setMin(1);
		fieldPixels.setStep(1);
		fieldPixels.setValue(1);
		fieldPixels.addValueChangeListener(e -> {
			lblEndUniverse.setText(END_UNIVERSE_PREFIX + artnet.getEndUniverse(fieldStartUniverse.getValue(), fieldPixels.getValue()));
		});
		addFormItem(fieldPixels, "Pixels");
		
		add(new OutputPatchPanel(artnet, fieldPixels), 3);
		
		setValues();
	}
	
	
	private void setValues() {
		if(artnet.getId() != null) {
			fieldName.setValue(artnet.getId());
		}
		
		if(artnet.getPixels() <= 0) {
			artnet.setPixels(1);
		}
		fieldPixels.setValue(artnet.getPixels());
		
		boxBroadcast.setValue(artnet.isBroadcast());
		if(artnet.getUnicastAddress() != null) {
			fieldIP.setValue(artnet.getUnicastAddress());
		}
		
		fieldSubnet.setValue(artnet.getSubnet());
		fieldStartUniverse.setValue(artnet.getStartUniverse());
		lblEndUniverse.setText(END_UNIVERSE_PREFIX + artnet.getEndUniverse(artnet.getStartUniverse(), artnet.getPixels()));
	}

	
	@Override
	public boolean save() {
		if(fieldName.getValue().isEmpty()) {
			return false;
		}
		artnet.setId(fieldName.getValue());
		artnet.setPixels(fieldPixels.getValue());
		artnet.setUnicastAddress(fieldIP.getValue());
		artnet.setBroadcast(boxBroadcast.getValue());
		artnet.setSubnet(fieldSubnet.getValue());
		artnet.setStartUniverse(fieldStartUniverse.getValue());
		return true;
	}

	@Override
	public String getIdField() {
		return fieldName.getValue();
	}

}
