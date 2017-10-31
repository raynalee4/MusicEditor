package cs3500.music.model;

import java.util.Comparator;

/**
 * Compares two {@code Note} to determine which one comes first in the {@code MusicModel}
 */
public class ByStart implements Comparator<Note> {

  @Override
  public int compare(Note o1, Note o2) {
    return (o1.getStart() - o2.getStart());
  }
}
