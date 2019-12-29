package de.lars.remotelightweb.ui.views;

import com.github.appreciated.layout.AreaLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.lars.remotelightclient.devices.ConnectionState;
import de.lars.remotelightclient.devices.Device;
import de.lars.remotelightclient.devices.DeviceManager;
import de.lars.remotelightclient.devices.arduino.Arduino;
import de.lars.remotelightclient.devices.artnet.Artnet;
import de.lars.remotelightclient.devices.link.chain.Chain;
import de.lars.remotelightclient.devices.remotelightserver.RemoteLightServer;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightweb.RemoteLightWeb;
import de.lars.remotelightweb.ui.MainLayout;
import de.lars.remotelightweb.ui.components.outputsettingpanels.ArduinoSettingsPanel;
import de.lars.remotelightweb.ui.components.outputsettingpanels.ArtnetSettingsPanel;
import de.lars.remotelightweb.ui.components.outputsettingpanels.ChainSettingsPanel;
import de.lars.remotelightweb.ui.components.outputsettingpanels.OutputSettingsPanel;
import de.lars.remotelightweb.ui.components.outputsettingpanels.RLServerSettingsPanel;

@CssImport("./styles/main-view-style.css")
@PageTitle("Outputs")
@Route(value = "", layout = MainLayout.class)
public class MainView extends VerticalLayout {
	private static final String CLASS_NAME = "main-view";
	
	private DeviceManager dm = RemoteLightWeb.getInstance().getAPI().getDeviceManager();
	private OutputManager om = RemoteLightWeb.getInstance().getAPI().getOutputManager();
	private FlexLayout layoutOutputs;
	private VerticalLayout layoutOptions;
	private ContextMenu contextMenu;

	public MainView() {
		initComponents();
		initLayout();
		
		addOutputsToLayout();
    }
	
	private void initComponents() {
		layoutOutputs = new FlexLayout();
		layoutOutputs.addClassName(CLASS_NAME + "__outputs");
		layoutOutputs.setHeightFull();
		
		layoutOptions = new VerticalLayout();
		layoutOptions.setHeightFull();
		layoutOptions.addClassName(CLASS_NAME + "__options");
		
		contextMenu = new ContextMenu();
		contextMenu.setOpenOnClick(true);
		contextMenu.addItem("Arduino", e -> showOutputSettings(new Arduino(null, null), true));
		contextMenu.addItem("RLServer", e -> showOutputSettings(new RemoteLightServer(null, null), true));
		contextMenu.addItem("Artnet", e -> showOutputSettings(new Artnet(null), true));
		contextMenu.addItem("Chain", e -> showOutputSettings(new Chain(null), true));
	}
	
	private void initLayout() {
		AreaLayout layout = new AreaLayout(new String[][] {
        	new String[] {"content"},
        	new String[] {"content"},
        	new String[] {"content"},
        	new String[] {"footer"}
        }).withItemAtArea(layoutOutputs, "content")
        		.withItemAtArea(layoutOptions, "footer");
        layout.setHeightFull();
        getStyle().set("overflow", "auto");
        setHeightFull();
        add(layout);
	}
	
	
	private void addOutputsToLayout() {
		layoutOutputs.removeAll();
		for(Device d : dm.getDevices()) {
			String type = "Unknown";
			if(d instanceof Arduino) {
				type = "Arduino";
			} else if(d instanceof RemoteLightServer) {
				type = "RLServer";
			} else if(d instanceof Artnet) {
				type = "Artnet";
			} else if(d instanceof Chain) {
				type = "Chain";
			}
			type += "\n";
			// TODO multiline button with device type
			
			Button btn = new Button(d.getId());
			btn.addClassName(CLASS_NAME + "__panels");
			btn.addClickListener(e -> deviceClicked(d));
			layoutOutputs.add(btn);
			
			if(om.getActiveOutput() != null && om.getActiveOutput() == d && d.getConnectionState() == ConnectionState.CONNECTED) {
				btn.getStyle().set("border-style", "dashed");
				btn.getStyle().set("border-width", "2px");
			}
		}
		
		Button add = new Button("Add");
		add.addClassName(CLASS_NAME + "__panels");
		contextMenu.setTarget(add);
		layoutOutputs.add(add);
	}
	
