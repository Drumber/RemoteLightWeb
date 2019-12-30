package de.lars.remotelightweb.ui.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.github.appreciated.layout.AreaLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.lars.remotelightclient.lua.LuaManager;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.types.SettingObject;
import de.lars.remotelightclient.utils.DirectoryUtil;
import de.lars.remotelightweb.RemoteLightWeb;
import de.lars.remotelightweb.backend.scripteditor.FileEditor;
import de.lars.remotelightweb.ui.MainLayout;
import de.lars.remotelightweb.ui.components.custom.PaperSlider;
import de.lars.remotelightweb.ui.utils.UIUtils;

@CssImport("./styles/scripts-view-style.css")
@PageTitle("Scripts")
@Route(value = "scripts", layout = MainLayout.class)
public class ScriptsView extends VerticalLayout {
	private final String CLASS_NAME = "scripts-view";
	
	private SettingsManager sm = RemoteLightWeb.getInstance().getAPI().getSettingsManager();
	private LuaManager luaManager = RemoteLightWeb.getInstance().getAPI().getLuaManager();
	private FlexLayout layoutScripts;
	private FormLayout layoutSpeed;
	private Div editDiv;
	
	public ScriptsView() {
		sm.addSetting(new SettingObject("scripts.speed", null, 50));
		
		initComponents();
		initLayout();
		initSpeedFooter();
		addScriptsToPanel();
		
		luaManager.setLuaExceptionListener(e -> {
			String text = e.getMessage();
			if(text.contains("stack traceback:")) {
				text = e.getMessage().substring(0, e.getMessage().indexOf("stack traceback:"));
				showError(text);
			}
		});
	}
	
	private void initComponents() {
		layoutScripts = new FlexLayout();
		layoutScripts.addClassName(CLASS_NAME + "__scripts");
		layoutScripts.setHeightFull();
		
		layoutSpeed = new FormLayout();
		editDiv = new Div();
		editDiv.add(UIUtils.addMargin(new Button("Edit script", e -> openEditDialog(luaManager.getActiveLuaScriptPath())), "5px 5px"));
		editDiv.add(UIUtils.addMargin(new Button("Add script", e -> addScript()), "5px 5px"));
		//editDiv.add(UIUtils.addMargin(new Button("Delete script", e -> deleteScript(luaManager.getActiveLuaScriptPath())), "5px 5px"));
		// TODO not working because the file is opened by Java RE
	}
	
	private void initLayout() {
		AreaLayout layout = new AreaLayout(new String[][] {
        	new String[] {"content"},
        	new String[] {"content"},
        	new String[] {"content"},
        	new String[] {"speed"},
        	new String[] {"edit"}
        }).withItemAtArea(layoutScripts, "content")
        		.withItemAtArea(layoutSpeed, "speed")
        		.withItemAtArea(editDiv, "edit");
        layout.setHeightFull();
        getStyle().set("overflow", "auto");
        setHeightFull();
        add(layout);
	}
	
	private void initSpeedFooter() {
		PaperSlider slider = new PaperSlider();
		slider.setMax(200);
		slider.setMin(20);
		slider.setValue((int) sm.getSettingObject("scripts.speed").getValue());
		slider.setPin(true);
		slider.addValueChangeListener(e -> {
			sm.getSettingObject("scripts.speed").setValue(e.getValue());
			luaManager.setDelay(e.getValue());
		});
		layoutSpeed.addFormItem(slider, "Speed");
	}
	
	
	private void addScriptsToPanel() {
		layoutScripts.removeAll();
		for(File script : luaManager.getLuaScripts(DirectoryUtil.getLuaPath())) {
			
			Button button = new Button(DirectoryUtil.getFileName(script));
			button.addClassName(CLASS_NAME + "__buttons");
			button.addClickListener(e -> {
				toggleScript(script.getAbsolutePath());
			});
			
			if(luaManager.getActiveLuaScriptPath() != null && luaManager.getActiveLuaScriptPath().equals(script.getAbsolutePath())) {
				button.getStyle().set("border-style", "dashed");
				button.getStyle().set("border-width", "2px");
			}
			layoutScripts.add(button);
		}
	}
	
