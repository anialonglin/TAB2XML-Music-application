package GUI;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import instruments.Guitar;
import instruments.Drumset;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Window;
import javafx.stage.Stage;
import models.ScorePartwise;
import models.measure.Measure;

public class PreviewMusic extends Application {

	private MainViewController mvc;
	@FXML
	private Pane pane;
	public Window convertWindow;
	@FXML
	private Button printButton;
	final BooleanProperty printButtonPressed = new SimpleBooleanProperty(false);
	private ScorePartwise scorePartwise;
	@FXML
	private AnchorPane anchorPane;
	@FXML
	TextField gotoMeasureField;
	private Guitar guitar;
	private Drumset drum;
	@FXML
	private Button closePreviewButton;
	@FXML
	private Slider noteSpacing;
	private int noteSpacingValue;
	@FXML
	private Slider FontSizeSlider;
	private int fontSize;
	@FXML
	private Slider StaffSpacingSlider;
	private int staffSpacingValue;
	@FXML
	private Slider musicLineSlider;
	private int musicLineSpacingValue;

	public PreviewMusic() {
	}

	@FXML
	public void initialize() {
	}

	public void setMainViewController(MainViewController mvcInput) {
		mvc = mvcInput;
	}

	// method to update the preview window
	public void update() throws IOException {

		/*
		 * Get the list of measures from the ScorePartwise object.
		 *
		 * We get the list of Parts, there should be only one Part in this list, so we
		 * get the first item, which is the Part, then we get the measures from that
		 * Part.
		 */
		scorePartwise = mvc.converter.getScorePartwise();
		/*
		 * The value for the menus are set here: Range of the font size 8 - 30 and font
		 * is 1/2 notespacing. Range of staff spacing: 10 - 30 Range of the note
		 * spacing: 20-60 Range of the music Line spacing: 120 - 300
		 */
		// initial Font size slider values:
		FontSizeSlider.setMax(Math.round(noteSpacing.getValue() / 2));
		FontSizeSlider.setMin(8);
		FontSizeSlider.setValue(12);
		FontSizeSlider.setShowTickLabels(true);
		FontSizeSlider.setShowTickMarks(true);

		// Changing the staff spacing slider affects the note spacing, font size, and
		// music line sliders

//		 staff spacing = 10:
//		 	Font max = 12
//		 	MusicLine spacing min = 120
//		  		noteSpacing Max = 25
//		 staff spacing = 15:
//		  		Font max = 17
//		  		MusicLine spacing min = 150
//		  		noteSpacing Max = 35
//		  staff spacing = 20:
//		  		Font max = 22
//		  		MusicLine spacing min = 180
//		 		noteSpacing Max = 45
//		  staff spacing = 25:
//		  		Font max = 27
//		  		MusicLine spacing min = 210
//		  		noteSpacing Max = 55
//		   staff spacing = 30:
//		  		Font max = 30
//		  		MusicLine spacing min = 240
//		  		noteSpacing Max = 60

		StaffSpacingSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				StaffSpacingSlider.setValue((double) newValue);
				int newSpacing = (int) StaffSpacingSlider.getValue();
				switch (newSpacing) {
				case 10:
					FontSizeSlider.setMax(12);
					FontSizeSlider.setValue(10);

					noteSpacing.setMax(25);
					noteSpacing.setValue(noteSpacing.getMin());

					musicLineSlider.setMin(120);
					musicLineSlider.setValue(musicLineSlider.getMin());
					break;
				case 15:
					FontSizeSlider.setMax(17);
					FontSizeSlider.setValue(12);

					noteSpacing.setMax(35);
					noteSpacing.setValue(noteSpacing.getMin());

					musicLineSlider.setMin(150);
					musicLineSlider.setValue(musicLineSlider.getMin());
					break;
				case 20:
					FontSizeSlider.setMax(22);
					FontSizeSlider.setValue(12);

					noteSpacing.setMax(45);
					noteSpacing.setValue(noteSpacing.getMin());

					musicLineSlider.setMin(180);
					musicLineSlider.setValue(musicLineSlider.getMin());
					break;
				case 25:
					FontSizeSlider.setMax(27);
					FontSizeSlider.setValue(12);

					noteSpacing.setMax(55);
					noteSpacing.setValue(noteSpacing.getMin());

					musicLineSlider.setMin(210);
					musicLineSlider.setValue(musicLineSlider.getMin());
					break;
				case 30:
					FontSizeSlider.setMax(30);
					FontSizeSlider.setValue(12);

					noteSpacing.setMax(60);
					noteSpacing.setValue(noteSpacing.getMin());

					musicLineSlider.setMin(240);
					musicLineSlider.setValue(musicLineSlider.getMin());
					break;
				}
				FontSizeSlider.setShowTickLabels(true);
				FontSizeSlider.setShowTickMarks(true);
				musicLineSlider.setShowTickLabels(true);
				musicLineSlider.setShowTickMarks(true);
			}

		});

		// Linking noteSpace slider to the output: changing note Space slider affects
		// the
		// range for the Font Size
		noteSpacing.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				noteSpacing.setValue((double) newValue);
				double newMax = noteSpacing.getValue() / 2;
				FontSizeSlider.setMax(Math.floor(newMax));
				FontSizeSlider.setMin(8);
				FontSizeSlider.setValue((newMax + 8) / 2);
				FontSizeSlider.setShowTickLabels(true);
				FontSizeSlider.setShowTickMarks(true);

			}
		});

		String instrument = getInstrument();
		if (instrument == "Guitar") {
			setDefault();
			this.guitar = new Guitar(scorePartwise, pane, getNoteSpacingValue(), getFontSize(), getStaffSpacingValue(), getMusicLineSpacingValue());
			this.guitar.drawGuitar();
		} else if (instrument == "Drumset") {
			this.drum = new Drumset(scorePartwise, pane);
			this.drum.draw();
		}

	}

	private void setDefault() {
		// TODO Auto-generated method stub
		setNoteSpacingValue(40); 
		setFontSize(12); 
		setStaffSpacingValue(10); 
		setMusicLineSpacingValue(200); 
	}

	// Method that handles the `print music sheet` button
	@FXML
	public <printButtonPressed> void printHandle() {

		WritableImage screenshot = anchorPane.snapshot(null, null);
		Printer printer = Printer.getDefaultPrinter();
		PageLayout layout = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);

		double pagePrintableWidth = layout.getPrintableWidth();
		double pagePrintableHeight = layout.getPrintableHeight();
		// Elmira: Something (most likely the structure of Guitar) cause problem with
		// scaling
		// As a quick fix I multiplied the width of the anchorPane by 1.5. @Irsa, if you
		// find a better fix
		// (or decide this fix is good enough) delete this comment.
		final double scaleX = pagePrintableWidth / (1.5 * screenshot.getWidth());
		final double scaleY = pagePrintableHeight / screenshot.getHeight();
		final ImageView print_node = new ImageView(screenshot);
		print_node.getTransforms().add(new Scale(scaleX, scaleX));

		PrinterJob printSheet = PrinterJob.createPrinterJob();

		if (printSheet != null && printSheet.showPrintDialog(pane.getScene().getWindow())) {

			double numberOfPages = Math.ceil(scaleX / scaleY);
			Translate gridTransform = new Translate(0, 0);
			print_node.getTransforms().add(gridTransform);
			for (int i = 0; i < numberOfPages; i++) {
				gridTransform.setY(-i * (pagePrintableHeight / scaleX));
				printSheet.printPage(layout, print_node);
			}

			printSheet.endJob();

		}

	}

	// Method that handles `play note` button
	@FXML
	public void playHandle() {
		String instrument = getInstrument();
		if (instrument == "Guitar") {
			this.guitar.playGuitarNote(); // Play method in Guitar class
		} else if (instrument == "Drumset") {
			this.drum.playDrumNote(); // Play method in Drumset class
		}
	}

	// Method that handle navigating to specific measure (1- size of measure list)
	// through 'Go' button
	public void handleGotoMeasure() {
		int measureNumber = Integer.parseInt(gotoMeasureField.getText());
		String instrument = getInstrument();
		// System.out.println("instrument:" + instrument);
		int count = 1;
		boolean measureFound = false;
		if (instrument == "Guitar") {
			List<Measure> measureList = this.guitar.getMeasureList();
			for (Iterator<Measure> iterator = measureList.iterator(); iterator.hasNext();) {
				Measure measure = (Measure) iterator.next();
				if (measureNumber == count) {
					this.guitar.highlightMeasureArea(measure);
					measureFound = true;
					break;
				}
				count++;
			}
		} else if (instrument == "Drumset") {
			List<Measure> measureList = this.drum.getMeasureList();
			for (Iterator<Measure> iterator = measureList.iterator(); iterator.hasNext();) {
				Measure measure = (Measure) iterator.next();
				if (measureNumber == count) {
					this.drum.highlightMeasureArea(measure);
					measureFound = true;
					break;
				}
				count++;
			}
		}
		if (!measureFound) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Measure " + measureNumber + " could not be found.");
			alert.setHeaderText("Preview Music Sheet");
			alert.show();
		}
	}

	// Method that handles `close` button
	@FXML
	public void closePreviewHandle() {
		// mvc.editHandle();
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to close this Preview window?",
				ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
		alert.setTitle("Close preview window");
		alert.setHeaderText("You are about to close the Preview window!");
		Optional<ButtonType> o = alert.showAndWait();

		if (o.get() == ButtonType.YES) {
			mvc.convertWindow.hide();
		}

	}

	// return a string representation of the instrument
	private String getInstrument() {
		String instrument = scorePartwise.getPartList().getScoreParts().get(0).getPartName();
		return instrument;

	}

	
	public int getNoteSpacingValue() {
		this.noteSpacingValue = (int) noteSpacing.getValue();
		return noteSpacingValue;
	}

	public int getFontSize() {
		this.fontSize = (int) FontSizeSlider.getValue();
		return fontSize;
	}

	public int getStaffSpacingValue() {
		this.staffSpacingValue = (int) StaffSpacingSlider.getValue();
		return staffSpacingValue;
	}

	public int getMusicLineSpacingValue() {
		this.musicLineSpacingValue = (int) musicLineSlider.getValue();
		return musicLineSpacingValue;
	}

	public void setNoteSpacingValue(int noteSpacingValue) {
		this.noteSpacingValue = noteSpacingValue;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public void setStaffSpacingValue(int staffSpacingValue) {
		this.staffSpacingValue = staffSpacingValue;
	}

	public void setMusicLineSpacingValue(int musicLineSpacingValue) {
		this.musicLineSpacingValue = musicLineSpacingValue;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub

	}
}