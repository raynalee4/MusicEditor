package cs3500.music.view;

import cs3500.music.model.*;

import java.awt.*;
import java.util.List;
import java.util.Map;

import javax.swing.*;

/**
 * A skeleton Frame (i.e., a window) in Swing
 */
public class GuiViewFrame extends javax.swing.JFrame implements IMusicView {

  private final ConcreteGuiViewPanel displayPanel;
  IMusicModel model;

  public static final int NOTE_H = 15;
  public static final int NOTE_W = 20;

  public static int NVP_H;
  public static final int GUI_H = 600;
  public static final int GUI_W = 1000;
  public static final int SCROLL_W = GUI_W;
  public static final int SCROLL_H = GUI_H / 2;
  public static int NVP_W;
  public static int MEASURE_W;

  /**
   * Creates new GuiView
   */
  public GuiViewFrame(IMusicModel model) {
    this.model = model;
    NVP_H = model.getPitchRange().size() * NOTE_H;
    MEASURE_W = NOTE_W * model.getSig().getBpm();
    NVP_W = (model.length() + 1) * NOTE_W;
    this.displayPanel = new ConcreteGuiViewPanel(model);
    this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    this.add(displayPanel);
    this.setResizable(false);
    this.pack();
  }

  /**
   * Moves the red line in the note view panel.
   *
   * @param forward whether the line is being moved forwards (right) or not
   */
  public void moveLine(boolean forward) {
    this.displayPanel.nvp.moveLine(forward);
  }


  /**
   * Gets the current beat (at which the red line is located).
   *
   * @return the current beat
   */
  public int getCurrentBeat() {
    return displayPanel.getCurrentBeat();
  }

  /**
   * Gets the display panel's NoteViewPanel
   *
   * @return the note view panel
   */
  public ConcreteGuiViewPanel.NoteViewPanel getNvp() {
    return displayPanel.getNvp();
  }
  /**
   * Get the JScrollPane.
   *
   * @return the JScrollPane
   */
  public JScrollPane getScrollPane() {
    return displayPanel.getScrollPane();
  }

  /**
   * Resizes the Note View Frame to accommodate new notes.
   */
  public void setNVPSize() {
    NVP_H = model.getPitchRange().size() * NOTE_H;
    NVP_W = (model.length() + 1) * NOTE_W;
    this.getNvp().resize();
  }

  /**
   * sets this Gui's display panel's current beat
   * @return
   */
  public void setCurrentBeat(int newBeat) {
    displayPanel.setCurrentBeat(newBeat);
  }

  /**
   * Returns the map of notes for this GUI.
   *
   * @return the map of notes
   */
  public Map<Integer, java.util.List<Note>> getNoteMap() {
    return this.displayPanel.getNoteMap();
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(GUI_W, GUI_H);
  }

  /**
   * Sets the practice mode to the given boolean.
   * @param p indictes whether MusicEditor is in practice mode or not
   */
  public void setPractice(boolean p) {
    this.displayPanel.setPractice(p);
  }

  /**
   * In practice mode, these are the notes that have been clicked
   * @param lon
   */
  public void setNotesClicked(List<OctavePitch> lon) {
    displayPanel.setNotesClicked(lon);
  }

  @Override
  public void drawNotes() {
    this.setVisible(true);
  }

  @Override
  public IMusicModel getModel() {
    return this.model;
  }
}