package instruments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import models.ScorePartwise;
import models.measure.Measure;
import models.measure.attributes.Clef;
import models.measure.note.Dot;
import models.measure.note.Note;
import models.measure.note.notations.Slide;
import models.measure.note.notations.Slur;
import models.measure.note.notations.Tied;
import GUI.GuitarHighlight;
import GUI.HighlightNote;
import GUI.draw.*;

public class Guitar {

	private ScorePartwise scorePartwise;
	@FXML
	private Pane pane;
	private List<Measure> measureList;
	private double x;
	private double y;
	private DrawMusicLines d;
	private HashMap<Measure, Double> xCoordinates;
	private HashMap<Measure, Double> yCoordinates;
	private double spacing;
	private int LineSpacing;
	private int noteTypeCounter;
	private DrawNote noteDrawer;
	private int fontSize;
	private int staffSpacing;
	private DrawSlur slurDrawer;
	private double harmonic;
	private int ActualCounter;

	public Guitar() {
	}

	public Guitar(ScorePartwise scorePartwise, Pane pane, int noteSpacing, int font, int StaffSpacing,
			int LineSpacing) {
		super();
		this.scorePartwise = scorePartwise;
		this.pane = pane;
		this.measureList = this.scorePartwise.getParts().get(0).getMeasures();
		this.x = 0;
		this.y = 0;
		this.LineSpacing = LineSpacing;
		this.fontSize = font;
		this.staffSpacing = StaffSpacing;
		xCoordinates = new HashMap<>();
		yCoordinates = new HashMap<>();
		this.spacing = noteSpacing;
		this.noteDrawer = new DrawNote();
		this.noteDrawer.setFont(this.fontSize);
		this.noteDrawer.setGraceFontSize(this.fontSize - 4);
		this.d = new DrawMusicLines(this.pane, noteSpacing, staffSpacing);
		this.slurDrawer = new DrawSlur();
		this.slurDrawer.setPane(this.pane);
		this.harmonic = 0;
	}

	/*
	 * This method is where the actual drawing of the guitar elements happen
	 */

	int xPosition;
	int yPosition;
	public void drawGuitar() {
		Clef clef = getClef();
		double width = this.pane.getMaxWidth();

		for (int i = 0; i < measureList.size(); i++) {
			int spaceRequired = 0;
			Measure measure = measureList.get(i);
			// clef of first line
			if (x == 0) {
				d.draw(x, y);
				double clefSpacing = this.staffSpacing + (this.staffSpacing / 2);
				DrawClef dc = new DrawClef(this.pane, clef, x, y + clefSpacing);
				dc.setFontSize(this.fontSize + 6);
				dc.draw(clefSpacing);
				x += spacing;
				spaceRequired += getSpacing();
			}
			List<Note> noteList = measure.getNotesBeforeBackup();
			spaceRequired += countNoteSpacesRequired(noteList);
			if (width >= spaceRequired) {
				drawMeasureNum(i);
				drawMeasureNotes(measure);
				width = width - spaceRequired;

			} else {
				while (width > 0) {
					d.draw(x, y);
					width -= spacing;
					x += spacing;
				}

				this.x = 0.0;
				this.y += this.LineSpacing;
				width = this.pane.getMaxWidth();

				d.draw(x, y);
				double clefSpacing = this.staffSpacing + (this.staffSpacing / 2);
				DrawClef dc = new DrawClef(this.pane, clef, x, y + clefSpacing);
				dc.setFontSize(this.fontSize + 6);
				dc.draw(clefSpacing);
				x += spacing;
				spaceRequired += getSpacing();
				drawMeasureNum(i);
				drawMeasureNotes(measure);
				width = width - spaceRequired;

			}
			xCoordinates.put(measure, x);
			yCoordinates.put(measure, y);
			double len = getLastLineCoordinateY() - getFirstLineCoordinateY();
			DrawBar bar = new DrawBar(this.pane, x, y, len);
			bar.draw();
			// System.out.println("Measure:" + measure + "X:" + x + "Y:" + y + pane);
		}

	}

