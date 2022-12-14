package GUI.draw;

import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import models.measure.note.Note;

// This class draws note type under the music lines
public class DrawBassNoteType {

	@FXML
	private Pane pane;
	private double startX;
	private double startY;
	private final double HalfNoteLength = 15;
	private final double QuarterNoteLength = 30;
	private Note note;

	public DrawBassNoteType() {

	}

	public DrawBassNoteType(Pane pane, Note note, double startX, double startY) {
		super();
		this.pane = pane;
		this.startX = startX;
		this.startY = startY;
		this.note = note;

	}

	public void drawType() {

		if (note.getChord() == null) {
			// startX += 25;

			String current = note.getType();
			if (current == "half") {
				drawHalfNotes();
			} else if (current == "quarter") {
				drawQuarterNotes();
			} else if (current == "eighth") {
				drawEighthNotes();
			} else if (current == "16th") {
				draw16thNotes();
			}
			// startX += 25;

		}
	}

	private void draw16thNotes() {
		double y1 = startY + 37;
		Line l = new Line();
		l.setStartX(startX);
		l.setEndX(startX);
		l.setStartY(y1);
		l.setEndY(y1 - QuarterNoteLength);
		pane.getChildren().add(l);

	}

	// shorter stick
	private void drawHalfNotes() {
		double y1 = startY + 37;
		Line l = new Line();
		l.setStartX(startX);
		l.setEndX(startX);
		l.setStartY(y1);
		l.setEndY(y1 - HalfNoteLength);
		pane.getChildren().add(l);

	}

	// longer stick
	private void drawQuarterNotes() {
		double y1 = startY + 37;
		Line l = new Line();
		l.setStartX(startX);
		l.setEndX(startX);
		l.setStartY(y1);
		l.setEndY(y1 - QuarterNoteLength);
		pane.getChildren().add(l);

	}

	// draw notes with beam
	private void drawEighthNotes() {
		double y1 = startY + 37;
		Line l = new Line();
		l.setStartX(startX);
		l.setEndX(startX);
		l.setStartY(y1);
		l.setEndY(y1 - QuarterNoteLength);
		pane.getChildren().add(l);

	}

	// draw the beam itself
	public void drawBeam(double startX, double startY, double length) {
		double y1 = startY + 37;
		Line l = new Line();
		l.setStartX(startX);
		l.setEndX(startX + length);
		l.setStartY(y1);
		l.setEndY(y1);
		l.setStrokeWidth(3);
		pane.getChildren().add(l);
	}

	// draw a dot
	public void drawDot(double centerX, double centerY, double r) {
		Circle circle = new Circle(centerX, centerY, r);
		circle.setFill(Color.BLACK);
		pane.getChildren().add(circle);
	}

	// setters
	public Pane getPane() {
		return pane;
	}

	public void setPane(Pane pane) {
		this.pane = pane;
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

}
