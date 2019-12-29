package de.lars.remotelightweb.ui.components.outputsettingpanels;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;

import de.lars.remotelightclient.devices.Device;
import de.lars.remotelightclient.devices.link.chain.Chain;
import de.lars.remotelightweb.RemoteLightWeb;

public class ChainSettingsPanel extends OutputSettingsPanel {

	private Chain chain;
	private FlexLayout layoutOutputs;
	private TextField fieldName;
	private ComboBox<String> boxOutputs;
	
	public ChainSettingsPanel(Chain chain, boolean setup) {
		super(chain, setup);
		this.chain = chain;
		
		fieldName = new TextField();
		addFormItem(fieldName, "Name / ID");
		
		layoutOutputs = new FlexLayout();
		layoutOutputs.getStyle().set("flex-wrap", "wrap");
		layoutOutputs.getStyle().set("align-content", "flex-start");
		layoutOutputs.setSizeFull();
		addFormItem(layoutOutputs, "Selected outputs");
		
		boxOutputs = new ComboBox<>();
		
		Button btnAdd = new Button("Add");
		btnAdd.addClickListener(e -> {
			if(boxOutputs.getValue() != null) {
				chain.addDevices(RemoteLightWeb.getInstance().getAPI().getDeviceManager().getDevice(boxOutputs.getValue()));
				initOutputBox();
				addOutputsToPanel();
			}
		});
		
		Div controlDiv = new Div(boxOutputs, btnAdd);
		add(controlDiv);
		
		setValues();
		initOutputBox();
		addOutputsToPanel();
	}
	
	private void setValues() {
		if(chain.getId() != null) {
			fieldName.setValue(chain.getId());
		}
	}
	
	private void initOutputBox() {
		// add devices to combobox
		List<String> outputs = new ArrayList<>();
		for(Device d : RemoteLightWeb.getInstance().getAPI().getDeviceManager().getDevices()) {
			if(!(d instanceof Chain) && !chain.getDevices().contains(d)) {
				outputs.add(d.getId());
			}
		}
		boxOutputs.clear();
		boxOutputs.setItems(outputs);
	}
	
	
	private void addOutputsToPanel() {
		layoutOutputs.removeAll();
		for(Device d : chain.getDevices()) {
			Button button = new Button(d.getId(), VaadinIcon.TRASH.create());
			button.getStyle().set("margin", "5px 5px");
			button.addClickListener(e -> {
				chain.removeDevice(d);
				initOutputBox();
				addOutputsToPanel();
			});
			layoutOutputs.add(button);
		}
	}

	@Override
	public boolean save() {
		if(fieldName.getValue().isEmpty()) {
			return false;
		}
		chain.setId(fieldName.getValue());
		return true;
	}

	@Override
	public String getIdField() {
		return fieldName.getValue();
	}
	
}