	private void drawMeasureNum(int i) {
		// TODO Auto-generated method stub
		String n = Integer.toString(i + 1);
		Text num = new Text();
		num.setX(x);
		num.setY(getFirstLineCoordinateY() + y - this.fontSize);
		num.setText(n);
		num.setViewOrder(-1);
		num.setFont(Font.font("Comic Sans MS", FontPosture.REGULAR, this.fontSize - 2));
		this.pane.getChildren().add(num);
	}

	// This method extracts a clef from a given measure
	public Clef getClef() {
		Clef clef = measureList.get(0).getAttributes().getClef();
		return clef;
	}

	// method to count the space required for notes in noteList
	private int countNoteSpacesRequired(List<Note> noteList) {
		int numOfSpace = 0;
		for (int i = 0; i < noteList.size(); i++) {
			Note n = noteList.get(i);
			if (!noteHasChord(n)) {
				numOfSpace++;
			}
		}
		int space = (int) (numOfSpace * getSpacing());

		return space;
	}

	// returns true if the guitar note has chord element
	public Boolean noteHasChord(Note n) {
		Boolean result = n.getChord() == null ? false : true;
		return result;
	}

	private void drawMeasureNotes(Measure measure) {
		this.noteTypeCounter = 3;
		this.ActualCounter = 0; 
		List<Note> noteList = measure.getNotesBeforeBackup();
		for (int i = 0; i < noteList.size(); i++) {
			Note note = noteList.get(i);
			if (noteHasTechnical(note)) {
				drawNoteWithTechnical(note, noteList, measure);
			}

		}

	}

	public Boolean noteHasTechnical(Note n) {
		Boolean result = n.getNotations().getTechnical() != null ? true : false;
		return result;
	}

	private void drawNoteWithTechnical(Note note, List<Note> noteList, Measure measure) {
		if (!noteHasChord(note)) {
			// Draw grace notes
			if (noteHasGrace(note)) {
				drawGraceNotes(note, noteList);
			} else {
				drawNoteWithoutGrace(note, noteList);

			}
			// drawSlur(note, noteList);

		} else if (noteHasChord(note)) {
			if (noteHasGrace(note)) {
				drawChordsWithGraceNotes(note, noteList);
			} else {
				drawChordWithoutGrace(note, noteList);

			}
			// drawSlur(note, noteList);
		}
		drawSlur(note, noteList);
		drawTie(note, noteList, measure);
		drawHarmonic(note, noteList);
		drawSlides(note, noteList);

	}

	private void drawSlides(Note note, List<Note> noteList) {
		if (noteHasSlide(note)) {
			List<Slide> slideList = note.getNotations().getSlides();
			for (Slide s : slideList) {
				int num = s.getNumber();
				String type = s.getType();
				if (type == "start") {
					int string = note.getNotations().getTechnical().getString();
					double positionY = getLineCoordinateY(string);
					Line line = new Line();
					line.setViewOrder(-1);
					line.setStroke(Color.BLACK);
					line.setStrokeWidth(this.fontSize / 6);
					line.setStartX(noteDrawer.getStartX() + (this.fontSize));
					line.setStartY(positionY + y + (this.fontSize / 2));
					Loop: for (int i = noteList.indexOf(note); i < noteList.size() - 1; i++) {
						Note next = noteList.get(i + 1);
						if (noteHasSlide(next)) {
							List<Slide> nextSlide = next.getNotations().getSlides();
							for (Slide ns : nextSlide) {
								int nextNum = ns.getNumber();
								String nextType = ns.getType();
								if (nextNum == num && nextType == "stop") {
									int nextString = note.getNotations().getTechnical().getString();
									double nextPositionY = getLineCoordinateY(nextString);
									line.setEndX(noteDrawer.getStartX() + spacing);
									line.setEndY(nextPositionY + y - (this.fontSize / 2));
									this.pane.getChildren().add(line);
									break Loop;
								}
							}
						}
					}
				}
			}
		}

	}

	private boolean noteHasSlide(Note note) {
		// TODO Auto-generated method stub
		Boolean res = note.getNotations().getSlides() == null ? false : true;
		return res;
	}

