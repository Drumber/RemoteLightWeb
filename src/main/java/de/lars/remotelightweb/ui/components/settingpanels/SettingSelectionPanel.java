package de.lars.remotelightweb.ui.components.settingpanels;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;

import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.settings.types.SettingSelection.Model;

public class SettingSelectionPanel extends SettingPanel {
	
	private ComboBox<String> box;
	private RadioButtonGroup<String> group;

	public SettingSelectionPanel(SettingSelection setting) {
		super(setting);
		
		if(setting.getModel() == Model.ComboBox) {
			box = new ComboBox<>();
			box.setItems(setting.getValues());
			box.setValue(setting.getSelected());
			box.addValueChangeListener(e -> onChanged(this));
			add(box);
			
		} else if(setting.getModel() == Model.RadioButton) {
			group = new RadioButtonGroup<>();
			group.setItems(setting.getValues());
			group.setValue(setting.getSelected());
			group.addValueChangeListener(e -> onChanged(this));
			add(group);
		}
		
		if(setting.getDescription() != null && !setting.getDescription().isEmpty()) {
			add(getHelpIcon());
		}
	}

	@Override
	public void setValue() {
		SettingSelection setting = (SettingSelection) getSetting();
		if(setting.getModel() == Model.ComboBox) {
			setting.setSelected(box.getValue());
		} else if(setting.getModel() == Model.RadioButton) {
			setting.setSelected(group.getValue());
		}
	}

	@Override
	public void updateComponents() {
	}

}
