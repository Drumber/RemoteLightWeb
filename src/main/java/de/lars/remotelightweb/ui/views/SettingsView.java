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

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.settings.Setting;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;
import de.lars.remotelightclient.settings.types.SettingSelection;
import de.lars.remotelightweb.RemoteLightWeb;
import de.lars.remotelightweb.ui.MainLayout;
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
		sm = RemoteLightWeb.getInstance().getAPI().getSettingsManager();
		settingPanels = new ArrayList<>();
		
		add(getSettingsContainer(SettingCategory.General, "General"));
		add(getSettingsContainer(SettingCategory.Others, "Others"));
		
		PaperSlider brightness = new PaperSlider();
		brightness.setMax(100);
		brightness.setPin(true);
		brightness.setValue((int) Main.getInstance().getSettingsManager().getSettingObject("out.brightness").getValue());
		brightness.addValueChangeListener(e -> {
			Main.getInstance().getOutputManager().setBrightness(brightness.getValue());
			Main.getInstance().getSettingsManager().getSettingObject("out.brightness").setValue(brightness.getValue());
		});
		add(new Label("Brightness"), brightness);
		
		Button close = new Button("Shutdown");
		add(close);
		close.addClickListener(e -> {
			Dialog dialog = new Dialog();
			Button web = new Button("Shutdown RemoteLightWeb", w -> {
				RemoteLightWeb.exitApplication();
			});
			web.getStyle().set("margin", "5px 5px");
			dialog.add(web);
			Button pi = new Button("Shutdown System (Raspberry Pi)", p -> {
				RemoteLightWeb.getInstance().getAPI().close(false);
				Runtime runtime = Runtime.getRuntime();
				try {
					runtime.exec("shutdown -h now");
				} catch (IOException ex) {
					Logger.error(ex, "Linux Shutdown >> Could not execute command!");
				}
			});
			pi.getStyle().set("margin", "5px 5px");
			dialog.add(pi);
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