	private void drawHarmonic(Note note, List<Note> noteList) {
		// TODO Auto-generated method stub
		if (noteHasHarmonic(note)) {
			double firstLine = getFirstLineCoordinateY();
			double xCenter = noteDrawer.getStartX() + this.fontSize / 2;
			double yCenter = firstLine + y - this.fontSize - (this.harmonic * this.fontSize);
			double radius = spacing / 25;
			Circle circle = new Circle(xCenter, yCenter, radius);
			circle.setFill(Color.WHITE);
			circle.setStroke(Color.BLACK);
			pane.getChildren().add(circle);

			for (int i = noteList.indexOf(note); i < noteList.size() - 1; i++) {
				Note next = noteList.get(i + 1);
				if (noteHasHarmonic(next)) {
					this.harmonic++;
					break;
				} else {
					this.harmonic = 0;
					break;
				}
			}
		} else {
			this.harmonic = 0;
		}
	}

	private boolean noteHasHarmonic(Note note) {
		// TODO Auto-generated method stub
		Boolean res = note.getNotations().getTechnical().getHarmonic() == null ? false : true;
		return res;
	}

	private void drawTie(Note note, List<Note> noteList, Measure measure) {
		// TODO Auto-generated method stub
		if (noteHasTie(note)) {
			slurDrawer.setStrokeWidth(this.fontSize);
			if (!noteHasChord(note)) {
				drawRegualrTied(note, noteList, measure);
			} else {
//				int f = note.getNotations().getTechnical().getFret();
//				List<Tied> tiedList = note.getNotations().getTieds();
//				for (Tied t : tiedList) {
//					String type = t.getType();
//					if (type == "start") {
//						// System.out.println(f +" "+ type + "\n");
//					}
//				}
				drawChordTied(note, noteList, measure);
			}

		}

	}

	private void drawChordTied(Note note, List<Note> noteList, Measure measure) {
		// TODO Auto-generated method stub
		List<Tied> tiedList = note.getNotations().getTieds();
		for (Tied t : tiedList) {
			String type = t.getType();
			if (type == "start") {
				int string = note.getNotations().getTechnical().getString();
				double positionY = getLineCoordinateY(string);
				slurDrawer.setStartX(noteDrawer.getStartX() + fontSize);
				
				slurDrawer.setStartY(positionY + fontSize / 2 + y);
				slurDrawer.setPlace(1);
				int measureIndex = measureList.indexOf(measure);
				Boolean foundStop = false;
				loop: for (int i = noteList.indexOf(note); i < noteList.size() - 1; i++) {
					Note next = noteList.get(i + 1);
					if (noteHasTie(next)) {
						if (noteHasChord(next)) {
							// int n = next.getNotations().getTechnical().getFret();
							List<Tied> nextList = next.getNotations().getTieds();
							for (Tied nt : nextList) {
								String nextType = nt.getType();
								if (nextType == "stop") {
									slurDrawer.setEndX(noteDrawer.getStartX() + spacing);
									foundStop = true;
									break loop;
								}
							}
						}
					}
				}
				if (!foundStop) {
					Measure nextMeasure = measureList.get(measureIndex + 1);
					List<Note> nextNoteList = nextMeasure.getNotesBeforeBackup();
					loop1: for (Note n : nextNoteList) {
						if (noteHasTie(n) && noteHasChord(n)) {
							// int n1 = n.getNotations().getTechnical().getFret();
							List<Tied> tiedList2 = n.getNotations().getTieds();
							for (Tied nt : tiedList2) {
								String nextType = nt.getType();
								if (nextType == "stop") {
									slurDrawer.setEndX(noteDrawer.getStartX() + spacing);
									// System.out.println(f +" "+ type + " | "+ n1 + " "+ nextType +"\n");
									break loop1;
								}
							}
						}
					}
				}
				slurDrawer.draw();
			}
		}
	}

