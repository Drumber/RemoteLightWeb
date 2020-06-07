package de.lars.remotelightweb.ui.components.outputsettingpanels;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;

import de.lars.remotelightcore.out.Output;

public class OutputPatchPanel extends VerticalLayout {
	
	public OutputPatchPanel(Output output) {
		this(output, null);
	}
	
	public OutputPatchPanel(Output output, IntegerField fieldPixel) {
		add(new H5("Output patch"));
		
		IntegerField fieldShift = new IntegerField("Shift pixel");
		fieldShift.setHasControls(true);
		fieldShift.setValue(output.getOutputPatch().getShift());
		int shiftBounds = output.getPixels() - 1;
		fieldShift.setMin(-shiftBounds);
		fieldShift.setMax(shiftBounds);
		
		fieldShift.addValueChangeListener(e -> {
			if(fieldPixel != null) {
				int bounds = fieldPixel.getValue() - 1;
				fieldShift.setMin(-bounds);
				fieldShift.setMax(bounds);
			}
			output.getOutputPatch().setShift(fieldShift.getValue());
		});
		this.add(fieldShift);
		
		
		IntegerField fieldClone = new IntegerField("Clone");
		fieldClone.setHasControls(true);
		fieldClone.setValue(output.getOutputPatch().getClone());
		fieldClone.setMin(0);
		fieldClone.setMax(output.getPixels() / 2);
		
		fieldClone.addValueChangeListener(e -> {
			if(fieldPixel != null) {
				fieldClone.setMax(fieldPixel.getValue() / 2);
			}
			output.getOutputPatch().setClone(fieldClone.getValue());
		});
		
		Checkbox checkMirror = new Checkbox("Mirror");
		checkMirror.getStyle().set("margin-left", "5px");
		checkMirror.setValue(output.getOutputPatch().isCloneMirrored());
		checkMirror.addValueChangeListener(e -> output.getOutputPatch().setCloneMirrored(checkMirror.getValue()) );
		
		this.add(new Div(fieldClone, checkMirror));
	}

}
