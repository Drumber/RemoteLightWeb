package de.lars.remotelightweb.ui.components.settingpanels;

import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;

import de.lars.remotelightclient.settings.types.SettingInt;

public class SettingIntPanel extends SettingPanel {
	
	private IntegerField field;

	public SettingIntPanel(SettingInt setting) {
		super(setting);
		
		field = new IntegerField();
		field.setHasControls(true);
		field.setValue(setting.getValue());
		field.setMin(setting.getMin());
		field.setMax(setting.getMax());
		field.setStep(setting.getStepsize());
		field.addValueChangeListener(e -> onChanged(this));
		this.setFlexGrow(2, field);
		add(field);
		
		if(setting.getDescription() != null && !setting.getDescription().isEmpty()) {
			// TODO help text
		}
	}

	@Override
	public void setValue() {
		((SettingInt) getSetting()).setValue(field.getValue());
	}

	@Override
	public void updateComponents() {
		field.setValue(((SettingInt) getSetting()).getValue());
	}

}