	private void drawRegualrTied(Note note, List<Note> noteList, Measure measure) {
		// TODO Auto-generated method stub
		List<Tied> tiedList = note.getNotations().getTieds();
		for (Tied t : tiedList) {
			String type = t.getType();
			if (type == "start") {
				int string = note.getNotations().getTechnical().getString();
				double positionY = getLineCoordinateY(string);
				slurDrawer.setStartX(noteDrawer.getStartX() + fontSize);
				slurDrawer.setStartY(positionY - fontSize / 2 + y);
				slurDrawer.setPlace(-1);
				int measureIndex = measureList.indexOf(measure);
				Boolean foundStop = false;
				loop: for (int i = noteList.indexOf(note); i < noteList.size() - 1; i++) {
					Note next = noteList.get(i + 1);
					if (noteHasTie(next)) {
						if (!noteHasChord(next)) {
							int n = next.getNotations().getTechnical().getFret();
							List<Tied> nextList = next.getNotations().getTieds();
							for (Tied nt : nextList) {
								String nextType = nt.getType();
								if (nextType == "stop") {
									slurDrawer.setEndX(noteDrawer.getStartX() + spacing);
									foundStop = true;
									break loop;
								}
							}
						}
					}
				}
				if (!foundStop) {
					Measure nextMeasure = measureList.get(measureIndex + 1);
					List<Note> nextNoteList = nextMeasure.getNotesBeforeBackup();
					loop1: for (Note n : nextNoteList) {
						if (noteHasTie(n) && !noteHasChord(n)) {
							int n1 = n.getNotations().getTechnical().getFret();
							List<Tied> tiedList2 = n.getNotations().getTieds();
							for (Tied nt : tiedList2) {
								String nextType = nt.getType();
								if (nextType == "stop") {
									slurDrawer.setEndX(noteDrawer.getStartX() + spacing);
									// System.out.println(f +" "+ type + " | "+ n1 + " "+ nextType +"\n");
									break loop1;
								}
							}
						}
					}
				}
				slurDrawer.draw();
			}
		}
	}

	private boolean noteHasTie(Note note) {
		// TODO Auto-generated method stub
		Boolean res = note.getNotations().getTieds() == null ? false : true;
		return res;
	}

	// draws bend elements if they exists
	private void drawBend(Note note) {
		if (noteHasBend(note)) {
			double firstMusicLine = getFirstLineCoordinateY() + y;
			DrawBend db = new DrawBend(pane, note, x, y, firstMusicLine);
			db.draw();
		}
	}

	// gets the Y coordinate of specific group of music lines based on given string
	// integer.
	private double getLineCoordinateY(int string) {
		return this.d.getMusicLineList().get(string - 1).getStartY(string - 1);
	}

	// returns the y coordinate the first music line of the 6-line group
	private double getFirstLineCoordinateY() {
		return this.d.getMusicLineList().get(0).getStartY(1);
	}

	// returns true if note has a chord tag
	private boolean noteHasBend(Note note) {
		Boolean res = note.getNotations().getTechnical().getBend() == null ? false : true;
		return res;
	}

	// returns true if note has a grace tag
	private boolean noteHasGrace(Note note) {
		Boolean res = note.getGrace() == null ? false : true;
		return res;
	}

	// count the number of graces in a row
	private int countGraceSpace(Note n, List<Note> noteList) {
		int index = noteList.indexOf(n);
		int res = 0;
		for (int i = index; i < noteList.size() - 1; i++) {
			Note current = noteList.get(i);
			Note next = noteList.get(i + 1);
			if (noteHasGrace(current) && !noteHasGrace(next)) {
				res++;

			} else if (noteHasGrace(current) && noteHasGrace(next)) {
				if (!noteHasChord(next)) {
					res++;
				}
			} else {
				break;
			}
		}
		return res;
	}

	// Draws the grace notes
	private void drawGraceNotes(Note note, List<Note> noteList) {
		int string = note.getNotations().getTechnical().getString();
		int num = countGraceSpace(note, noteList);
		double xPosition = x + spacing / 2;
		int fret = note.getNotations().getTechnical().getFret();
		double graceSpacing = 0;
		if (fret < 10) {
			graceSpacing = xPosition - (spacing / (4 / num));
		} else {
			graceSpacing = xPosition - (spacing / (4 / num) + num);
		}
		double positionY = getLineCoordinateY(string);
		noteDrawer.setPane(pane);
		noteDrawer.setNote(note);
		noteDrawer.setStartX(graceSpacing);
		noteDrawer.setStartY(positionY + 3 + y);
		noteDrawer.drawGuitarGrace();
		// drawSlur(note, noteList);
	}

