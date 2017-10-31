package cs3500.music.model;

import java.util.Comparator;

/**
 * Compares two {@code OctavePitch} to determine which is lower and which is higher.
 */
public class ByPitch implements Comparator<OctavePitch> {

  @Override
  public int compare(OctavePitch p1, OctavePitch p2) {
    if (p1.oct == p2.oct) {
      return Pitch.pitchOrder.indexOf(p1.pitch) - Pitch.pitchOrder.indexOf(p2.pitch);
    }
    else {
      return p1.oct - p2.oct;
    }
  }
}
