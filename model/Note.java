package cs3500.music.model;

import java.util.Objects;

/**
 * Represents a note in terms of pitch and time.
 */
public class Note {

  private OctavePitch octPitch;
  private int start;
  private int duration;
  private int instrument;
  private int volume;

  /**
   * Outlines all the components of a note.
   * @param pitch     this Note's pitch
   * @param start     this Note's starting bar, where if in fraction form,
   *                  the denominator would be a factor of eight.
   *                  (eg. 6.125, 4.5, 87.25, etc)
   * @param duration  The duration of the note, also equal to a sum of eighth notes
   */
  public Note(OctavePitch pitch, int start, int duration) {
    try {
      this.setPitch(pitch);
      this.setStart(start);
      this.setDuration(duration);
      this.setInstrument(1);
      this.setVolume(100);

    }
    catch (NullPointerException n) {
      System.out.print("cannot have null parameters");
    }
  }

  /**
   * Sets this Note's pitch to the given Pitch.
   * @param p     This note's new pitch
   */
  public void setPitch(OctavePitch p) {
    this.octPitch = p;
  }


  /**
   * Sets this Note's starting point to the given stating point.
   * @param s     The note's new starting point
   * @throws IllegalArgumentException if s is not in a denomination of an eighth note
   */
  public void setStart(int s) throws IllegalArgumentException {
    if (s >= 0) {
      this.start = s;
    }
    else {
      throw new IllegalArgumentException("Cannot start on a negative beat");
    }
  }

  /**
   * Sets this MusicModel's note to start at the given start point.
   * @param d      The note's new duration
   * @throws IllegalArgumentException if d is not in a denomination of an eighth note
   */
  public void setDuration(int d) throws IllegalArgumentException {
    if (d > 0) {
      this.duration = d;
    }
    else {
      throw new IllegalArgumentException("A duration must be a positive number");
    }
  }

  public void setInstrument(int i) {
    if (i >= 0 && i <= 108) {
      this.instrument = i;
    }
    else {
      throw new IllegalArgumentException("An instrument must be between 0 and 108");
    }
  }

  public void setVolume(int v) {
    if (v >= 0 && v <= 127) {
      this.volume = v;
    }
    else {
      throw new IllegalArgumentException("Volume must be between 0 and 127");
    }
  }

  /**
   * Returns the value of this Note's pitch.
   * @return    this.pitch
   */
  public OctavePitch getPitch() {
    return new OctavePitch(octPitch.pitch, octPitch.oct);
  }

  /**
   * Returns the value of this Note's starting bar.
   * @return    this.start
   */
  public int getStart() {
    int result = start * 1;
    return result;
  }

  /**
   * Returns the value of this Note's duration, in quarter notes.
   * @return    this.duration
   */
  public int getDuration() {
    int result = duration * 1;
    return result;
  }

  /**
   * Returns the value of this note's instrument.
   * @return this instrument key
   */
  public int getInstrument() {
    int result = instrument * 1;
    return result;
  }

  /**
   * Returns the value of this note's volume.
   * @return this note's volume
   */
  public int getVolume() {
    int result = volume * 1;
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    else if (o instanceof Note) {
      Note other = (Note) o;
      return (other.octPitch.pitch.name.equals(this.octPitch.pitch.name)
              && other.octPitch.oct == this.octPitch.oct
              && other.start == this.start
              && other.duration == this.duration);
    }
    else {
      return false;
    }
  }

  @Override
  public String toString() {
    return this.octPitch.toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(octPitch.pitch.name, octPitch.oct, start, duration);
  }

  /**
   * Determines the value of this note on a 120 key keyboard.
   *
   * @return the value of this note on a 120 key keyboard
   */
  public int keyboardValue() {
    return octPitch.keyboardValue();
  }
}