	// draw regular notes (no grace, no chords)
	private void drawNoteWithoutGrace(Note note, List<Note> noteList) {
		int string = note.getNotations().getTechnical().getString();

		double positionY = getLineCoordinateY(string) + 3;// +getLineCoordinateY(string+1))/2;

		d.draw(x, y);
		noteDrawer.setPane(pane);
		noteDrawer.setNote(note);
		noteDrawer.setStartX(x + spacing / 2);
		noteDrawer.setStartY(positionY + y);
		x += spacing;
		noteDrawer.drawFret();

		drawBend(note);
		drawType(note, noteList);
		// drawSlur(note, noteList);

	}

	// Regular chords
	private void drawChordWithoutGrace(Note note, List<Note> noteList) {
		int string = note.getNotations().getTechnical().getString();
		double positionY = getLineCoordinateY(string);
		noteDrawer.setPane(pane);
		noteDrawer.setNote(note);
		noteDrawer.setStartX(noteDrawer.getStartX());
		noteDrawer.setStartY(positionY + 3 + y);
		noteDrawer.drawFret();
		drawBend(note);
		/*
		 * double py = getLastLineCoordinateY(); DrawNoteType type = new
		 * DrawNoteType(pane, note, noteDrawer.getStartX() + 7, py + y);
		 * type.drawType(); drawType(note, noteList);
		 */
	}

	// draw grace notes that have chords
	private void drawChordsWithGraceNotes(Note note, List<Note> noteList) {
		// TODO Auto-generated method stub
		int string = note.getNotations().getTechnical().getString();
		double positionY = getLineCoordinateY(string);
		noteDrawer.setPane(pane);
		noteDrawer.setNote(note);
		noteDrawer.setStartX(noteDrawer.getStartX());
		noteDrawer.setStartY(positionY + 3 + y);
		noteDrawer.drawGuitarGrace();
		drawBend(note);
		// drawSlur(note, noteList);
		/*
		 * double py = getLastLineCoordinateY(); DrawNoteType type = new
		 * DrawNoteType(pane, note, noteDrawer.getStartX() + 7, py + y);
		 * type.drawType();
		 */
	}

