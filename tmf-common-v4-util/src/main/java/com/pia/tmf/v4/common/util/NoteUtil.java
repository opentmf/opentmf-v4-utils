package com.pia.tmf.v4.common.util;

import com.pia.tmf.v4.common.model.Note;
import java.util.Collection;
import lombok.Generated;

/**
 * @author Gokhan Demir
 */
public class NoteUtil {

  @Generated
  private NoteUtil() {
    throw new UnsupportedOperationException(
        "NoteUtil is a utility class only with static methods, therefore cannot be instantiated.");
  }

  /**
   * Returns true if any note in the collection contains the requested text.
   * @param notes the note collection.
   * @param text the text to match against note.getText()
   * @return true if any note in the collection contains the requested text.
   */
  public static boolean noteListContainsText(Collection<Note> notes, String text) {
    return notes.stream().filter(note -> note.getText() != null)
        .anyMatch(note -> note.getText().contains(text));
  }
}
