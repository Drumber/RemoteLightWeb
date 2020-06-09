package de.lars.remotelightweb.ui.views;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.lars.remotelightweb.RemoteLightWeb;
import de.lars.remotelightweb.ui.MainLayout;

@CssImport("./styles/about-view-style.css")
@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
public class AboutView extends VerticalLayout {
	
	public AboutView() {
		setAlignItems(Alignment.CENTER);
		addClassName("layout");
		
		HorizontalLayout title = new HorizontalLayout();
		title.setAlignItems(Alignment.END);
		title.add(getLabel("RemoteLightWeb", "title"));
		title.add(getLabel(RemoteLightWeb.VERSION, "version"));
		add(title);
		
		add(getLabel("by Lars Obrath", "author"));
		add(new Anchor("https://github.com/Drumber/RemoteLight", getLabel("View on GitHub", "github")));
		
		add(getLabel("Open Source Libraries", "credits"));
		add(getCreditLbl("Vaadin Web Framework", "https://vaadin.com/"));
		add(getCreditLbl("App Layout by Appreciated", "https://github.com/appreciated/vaadin-app-layout"));
		add(getCreditLbl("Css Grid Layout by Appreciated", "https://github.com/appreciated/grid-layout"));
		add(getCreditLbl("Color picker by Appreciated / Juchar" , "https://github.com/Juchar/color-picker"));
		add(getCreditLbl("RemoteLightCore & Dependencies", "https://github.com/Drumber/RemoteLight/blob/master/pom.xml#L46"));
	}
	
	
	private Label getLabel(String text, String classname) {
		Label lbl = new Label(text);
		lbl.addClassName(classname);
		return lbl;
	}
	
	private Anchor getCreditLbl(String text, String url) {
		return new Anchor(url, getLabel(text, "credit-items"));
	}

}
