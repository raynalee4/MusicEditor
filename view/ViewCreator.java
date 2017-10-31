package cs3500.music.view;

import javax.sound.midi.*;

import cs3500.music.model.IMusicModel;

/**
 * Represents a view creator; has a single static method that takes in a String
 * and returns an {@code IMusicView} based on said String.
 */
public class ViewCreator {

  /**
   * Creates and returns an IMusicView based on given string.
   *
   * @param type the type of IMusicView to create and return
   * @param model the IMusicModel to view
   * @return the type of IMusicView
   * @throws IllegalArgumentException if the type is invalid
   */
  public static IMusicView create(String type, IMusicModel model) throws IllegalArgumentException {

    switch (type) {
      case "console":
        return new ConsoleView(model);

      case "midi":
        try {
          return new MidiViewImpl(model);
        } catch (MidiUnavailableException | InvalidMidiDataException e) {
          e.printStackTrace();
        }

      case "visual":
        return new GuiViewFrame(model);
      case "full":
        try {
          return new CompositeView(new GuiViewFrame(model), new MidiViewImpl(model));
        }
        catch (MidiUnavailableException | InvalidMidiDataException e) {
          e.printStackTrace();
        }
      default:
        throw new IllegalArgumentException("Invalid view type.");
    }

  }
}
