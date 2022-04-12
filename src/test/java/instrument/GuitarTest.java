package instrument;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import converter.Score;
import instruments.Guitar;
import javafx.scene.layout.Pane;
import models.ScorePartwise;
import models.measure.Measure;
import models.measure.attributes.Clef;
import models.measure.note.Note;
import models.measure.note.notations.Notations;
import utility.MusicXMLCreator;

public class GuitarTest extends ApplicationTest {

	private ScorePartwise scorePartwise;
	private Guitar g;
	private Measure m1;
	private int noteSpacing = 50;
	private int font = 12;
	private int staffSpacing = 10;
	private int LineSpacing = 40;
	private String fontName = "Impact";

	@BeforeEach
	public void setUp() throws Exception {
		String input = """
				|-----------0-----|-0---------------|
				|---------0---0---|-0---------------|
				|-------1-------1-|-1---------------|
				|-----2-----------|-2---------------|
				|---2-------------|-2---------------|
				|-0---------------|-0---------------|
				 """;
		Score score = new Score(input);
		MusicXMLCreator mxlc = new MusicXMLCreator(score);
		scorePartwise = mxlc.getScorePartwise();
		g = new Guitar(scorePartwise, null, noteSpacing, font, staffSpacing, LineSpacing, fontName);
		m1 = g.getMeasureList().get(0);

	}

	// Testing extractClef() to see if the extracted clef from measure input is same
	// as the actual clef
	@Test
	void testExtractClef() {

		// clef from the extractClef
		Clef c1 = g.getClef();

		// Actual clef
		Clef c2 = scorePartwise.getParts().get(0).getMeasures().get(0).getAttributes().getClef();

		assertEquals(c1, c2);
	}

	// given a note with a chord test if method returns true
	@Test
	void testNoteHasChordTrue() {
		// note from the actual input measure 2 note 2 which has chord
		List<Note> noteList = scorePartwise.getParts().get(0).getMeasures().get(1).getNotesBeforeBackup();
		Note n = noteList.get(1);
		Boolean res = g.noteHasChord(n);
		assertTrue(res, "note has chord");

	}

	// given a note without a chord test if method returns false
	@Test
	void testNoteHasChordFalse() {
		// note from the actual input measure 1 note 2 which has no chord
		List<Note> noteList = scorePartwise.getParts().get(0).getMeasures().get(0).getNotesBeforeBackup();
		Note n = noteList.get(1);
		Boolean res = g.noteHasChord(n);
		assertFalse(res, "note has no chord");
	}

	// Given a note that has technical attributes, checks if method returns true
	@Test
	void testNoteHasTechnicalTrue() {
		// note from the actual input measure 2 note 2 which has technical
		List<Note> noteList = scorePartwise.getParts().get(0).getMeasures().get(1).getNotesBeforeBackup();
		Note n = noteList.get(1);
		Boolean res = g.noteHasTechnical(n);
		assertTrue(res, "note has technical attributes");
	}

	// Given a note that doesnt have technical attributes, checks if method returns
	// false
	@Test
	void testNoteHasTechnicalFalse() {
		// all notes from input have technical so we create a local note without any
		Note n = new Note();
		Notations notation = new Notations();
		notation.setTechnical(null);
		n.setNotations(notation);
		Boolean res = g.noteHasTechnical(n);
		assertFalse(res, "note has technical attributes");
	}

	// Test that getMeasureList() returns the correct list
	@Test
	void testGetMeasureList() {
		// Actual measure list-->expected
		List<Measure> m = scorePartwise.getParts().get(0).getMeasures();

		// measure list from the Guitar class
		List<Measure> m0 = g.getMeasureList();

		assertSame(m, m0);
	}

