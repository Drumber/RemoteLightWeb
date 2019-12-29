package de.lars.remotelightweb.ui.components.settingpanels;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;

import de.lars.remotelightclient.settings.types.SettingString;

public class SettingStringPanel extends SettingPanel {
	
	private TextField field;

	public SettingStringPanel(SettingString setting) {
		super(setting);
		
		field = new TextField();
		field.setValue(setting.getValue());
		field.addValueChangeListener(e -> onChanged(this));
		this.setFlexGrow(2, field);
		add(field);
		
		if(setting.getDescription() != null && !setting.getDescription().isEmpty()) {
			// TODO help text
		}
	}

	@Override
	public void setValue() {
		((SettingString) getSetting()).setValue(field.getValue());
		
	}

	@Override
	public void updateComponents() {
		field.setValue(((SettingString) getSetting()).getValue());
	}

}
