package cs3500.music.controller;

import java.awt.event.MouseEvent;
import java.util.Map;

/**
 * Handles Mouse events.
 */
public class MouseListener implements java.awt.event.MouseListener {
  private Map<Integer,Runnable> mouseClickedMap;
  private Map<Integer,Runnable> mousePressedMap;
  private Map<Integer,Runnable> mouseReleasedMap;
  private int x; // x of mouse
  private int y; // y of mouse


  /**
   * Get the mouse click's x value.
   *
   * @return the mouse click's x value
   */
  int getX() {
    return x;
  }

  /**
   * Get the mouse click's y value.
   *
   * @return the mouse click's y value.
   */
  int getY() {
    return y;
  }

  /**
   * Set the mouse clicked map.
   *
   * @param mcm the mouse click map to set
   */
  void setMouseClickedMap(Map<Integer,Runnable> mcm) {
    mouseClickedMap = mcm;
  }

  /**
   * Set the mouse pressed map.
   *
   * @param mpm the mouse pressed map to set
   */
  void setMousePressedMap(Map<Integer,Runnable> mpm) {
    mousePressedMap = mpm;
  }

  /**
   * Set the mouse released map.
   *
   * @param mrm the mouse released map to set
   */
  void setMouseReleasedMap(Map<Integer,Runnable> mrm) {
    mouseReleasedMap = mrm;
  }

  Map<Integer,Runnable> getMouseReleasedMap() {
    return this.mouseReleasedMap;
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    x = e.getX();
    y = e.getY();
    if (mouseClickedMap.containsKey(e.getButton())) {
      mouseClickedMap.get(e.getButton()).run();
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
    x = e.getX();
    y = e.getY();
    if (mousePressedMap.containsKey(e.getButton())) {
      mousePressedMap.get(e.getButton()).run();
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    x = e.getX();
    y = e.getY();

    if (mouseReleasedMap.containsKey(e.getButton())) {
      mouseReleasedMap.get(e.getButton()).run();
    }
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    // does not apply
  }

  @Override
  public void mouseExited(MouseEvent e) {
    // does not apply
  }
}