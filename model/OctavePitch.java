package cs3500.music.model;

import java.util.Objects;

/**
 * Represents a pitch with a specific octave.
 */
public class OctavePitch {

  Pitch pitch;
  int oct;


  /**
   * Represents a specific pitch in a certain octave.
   * @param p       The Pitch
   * @param oct     The octave
   */
  public OctavePitch(Pitch p, int oct) {
    this.pitch = p;
    if (oct >= 0 && oct <= 10) {
      this.oct = oct;
    }
    else {
      throw new IllegalArgumentException("octave must be between 0 and 10, inclusive");
    }
  }

  /**
   * This OctavePitch's pitch.
   * @return the pitch
   */
  public Pitch getPitch() {
    return this.pitch;
  }

  /**
   * This OctavePitch's octave.
   * @return the oct
   */
  public int getOct() {
    int result = oct * 1;
    return result;
  }

  /**
   * Determines the value of this note on a 120 key keyboard, relative to
   * the type of key that it is (black vs. white).
   *
   * @return the value of this note on a 120 key keyboard
   */
  int keyboardValue() {
    OctavePitch lowestNote = new OctavePitch(Pitch.C,0);
    int value = 0;

    boolean isSharp = this.toString().contains("#");

    // handle black keys
    if (isSharp) {
      int noteDistance = this.distance(lowestNote);
      for (int i = 0; i < noteDistance; i++) {
        if (lowestNote.increment().toString().contains("#")) {
          value++;
        }
        lowestNote = lowestNote.increment();
      }
    }

    // handle white keys
    else {
      int noteDistance = this.distance(lowestNote);
      for (int i = 0; i < noteDistance; i++) {
        if (!lowestNote.increment().toString().contains("#")) {
          value++;
        }
        lowestNote = lowestNote.increment();
      }
    }

    return value;
  }

  /**
   * 
   * @param compare
   * @return
   */
  private int distance(OctavePitch compare) {
    int dist = 0;
    if (this.oct == compare.oct) {
      dist = Math.abs(Pitch.pitchOrder.indexOf(this.pitch) - Pitch.pitchOrder.indexOf(compare.pitch));

    }
    else if (this.oct > compare.oct) {
      OctavePitch op = compare;
      while (!op.equals(this)) {
        op = op.increment();
        dist++;
      }
    }
    else {
      OctavePitch op = this;
      while (!op.equals(compare)) {
        op = op.increment();
        dist++;
      }
    }
    return dist;
  }

  /**
   * Increment an OctavePitch by one.
   *
   * @return an OctavePitch that is 1 greater in value than the one
   *         it was calld on
   */
  public OctavePitch increment() {

    OctavePitch op;

    if (this.pitch == Pitch.B) {
      op = new OctavePitch(pitch.next(),oct + 1);
    }
    else {
      op = new OctavePitch(pitch.next(),oct);
    }

    return op;

  }

  @Override
  public String toString() {
    return pitch.name + oct;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    else if (o instanceof OctavePitch) {
      OctavePitch other = (OctavePitch) o;
      return other.pitch.name.equals(this.pitch.name) && other.oct == this.oct;
    }
    else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(pitch.name, oct);
  }
}
