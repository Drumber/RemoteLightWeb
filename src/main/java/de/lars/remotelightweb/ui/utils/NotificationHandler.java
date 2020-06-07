package de.lars.remotelightweb.ui.utils;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import de.lars.remotelightcore.notification.NotificationManager;
import de.lars.remotelightcore.notification.listeners.NotificationListener;
import de.lars.remotelightcore.notification.listeners.NotificationOptionListener;

public class NotificationHandler {
	
	private NotificationManager manager;
	
	public NotificationHandler(NotificationManager notificationManager) {
		manager = notificationManager;
		manager.addNotificationListener(notificationListener);
	}
	
	/** triggered when a new notification is added */
	private NotificationListener notificationListener = new NotificationListener() {
		@Override
		public void onNotification(NotificationManager manager) {
			if(manager.hasNext()) {
				showNotification(manager.getNext());
			}
		}
	};
	
	protected void showNotification(de.lars.remotelightcore.notification.Notification noti) {
		VerticalLayout layout = new VerticalLayout();
		layout.setPadding(false);
		
		String title = noti.getTitle();
		String message = noti.getMessage();
		String[] options = noti.getOptions();
		
		if(title != null && !title.isEmpty()) {
			H5 lblTitle = new H5(title);
			layout.add(lblTitle);
		}
		
		if(message != null && !message.isEmpty()) {
			Label lblMessage = new Label(message);
			layout.add(lblMessage);
		}
		
		if(options != null && options.length > 0) {
			HorizontalLayout layoutOption = new HorizontalLayout();
			layoutOption.setPadding(false);
			
			for(int i = 0; i < options.length; i++) {
				final String option = options[i];
				final int index = i;
				Anchor anchor = new Anchor();
				anchor.setText(option);
				anchor.getStyle().set("cursor", "pointer");
				anchor.getElement().addEventListener("click", e -> {
					// fire option click event
					NotificationOptionListener listener = noti.getOptionListener();
					if(listener != null)
						listener.onOptionClicked(option, index);
				});
				layoutOption.add(anchor);
			}
			layout.add(layoutOption);
		}
		
		Notification notification = new Notification(layout);
		notification.setDuration(noti.getDisplayTime());
		notification.open();
	}

}
