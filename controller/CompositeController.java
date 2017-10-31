package cs3500.music.controller;

import cs3500.music.model.*;
import cs3500.music.view.*;

import javax.swing.Timer;
import javax.sound.midi.*;
import java.awt.event.*;
import java.util.*;

/**
 * A controller for playing midi and scanning Gui concurrently.
 */
public class CompositeController extends GUIController {

  CompositeView comp;
  private boolean pause;
  private boolean practiceMode;
  private List<OctavePitch> notesClicked;

  /**
   * Constructs a {@link CompositeView}, given a {@link GuiViewFrame}.
   *
   * @param gui the gui view to control
   * @throws IllegalArgumentException if the model is null
   */
  public CompositeController(GuiViewFrame gui) throws IllegalArgumentException {
    super(gui);
    this.practiceMode = false;
    this.notesClicked = new ArrayList<>();
    try {
      this.comp = new CompositeView(gui, new MidiViewImpl(gui.getModel()));
    } catch (MidiUnavailableException | InvalidMidiDataException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void initKeyboardListener() {
    // Create maps for each of the types of key events (pressing a key,
    // releasing a key, typing a key).
    Map<Integer,Runnable> keyPressedMap = new HashMap<>();
    Map<Integer,Runnable> keyReleasedMap = new HashMap<>();
    Map<Character,Runnable> keyTypedMap = new HashMap<>();

    keyPressedMap.put(37, new CompScrollLeft()); // left arrow
    keyPressedMap.put(39, new CompScrollRight()); // right arrow
    // Since there are no home or end buttons on many laptops,
    // Pressing s will start the song, and pressing d will stop the song.
    keyPressedMap.put(83, new CompStartSong()); // s
    keyPressedMap.put(68, new CompStopSong()); // d
    keyPressedMap.put(80, new StartPracticeMode()); // p
    keyPressedMap.put(81, new StopPracticeMode()); // q
    // Pressing the spacebar will pause or resume the song
    keyPressedMap.put(32, new Pause()); // spacebar

    keyPressedMap.put(87, new TempoDown()); // w
    keyPressedMap.put(69, new TempoUp()); // e

    KeyboardListener listener = new KeyboardListener();
    listener.setKeyPressedMap(keyPressedMap);
    listener.setKeyReleasedMap(keyReleasedMap);
    listener.setKeyTypedMap(keyTypedMap);

    comp.gui.addKeyListener(listener);
  }

  protected void initMouseListener() {
    Map<Integer, Runnable> mouseClickedMap = new HashMap<>();
    Map<Integer, Runnable> mousePressedMap = new HashMap<>();
    Map<Integer, Runnable> mouseReleasedMap = new HashMap<>();

    MouseListener listener = new MouseListener();

    mousePressedMap.put(1, new CompAddNote(comp.gui, listener));

    listener.setMouseClickedMap(mouseClickedMap);
    listener.setMousePressedMap(mousePressedMap);
    listener.setMouseReleasedMap(mouseReleasedMap);

    comp.gui.addMouseListener(listener);
  }

  @Override
  public void launch() {
    initKeyboardListener(); // initiate the keyboard listener
    initMouseListener(); // initiate the mouse listener
    comp.drawNotes();
  }

  /**
   * Runnable function object that moves the red line right when
   * the right key is pressed.
   */
  class CompScrollRight extends ScrollRight implements Runnable {

    @Override
    public void run() {
      super.run();
      notesClicked.clear();
      comp.gui.setNotesClicked(new ArrayList<>());
      comp.midi.setSeqTick(comp.midi.getSeqTick() + 1);
    }
  }

  /**
   * Runnable function object that moves the red line left when
   * the left key is pressed.
   */
  class CompScrollLeft extends ScrollLeft implements Runnable {

    @Override
    public void run() {
      super.run();
      notesClicked.clear();
      comp.gui.setNotesClicked(new ArrayList<>());
      comp.midi.setSeqTick(comp.midi.getSeqTick() - 1);
    }
  }


  /**
   * Pauses the composite view.
   */
  class Pause implements Runnable {

    @Override
    public void run() {
      if (!pause) {
        comp.midi.stopSeq();
        comp.t.stop();
      }
      else {
        comp.midi.startSeq();
        comp.t.restart();
      }
      pause = !pause;
    }
  }


  /**
   * Plays the song from the beginning.
   */
  class CompStartSong extends StartSong implements Runnable {

    @Override
    public void run() {
      super.run();
      comp.midi.resetSeq();
      comp.midi.drawNotes();
      if (pause) {
        comp.midi.stopSeq();
      }
      else {
        try {
          Thread.sleep(250); // synchronizes the start of the red line and midi playing
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Ends the song.
   */
  class CompStopSong extends StopSong implements Runnable {

    @Override
    public void run() {
      super.run();
      comp.midi.stopSeq();
    }
  }

  /**
   * Starts practice mode. Practice mode is quit by using the 'q' character.
   */
  class StartPracticeMode implements Runnable {

    @Override
    public void run() {
      if (pause) { // only start practice mode if paused

        practiceMode = true;
        comp.gui.setPractice(true);
        notesClicked.clear();
        comp.gui.setNotesClicked(new ArrayList<>());
      }
    }
  }

  /**
   * Stops practice mode, and clears list of notes to check. Practice mode can
   * also be ended by pressing 's' to move the red line back to the beginning
   * of the song, or 'd' to move it to the end.
   */
  class StopPracticeMode implements Runnable {

    @Override
    public void run() {
      if (pause) { // only attempt to quit practice mode if paused
        practiceMode = false;
        comp.gui.setPractice(false);
        notesClicked.clear();
        comp.gui.setNotesClicked(new ArrayList<>());
      }
    }
  }

  /**
   * Runnable function object that adds a note to the GUI with a mouse click
   * on the keyboard. If practice mode is on, this will add the note to the list
   * of notes that a user must click.
   * <p>
   * Practice mode works as such: if a correct key is pressed with the left mouse
   * button and it hasn't already been clicked on, it will be added to the list
   * of notes to click. Once the size of the list of notes to click is the same
   * size as the number of notes at the current beat, we know all the notes to
   * click have been clicked; thus, we call run on a ScrollRight runnable, and
   * clear the list of notes to click for the next beat.
   * </p>
   */
  class CompAddNote implements Runnable {
    GuiViewFrame gui;
    IMusicModel model;
    MouseListener listener;
    Runnable scrollRight = new ScrollRight();
    Note note;
    ActionListener al = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        model.editNote(note, NoteField.DURATION, Integer.toString(note.getDuration() + 1));
        gui.moveLine(true);
      }
    };
    public Timer t;

    /**
     * Constructor for a runnable AddNote function object.
     *
     * @param listener the mouse listener being used
     */
    CompAddNote(GuiViewFrame gui, MouseListener listener) {
      this.listener = listener;
      this.gui = gui;
      this.model = gui.getModel();
      this.listener = listener;
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

      note = new Note(p, curBeat, 1);

      // If we are not in practice mode, add the note to the mode, and
      // resize the GUI if necessary.
      if (!practiceMode) {
        model.addNote(note);
        gui.setNVPSize();
      }

      // If we are in practice mode, check to see if the note matches any notes
      // at the current beat, and if it hasn't yet been added to the list of notes
      // to click at that beat; if both conditions are true, add it to the list of
      // notes to click.
      else {
        if (noteMatch(p, curBeat) && !notesClicked.contains(p)) {
          notesClicked.add(p);
          gui.setNotesClicked(notesClicked);
        }
        // If the size of the notesClicked list is equal to the size of the map of
        // notes at the current beat, scroll right by one to the next beat, and
        // clear the list of notes to click.
        if (notesClicked.size() == gui.getNoteMap().get(curBeat).size()) {
          comp.midi.startSeq();
          try {
            Thread.sleep(comp.midi.getModel().getTempo() / 1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          comp.midi.stopSeq();
          if (model.getRepeats().containsKey(curBeat) &&
                  !comp.midi.repeated.get(model.getRepeats().get(curBeat))) {
            gui.setCurrentBeat(model.getRepeats().get(curBeat).getGoBack());
            comp.midi.repeated.put(model.getRepeats().get(curBeat), true);
          }
          if (model.getMultiEnding() != null && model.getMultiEnding().getBuildUp().getGoBack() == curBeat) {
            comp.midi.playMultiEnding(model.getMultiEnding());
          }
          else {
            scrollRight.run();
          }
          notesClicked.clear();
          gui.setNotesClicked(new ArrayList<>());
        }
      }

      t = new Timer(comp.midi.getModel().getTempo() / 1000, al);
      t.start();
      Map<Integer, Runnable> mrm = listener.getMouseReleasedMap();
      mrm.put(1, new StopAddNote(t));
      listener.setMouseReleasedMap(mrm);


    }
    /**
     * Determines whether pitch of the given note, and thus the note itself,
     * matches any of the note's at the current beat.
     *
     * @param p the note's pitch
     * @param curBeat the current beat
     * @return whether the given note matches the note at the current beat
     */
    public boolean noteMatch(OctavePitch p, int curBeat) {

      for (Note n : gui.getNoteMap().get(curBeat)) {
        if (n.getPitch().equals(p)) {
          return true;
        }
      }

      return false;
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




  /**
   * Increases the tempo of the song by decreasing 1 ms/beat
   */
  class TempoUp implements Runnable {

    @Override
    public void run() {
      comp.getModel().setTempo(comp.getModel().getTempo() - 1000);
      comp.midi.setBpm(60000000 / comp.getModel().getTempo());
    }
  }

  /**
   * Decreases the tempo of the song by adding 1 ms/beat
   */
  class TempoDown implements Runnable {

    @Override
    public void run() {
      comp.getModel().setTempo(comp.getModel().getTempo() + 1000);
      comp.midi.setBpm(60000000 / comp.getModel().getTempo());
    }
  }

}
