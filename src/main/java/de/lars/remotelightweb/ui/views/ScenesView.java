package de.lars.remotelightweb.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.lars.remotelightclient.devices.ConnectionState;
import de.lars.remotelightclient.scene.Scene;
import de.lars.remotelightclient.scene.SceneManager;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightweb.RemoteLightWeb;
import de.lars.remotelightweb.ui.MainLayout;

@CssImport("./styles/animations-view-style.css")
@PageTitle("Scenes")
@Route(value = "scenes", layout = MainLayout.class)
public class ScenesView extends VerticalLayout {
	private final String CLASS_NAME = "animations-view";
	
	private SettingsManager sm = RemoteLightWeb.getInstance().getAPI().getSettingsManager();
	private SceneManager scm = RemoteLightWeb.getInstance().getAPI().getSceneManager();
	private FlexLayout layoutScenes;
	
	public ScenesView() {
		initComponents();
		addScenesToPanel();
	}
	
	private void initComponents() {
		layoutScenes = new FlexLayout();
		layoutScenes.addClassName(CLASS_NAME + "__animations");
		layoutScenes.setHeightFull();
		getStyle().set("overflow", "auto");
        setHeightFull();
        add(layoutScenes);
	}
	
	
	private void addScenesToPanel() {
		layoutScenes.removeAll();
		for(Scene s : scm.getScenes()) {
			Button button = new Button(s.getDisplayname());
			button.addClassName(CLASS_NAME + "__buttons");
			button.getElement().setProperty("title", s.getDisplayname());
			
			if(scm.getActiveScene() != null && scm.getActiveScene().getName().equals(s.getName())) {
				button.getStyle().set("border-style", "dashed");
				button.getStyle().set("border-width", "2px");
			}
			button.addClickListener(e -> toggleScene(s));
			
			layoutScenes.add(button);
		}
	}
	
	private void toggleScene(Scene s) {
		if(scm.getActiveScene() != null && scm.getActiveScene().getName().equals(s.getName())) {
			scm.stop();
		} else {
			if(RemoteLightWeb.getInstance().getAPI().getOutputManager().getActiveOutput() != null &&
					RemoteLightWeb.getInstance().getAPI().getOutputManager().getActiveOutput().getState() == ConnectionState.CONNECTED)
			{
				scm.start(s);
			} else {
				Notification.show("No active output.");
			}
		}
		addScenesToPanel();
	}

}
