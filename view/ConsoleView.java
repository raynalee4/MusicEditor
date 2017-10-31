package cs3500.music.view;

import cs3500.music.model.IMusicModel;

/**
 * Renders the console String in the IMusicModel
 */
public class ConsoleView implements IMusicView{

  IMusicModel model;

  public ConsoleView(IMusicModel model) {
    this.model = model;
  }

  @Override
  public String toString() {
    return model.printMusic();
  }

  @Override
  public void drawNotes() {
    System.out.print(model.printMusic());
  }

  @Override
  public IMusicModel getModel() {
    return this.model;
  }

}