	// Test that setMeasureList() set the correct list
	@Test
	void testSetMeasureList() {
		// Actual measure list-->expected
		List<Measure> m = scorePartwise.getParts().get(0).getMeasures();

		// create a new measure list to set
		String input1 = """
				|-----------0-----|-0---------------|
				|---------0---0---|-0---------------|
				|-------1-------1-|-1---------------|
				|-----2-----------|-2---------------|
				|---2-------------|-2---------------|
				|-0---------------|-0---------------|

				|-----------0-----|-0---------------|
				|---------0---0---|-0---------------|
				|-------1-------1-|-1---------------|
				|-----2-----------|-2---------------|
				|---2-------------|-2---------------|
				|-0---------------|-0---------------|
				  """;
		Score score1 = new Score(input1);
		MusicXMLCreator mxlc1 = new MusicXMLCreator(score1);
		ScorePartwise scorePartwise1 = mxlc1.getScorePartwise();
		Guitar g1 = new Guitar(scorePartwise1, null, 50, 50, 12, 30, "Impact");
		List<Measure> newList = g1.getMeasureList();

		// setting the meaasure of the g to newList
		g.setMeasureList(newList);

		// Getting the new list from g which should be different from m
		List<Measure> m0 = g.getMeasureList();

		assertNotSame(m, m0);
		assertSame(newList, m0);
	}

	@Test
	void testCountNote() {
		List<Note> n = m1.getNotesBeforeBackup();
		int actual = g.countNotes(n);
		int expected = 8;
		assertEquals(actual, expected);
	}

	@Test
	void testCountNoteSpacesRequired() {
		List<Note> n = m1.getNotesBeforeBackup();
		int actual = g.countNoteSpacesRequired(n);
		int expected = 8 * noteSpacing;
		assertEquals(actual, expected, "the actual is: " + actual + " | the expected: " + expected);
	}

	@Test
	void testNoteHasSlide() {
		List<Note> n = m1.getNotesBeforeBackup();
		Note note = n.get(0);
		Boolean res = g.noteHasSlide(note);
		assertFalse(res, "note has slide attributes");
		// create a new measure list to set
		String input1 = """

				E|--------------------------|---------------------------|
				B|--------------------------|---------------------------|
				G|--------------------------|---------------------------|
				D|------------------2---5---|-8---7--s14-----------12---|
				A|--0-----------4-----------|---------------------------|
				D|--------------------------|---------------------------|
				                3   1   4     4   3    4               2
										  """;
		Score score1 = new Score(input1);
		MusicXMLCreator mxlc1 = new MusicXMLCreator(score1);
		ScorePartwise scorePartwise1 = mxlc1.getScorePartwise();
		Guitar g1 = new Guitar(scorePartwise1, null, 50, 50, 12, 30, "Impact");
		List<Measure> newList = g1.getMeasureList();
		Measure one = newList.get(1);
		List<Note> n1 = one.getNotesBeforeBackup();
		Note note1 = n1.get(1);
		Boolean res1 = g.noteHasSlide(note1);
		assertTrue(res1, "note has slide attributes");

	}

	@Test
	void testNoteHasHarmonic() {
		List<Note> n = m1.getNotesBeforeBackup();
		Note note = n.get(0);
		Boolean res = g.noteHasHarmonic(note);
		assertFalse(res, "note has harmonic attributes");

		// create a new measure list to set
		String input1 = """
								    arm.
				     |       |       |          |          |        |
				  3/4
				E|---------------------------|-15p12-10p9-12p10-6p5-8p6-----|
				B|---------------------------|--------------------------8-5-|
				G|---------------------------|------------------------------|
				D|--[7]----------------------|------------------------------|
				A|--[7]----------------------|------------------------------|
				D|--[7]----------------------|------------------------------|
				                                4  1  2 1  4  2 2 1 4 2 4 1
								""";
		Score score1 = new Score(input1);
		MusicXMLCreator mxlc1 = new MusicXMLCreator(score1);
		ScorePartwise scorePartwise1 = mxlc1.getScorePartwise();
		Guitar g1 = new Guitar(scorePartwise1, null, 50, 50, 12, 30, "Impact");
		List<Measure> newList = g1.getMeasureList();
		Measure one = newList.get(0);
		List<Note> n1 = one.getNotesBeforeBackup();
		Note note1 = n1.get(0);
		Boolean res1 = g.noteHasHarmonic(note1);
		assertTrue(res1, "note has harmonic attributes");

	}