	private void drawType(Note note, List<Note> noteList) {
		String current = note.getType();
		String lastType = "";
		int index = noteList.indexOf(note);
		// int actualCurrent = note.getTimeModification().getActualNotes();
		int actualLast = 0;
		int l = 0; 
		for (int i = index; i > 0; i--) {
			Note last = noteList.get(i - 1);
			if (last != null && !noteHasChord(last) && !noteHasGrace(last)) {
				l = last.getNotations().getTechnical().getFret(); 
				lastType = last.getType();
				if (noteHasActual(last)) {
					actualLast = last.getTimeModification().getActualNotes();
				}
				
					break;
				
			}
		}
		double py = getLastLineCoordinateY();
		double shortStick = 15;
		DrawNoteType type = new DrawNoteType(pane, noteDrawer.getStartX() + 4, py + y, shortStick);

		switch (current) {
		case "half":
			type.drawShortLine();
			drawDot(note, type, py + shortStick);
			break;
		case "quarter":
			type.drawLongLine();
			drawDot(note, type, py);
			break;
		case "eighth":
			type.drawLongLine();
			drawDot(note, type, py);
			if (lastType == "eighth" && actualLast <= 0) {
				if (this.noteTypeCounter > 0) {
					type.drawBeam(-spacing);
					this.noteTypeCounter--;
				} else {
					this.noteTypeCounter = 3;
				}
			} else if (lastType == "16th") {
				type.drawBeam(-spacing);
			} else {
				type.drawBeam(spacing / 4);
			}
			break;
		case "16th":
			type.drawLongLine();
			if (lastType == "16th") {
				if (this.noteTypeCounter > 0) {
					type.drawBeam(-spacing);
					type.setStartY(py + y - 5);
					type.drawBeam(-spacing);
					this.noteTypeCounter--;
				} else {
					this.noteTypeCounter = 3;
				}
			} else if (lastType == "eighth") {
				//System.out.println(this.noteTypeCounter);
				if (this.noteTypeCounter == 3) {
					type.drawBeam(-spacing);
					type.setStartY(py + y - 5);
					type.drawBeam(-spacing / 2);
				} else {
					type.drawBeam(-spacing);
				}

			} else {
				type.drawBeam(spacing / 4);
				type.setStartY(py + y - 5);
				type.drawBeam(spacing / 4);
			}
		case "32nd":
		/*
				 * else if (lastType == "eighth") { System.out.println(this.noteTypeCounter);
				 * if(this.noteTypeCounter == 3) { type.drawBeam(-spacing); type.setStartY(py +
				 * y - 5); type.drawBeam(-spacing/2); } else { type.drawBeam(-spacing); }
				 * 
				 * } else { type.drawBeam(spacing / 4); type.setStartY(py + y - 5);
				 * type.drawBeam(spacing / 4); }
				 */

			break;
		}
		
		if(current =="32nd") {
			int f = note.getNotations().getTechnical().getFret(); 
			//System.out.println("Current note: "+f+ " has type: "+ current+" | Last note is: "+ l+" | counter is: " + this.noteTypeCounter); 
			type.drawLongLine();
			if (lastType == "32nd") {
				if (this.noteTypeCounter > 0) {
					type.drawBeam(-spacing);
					type.setStartY(py + y - 5);
					type.drawBeam(-spacing);
					type.setStartY(py + y - 10);
					type.drawBeam(-spacing);
					this.noteTypeCounter--;
					//System.out.println("The last note is 32nd, counter now is: "+this.noteTypeCounter); 
			} else {
					//type.drawBeam(-spacing);
					this.noteTypeCounter = 3;
					//System.out.println("The last note is not 32nd, counter now is: "+this.noteTypeCounter); 
				}
			}
			else {
				this.noteTypeCounter = 3; 
			}
		}

//		if (noteHasActual(note)) {
//			System.out.println(this.ActualCounter);
//			if (this.ActualCounter < 2 ) {
//				int currentActual = note.getTimeModification().getActualNotes();
//				if (currentActual == actualLast) {
//					// type.setStartX(noteDrawer.getStartX());
//					type.drawActual(actualLast, spacing, fontSize);
//				}
//			}
//			else if(this.ActualCounter >= 2) {
//				System.out.println("yes"); 
//			}
//		}
//		this.ActualCounter = 0; 

	}

	private void drawDot(Note note, DrawNoteType type, double py) {
		// TODO Auto-generated method stub
		if (noteHasDot(note)) {
			int count = countDotNumber(note);
			double xCenter = noteDrawer.getStartX() + 10;
			double yCenter = py + y + 10;
			double radius = spacing / 25;
			while (count > 0) {
				type.drawDot(xCenter, yCenter, radius);
				xCenter += 2 * radius + radius;
				count--;
			}
		}
	}

	private boolean noteHasActual(Note note) {
		Boolean res = note.getTimeModification() == null ? false : true;
		return res;
	}

	private int countDotNumber(Note note) {
		// TODO Auto-generated method stub
		int res = 0;
		List<Dot> dotList = note.getDots();
		for (int i = 0; i < dotList.size(); i++) {
			res++;
		}

		return res;
	}

	// returns true if note has a dot
	private boolean noteHasDot(Note note) {
		// TODO Auto-generated method stub
		Boolean res = note.getDots() == null ? false : true;
		return res;
	}

