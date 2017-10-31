package cs3500.music.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;


/**
 * Interface for the music creator model.
 */
public interface IMusicModel {

  /**
   * Adds the given note to this piece of music.
   * @param n the note to add
   */
  void addNote(Note n);

  /**
   * If found, removes the given note from this music piece.
   * @param n the note to remove
   */
  void removeNote(Note n);

  /**
   * Changes an aspect of a note in this music piece.
   * @param n       the note to change
   * @param nf      the part of the note to change
   * @param change  what the note will be changed to
   */
  void editNote(Note n, NoteField nf, String change);

  /**
   * Incorporates the given music piece into this music piece.
   * @param other   The music to add to this music
   * @param insert  The bar to insert the given music at, relative to other's starting point
   */
  void combine(IMusicModel other, int insert);

  /**
   * Adds the given repeat to the repeats in this IMusicModel.
   * @param r the repeat to be added.
   */
  void addRepeat(Repeat r);

  /**
   * Sets this @code{ IMusicModel }'s range of pitches.
   */
  void setPitchRange();

  /**
   * Sets up for the @code{ printMusic } String.
   */
  void setBeatXPitch();

  /**
   * Sets the tempo to be the given int, in microseconds per beat
   * @param tempo the desired tempo
   */
  void setTempo(int tempo);

  /**
   * This.repeats gets overridden with the given list
   * @param reps this IMusicModel's repeats
   */
  void setRepeats(Map<Integer, Repeat> reps);

  /**
   * Sets a multiEnding for this IMusicModel
   * @param m  this IMusicModel's end
   */
  void setMultiEnding(MultiEnding m);

  /**
   * The length of this piece of music, in beats.
   * @return      the last beat
   */
  int length();

  /**
   * Sorts this @code{ IMusicModel }'s notes by their start time.
   */
  void sortNotes();

  /**
   * Sorts this @code{ IMusicModel }'s pitch range from lowest to highest.
   */
  void sortPitches();

  /**
   * All the notes in this piece of music.
   * @return a list of all notes in this music
   */
  List<Note> getNotes();

  /**
   * The tempo, in microseconds per beat, of this music composition
   * @return    the tempo;
   */
  int getTempo();

  /**
   * This music piece's signature.
   * @return a signature matching the one of this musical piece's
   */
  Signature getSig();

  /**
   * Returns a list of the pitches in this music's pitch range.
   * @return a list of pitches, in order from lowest to highest
   */
  ArrayList<OctavePitch> getPitchRange();

  /**
   * Gives a list of all Repeats in this IMusicModel.
   * @return a list of Repeat
   */
  Map<Integer, Repeat> getRepeats();

  /**
   * Returns the multiEnding for this IMusicModel, and null if there is not one present.
   * @return    the MultiEnding
   */
  MultiEnding getMultiEnding();

  /**
   * Returns a "grid" representing this @code{ IMusicModel } using beats and pitches.
   * Going down are the beats, and across are the pitches of notes.
   * At the start of a note, the note's pitch and start time will be denoted by an "X",
   * and the duration of the note will be marked as "|"
   * @return a visual representation of this @code{ IMusicModel } as a String.
   */
  public String printMusic();

  @Override
  public String toString();

}