	@Test
	void testNoteHasTie() {
		List<Note> n = m1.getNotesBeforeBackup();
		Note note = n.get(0);
		Boolean res = g.noteHasTie(note);
		assertFalse(res, "note has tie attributes");

		// create a new measure list to set
		String input1 = """

				|--0---0---|T----0----------|
				|----------|------------3---|
				|--3---3---|----------------|
				|----------|----------------|
				|----------|----------------|
				|----------|----------------|
									""";
		Score score1 = new Score(input1);
		MusicXMLCreator mxlc1 = new MusicXMLCreator(score1);
		ScorePartwise scorePartwise1 = mxlc1.getScorePartwise();
		Guitar g1 = new Guitar(scorePartwise1, null, 50, 50, 12, 30, "Impact");
		List<Measure> newList = g1.getMeasureList();
		Measure one = newList.get(0);
		List<Note> n1 = one.getNotesBeforeBackup();
		Note note1 = n1.get(2);
		Boolean res1 = g.noteHasTie(note1);
		assertTrue(res1, "note has tie attributes");

	}

	@Test
	void testNoteHasBend() {
		List<Note> n = m1.getNotesBeforeBackup();
		Note note = n.get(0);
		Boolean res = g.noteHasBend(note);
		assertFalse(res, "note has bend attributes");

		// create a new measure list to set
		String input1 = """


				|----------|----------1-------|
				|----------|--4---------1-----|
				|--3b5-3---|--3b5-5b6-----3b5-|
				|----------|------------------|
				|----------|------------------|
				|----------|------------------|
													""";
		Score score1 = new Score(input1);
		MusicXMLCreator mxlc1 = new MusicXMLCreator(score1);
		ScorePartwise scorePartwise1 = mxlc1.getScorePartwise();
		Guitar g1 = new Guitar(scorePartwise1, null, 50, 50, 12, 30, "Impact");
		List<Measure> newList = g1.getMeasureList();
		Measure one = newList.get(0);
		List<Note> n1 = one.getNotesBeforeBackup();
		Note note1 = n1.get(0);
		Boolean res1 = g.noteHasBend(note1);
		assertTrue(res1, "note has tie attributes");

	}

	@Test
	void testNoteHasGrace() {
		List<Note> n = m1.getNotesBeforeBackup();
		Note note = n.get(0);
		Boolean res = g.noteHasGrace(note);
		assertFalse(res, "note has bend attributes");

		// create a new measure list to set
		String input1 = """
								      |       |       |       |
				E|------------g4h08h15-------------5-6-5-5---5---|
				B|------------g9h12p11---6-------8-------------6-|
				G|-----------------------------------------------|
				D|---------------------------7-----------------7-|
				A|----g3s15--------------8-------7---------------|
				D|---------------------------------------0-------|


				""";
		Score score1 = new Score(input1);
		MusicXMLCreator mxlc1 = new MusicXMLCreator(score1);
		ScorePartwise scorePartwise1 = mxlc1.getScorePartwise();
		Guitar g1 = new Guitar(scorePartwise1, null, 50, 50, 12, 30, "Impact");
		List<Measure> newList = g1.getMeasureList();
		Measure one = newList.get(0);
		List<Note> n1 = one.getNotesBeforeBackup();
		Note note1 = n1.get(0);
		Boolean res1 = g.noteHasGrace(note1);
		assertTrue(res1, "note has tie attributes");

	}

	@Test
	void testCountGraceSpace() {
		String input1 = """
							      |       |       |       |
				E|------------g4h08h15-------------5-6-5-5---5---|
				B|------------g9h12p11---6-------8-------------6-|
				G|-----------------------------------------------|
				D|---------------------------7-----------------7-|
				A|----g3s15--------------8-------7---------------|
				D|---------------------------------------0-------|


				""";
		Score score1 = new Score(input1);
		MusicXMLCreator mxlc1 = new MusicXMLCreator(score1);
		ScorePartwise scorePartwise1 = mxlc1.getScorePartwise();
		Guitar g1 = new Guitar(scorePartwise1, null, 50, 50, 12, 30, "Impact");
		List<Measure> newList = g1.getMeasureList();
		Measure one = newList.get(0);
		List<Note> n1 = one.getNotesBeforeBackup();
		Note note1 = n1.get(0);
		int expected = 1;
		int actual = g.countGraceSpace(note1, n1);
		assertEquals(actual, expected, "the actual is: " + actual + " | the expected: " + expected);

		Note note2 = n1.get(2);
		int expected2 = 2;
		int actual2 = g.countGraceSpace(note2, n1);
		assertEquals(actual2, expected2, "the actual is: " + actual2 + " | the expected: " + expected2);

	}

