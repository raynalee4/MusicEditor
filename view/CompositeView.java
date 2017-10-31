package cs3500.music.view;

import cs3500.music.model.*;

import javax.sound.midi.*;
import javax.swing.Timer;
import javax.swing.JFrame;
import java.awt.event.*;

/**
 * An IMusicView with both audio and visual components
 */
public class CompositeView extends JFrame implements IMusicView {

  IMusicModel model;
  public GuiViewFrame gui;
  public MidiViewImpl midi;
  int tempo;
  int midiWait = 2000; // allows the Midi and red-line timer to wait for Gui to draw everything
  float bpm;

  ActionListener al =  new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      int beat = midi.getSeqTick();
      gui.setCurrentBeat(beat);
    }
  };
  public Timer t;

  public CompositeView(GuiViewFrame g, MidiViewImpl m) throws MidiUnavailableException {
    if (g.getModel().equals(m.getModel())) {
      this.gui = g;
      this.midi = m;
      this.bpm = midi.getBpm();
      this.model = g.getModel();
      this.tempo = g.getModel().getTempo();
    }
    else {
      throw new IllegalArgumentException("gui and midi must have the same model");
    }
  }

  @Override
  public void drawNotes() {
    int delay = (int) ((60000 / bpm));
    t = new Timer(delay, al);
    t.setInitialDelay((int) (60000 / bpm));
    gui.drawNotes();
    try {
      Thread.sleep(midiWait);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    t.start();
    midi.drawNotes();
    t.stop();
  }

  @Override
  public IMusicModel getModel() {
    return this.model;
  }



}
