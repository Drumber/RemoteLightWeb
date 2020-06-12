package de.lars.remotelightweb.ui.components.dialogs;

import java.io.IOException;

import org.tinylog.Logger;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import de.lars.remotelightcore.settings.types.SettingString;
import de.lars.remotelightweb.RemoteLightWeb;

public class ShutdownDialog extends Dialog {
	private static final long serialVersionUID = -3562261886185180079L;
	
	private Button btnRLWeb;
	private Button btnSystem;
	
	public ShutdownDialog() {
		initComponents();
		
		VerticalLayout layout = new VerticalLayout();
		layout.setPadding(false);
		
		layout.add(new H3("Shutdown"));
		Div divBtnWrapper = new Div();
		divBtnWrapper.getStyle()
			.set("margin", "auto")
			.set("max-width", "450px")
			.set("box-sizing", "border-box");
		
		Label lblRlweb = new Label("Close RemoteLightWeb and keep the system running.");
		lblRlweb.getStyle()
			.set("word-wrap", "break-word")
			.set("white-space", "break-spaces");
		divBtnWrapper.add(lblRlweb);
		divBtnWrapper.add(btnRLWeb);
		
		Label lblSystem = new Label("Close RemoteLightWeb and shutdown the system.");
		lblSystem.getStyle()
			.set("word-wrap", "break-word")
			.set("white-space", "break-spaces");
		divBtnWrapper.add(lblSystem);
		divBtnWrapper.add(btnSystem);
		
		layout.add(divBtnWrapper);
		add(layout);
	}
	
	protected void initComponents() {
		btnRLWeb = new Button("Shutdown RemoteLightWeb", w -> {
			RemoteLightWeb.exitApplication();
		});
		btnRLWeb.getStyle()
			.set("width", "100%")
			.set("margin-bottom", "20px");
		
		btnSystem = new Button("Shutdown System", p -> {
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
		btnSystem.getStyle().set("width", "100%");
	}

}
