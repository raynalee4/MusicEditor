package cs3500.music.view;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import cs3500.music.model.Note;
import cs3500.music.model.Repeat;
import cs3500.music.model.OctavePitch;
import cs3500.music.model.IMusicModel;

/**
 * A dummy view that simply draws a string
 */
public class ConcreteGuiViewPanel extends JPanel {

  NoteViewPanel nvp;
  JPanel topRow; // Beats
  JPanel sideColumn; // Pitch range
  JScrollPane scroll;

  // Info needed to construct GUI.
  List<OctavePitch> ops; // List of octave pitches
  public static final int NUM_WHITE = 70;
  public static final int DX_WHITE = (GuiViewFrame.GUI_W - 50) / NUM_WHITE;
  public static final int BLACK_KEY_HEIGHT = GuiViewFrame.GUI_H / 5;
  public static final int BLACK_KEY_WIDTH = (GuiViewFrame.GUI_W - 50) / 115;

  private int height; // Height of the frame
  private int width; // Height of the width

  private int currentBeat; // Current beat of red line
  private Map<Integer, List<Note>> noteMap = new TreeMap<>();
  private boolean practice;
  private List<OctavePitch> notesClicked;


  /**
   * Default constructor for a ConcreteGuiViewPanel.
   */
  public ConcreteGuiViewPanel(IMusicModel model) {
    ops = model.getPitchRange();
    nvp = new NoteViewPanel(model);
    nvp.setMinimumSize(new Dimension(GuiViewFrame.SCROLL_W, GuiViewFrame.SCROLL_H));
    nvp.setPreferredSize(new Dimension(GuiViewFrame.NVP_W, GuiViewFrame.NVP_H));

    topRow = new ColumnHeaderView(model);
    sideColumn = new RowHeaderView(model);

    scroll = new JScrollPane(nvp, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scroll.setPreferredSize(new Dimension(GuiViewFrame.SCROLL_W, GuiViewFrame.SCROLL_H));
    scroll.setColumnHeaderView(topRow);
    scroll.setRowHeaderView(sideColumn);
    topRow.setPreferredSize(new Dimension(GuiViewFrame.NVP_W,20));
    sideColumn.setPreferredSize(new Dimension(40,GuiViewFrame.NVP_H));

    add(scroll);

    initMap(model);

    height = GuiViewFrame.GUI_H;
    width = GuiViewFrame.GUI_W;

  }

  /**
   * Initialize the tree map of notes, where the key is the current beat, and the
   * value is the list of notes at the beat.
   */
  private void initMap(IMusicModel model) {
    ArrayList<Note> notesToAdd;

    for (int i = 0; i <= model.length(); i++) {
      notesToAdd = new ArrayList<>();
      for (Note n : model.getNotes()) {
        if (i >= n.getStart() && i < n.getStart() + n.getDuration()) {
          notesToAdd.add(n);
        }
      }
      noteMap.put(i,notesToAdd);
    }
  }

  /**
   * Returns the map of notes for this GUI.
   *
   * @return the map of notes
   */
  public Map<Integer, List<Note>> getNoteMap() {
    return this.noteMap;
  }

  @Override
  public void paintComponent(Graphics g) {
    // Handle the default painting
    super.paintComponent(g);
    // Look for more documentation about the Graphics class,
    // and methods on it that may be useful
    draw(g);
  }

  /**
   * Draw the GUI.
   *
   * @param g the component's GUI.
   */
  private void draw(Graphics g) {
    paintKeyboard(g);
    repaint();
  }

  /**
   * Return the JScrollPane.
   *
   * @return the JScrollPane
   */
  JScrollPane getScrollPane() {
    return scroll;
  }

  /**
   * Get the current beat.
   *
   * @return the current beat
   */
  int getCurrentBeat() {
    return currentBeat;
  }

  /**
   * Get the note view panel.
   *
   * @return the note view panel.
   */
  NoteViewPanel getNvp() {
    return nvp;
  }

  /**
   * Sets the practice mode to the given boolean.
   * @param p indictes whether MusicEditor is in practice mode or not
   */
  public void setPractice(boolean p) {
    this.practice = p;
  }

  /**
   * In practice mode, these are the notes that have been clicked
   * @param lon
   */
  public void setNotesClicked(List<OctavePitch> lon) {
    this.notesClicked = lon;
  }

  /**
   * Paints the white keys that the red line is currently on in the note view panel.
   *
   * @param g the component's graphics
   */
  private void paintWhiteKeys(Graphics g) {
    int whiteNoteCounter; // counts the note, where the leftmost note is 0,
    // and the rightmost note is 70
    List<Note> toDraw = new ArrayList<>();
    if (practice) {
      for (Note n : noteMap.get(currentBeat)) {
        if(!notesClicked.contains(n.getPitch())) {
          toDraw.add(n);
        }
      }
    }
    else {
      toDraw = noteMap.get(currentBeat);
    }
    if (toDraw != null) {
      for (Note n : toDraw) {
        if (!n.getPitch().toString().contains("#")) {
          whiteNoteCounter = n.keyboardValue();
          updateWhiteKeys(whiteNoteCounter, g);
        }
      }
    }
  }

  /**
   * Paints the black keys that the red line is currently on in the note view panel.
   *
   * @param g the component's graphics
   */
  private void paintBlackKeys(Graphics g) {
    int blackNoteCounter; // counts the note, where the leftmost note is 0,
    // and the rightmost note is 50
    List<Note> toDraw = new ArrayList<>();
    if (practice) {
      for (Note n : noteMap.get(currentBeat)) {
        if(!notesClicked.contains(n.getPitch())) {
          toDraw.add(n);
        }
      }
    }
    else {
      toDraw = noteMap.get(currentBeat);
    }
    if (toDraw != null) {
      for (Note n : toDraw) {
        if (n.getPitch().toString().contains("#")) {
          blackNoteCounter = n.keyboardValue() - 1;
          updateBlackKeys(blackNoteCounter, g);
        }
      }
    }
  }

  /**
   * Updates the white keys on the GUI when the beat changes.
   *
   * @param whiteNoteCounter the number of black notes to count to
   * @param g the component's graphics
   */
  private void updateWhiteKeys(int whiteNoteCounter, Graphics g) {
    g.setColor(Color.ORANGE);

    g.fillRect(25 + DX_WHITE * whiteNoteCounter, height / 2, DX_WHITE, height / 2 - 100);
  }

  /**
   * Updates the black keys on the GUI when the beat changes.
   *
   * @param blackNoteCounter the number of black notes to count to
   * @param g the component's graphics
   */
  private void updateBlackKeys(int blackNoteCounter, Graphics g) {
    g.setColor(Color.ORANGE);
    int x = 25 + (DX_WHITE * 7 * (blackNoteCounter / 5));
    for (int i = 0; i < blackNoteCounter % 5; i++) {
      x += DX_WHITE;
      if (i == 1) {
        x += DX_WHITE;
      }
    }
    g.fillRect(x + DX_WHITE - 4, height / 2,
            BLACK_KEY_WIDTH, BLACK_KEY_HEIGHT);
  }


  /**
   * Paint the keyboard onto the GUI.
   *
   * @param g the component's graphics
   */
  private void paintKeyboard(Graphics g) {

    // Draw the gray rectangle.
    g.setColor(Color.lightGray);
    g.fillRect(0, height / 2, width + 1, height / 2 + 1);

    // Draw the white background for the keys.
    g.setColor(Color.white);
    g.fillRect(25, height / 2, width - 88, height / 2 - 100);

    // Outline the piano.
    g.setColor(Color.black);
    g.drawRect(25, height / 2, width - 88, height / 2 - 100);

    // Draw the white keys.
    g.setColor(Color.black);
    // DX_WHITE is the distance between each white key.
    for (int i = 0; i < DX_WHITE * NUM_WHITE; i += DX_WHITE) {
      g.drawRect(25 + i, height / 2, DX_WHITE, height / 2 - 100);
    }
    paintWhiteKeys(g);

    // Draw black keys.
    g.setColor(Color.black);
    int x = 0;
    for (int j = 0; j < 50; j++) {
      if (j % 5 == 2 || j % 5 == 0) {
        x += (DX_WHITE * 2);
      }
      else {
        x += DX_WHITE;
      }
      g.fillRect(x + DX_WHITE - 5, height / 2,
              (width - 50) / 115, BLACK_KEY_HEIGHT);
    }
    paintBlackKeys(g);
    if (practice) {
      Font font = new Font("Ariel", Font.PLAIN, 36);
      g.setFont(font);
      g.setColor(Color.black);
      g.drawString("Practice Mode Enabled", 10, GuiViewFrame.GUI_H - 30);
    }
  }

  public void setCurrentBeat(int newBeat) {
    currentBeat = newBeat;
    nvp.lineX = newBeat * GuiViewFrame.NOTE_W;
  }


  /**
   * Inner class that represents the note view of the Music Editor. It is the viewport of
   * the {@code JScrollpane} that occupies the upper half of the GUI.
   *
   */
  public class NoteViewPanel extends JPanel {

    int lineX = 0;     // Line's initial x position.
    IMusicModel model;

    /**
     * Construct a note view panel.
     *
     * @param model the model to view
     */
    public NoteViewPanel(IMusicModel model) {
      this.model = model;
    }

    /**
     * Draw all notes in the model.
     *
     * @param g the component's graphics
     */
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      drawAllNotes(g);
      drawGrid(g);
      drawRedLine(g);
    }

    public void resize() {
      setPreferredSize(new Dimension(GuiViewFrame.NVP_W, GuiViewFrame.NVP_H));
      ops = model.getPitchRange();
      sideColumn.setPreferredSize(new Dimension(40, GuiViewFrame.NVP_H));
    }

    /**
     * Draw the grid for the note view panel.
     *
     * @param g the component's graphics
     */
    private void drawAllNotes(Graphics g) {
      for (Note n : model.getNotes()) {
        drawNote(g, n);
      }
    }

    /**
     * Draw the grid for the note view panel.
     *
     * @param g the component's graphics
     */
    private void drawGrid(Graphics g) {
      int rMax;
      int cMax;
      if (model.length() * GuiViewFrame.NOTE_W < GuiViewFrame.SCROLL_W) {
        cMax = GuiViewFrame.SCROLL_W / GuiViewFrame.NOTE_W / model.getSig().bpm;
      }
      else {
        cMax = model.length() / model.getSig().bpm;
      }
      if (model.getPitchRange().size() * GuiViewFrame.NOTE_H < GuiViewFrame.SCROLL_H) {
        rMax = GuiViewFrame.SCROLL_H / GuiViewFrame.NOTE_H;
      }
      else {
        rMax = model.getPitchRange().size();
      }
      g.setColor(Color.black);
      for (int r = 0; r <= rMax; r++) {
        g.drawLine(0, r * GuiViewFrame.NOTE_H, (cMax + 1) * GuiViewFrame.MEASURE_W,
                r * GuiViewFrame.NOTE_H);
      }
      for (int c = 0; c <= cMax; c++) {
        g.drawLine(c * GuiViewFrame.MEASURE_W, 0, c * GuiViewFrame.MEASURE_W, (rMax + 1) * GuiViewFrame.NOTE_H);
      }
      for (int i : model.getRepeats().keySet()) {
        g.fillRect((i + 1) * GuiViewFrame.NOTE_W - 5, 0, 5, GuiViewFrame.NVP_H);
        g.fillRect(model.getRepeats().get(i).getGoBack() * GuiViewFrame.NOTE_W, 0, 5, GuiViewFrame.NVP_H);
      }
      if (model.getMultiEnding() != null) {
        g.fillRect(model.getMultiEnding().getBuildUp().getGoBack() * GuiViewFrame.NOTE_W, 0, 5, GuiViewFrame.NVP_H);
        for (Repeat r : model.getMultiEnding().getEndings()) {
          g.fillRect(r.getGoBack() * GuiViewFrame.NOTE_W - 5, 0, 5, GuiViewFrame.NVP_H);
        }
      }
    }

    /**
     * Draw a note onto the note view panel.
     *
     * @param g the component's graphics
     * @param n the note to draw
     */
    private void drawNote(Graphics g, Note n) {
      g.setColor(Color.CYAN);
      g.fillRect(n.getStart() * GuiViewFrame.NOTE_W,
              (model.getPitchRange().size() - model.getPitchRange().indexOf(n.getPitch()) - 1) * GuiViewFrame.NOTE_H,
              GuiViewFrame.NOTE_W * n.getDuration(), GuiViewFrame.NOTE_H);
      g.setColor(Color.BLUE);
      g.fillRect(n.getStart() * GuiViewFrame.NOTE_W,
              (model.getPitchRange().size() - model.getPitchRange().indexOf(n.getPitch()) - 1) * GuiViewFrame.NOTE_H,
              GuiViewFrame.NOTE_W, GuiViewFrame.NOTE_H);
    }

    /**
     * Draw the red line on the note view panel.
     *
     * @param g the component's graphics
     */
    private void drawRedLine(Graphics g) {
      Rectangle currentView = scroll.getViewport().getViewRect();
      int curPlace = scroll.getHorizontalScrollBar().getValue();
      int max = scroll.getHorizontalScrollBar().getMaximum();
      int toMove = currentView.width; //max / model.length() * 16;
      g.setColor(Color.red);
      g.drawLine(lineX,0,lineX,GuiViewFrame.NVP_H);
      if (lineX > currentView.x + currentView.width) {
        if (curPlace + toMove > max) {
          scroll.getHorizontalScrollBar().setValue(max);
        }
        else {
          scroll.getHorizontalScrollBar().setValue(curPlace + toMove);
        }
      }
      else if (lineX < currentView.x) {
        if (curPlace - toMove < 0) {
          scroll.getHorizontalScrollBar().setValue(0);
        }
        else {
          scroll.getHorizontalScrollBar().setValue(curPlace - toMove);
        }
      }
    }

    /**
     * Moves the red line on the note view panel.
     *
     * @param forward whether the line should be moved forwards or not
     */
    public void moveLine(boolean forward) {
      if (forward) {
        if (lineX + GuiViewFrame.NOTE_W < GuiViewFrame.NVP_W) {
          lineX += GuiViewFrame.NOTE_W;
          currentBeat++;
        }
      }

      else {
        if (lineX - GuiViewFrame.NOTE_W >= 0) {
          lineX -= GuiViewFrame.NOTE_W;
          currentBeat--;
        }
      }

    }

  }

