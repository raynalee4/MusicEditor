package cs3500.music.model;

/**
 * Indicates a musical repeat.
 */
public class Repeat {


  private int goBack;
  private int mark;

  /**
   * Constructs a new repeat, which will repeat the notes in between the two values.
   * @param mark the end of the repeated section, which will signal when to repeat
   * @param goBack where the repeated music will start
   */
  public Repeat(int goBack, int mark) {
    if (mark > goBack && goBack >= 0) {
      this.mark = mark;
      this.goBack = goBack;
    }
  }

  /**
   * Gets this Repeat's mark.
   * @return  end of the repeat
   */
  public int getMark() {
    return this.mark;
  }

  /**
   * Gets this Repeat's goBack.
   * @return beginning of repeat
   */
  public int getGoBack() {
    return this.goBack;
  }


}
