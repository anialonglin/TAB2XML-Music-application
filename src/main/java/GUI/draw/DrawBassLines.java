package GUI.draw;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

// this class draws music lines (group of 6 lines) with the width of  50; 
public class DrawBassLines extends Node {

	@FXML
	private Pane pane;
	private double length;
	private MLine musicLine;
	private List<MLine> musicLineList;
	private int staffSpacing; 
	private double noteSpacing;

	// Constructor 1
	public DrawBassLines() {

	}

	// Constructor 2
	public DrawBassLines(Pane pane, int length) {
		super();
		this.pane = pane;
		this.musicLineList = new ArrayList<MLine>();
		this.length = length; 
	}

	// Constructor 3
	public DrawBassLines(Pane pane, int noteSpacing, int staffSpacing) {
		super();
		this.pane = pane;
		this.musicLineList = new ArrayList<MLine>();
		this.noteSpacing= noteSpacing; 
		this.staffSpacing = staffSpacing; 
	}

	// Method that does the actual drawing
	public void draw(double x, double y) {
		for (int i = 0; i < 4; i++) {
//			musicLine = new MLine(pane, x, y, x + this.length, y, i + 1);
//			y += 15;
//			musicLineList.add(musicLine);
			musicLine = new MLine(pane, x, y, x + this.noteSpacing, y, i + 1);
			musicLine.createLines();
			y += staffSpacing;
			musicLineList.add(musicLine);
		}
	}

	// this method allows access to the list of 6 lines at any moment
	public List<MLine> getMusicLineList() {
		return musicLineList;
	}

	// getters and setters
	public Pane getPane() {
		return pane;
	}

	public void setPane(Pane pane) {
		this.pane = pane;
	}

}
