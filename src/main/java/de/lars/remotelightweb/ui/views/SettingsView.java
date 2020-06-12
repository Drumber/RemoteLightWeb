package de.lars.remotelightweb.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.lars.remotelightcore.settings.Setting;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightweb.RemoteLightWeb;
import de.lars.remotelightweb.backend.utils.UpdateUtil;
import de.lars.remotelightweb.ui.MainLayout;
import de.lars.remotelightweb.ui.components.dialogs.ShutdownDialog;
import de.lars.remotelightweb.ui.components.dialogs.UpdateDialog;
import de.lars.remotelightweb.ui.components.settingpanels.SettingPanel;
import de.lars.remotelightweb.ui.utils.SettingPanelUtil;

@PageTitle("Settings")
@Route(value = "settings", layout = MainLayout.class)
public class SettingsView extends VerticalLayout {
	
	private SettingsManager sm;
	private List<SettingPanel> settingPanels;
	private List<String> settingBlacklist = Arrays.asList(new String[] {"main.checkupdates","ui.hideintray"});
	
	public SettingsView() {
		sm = RemoteLightWeb.getInstance().getCore().getSettingsManager();
		settingPanels = new ArrayList<>();
		
		add(getSettingsContainer(SettingCategory.General, "General"));
		add(getSettingsContainer(SettingCategory.Others, "Others"));
		
		add(new Button("Check for updates", e -> {
			UpdateUtil updater = RemoteLightWeb.getInstance().getUpdateUtil();
			updater.check();
			new UpdateDialog(updater.getParser()).open();
		}));
		
		Button btnShutdown = new Button("Shutdown");
		add(btnShutdown);
		btnShutdown.addClickListener(e -> {
			ShutdownDialog dialog = new ShutdownDialog();
			dialog.open();
		});
	}
	
	private VerticalLayout getSettingsContainer(SettingCategory category, String title) {
		VerticalLayout layout = new VerticalLayout();
		H3 catTitle = new H3(title);
		layout.add(catTitle);
		
		for(Setting s : sm.getSettingsFromCategory(category)) {
			if(settingBlacklist.contains(s.getId())) {
				continue;
			}
			SettingPanel spanel = SettingPanelUtil.getSettingPanel(s);
			settingPanels.add(spanel);
			layout.add(spanel);
			
			// style setting listener
			if(s.getId().equals("ui.style")) {
				spanel.setSettingChangedListener(l -> {
					spanel.setValue();
					MainLayout.getInstance().setDarkMode(((SettingSelection) l.getSetting()).getSelected().equalsIgnoreCase("Dark"));
				});
			}
		}
		return layout;
	}
	
	@Override
	protected void onDetach(DetachEvent detachEvent) {
		for(SettingPanel sp : settingPanels) {
			sp.setValue();
		}
		super.onDetach(detachEvent);
	}

}
