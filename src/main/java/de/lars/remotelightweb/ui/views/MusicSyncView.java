package de.lars.remotelightweb.ui.views;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

import com.github.appreciated.card.Card;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.devices.ConnectionState;
import de.lars.remotelightclient.musicsync.InputUtil;
import de.lars.remotelightclient.musicsync.MusicEffect;
import de.lars.remotelightclient.musicsync.MusicSyncManager;
import de.lars.remotelightclient.musicsync.sound.Shared;
import de.lars.remotelightclient.musicsync.sound.SoundProcessing;
import de.lars.remotelightclient.settings.Setting;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.types.SettingObject;
import de.lars.remotelightweb.RemoteLightWeb;
import de.lars.remotelightweb.ui.MainLayout;
import de.lars.remotelightweb.ui.components.custom.PaperSlider;
import de.lars.remotelightweb.ui.components.settingpanels.SettingPanel;
import de.lars.remotelightweb.ui.utils.SettingPanelUtil;

@CssImport("./styles/musicsync-view-style.css")
@PageTitle("MusicSync")
@Route(value = "musicsync", layout = MainLayout.class)
public class MusicSyncView extends FlexLayout {
	private final String CLASS_NAME = "musicsync-view";
	
	private SettingsManager sm = RemoteLightWeb.getInstance().getAPI().getSettingsManager();
	private MusicSyncManager msm = RemoteLightWeb.getInstance().getAPI().getMusicSyncManager();
	private FlexLayout layoutEffects;
	private VerticalLayout layoutEffectOptions;
	private VerticalLayout layoutSettings;
	
	public MusicSyncView() {
		sm.addSetting(new SettingObject("musicsync.sensitivity", null, 20));
		sm.addSetting(new SettingObject("musicsync.adjustment", null, 200));
		
		initComponents();
		initLayout();
		initSettingsLayout();
		addEffectsToPanel();
	}
	
	private void initComponents() {
		layoutEffects = new FlexLayout();
		layoutEffects.addClassName(CLASS_NAME + "__effects");
		layoutEffects.setHeightFull();
		layoutEffects.getStyle().set("overflow", "auto");
		layoutEffects.getStyle().set("padding", "10px");
		
		layoutEffectOptions = new VerticalLayout();
		layoutEffectOptions.addClassName(CLASS_NAME + "__options");
		layoutEffectOptions.getStyle().set("flex-basis", "50%");
		layoutEffectOptions.getStyle().set("flex-grow", "1");
		
		layoutSettings = new VerticalLayout();
		layoutSettings.addClassName(CLASS_NAME + "__options");
		layoutSettings.getStyle().set("flex-basis", "50%");
		layoutSettings.getStyle().set("flex-grow", "1");
	}
	
	private void initLayout() {
		FlexLayout innerLayout = new FlexLayout(layoutEffectOptions, layoutSettings);
		innerLayout.getStyle().set("flex-wrap", "wrap");
		innerLayout.getStyle().set("flex-direction", "row-reverse");
		innerLayout.getStyle().set("overflow", "auto");
		innerLayout.setSizeFull();
		Card card = new Card(innerLayout);
		card.getStyle().set("margin", "10px");
		card.getStyle().set("max-height", "40%");
		//UIUtils.configureCard(card);
        
		add(layoutEffects, card);
		getStyle().set("flex-flow", "column");
        setHeightFull();
        setFlexGrow(1, layoutEffects);
	}
	
	
	/**
	 * Sensitivity, Input etc
	 */
	private void initSettingsLayout() {
		PaperSlider sensitivity = new PaperSlider();
		sensitivity.setMin(10);
		sensitivity.setMax(400);
		sensitivity.setValue((int) sm.getSettingObject("musicsync.sensitivity").getValue());
		sensitivity.addValueChangeListener(e -> {
			sm.getSettingObject("musicsync.sensitivity").setValue(sensitivity.getValue());
			msm.setSensitivity(sensitivity.getValue() / 100.0);
		});
		layoutSettings.add(new Label("Sensitivity"), sensitivity);
		
		PaperSlider adjustment = new PaperSlider();
		adjustment.setMin(50);
		adjustment.setMax(1200);
		adjustment.setValue((int) sm.getSettingObject("musicsync.adjustment").getValue());
		adjustment.addValueChangeListener(e -> {
			sm.getSettingObject("musicsync.adjustment").setValue(adjustment.getValue());
			msm.setAdjustment(adjustment.getValue() / 100.0);
		});
		layoutSettings.add(new Label("Gain"), adjustment);
		
		addInputsToLayout();
	}
	
	
	private void addEffectsToPanel() {
		layoutEffects.removeAll();
		for(MusicEffect m : msm.getMusicEffects()) {
			Button button = new Button(m.getDisplayname());
			button.addClassName(CLASS_NAME + "__buttons");
			button.getElement().setProperty("title", m.getDisplayname());
			
			if(msm.getActiveEffect() != null && msm.getActiveEffect().getName().equals(m.getName())) {
				button.getStyle().set("border-style", "dashed");
				button.getStyle().set("border-width", "2px");
				showEffectOptions();
			}
			button.addClickListener(e -> toggleEffect(m));
			
			layoutEffects.add(button);
		}
	}
	
