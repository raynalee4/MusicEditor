package cs3500.music.model;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a musical pitch.
 */
public enum Pitch {

  A("A"), ASHARP("A#"), B("B"), C("C"), CSHARP("C#"), D("D"), DSHARP("D#"), E("E"), F("F"),
  FSHARP("F#"), G("G"), GSHARP("G#");

  public final String name;
  private static Pitch[] vals = values();

  Pitch(String s) {

    this.name = s;
  }

  // Gives the Pitch's as a list of Pitch's, so they can be accessed statically outside
  // of the class.
  public static final List<Pitch> pitchOrder  =
      new ArrayList<>(Arrays.asList(C, CSHARP, D, DSHARP, E, F, FSHARP, G, GSHARP, A, ASHARP, B));


  /**
   * Gets and returns the next item in the enumeration (e.g., A -> ASHARP).
   *
   * @return the next item in the enumeration
   */
  public Pitch next() {
    return vals[(this.ordinal() + 1) % vals.length];
  }

}
