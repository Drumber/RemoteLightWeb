package de.lars.remotelightweb.ui.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tinylog.Logger;

import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.lars.remotelightcore.settings.Setting;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingSelection;
import de.lars.remotelightcore.settings.types.SettingString;
import de.lars.remotelightweb.RemoteLightWeb;
import de.lars.remotelightweb.backend.utils.UpdateUtil;
import de.lars.remotelightweb.ui.MainLayout;
import de.lars.remotelightweb.ui.components.UpdateDialog;
import de.lars.remotelightweb.ui.components.custom.PaperSlider;
import de.lars.remotelightweb.ui.components.settingpanels.SettingPanel;
import de.lars.remotelightweb.ui.utils.SettingPanelUtil;

@PageTitle("Settings")
@Route(value = "settings", layout = MainLayout.class)
public class SettingsView extends VerticalLayout {
	
	private SettingsManager sm;
	private List<SettingPanel> settingPanels;
	private List<String> settingBlacklist = Arrays.asList(new String[] {"ui.language","main.checkupdates","ui.hideintray"});
	
	public SettingsView() {
		sm = RemoteLightWeb.getInstance().getCore().getSettingsManager();
		settingPanels = new ArrayList<>();
		
		add(getSettingsContainer(SettingCategory.General, "General"));
		add(getSettingsContainer(SettingCategory.Others, "Others"));
		
		PaperSlider brightness = new PaperSlider();
		brightness.setMax(100);
		brightness.setPin(true);
		brightness.setValue((int) RemoteLightWeb.getInstance().getCore().getSettingsManager().getSettingObject("out.brightness").getValue());
		brightness.addValueChangeListener(e -> {
			RemoteLightWeb.getInstance().getCore().getOutputManager().setBrightness(brightness.getValue());
			RemoteLightWeb.getInstance().getCore().getSettingsManager().getSettingObject("out.brightness").setValue(brightness.getValue());
		});
		add(new Label("Brightness"), brightness);
		
		add(new Button("Check for updates", e -> {
			UpdateUtil updater = RemoteLightWeb.getInstance().getUpdateUtil();
			updater.check();
			new UpdateDialog(updater.getParser()).open();;
		}));
		
		Button btnShutdown = new Button("Shutdown");
		add(btnShutdown);
		btnShutdown.addClickListener(e -> {
			Dialog dialog = new Dialog();
			Button web = new Button("Shutdown RemoteLightWeb", w -> {
				RemoteLightWeb.exitApplication();
			});
			web.getStyle().set("margin", "5px 5px");
			dialog.add(web);
			Button system = new Button("Shutdown System", p -> {
				RemoteLightWeb.getInstance().getCore().close(false);
				Runtime runtime = Runtime.getRuntime();
				
				String shutdownCmd = ((SettingString) RemoteLightWeb.getInstance().getCore().getSettingsManager().getSettingFromId("rlweb.shutdowncmd")).getValue();
				if(shutdownCmd == null || shutdownCmd.isEmpty()) {
					shutdownCmd = "shutdown -h now";
				}
				
				try {
					runtime.exec(shutdownCmd);
				} catch (IOException ex) {
					Logger.error(ex, "Shutdown >> Could not execute command!");
				}
			});
			system.getStyle().set("margin", "5px 5px");
			dialog.add(system);
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