	private void drawSlur(Note note, List<Note> noteList) {
		if (noteHasSlur(note)) {
			// int f = note.getNotations().getTechnical().getFret();
			// System.out.println(f +"\n");
			int string = note.getNotations().getTechnical().getString();
			double positionY = getLineCoordinateY(string);
			List<Slur> slurList = note.getNotations().getSlurs();
			for (Slur s : slurList) {
				int num = s.getNumber();
				String type = s.getType();
				if (type == "start") {
					slurDrawer.setStartX(noteDrawer.getStartX());
					slurDrawer.setStrokeWidth(this.fontSize / 2);
					int index = noteList.indexOf(note);
					lookForStop: for (int i = index; i < noteList.size() - 1; i++) {
						Note next = noteList.get(i + 1);
						if (noteHasSlur(next)) {
							List<Slur> nextSlurs = next.getNotations().getSlurs();
							for (Slur ns : nextSlurs) {
								int nsNum = ns.getNumber();
								String nsType = ns.getType();
								// String placement = "";
								if (nsType == "stop" && nsNum == num) {
									if (noteHasGrace(next)) {
										slurDrawer.setEndX(noteDrawer.getStartX() + spacing / 4);
									} else if ((noteHasGrace(note) && !noteHasGrace(next))
											|| (!noteHasGrace(note) && noteHasGrace(next))) {
										slurDrawer.setEndX(noteDrawer.getStartX() + spacing / 2);
									} else {
										slurDrawer.setEndX(noteDrawer.getStartX() + spacing + spacing / 4);
									}
									String placement = "";
									if (s.getPlacement() != null) {
										placement = s.getPlacement();
									}
									if (noteHasChord(note)) {
										slurDrawer.setStartY(positionY + fontSize + y);
										slurDrawer.setPlace(1);
									} else {
										slurDrawer.setStartY(positionY - fontSize + y);
										slurDrawer.setPlace(-1);
									}
									// int c = note.getNotations().getTechnical().getFret();
									// int n = next.getNotations().getTechnical().getFret();
									// System.out.println("grace between: "+ c+ " and "+ n);
									slurDrawer.draw();
									break lookForStop;
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean noteHasSlur(Note note) {
		Boolean res = note.getNotations().getSlurs() == null ? false : true;
		return res;
	}

	public void highlightMeasureArea(Measure measure) {
		double x = 0;
		double y = 0;

		ObservableList<?> children = pane.getChildren();
		ArrayList<Rectangle> removeRect = new ArrayList<Rectangle>();
		for (Iterator<?> iterator = children.iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			if (object instanceof Rectangle) {
				removeRect.add((Rectangle) object);
			}
		}

		for (Iterator<Rectangle> iterator = removeRect.iterator(); iterator.hasNext();) {
			Rectangle rect = (Rectangle) iterator.next();
			pane.getChildren().remove(rect);
		}
		x = 0;
		y = 0;
		// now iterate again to highlight it in red
		for (int i = 0; i < measureList.size(); i++) {
			double w = getXCoordinatesForGivenMeasure(measureList.get(i)) - x;
			double yf = getYCoordinatesForGivenMeasure(measureList.get(i));
			if (yf > y) {
				// we have moved on to new Line
				x = 0;
				w = getXCoordinatesForGivenMeasure(measureList.get(i)) - x;
				y = yf;
			}
			if (measure.equals(measureList.get(i))) {
				Rectangle rectangle = new Rectangle(x, yf, w, 50);
				rectangle.setFill(Color.TRANSPARENT);
				rectangle.setStyle("-fx-stroke: red;");
				pane.getChildren().add(rectangle);
				Object b4 = pane.getParent().getParent().getParent().getParent();
				if(b4 instanceof ScrollPane) {
					ScrollPane sp = (ScrollPane)b4;
					double rectBounds = rectangle.getBoundsInLocal().getMaxY();
					double thisBounds = pane.getBoundsInLocal().getMaxY();
					double val = rectBounds/thisBounds;
					sp.setVvalue(val);
				}
			}
			x = getXCoordinatesForGivenMeasure(measureList.get(i));
			y = yf;
		}
	}

	
	public void highlightNote() {
				
					Note currentNote;
					double xPositionNote=0;
					double yPositionNote=0;
					x = 0;
					y = 0;
					ArrayList<Rectangle> r = new ArrayList<Rectangle>();
					ArrayList<Double> noteDuration = new ArrayList<Double>();
					
					for (int i = 0; i < measureList.size(); i++) {
						Measure measure = measureList.get(i);
						List<Note> noteList = measure.getNotesBeforeBackup();
						double w = getXCoordinatesForGivenMeasure(measureList.get(i)) - x;
						double yf = getYCoordinatesForGivenMeasure(measureList.get(i));
						if (yf > y) {
							x = 0;
							w = getXCoordinatesForGivenMeasure(measureList.get(i)) - x;
							y = yf;
						}
						
						for (int j = 0; j < noteList.size(); j++) {
							currentNote = noteList.get(j);
							yPositionNote = getYCoordinatesForGivenMeasure(measureList.get(i));
							xPositionNote = getXCoordinatesForGivenMeasure(measureList.get(i));
							if (!noteHasChord(currentNote)) {
								if (noteHasGrace(currentNote)) {
									int num = countGraceSpace(currentNote, noteList);
									xPositionNote = noteDrawer.getStartX() + spacing / 2;
									int fret = currentNote.getNotations().getTechnical().getFret();
									double graceSpacing = 0;
									if (fret < 10) {
										graceSpacing = xPositionNote - (spacing / (4 / num));
									} else {
										graceSpacing = xPositionNote - (spacing / (4 / num) + num);
									}
									xPositionNote = graceSpacing;
								}
								else {
										xPositionNote = x+spacing/2;		
								}
							}
							else if (noteHasChord(currentNote)) {
								if (noteHasGrace(currentNote)) {
									xPositionNote = noteDrawer.getStartX();
								} else {
									xPositionNote = noteDrawer.getStartX();
								}
							}
							Rectangle rectangle = new Rectangle();
							rectangle.setWidth(15);
							rectangle.setHeight(70);
							
							//rectangle.setStyle("-fx-stroke: red;");
							rectangle.setTranslateX(xPositionNote-2);
							rectangle.setTranslateY(yPositionNote-15);
							rectangle.setFill(Color.TRANSPARENT);
							pane.getChildren().add(rectangle);
							
							r.add(rectangle);                
							this.x += currentNote.getChord() == null && currentNote.getGrace() == null ? this.spacing : 0;
							if (currentNote.getDuration() !=null) {
								int duration= currentNote.getDuration();
								double duration2 =1000.0/((double)duration);
								noteDuration.add(duration2);
							}

			}}
			
			GuitarHighlight note = new GuitarHighlight(this, r, noteDuration);
			note.start();
		
			ObservableList children = pane.getChildren();
	 		ArrayList<Rectangle> removeRect = new ArrayList<Rectangle>();
	 		for (Iterator iterator = children.iterator(); iterator.hasNext();) {
	 			Object object = (Object) iterator.next();
	 			if (object instanceof Rectangle) {
	 				if (((Rectangle) object).getStyle().equals("-fx-stroke: TRANSPARENT;")) {
	 					removeRect.add((Rectangle) object);
	 				}
	 			}
	 		}

	 		for (Iterator iterator = removeRect.iterator(); iterator.hasNext();) {
	 			Rectangle rect = (Rectangle) iterator.next();
	 			pane.getChildren().remove(rect);
	 		}
		
  }

public Boolean playing;
public void starthighlight() {
	 this.playing = true;
}
public void stophighlight() {
	 this.playing=false;
}
	// return X coordinates for given measure
	public double getXCoordinatesForGivenMeasure(Measure measure) {
		return xCoordinates.get(measure);
	}

	// return Y coordinates for given measure
	public double getYCoordinatesForGivenMeasure(Measure measure) {
		return yCoordinates.get(measure);
	}

	private double getLastLineCoordinateY() {
		return this.d.getMusicLineList().get(5).getStartY(6);

	}

	// Getters and setters

	public ScorePartwise getScorePartwise() {
		return scorePartwise;
	}

	public void setScorePartwise(ScorePartwise scorePartwise) {
		this.scorePartwise = scorePartwise;
	}

	public Pane getPane() {
		return pane;
	}

	public void setPane(Pane pane) {
		this.pane = pane;
	}

	public List<Measure> getMeasureList() {
		return measureList;
	}

	public void setMeasureList(List<Measure> measureList) {
		this.measureList = measureList;
	}

	public double getSpacing() {
		return spacing;
	}

	public int getLineSpacing() {
		return LineSpacing;
	}

	public void setLineSpacing(int lineSpacing) {
		LineSpacing = lineSpacing;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public int getStaffSpacing() {
		return staffSpacing;
	}

	public void setStaffSpacing(int staffSpacing) {
		this.staffSpacing = staffSpacing;
	}

	public void setSpacing(double spacing) {
		this.spacing = spacing;
	}

	// End getters and setters
}