	@Test
	void testNextHasActual() {
		List<Note> n = m1.getNotesBeforeBackup();
		Note note = n.get(0);
		Boolean res = g.nextHasActual(note, n);
		assertFalse(res, "note has bend attributes");

		// create a new measure list to set
		String input1 = """
									6/8
				|--0-----0-----|T------0----------|
				|--------------|--------------3---|
				|--3-----3-----|------------------|
				|--------------|------------------|
				|--------------|------------------|
				|--------------|------------------|


								""";
		Score score1 = new Score(input1);
		MusicXMLCreator mxlc1 = new MusicXMLCreator(score1);
		ScorePartwise scorePartwise1 = mxlc1.getScorePartwise();
		Guitar g1 = new Guitar(scorePartwise1, null, 50, 50, 12, 30, "Impact");
		List<Measure> newList = g1.getMeasureList();
		Measure one = newList.get(1);
		List<Note> n1 = one.getNotesBeforeBackup();
		Note note1 = n1.get(2);
		Boolean res1 = g.nextHasActual(note1, n1);
		assertTrue(res1, "note has tie attributes");

	}

	@Test
	void testGetActualNum() {
		String input1 = """
								6/8
				|--0-----0-----|T------0----------|
				|--------------|--------------3---|
				|--3-----3-----|------------------|
				|--------------|------------------|
				|--------------|------------------|
				|--------------|------------------|



				""";
		Score score1 = new Score(input1);
		MusicXMLCreator mxlc1 = new MusicXMLCreator(score1);
		ScorePartwise scorePartwise1 = mxlc1.getScorePartwise();
		Guitar g1 = new Guitar(scorePartwise1, null, 50, 50, 12, 30, "Impact");
		List<Measure> newList = g1.getMeasureList();
		Measure one = newList.get(1);
		List<Note> n1 = one.getNotesBeforeBackup();
		Note note1 = n1.get(2);
		int expected = 2;
		int actual = g.getActualNum(note1, n1);
		assertEquals(actual, expected, "the actual is: " + actual + " | the expected: " + expected);
	}

	@Test
	void testNextIsLastNote() {
		List<Note> n = m1.getNotesBeforeBackup();
		Note note = n.get(0);
		Boolean res = g.nextIsLastNote(note, n);
		assertFalse(res, "next note is not last");

		Note note1 = n.get(6);
		Boolean res1 = g.nextIsLastNote(note1, n);
		assertTrue(res1, "next note is last");

	}

	@Test
	void testGetLastType() {
		List<Note> n = m1.getNotesBeforeBackup();
		Note note = n.get(1);
		String actual = g.getLastType(note, n);
		String expected = "eighth";
		assertEquals(actual, expected, "the actual is: " + actual + " | the expected: " + expected);
	}

	@Test
	void testGetNextType() {
		List<Note> n = m1.getNotesBeforeBackup();
		Note note = n.get(0);
		String actual = g.getNextType(note, n);
		String expected = "eighth";
		assertEquals(actual, expected, "the actual is: " + actual + " | the expected: " + expected);
	}

	@Test
	void testNoteHasActual() {
		List<Note> n = m1.getNotesBeforeBackup();
		Note note = n.get(0);
		Boolean res = g.nextIsLastNote(note, n);
		assertFalse(res, "note has Actual attributes");

		String input1 = """
								6/8
				|--0-----0-----|T------0----------|
				|--------------|--------------3---|
				|--3-----3-----|------------------|
				|--------------|------------------|
				|--------------|------------------|
				|--------------|------------------|



				""";
		Score score1 = new Score(input1);
		MusicXMLCreator mxlc1 = new MusicXMLCreator(score1);
		ScorePartwise scorePartwise1 = mxlc1.getScorePartwise();
		Guitar g1 = new Guitar(scorePartwise1, null, 50, 50, 12, 30, "Impact");
		List<Measure> newList = g1.getMeasureList();
		Measure one = newList.get(1);
		List<Note> n1 = one.getNotesBeforeBackup();
		Note note1 = n1.get(2);
		boolean res1 = g.noteHasActual(note1);
		assertTrue(res1, "note has Actual attributes");

	}
}