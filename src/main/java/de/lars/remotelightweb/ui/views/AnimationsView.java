package de.lars.remotelightweb.ui.views;

import java.util.List;

import com.github.appreciated.layout.AreaLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.animation.AnimationManager;
import de.lars.remotelightclient.devices.ConnectionState;
import de.lars.remotelightclient.settings.Setting;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.types.SettingObject;
import de.lars.remotelightweb.RemoteLightWeb;
import de.lars.remotelightweb.ui.MainLayout;
import de.lars.remotelightweb.ui.components.custom.PaperSlider;
import de.lars.remotelightweb.ui.components.settingpanels.SettingPanel;
import de.lars.remotelightweb.ui.utils.SettingPanelUtil;

@CssImport("./styles/animations-view-style.css")
@PageTitle("Animations")
@Route(value = "animations", layout = MainLayout.class)
public class AnimationsView extends VerticalLayout {
	private final String CLASS_NAME = "animations-view";
	
	private SettingsManager sm = RemoteLightWeb.getInstance().getAPI().getSettingsManager();
	private AnimationManager am = RemoteLightWeb.getInstance().getAPI().getAnimationManager();
	private FlexLayout layoutAnimations;
	private VerticalLayout layoutOptions;
	private FormLayout layoutSpeed;
	
	public AnimationsView() {
		sm.addSetting(new SettingObject("animations.speed", null, 50));
		initComponents();
		initLayout();
		initSpeedFooter();
		
		addAnimationsToPanel();
	}
	
	private void initComponents() {
		layoutAnimations = new FlexLayout();
		layoutAnimations.addClassName(CLASS_NAME + "__animations");
		layoutAnimations.setHeightFull();
		
		layoutOptions = new VerticalLayout();
		layoutOptions.setHeightFull();
		layoutOptions.addClassName(CLASS_NAME + "__options");
		
		layoutSpeed = new FormLayout();
	}
	
	private void initLayout() {
		AreaLayout layout = new AreaLayout(new String[][] {
        	new String[] {"content"},
        	new String[] {"content"},
        	new String[] {"content"},
        	new String[] {"options"},
        	new String[] {"speed"}
        }).withItemAtArea(layoutAnimations, "content")
        		.withItemAtArea(layoutOptions, "options")
        		.withItemAtArea(layoutSpeed, "speed");
        layout.setHeightFull();
        getStyle().set("overflow", "auto");
        setHeightFull();
        add(layout);
	}
	
	private void initSpeedFooter() {
		PaperSlider slider = new PaperSlider();
		slider.setMax(200);
		slider.setMin(20);
		slider.setValue((int) sm.getSettingObject("animations.speed").getValue());
		slider.setPin(true);
		slider.addValueChangeListener(e -> {
			sm.getSettingObject("animations.speed").setValue(e.getValue());
			am.setDelay(e.getValue());
		});
		layoutSpeed.addFormItem(slider, "Speed");
	}
	
	
	private void addAnimationsToPanel() {
		layoutAnimations.removeAll();
		for(Animation a : am.getAnimations()) {
			Button button = new Button(a.getDisplayname());
			button.addClassName(CLASS_NAME + "__buttons");
			
			if(am.getActiveAnimation() != null && am.getActiveAnimation().getName().equals(a.getName())) {
				button.getStyle().set("border-style", "dashed");
				button.getStyle().set("border-width", "2px");
				showAnimationSettings();
			}
			button.addClickListener(e -> {
				toggleAnimation(a);
				showAnimationSettings();
			});
			
			layoutAnimations.add(button);
		}
	}
	
	private void toggleAnimation(Animation ani) {
		if(am.getActiveAnimation() != null && am.getActiveAnimation().getName().equals(ani.getName())) {
			am.stop();
		} else {
			if(RemoteLightWeb.getInstance().getAPI().getOutputManager().getActiveOutput() != null &&
					RemoteLightWeb.getInstance().getAPI().getOutputManager().getActiveOutput().getState() == ConnectionState.CONNECTED)
			{
				am.start(ani);
			} else {
				Notification.show("No active output.");
			}
		}
		addAnimationsToPanel();
	}
	
	private void showAnimationSettings() {
		layoutOptions.removeAll();
		
		List<Setting> settings = am.getCurrentAnimationOptions();
		if(settings != null && settings.size() > 0) {
			for(Setting s : settings) {
				SettingPanel panel = SettingPanelUtil.getSettingPanel(s);
				panel.setSettingChangedListener(e -> panel.setValue());
				layoutOptions.add(panel);
			}
		}
	}

}
