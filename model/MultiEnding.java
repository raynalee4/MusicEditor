package cs3500.music.model;

import javax.sound.midi.*;
import java.util.*;

/**
 * Created by rogerlevinson on 6/27/17.
 */
public class MultiEnding {

  private Repeat buildUp;
  private List<Repeat> endings = new ArrayList<>();

  public MultiEnding(List<Repeat> lor) {
    if (lor.size() > 2) {
      buildUp = lor.get(0);
    }
    for (int i = 1; i < lor.size(); i++) {
      if (lor.get(i).getGoBack() == lor.get(i - 1).getMark()) {
        endings.add(lor.get(i));
      }
    }
  }

  public Repeat getBuildUp() {
    return this.buildUp;
  }

  public List<Repeat> getEndings() {
    return this.endings;
  }

  public int getStart() {
    return buildUp.getGoBack();
  }
}