	private void toggleEffect(MusicEffect m) {
		if(msm.getActiveEffect() != null && msm.getActiveEffect().getName().equals(m.getName())) {
			msm.stop();
			layoutEffectOptions.removeAll();
		} else {
			if(RemoteLightWeb.getInstance().getAPI().getOutputManager().getActiveOutput() != null &&
					RemoteLightWeb.getInstance().getAPI().getOutputManager().getActiveOutput().getState() == ConnectionState.CONNECTED)
			{
				msm.start(m);
				showEffectOptions();
			} else {
				Notification.show("No active output.");
			}
		}
		addEffectsToPanel();
	}
	
	
	private void showEffectOptions() {
		layoutEffectOptions.removeAll();
		if(msm.getCurrentMusicEffectOptions() != null && msm.getCurrentMusicEffectOptions().size() > 0) {
			List<Setting> options = msm.getCurrentMusicEffectOptions();
			
			for(Setting s : options) {
				SettingPanel spanel = SettingPanelUtil.getSettingPanel(s);
				spanel.setSettingChangedListener(l -> spanel.setValue());
				layoutEffectOptions.add(spanel);
			}
		}
	}

	
	/**
	 * Inputs
	 */
	private void addInputsToLayout() {
		layoutSettings.add(new Label("Inputs:"));
		String input = (String) sm.getSettingObject("musicsync.input").getValue();
		RadioButtonGroup<String> buttonGroup = new RadioButtonGroup<>();
		buttonGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
		List<String> groupItems = new ArrayList<>();
		String lastInput = null;
		
		for(Mixer.Info info : Shared.getMixerInfo(false, true)) {
			Mixer mixer = AudioSystem.getMixer(info);
			
			if(InputUtil.isLineSupported(mixer)) {
				groupItems.add(Shared.toLocalString(info));
				
				if(input != null) {
					if(input.equals(info.toString())) {
						lastInput = Shared.toLocalString(info);
					}
				}
			}
		}
		if(groupItems.size() > 0) {
			buttonGroup.setItems(groupItems);
			if(lastInput != null) {
				buttonGroup.setValue(lastInput);
			}
			
			buttonGroup.addValueChangeListener(e -> {
				for(Mixer.Info info : Shared.getMixerInfo(false, true)) {
					if(e.getValue().equals(Shared.toLocalString(info))){
						Mixer newMixer = AudioSystem.getMixer(info);
						SoundProcessing.setMixer(newMixer);
						//save last selected to data file
						sm.getSettingObject("musicsync.input").setValue(info.toString());
						//refresh SoundProcessor
						Main.getInstance().getMusicSyncManager().newSoundProcessor();
						break;
					}
				}
			});
			layoutSettings.add(buttonGroup);
		} else {
			layoutSettings.add(new Label("No input found."));
		}
	}
	
}
