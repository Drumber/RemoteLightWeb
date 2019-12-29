package de.lars.remotelightweb.ui.components.custom;

import com.vaadin.flow.component.AbstractSinglePropertyField;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.component.PropertyDescriptor;
import com.vaadin.flow.component.PropertyDescriptors;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.shared.Registration;

@Tag("paper-slider")
@NpmPackage(value = "@polymer/paper-slider", version = "3.0.1")
@JsModule("@polymer/paper-slider/paper-slider.js")
public class PaperSlider extends AbstractSinglePropertyField<PaperSlider, Integer> {

    private static final PropertyDescriptor<Boolean, Boolean> pinProperty = PropertyDescriptors.propertyWithDefault("pin", false);

    public PaperSlider() {
        super("value", 0, false);
    }

    public void setPin(boolean pin) {
        pinProperty.set(this, pin);
    }

    public boolean isPin() {
        return pinProperty.get(this);
    }
    
    public void setValue(int value) {
    	getElement().setProperty("value", value);
    }
    
    public Integer getValue() {
    	return getElement().getProperty("value", 0);
    }
    
    public void setMin(int min) {
    	getElement().setProperty("min", min);
    }
    
    public void setMax(int max) {
    	getElement().setProperty("max", max);
    }
    
    public Registration addClickListener(ComponentEventListener<ClickEvent> listener) {
        return addListener(ClickEvent.class, listener);
    }

    public void increment() {
        getElement().callJsFunction("increment");
    }
    
    @DomEvent("click")
    public static class ClickEvent extends ComponentEvent<PaperSlider> {

        private int x, y;

        public ClickEvent(PaperSlider source,
                          boolean fromClient,
                          @EventData("event.offsetX") int x,
                          @EventData("event.offsetY") int y) {
            super(source, fromClient);
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
    
}