	private void deviceClicked(Device d) {
		showOutputSettings(d, false);
	}
	
	
	private void showOutputSettings(Device d, boolean setup) {
		OutputSettingsPanel panel = null;
		
		if(d instanceof Arduino) {
			panel = new ArduinoSettingsPanel((Arduino) d, setup);
		} else if(d instanceof RemoteLightServer) {
			panel = new RLServerSettingsPanel((RemoteLightServer) d, setup);
		} else if(d instanceof Artnet) {
			panel = new ArtnetSettingsPanel((Artnet) d, setup);
		} else if(d instanceof Chain) {
			panel = new ChainSettingsPanel((Chain) d, setup);
		}
		
		if(panel != null) {
			layoutOptions.removeAll();
			layoutOptions.add(getOutputSettingsBgr(panel, setup));
		}
	}
	
	private Component getOutputSettingsBgr(OutputSettingsPanel panel, boolean setup) {
		VerticalLayout layout = new VerticalLayout();
		layout.setHeightFull();
		FlexLayout buttonLay = new FlexLayout();
		buttonLay.getStyle().set("flex-wrap", "wrap");
		buttonLay.getStyle().set("align-content", "flex-start");
		
		Button cancel = new Button("Cancel", e -> hideSettingsPanel());
		buttonLay.add(cancel);
		
		Button save = new Button("Save", e -> saveOutput(panel));
		buttonLay.add(save);
		
		if(!setup) {
			Button remove = new Button("Remove", e -> removeOutput(panel));
			buttonLay.add(remove);
			
			Button activate = new Button( (om.getActiveOutput() == panel.getDevice() && panel.getDevice().getConnectionState() == ConnectionState.CONNECTED)
					? "Deactivate" : "Activate", e -> activateOutput(panel));
			buttonLay.add(activate);
		}
		
		for(int i = 0; i < buttonLay.getComponentCount(); i++)
			((Button) buttonLay.getComponentAt(i)).getStyle().set("margin", "5px 5px");
		
		layout.add(panel);
		layout.add(buttonLay);
		return layout;
	}
	
	
	
	private void saveOutput(OutputSettingsPanel panel) {
		if(!panel.getIdField().isEmpty()) {
			
			if(!dm.isIdUsed(panel.getIdField()) || panel.getIdField().equals(panel.getDevice().getId())) {
				
				panel.save();
				Device device = panel.getDevice();
				
				if(panel.isSetup()) {
					if(dm.addDevice(device)) {
						addOutputsToLayout();
						hideSettingsPanel();
						Notification.show("Output successfully added!");
					} else {
						Notification.show("Name/ID is already in use!");
					}
				} else {
					addOutputsToLayout();
					Notification.show("Saved changes.");
				}
			} else {
				Notification.show("Name/ID is already in use!");
			}
		} else {
			Notification.show("The Name/ID field must not be empty!");
		}
	}
	
	
	private void removeOutput(OutputSettingsPanel panel) {
		Device device = panel.getDevice();
		if(!panel.isSetup() && dm.isIdUsed(device.getId())) {
			dm.removeDevice(device);
			addOutputsToLayout();
			hideSettingsPanel();
			Notification.show("Output has been removed!");
		} else {
			Notification.show("Could not remove output!");
		}
	}
	
	
	private void activateOutput(OutputSettingsPanel panel) {
		Device device = panel.getDevice();
		if(!panel.isSetup() && dm.isIdUsed(device.getId())) {
			
			if(device.getConnectionState() == ConnectionState.CONNECTED) {
				om.deactivate(device);
			} else {
				om.setActiveOutput(device);
			}
			addOutputsToLayout();
			showOutputSettings(device, false);
			if(device.getConnectionState() == ConnectionState.FAILED) {
				Notification.show("Could not connect!");
			}
		}
	}
	
	
	private void hideSettingsPanel() {
		layoutOptions.removeAll();
	}

}
