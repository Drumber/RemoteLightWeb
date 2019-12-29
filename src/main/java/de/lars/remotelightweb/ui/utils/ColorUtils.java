package de.lars.remotelightweb.ui.utils;

import java.awt.Color;

public class ColorUtils {
	
	public static String getColorHex(Color color) {
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}
	
	public static Color[] getColorPalette() {
		return new Color[] {new Color(255,255,255), new Color(255,0,0), new Color(255,69,0), new Color(255,165,0), new Color(255,215,0),
				new Color(255,255,0), new Color(154,205,50), new Color(124,252,0), new Color(0,255,0), new Color(0,250,154), new Color(32,178,170),
				new Color(0,255,255), new Color(127,255,212), new Color(0,191,255), new Color(30,144,255), new Color(0,0,255), new Color(138,43,226),
				new Color(128,0,128), new Color(255,0,255)};
	}

}
