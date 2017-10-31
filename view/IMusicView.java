package cs3500.music.view;

import cs3500.music.model.*;

/**
 * Outlines the relevant methods for portraying a musical composition.
 * Can either play the music, return a String console view, or a Java Swing
 * application view.
 */
public interface IMusicView {

  /**
   * Renders the notes in this piece of music.
   */
  void drawNotes();

  /**
   * Gets the {@link IMusicModel} associated with this view
   * @return the model
   */
  IMusicModel getModel();

}
