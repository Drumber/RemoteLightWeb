package de.lars.remotelightweb.ui.views;

import com.github.appreciated.card.Card;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
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
import de.lars.remotelightclient.utils.OutputUtil;
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
public class MainView extends FlexLayout {
	private static final String CLASS_NAME = "main-view";
	
	private DeviceManager dm = RemoteLightWeb.getInstance().getAPI().getDeviceManager();
	private OutputManager om = RemoteLightWeb.getInstance().getAPI().getOutputManager();
	private FlexLayout layoutOutputs;
	private VerticalLayout layoutOptions;
	private ContextMenu contextMenuAdd;
	private Card cardOptions;

	public MainView() {
		initComponents();
		initLayout();
		
		addOutputsToLayout();
    }
	
	private void initComponents() {
		layoutOutputs = new FlexLayout();
		layoutOutputs.addClassName(CLASS_NAME + "__outputs");
		layoutOutputs.setHeightFull();
		layoutOutputs.getStyle().set("overflow", "auto");
		layoutOutputs.getStyle().set("padding", "10px");
		
		layoutOptions = new VerticalLayout();
		layoutOptions.setHeightFull();
		layoutOptions.setPadding(false);
		layoutOptions.addClassName(CLASS_NAME + "__options");
		
		contextMenuAdd = new ContextMenu();
		contextMenuAdd.setOpenOnClick(true);
		contextMenuAdd.addItem("Arduino", e -> showOutputSettings(new Arduino(null, null), true));
		contextMenuAdd.addItem("RLServer", e -> showOutputSettings(new RemoteLightServer(null, null), true));
		contextMenuAdd.addItem("Artnet", e -> showOutputSettings(new Artnet(null), true));
		contextMenuAdd.addItem("Chain", e -> showOutputSettings(new Chain(null), true));
	}
	
	private void initLayout() {
        VerticalLayout innerLayout = new VerticalLayout(layoutOptions);
		innerLayout.setSizeFull();
		cardOptions = new Card(innerLayout);
		cardOptions.setVisible(false);
		cardOptions.getStyle().set("margin", "10px");
		cardOptions.getStyle().set("max-height", "50%");
		//UIUtils.configureCard(card);
        
		add(layoutOutputs, cardOptions);
		getStyle().set("flex-flow", "column");
        setHeightFull();
        setFlexGrow(1, layoutOutputs);
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
			addContextMenu(btn, d);
			layoutOutputs.add(btn);
			
			if(om.getActiveOutput() != null && om.getActiveOutput() == d && d.getConnectionState() == ConnectionState.CONNECTED) {
				btn.getStyle().set("border-style", "dashed");
				btn.getStyle().set("border-width", "2px");
			}
		}
		
		Button add = new Button("Add");
		add.addClassName(CLASS_NAME + "__panels");
		contextMenuAdd.setTarget(add);
		layoutOutputs.add(add);
	}
	
	private void deviceClicked(Device d) {
		showOutputSettings(d, false);
	}
	
	// add right click menu to button
	private void addContextMenu(Button button, Device d) {
		ContextMenu contextMenu = new ContextMenu(button);
		contextMenu.addItem((om.getActiveOutput() == d && d.getConnectionState() == ConnectionState.CONNECTED)
					? "Deactivate" : "Activate", e -> {
						toggleOutput(d);
					});
		contextMenu.addItem("Edit", e -> {
			showOutputSettings(d, false);
		});
		contextMenu.addItem("Remove", e -> {
			removeOutput(d);
		});
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
			cardOptions.setVisible(true);
			layoutOptions.add(getOutputSettingsBgr(panel, setup));
		}
	}
	
	private Component getOutputSettingsBgr(OutputSettingsPanel panel, boolean setup) {
		VerticalLayout layout = new VerticalLayout();
		layout.setHeightFull();
		layout.setPadding(false);
		panel.getStyle().set("max-width", "1000px");
		
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
					? "Deactivate" : "Activate", e -> toggleOutput(panel));
			buttonLay.add(activate);
		}
		
		for(int i = 0; i < buttonLay.getComponentCount(); i++)
			((Button) buttonLay.getComponentAt(i)).getStyle().set("margin", "5px 5px");
		
		H5 title = new H5("Configuration " + panel.getDevice().getId() + String.format(" (%s)", OutputUtil.getOutputTypeAsString(panel.getDevice())));
		
		Div parentOptions = new Div(title, panel);
		parentOptions.setSizeFull();
		parentOptions.getStyle().set("overflow", "auto");
		layout.add(parentOptions);
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
						Notification.show("Output successfully added!", 2000, Position.BOTTOM_START);
					} else {
						Notification.show("Name/ID is already in use!", 3000, Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_ERROR);
					}
				} else {
					addOutputsToLayout();
					Notification.show("Saved changes.", 2000, Position.BOTTOM_START);
				}
			} else {
				Notification.show("Name/ID is already in use!", 3000, Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_ERROR);
			}
		} else {
			Notification.show("The Name/ID field must not be empty!", 3000, Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_ERROR);
		}
	}
	
	
	private void removeOutput(OutputSettingsPanel panel) {
		if(panel.isSetup()) {
			Notification.show("Could not remove output! Output is not configured.", 3000, Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		Device device = panel.getDevice();
		removeOutput(device);
	}
	
	private void removeOutput(Device device) {
		if(dm.isIdUsed(device.getId())) {
			dm.removeDevice(device);
			addOutputsToLayout();
			hideSettingsPanel();
			Notification.show("Output has been removed!", 2000, Position.BOTTOM_START);
		} else {
			Notification.show("Could not remove output! Device not found.", 3000, Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_ERROR);
		}
	}
	
	
	private void toggleOutput(OutputSettingsPanel panel) {
		if(panel.isSetup()) {
			Notification.show("Please save the output first", 3000, Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_ERROR);
			return;
		}
		toggleOutput(panel.getDevice());
	}
	
	private void toggleOutput(Device device) {
		if(dm.isIdUsed(device.getId())) {
			if(device.getConnectionState() == ConnectionState.CONNECTED) {
				om.deactivate(device);
			} else {
				om.setActiveOutput(device);
			}
			addOutputsToLayout();
			showOutputSettings(device, false);
			if(device.getConnectionState() == ConnectionState.FAILED) {
				Notification.show("Could not connect!", 3000, Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_ERROR);
			}
		}
	}
	
	
	private void hideSettingsPanel() {
		layoutOptions.removeAll();
		cardOptions.setVisible(false);
	}

}
