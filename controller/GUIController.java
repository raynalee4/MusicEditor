package cs3500.music.controller;


import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;

import cs3500.music.model.*;
import cs3500.music.view.*;

/**
 * Represents a Controller for the GUI View.
 */
public class GUIController implements IMusicController {
  private GuiViewFrame gui;

  /**
   * Constructs a GUIController, given a MusicModel.
   *
   * @param gui view to control
   * @throws IllegalArgumentException if the model is null
   */
  public GUIController(GuiViewFrame gui)
          throws IllegalArgumentException {
    Objects.requireNonNull(gui);
    this.gui = gui;
    gui.setPractice(false);
  }



  /**
   * Initializes and configures the key listener.
   */
  protected void initKeyboardListener() {
    // Create maps for each of the types of key events (pressing a key,
    // releasing a key, typing a key).

    Map<Integer,Runnable> keyPressedMap = new HashMap<>();
    Map<Integer,Runnable> keyReleasedMap = new HashMap<>();
    Map<Character,Runnable> keyTypedMap = new HashMap<>();

    // Pressing home or end will respectively jump to the start of
    // the composition, or jump to the end of the composition.
    keyPressedMap.put(37, new ScrollLeft()); // left arrow
    keyPressedMap.put(39, new ScrollRight()); // right arrow
    // Pressing s will start the song, and pressing d will
    // stop the song.
    keyPressedMap.put(83, new StartSong()); // s
    keyPressedMap.put(68, new StopSong()); // d

    KeyboardListener listener = new KeyboardListener();
    listener.setKeyPressedMap(keyPressedMap);
    listener.setKeyReleasedMap(keyReleasedMap);
    listener.setKeyTypedMap(keyTypedMap);

    gui.addKeyListener(listener);
  }

  /**
   * Initializes and configures the mouse listener.
   */
  protected void initMouseListener() {
    Map<Integer,Runnable> mouseClickedMap = new HashMap<>();
    Map<Integer,Runnable> mousePressedMap = new HashMap<>();
    Map<Integer,Runnable> mouseReleasedMap = new HashMap<>();

    MouseListener listener = new MouseListener();

    mousePressedMap.put(1, new AddNote(gui, listener));

    listener.setMouseClickedMap(mouseClickedMap);
    listener.setMousePressedMap(mousePressedMap);
    listener.setMouseReleasedMap(mouseReleasedMap);

    gui.addMouseListener(listener);
  }

  @Override
  public void launch() {
    initKeyboardListener(); // initiate the keyboard listener
    initMouseListener(); // initiate the mouse listener
    gui.drawNotes();
  }

  /**
   * Runnable function object that moves the red line right when
   * the right key is pressed.
   */
  class ScrollRight implements Runnable {

    @Override
    public void run() {
      gui.moveLine(true);
    }
  }

  /**
   * Runnable function object that moves the red line left when
   * the left key is pressed.
   */
  class ScrollLeft implements Runnable {

    @Override
    public void run() {
      gui.moveLine(false);
    }
  }

  /**
   * Plays the song from the beginning.
   */
  class StartSong implements Runnable {

    @Override
    public void run() {
      gui.setCurrentBeat(0);
    }
  }

  /**
   * Ends the song.
   */
  class StopSong implements Runnable {

    @Override
    public void run() {
      gui.setCurrentBeat(gui.getModel().length());
    }
  }




  class AddNote implements Runnable {

    IMusicModel model;
    Note note;
    GuiViewFrame gui;
    MouseListener listener;
    ActionListener al = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        model.editNote(note, NoteField.DURATION, Integer.toString(note.getDuration() + 1));
        gui.moveLine(true);
      }
    };
    public Timer t;

    /**
     * Constructor for a runnable AddExtendedNote function object.
     *
     * @param gui the gui view frame to which to add this note
     * @param listener the mouse listener being used
     */
    public AddNote(GuiViewFrame gui, MouseListener listener) {
      this.gui = gui;
      this.listener = listener;
      this.model = gui.getModel();
      t = new Timer(model.getTempo() / 1000, al);
    }

    @Override
    public void run() {
      int x = listener.getX();
      int y = listener.getY();
      int numWhiteKeys = 70;
      int curBeat = gui.getCurrentBeat(); // x value of red line
      int octWidth = ConcreteGuiViewPanel.DX_WHITE * 7;
      int dxNote = ConcreteGuiViewPanel.DX_WHITE;
      int modX = (x - 25) % octWidth;
      boolean isSharp = false;

      if (x < 25 || x > 25 + (dxNote * numWhiteKeys)
              || y < GuiViewFrame.GUI_H / 2 || y > GuiViewFrame.GUI_H - 100) {
        return ;
      }

      int keyStart = (dxNote - 4);
      for (int i = 0; i < 5; i++) {
        if (modX >= keyStart && modX <= keyStart + ConcreteGuiViewPanel.BLACK_KEY_WIDTH
                && y <= (GuiViewFrame.GUI_H + 30) / 2 + ConcreteGuiViewPanel.BLACK_KEY_HEIGHT) {
          isSharp = true;
        }
        keyStart += dxNote;
        if (i == 1) {
          keyStart += dxNote;
        }
      }

      OctavePitch p;

      int numInOct = -1;
      int keyVal;
      if (isSharp) {
        for (int i = 0; i < modX; i += dxNote) {
          numInOct++;
          if (i / dxNote == 2) {
            i += dxNote;
          }
        }
        keyVal = ((x - 25) / octWidth * 5) + numInOct;
        p = getPitchFromKeyboard(keyVal, true);
      }
      else {
        for (int i = 0; i < modX; i += dxNote) {
          numInOct++;
        }
        keyVal = ((x - 25) / octWidth * 7) + numInOct;
        p = getPitchFromKeyboard(keyVal, false);
      }

      t.start();
      Map<Integer, Runnable> mrm = listener.getMouseReleasedMap();
      mrm.put(1, new StopAddNote(t));
      listener.setMouseReleasedMap(mrm);
      note = new Note(p, curBeat, 1);
      model.addNote(note);
      gui.setNVPSize();
    }




    /**
     * Returns the OctavePitch for the Note that is added
     * @param kv    the number black or white key this note holds
     * @param sharp is this a sharp key
     * @return the corresponding OctavePitch
     */
    public OctavePitch getPitchFromKeyboard(int kv, boolean sharp) {
      int oct;
      Pitch p;
      if (sharp) {
        p = Pitch.CSHARP;
        for (int i = 2, j = 0; j < kv % 5; i++) {
          if (Pitch.pitchOrder.get(i).name.contains("#")) {
            p = Pitch.pitchOrder.get(i);
            j++;
          }
        }
        oct = kv / 5;
      }
      else {
        p = Pitch.C;
        for (int i = 1, j = 0; j < kv % 7; i++) {
          if (!Pitch.pitchOrder.get(i).name.contains("#")) {
            p = Pitch.pitchOrder.get(i);
            j++;
          }
        }

        oct = kv / 7;
      }
      return new OctavePitch(p, oct);
    }
  }

  class StopAddNote implements Runnable {

    Timer t;

    public StopAddNote(Timer t) {
      this.t = t;
    }

    @Override
    public void run() {
      t.stop();
    }
  }



}