	private void toggleScript(String path) {
		if(luaManager.getActiveLuaScriptPath() != null && luaManager.getActiveLuaScriptPath().equals(path)) {
			luaManager.stopLuaScript();
		} else {
			luaManager.runLuaScript(path);
		}
		try {
			Thread.sleep(5);
		} catch (InterruptedException e1) {}
		addScriptsToPanel();
	}
	
	
	private void showError(final String error) {
		getUI().get().access(() -> {
			VerticalLayout div = new VerticalLayout();
			Dialog dialog = new Dialog(div);
			div.add(new H4("An error occured while executing the Lua script"));
			div.add(new Label(error));
			div.add(new Button("Close", e -> dialog.close()));
			dialog.open();
		});
	}
	
	
	private void openEditDialog(String filePath) {
		if(filePath == null || filePath.isEmpty()) {
			Notification.show("No script selected!");
			return;
		}
		
		Dialog dialog = new Dialog();
		dialog.setSizeFull();
		
		dialog.add(new Label(filePath));
		
		TextArea area = new TextArea();
		area.setSizeFull();
		
		String script;
		try {
			script = FileEditor.readFileAsString(filePath);
		} catch (Exception e) {
			dialog.close();
			Notification.show("Error while reading Lua script: " + e.getMessage());
			return;
		}
		area.setValue(script);
		dialog.add(area);
		
		dialog.add(new Button("Cancel", e -> dialog.close()));
		Button save = new Button("Save", e -> {
			try {
				FileEditor.writeStringToFile(filePath, area.getValue());
				dialog.close();
			} catch (FileNotFoundException e1) {
				Notification.show("Could not save script: " + e1.getMessage());
			}
		});
		dialog.add(save);
		dialog.open();
	}
	
	private void addScript() {
		Dialog dialog = new Dialog();
		TextField name = new TextField("Script name");
		dialog.add(name);
		
		dialog.add(new Button("Cancel", e -> dialog.close()));
		Button add = new Button("Add", e -> {
			if(name.getValue().isEmpty()) {
				name.setErrorMessage("Please enter a name.");
				return;
			}
			try {
				boolean success = FileEditor.createFile(DirectoryUtil.getLuaPath() + name.getValue() + ".lua");
				if(success) {
					dialog.close();
					luaManager.scanLuaScripts(DirectoryUtil.getLuaPath());
					addScriptsToPanel();
					openEditDialog(DirectoryUtil.getLuaPath() + name.getValue() + ".lua");
				} else {
					Notification.show("Could not create file! Does it already exists?");
				}
			} catch (IOException e1) {
				Notification.show("Could not create file: " + e1.getMessage());
			}
		});
		dialog.add(add);
		dialog.open();
	}
	
	private void deleteScript(String filePath) {
		if(filePath == null || filePath.isEmpty()) {
			Notification.show("No script selected!");
			return;
		}
		luaManager.stopLuaScript();
		
		Dialog dialog = new Dialog();
		VerticalLayout layout = new VerticalLayout();
		layout.add(new Label("Are you sure you want to delete '" + filePath + "'?"));
		
		Button delete = new Button("Delete", e -> {
			boolean rmvd = FileEditor.deleteFile(filePath);
			addScriptsToPanel();
			Notification.show(rmvd ? "Script successfully deleted!" : "Could not delete script!");
			dialog.close();
		});
		Button cancel = new Button("Cancel", e -> dialog.close());
		delete.getStyle().set("margin", "5px 5px");
		cancel.getStyle().set("margin", "5px 5px");
		
		layout.add(new Div(cancel, delete));
		dialog.add(layout);
		dialog.open();
	}

}
