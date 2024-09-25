package com.pia.tmf.v4.common.util;

import static org.junit.jupiter.api.Assertions.*;

import com.pia.tmf.v4.common.model.Note;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

/**
 * @author Gokhan Demir
 */
class NoteUtilTests {

  @Test
  void noteListContainsTextReturnsTrueWhenMatchFound() {
    Note note1 = new Note();
    note1.setText("Hello World");
    Note note2 = new Note();
    note2.setText("Goodbye World");

    boolean result = NoteUtil.noteListContainsText(Arrays.asList(note1, note2), "Hello");

    assertTrue(result);
  }

  @Test
  void noteListContainsTextReturnsFalseWhenNoMatch() {
    Note note1 = new Note();
    note1.setText("Hello World");
    Note note2 = new Note();
    note2.setText("Goodbye World");

    boolean result = NoteUtil.noteListContainsText(Arrays.asList(note1, note2), "Hola");

    assertFalse(result);
  }

  @Test
  void noteListContainsTextReturnsFalseWhenNoteTextIsNull() {
    Note note1 = new Note();
    note1.setText(null);
    Note note2 = new Note();
    note2.setText("Goodbye World");

    boolean result = NoteUtil.noteListContainsText(Arrays.asList(note1, note2), "Hello");

    assertFalse(result);
  }

  @Test
  void noteListContainsTextReturnsFalseWhenNoteListIsEmpty() {
    boolean result = NoteUtil.noteListContainsText(Collections.emptyList(), "Hello");

    assertFalse(result);
  }
}
