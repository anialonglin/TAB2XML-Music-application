package GUI.draw;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.QuadCurve;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import models.measure.note.Note;

public class DrawBend {

	private Note note;
	@FXML
	private Pane pane;
	private double startX;
	private double startY;
	private double firstML;
	private double spacing;
	private String font;
	private int fontSize; 

	public DrawBend(Pane pane, Note note, double x, double y, double firstML, double spacing, String font, int fs) {
		super();
		this.note = note;
		this.pane = pane;
		this.startX = x;
		this.startY = y;
		this.firstML = firstML;
		this.spacing = spacing; 
		this.font = font;
		this.fontSize = fs; 
	}

	public void draw() {
		QuadCurve quadCurve = new QuadCurve();

		// Adding properties to the Quad Curve
		double x1 = this.startX;
		double y1 = this.startY ;
		double x2 = x1 + (spacing/4);
		double y2 = this.firstML - (spacing/2)-fontSize/2;
		double controlX = x2;
		double controlY = y1;
		
		quadCurve = getCurve(x1, y1, x2, y2, controlX, controlY);
		pane.getChildren().add(quadCurve);
		
		Text bend = new Text(quadCurve.getEndX()+5, quadCurve.getEndY()-5, getBendvalue());
		bend.setFont(Font.font(getFont(), FontPosture.REGULAR, this.fontSize/2));
		pane.getChildren().add(bend);

	}

	private String getBendvalue() {
		double bend = this.note.getNotations().getTechnical().getBend().getBendAlter();
		String s = "";
		if (bend == 2.0) { 
			s = "1";
		}
		else if(bend == 1.0) {
			s = "1/2";
		}
		return s;
	}
	private QuadCurve getCurve(double x1, double y1, double x2, double y2, double controlX, double controlY) {
		QuadCurve quadCurve = new QuadCurve();
		quadCurve.setStartX(x1);
		quadCurve.setStartY(y1);
		quadCurve.setEndX(x2);
		quadCurve.setEndY(y2);
		quadCurve.setControlX(controlX);
		quadCurve.setControlY(controlY);

		quadCurve.setFill(Color.TRANSPARENT);
		quadCurve.setStroke(Color.BLACK);
		quadCurve.setStrokeWidth(1);
		quadCurve.setViewOrder(-1);
		return quadCurve;
		
	}

	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}

	public double getStartX() {
		return startX;
	}

	public void setStartX(double startX) {
		this.startX = startX;
	}

	public double getStartY() {
		return startY;
	}

	public void setStartY(double startY) {
		this.startY = startY;
	}

	public double getSpacing() {
		return spacing;
	}

	public void setSpacing(double spacing) {
		this.spacing = spacing;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}
	

}
