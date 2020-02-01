package de.lars.remotelightweb.ui.components;

import org.tinylog.Logger;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import de.lars.remotelightweb.RemoteLightWeb;
import de.lars.remotelightweb.backend.utils.UpdateUtil;
import de.lars.remotelightweb.ui.utils.UIUtils;
import de.lars.updater.sites.GitHubParser;

public class UpdateDialog extends Dialog {
	
	public UpdateDialog(GitHubParser parser) {
		VerticalLayout l = new VerticalLayout();
		l.setSizeFull();
		add(l);
		l.add(new H3("Updater"));
		
		l.add(new Label(parser.isNewVersionAvailable() ? "New version available!" : "No new version available."));
		l.add(new Label("Installed: " + RemoteLightWeb.VERSION));
		l.add(new Label("Latest: " + parser.getNewestVersionTag()));
		l.add(new Anchor(parser.getNewestUrl(), parser.getNewestUrl()));
		
		if(parser.isNewVersionAvailable()) {
			Button btnIgnore = UIUtils.createButton("Ignore", "5px 5px");
			btnIgnore.addClickListener(e -> ignore());
			add(btnIgnore);
			
			Button btnUpdate = UIUtils.createButton("Update now", "5px 5px");
			btnUpdate.addClickListener(e -> update());
			add(btnUpdate);
		} else {
			add(new Button("Close", e -> close()));
		}
	}
	
	
	private void ignore() {
		close();
		removeAll();
		VerticalLayout l = new VerticalLayout();
		l.setSizeFull();
		add(l);
		
		l.add(new Label("The update can also be installed later in the settings."));
		l.add(new Button("Ok", e -> close()));
		open();
	}
	
	
	private void update() {
		close();
		removeAll();
		VerticalLayout l = new VerticalLayout();
		l.setSizeFull();
		add(l);
		
		l.add(new H3("Important information"));
		l.add(new Label("The program will be closed while the new version is downloaded in the background. "
				+ "Please do not power off the system! RemoteLightWeb will automatically restart after the update is complete."));
		l.add(new Label("You can choose between two options:"));
		l.add(new Label("1. (Update and restart) RemoteLightWeb will automatically restart after the update is complete."));
		l.add(new Label("2. (Update and shutdown) The system will shut down after the update has been successfully completed."));
		
		UpdateUtil updateUtil = RemoteLightWeb.getInstance().getUpdateUtil();
		
		add(UIUtils.createButton("Cancel", "5px 5px", e -> close()));
		add(UIUtils.createButton("Update and restart", "5px 5px", e -> {
			try {
				updateUtil.install(false);
			} catch (Exception e1) {
				Logger.error(e1, "Error while updating");
				error("Error while updating", e1);
			}
		}));
		add(UIUtils.createButton("Update and shutdown", "5px 5px", e -> {
			try {
				updateUtil.install(true);
			} catch (Exception e1) {
				Logger.error("Error while updating", e);
				error("Error while updating", e1);
			}
		}));
		open();
	}
	
	
	private void error(String message, Exception ex) {
		close();
		removeAll();
		VerticalLayout l = new VerticalLayout();
		l.setSizeFull();
		add(l);
		
		l.add(new H3("An error has occurred"));
		l.add(new Label(message));
		l.add(new Label(ex.getMessage()));
		l.add(new Button("Close", e -> close()));
		open();
	}

}
