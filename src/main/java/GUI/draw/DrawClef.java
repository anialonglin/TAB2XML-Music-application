package GUI.draw;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import models.measure.attributes.Clef;

//This class draw an exctracted clef on a given pane
public class DrawClef {

	private Clef clef;
	@FXML
	private Pane pane;
	private double x;
	private double y;
	private double fontSize;
	private String font; 
	private double drumClefTopPosition;
	private double drumClefBottomPosition;

	// Constructor 1
	public DrawClef(Pane pane, Clef clef, double x, double y, String font) {
		super();
		this.clef = clef;
		this.pane = pane;
		this.x = x;
		this.y = y;
		this.font = font; 
	}

	// constructor 2
	public DrawClef() {

	}

	/**
	 * Constructor for draw clef class for drumset.
	 *
	 * @param pane                   - The pane on which the clef will be drawn
	 * @param drumClefTopPosition    - The y-coordinate of the top of the clef
	 *                                 (should be octave 5, note "D")
	 * @param drumClefBottomPosition - The y-coordinate of the bottom of the clef
	 *                                 (should be octave 4, note "E")
	 */
	public DrawClef(Pane pane, double drumClefTopPosition, double drumClefBottomPosition) {
		this.pane = pane;
		this.drumClefTopPosition = drumClefTopPosition;
		this.drumClefBottomPosition = drumClefBottomPosition;
	}

	/**
	 * Draws the drum clef.
	 */
	public void drawDrumClef() {
		this.drawDrumClef1();
		this.drawDrumClef2();
	}

	/**
	 * Draws the left rectangle of the drum clef.
	 */
	private void drawDrumClef1() {

		Rectangle r1 = new Rectangle();
		r1.setWidth(5);
		r1.setHeight(this.drumClefBottomPosition - this.drumClefTopPosition);
		r1.setTranslateX(10);
		r1.setTranslateY(this.drumClefTopPosition);
		pane.getChildren().add(r1);

	}

	/**
	 * Draws the right rectangle of the drum clef.
	 */
	private void drawDrumClef2() {

		Rectangle r1 = new Rectangle();
		r1.setWidth(5);
		r1.setHeight(this.drumClefBottomPosition - this.drumClefTopPosition);
		r1.setTranslateX(18);
		r1.setTranslateY(this.drumClefTopPosition);
		pane.getChildren().add(r1);

	}

	// the method called for the drawing the clef on Pane.

	public void draw(double spacing) {
		String name = this.clef.getSign();
		if (name == "TAB") {
			drawVertical(name, spacing);
		}
	}

	/*
	 * The method that prints a given string in vertical format for example if
	 * string = TAB then we have: 
	 * T 
	 * A 
	 * B
	 */
	private void drawVertical(String name, double spacing) {
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			String s = Character.toString(c);
			Text text = new Text(this.x, this.y, s);
			text.setFont(Font.font(this.font, FontWeight.BOLD, FontPosture.REGULAR, getFontSize()));
			pane.getChildren().add(text);
			y += spacing;
		}
	}

	/**
	 * Draw the time signature.
	 *
	 * @param beats            - The beats of the time signature (top number)
	 * @param beatType         - The beat type of the time signature (bottom number)
	 * @param xPosition        - The x-position of the time signature
	 * @param yPosition        - The y-position of the top of the measure
	 * @param fontSize         - The font size of the drum notes
	 * @param musicLineSpacing - The extra spacing between music lines
	 */
	public void drawTimeSignature(int beats, int beatType, double xPosition, double yPosition, double fontSize, double musicLineSpacing) {
		Text beatsText = new Text(xPosition - 5 * fontSize, yPosition + 30 * fontSize + 5 * musicLineSpacing, Integer.toString(beats));
		Text beatTypeText = new Text(xPosition - 5 * fontSize, yPosition + 50 * fontSize + 7 * musicLineSpacing, Integer.toString(beatType));

		beatsText.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, fontSize * 30 + musicLineSpacing * 2));
		beatTypeText.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, fontSize * 30 + musicLineSpacing * 2));

		pane.getChildren().add(beatsText);
		pane.getChildren().add(beatTypeText);
	}

	// Getters and Setters
	public Clef getClef() {
		return clef;
	}

	public void setClef(Clef clef) {
		this.clef = clef;
	}

	public Pane getPane() {
		return pane;
	}

	public void setPane(Pane pane) {
		this.pane = pane;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	public double getFontSize() {
		return fontSize; 
	}
	public void setFontSize(double font) {
		this.fontSize = font; 
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}
	

}