  /**
   * Inner class that represents the column header of the {@code JScrollPane} on the upper half
   * of the GUI.
   */
  public class ColumnHeaderView extends JPanel {

    IMusicModel model;

    /**
     * Constructs a column header view.
     *
     * @param model the model to view
     */
    public ColumnHeaderView(IMusicModel model) {
      this.model = model;
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      drawBeats(g);
    }

    /**
     * Draws the beats onto the column header.
     *
     * @param g the component's graphics
     */
    private void drawBeats(Graphics g) {
      g.setColor(Color.black);
      Font font = new Font("Ariel", Font.PLAIN, 12);

      g.setFont(font);

      for (int i = 0; i <= model.length(); i++) {
        if (i % model.getSig().bpm == 0) {
          g.drawString("" + i, i * GuiViewFrame.NOTE_W, 15);
        }
      }
    }


  }

  /**
   * Inner class that represents the row header of the {@code JScrollPane} on the upper half
   * of the GUI.
   */
  public class RowHeaderView extends JPanel {

    IMusicModel model;

    /**
     * Constructs a row header view.
     *
     * @param model the model to view
     */
    public RowHeaderView(IMusicModel model) {
      this.model = model;
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);

      drawPitchRange(g);
    }

    /**
     * Draws the pitch range vertically onto the row header.
     *
     * @param g the component's graphics
     */
    public void drawPitchRange(Graphics g) {
      g.setColor(Color.black);
      Font font = new Font("Ariel", Font.PLAIN, 12);

      g.setFont(font);

      int counter = 1;

      for (int i = ops.size() - 1; i >= 0; i--) {
        OctavePitch op = ops.get(i);
        g.drawString(op.toString(), 10, counter * GuiViewFrame.NOTE_H - 1);
        counter++;
      }

    }

  }

}