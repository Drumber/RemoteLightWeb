package de.lars.remotelightweb.ui.views;

import java.awt.Color;

import com.github.juchar.colorpicker.ColorPicker;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightweb.ui.MainLayout;
import de.lars.remotelightweb.ui.utils.ColorUtils;

@PageTitle("Colors")
@Route(value = "colors", layout = MainLayout.class)
public class ColorsView extends VerticalLayout {
	
	public ColorsView() {
		ColorPicker cp =  new ColorPicker(Color.RED, Color.RED);
		cp.setAlphaEnabled(false);
		cp.setPinnedPalettes(true);
		cp.setPalette(ColorUtils.getColorPalette());
		
		cp.addValueChangeListener(e -> {
			OutputManager.addToOutput(PixelColorUtils.colorAllPixels(cp.getValue(), Main.getLedNum()));
		});
		
		add(cp);
	}

}
