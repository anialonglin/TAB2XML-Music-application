package GUI.draw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.Node;
import javafx.scene.text.Text;

import converter.Score;

import models.ScorePartwise;
import models.measure.Measure;
import models.measure.note.Note;

import instruments.Drumset;

import custom_exceptions.TXMLException;

public class DrawNoteTest {

	String f; 
	@Test
	void testDrawFret() {
		try {
		    String input ="""
				|-----------0-----|-0---------------|
				|---------0---0---|-0---------------|
				|-------1-------1-|-1---------------|
				|-----2-----------|-2---------------|
				|---2-------------|-2---------------|
				|-0---------------|-0---------------|
		    """;

		    Pane pane = new Pane();

		    Score score = new Score(input);
	
		    ScorePartwise scorePartwise = score.getModel();

		    double x = 0;
		    double y = 0;

		    for (Measure measure : scorePartwise.getParts().get(0).getMeasures()) {

		    	for (Note note : measure.getNotesBeforeBackup()) {
		    		// Draw note
		    		int fret = note.getNotations().getTechnical().getFret();
		    		DrawNote drawNote = new DrawNote(pane, note, x, y, 12, f);
		    		drawNote.drawFret();

		    		// Check pane to make sure the note was added
		    		Node addedNode = pane.getChildren().get(pane.getChildren().size()-1);
		    		if (addedNode instanceof Text) {
		    			Text textNode = (Text) addedNode;

		    			assertEquals(x, textNode.getX(), "Text node has incorrect x-coordinate: expected " + x + ", but received " + textNode.getX());
		    			assertEquals(y, textNode.getY(), "Text node has incorrect y-coordinate: expected " + y + ", but received " + textNode.getY());
		    			assertEquals(Integer.toString(fret), textNode.getText(), "Text node has incorrect text value: expected " + fret + ", but receieved" + textNode.getText());
		    		} else {
		    			fail("Text node was not added");
		    		}
		    	}
		    }
		} catch (TXMLException e) {
			fail("TXMLException thrown\n" + e.getMessage());
		}
	}
}
