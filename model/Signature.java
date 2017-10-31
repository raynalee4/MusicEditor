package cs3500.music.model;

import java.util.Objects;

/**
 * Created by rogerlevinson on 6/7/17.
 */
public class Signature {

  public int bpm;
  public int beatLength;

  /**
   * Represents a standard musical signature.
   * @param bpm         Standing for beats per measure, this is the number of beats
   *                    that make up a measure
   * @param beatLength  1/beatLength is the type of note that makes up one beat.
   *                    For example, a beatLength of 4 would mean a quarter note per beat.
   */
  public Signature(int bpm, int beatLength) {
    this.bpm = bpm;
    this.beatLength = beatLength;
  }

  /**
   * Gets the beats per minute from this signature.
   *
   * @return the beats per minute
   */
  public int getBpm() {
    return this.bpm;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    else if (o instanceof Signature) {
      Signature other = (Signature) o;
      return other.bpm == this.bpm && other.beatLength == this.beatLength;
    }
    else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(bpm, beatLength);
  }
}
