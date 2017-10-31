package cs3500.music.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;

/**
 * Represents a KeyboardListener that has maps for key presses, key
 * releases, and key types. Each key is a KeyCode or KeyChar, and their
 * respective values are function objects that implement Runnable; when
 * the keys are pressed, the function object will run.
 */
public class KeyboardListener implements KeyListener {
  private Map<Integer,Runnable> keyPressedMap;
  private Map<Integer,Runnable> keyReleasedMap;
  private Map<Character,Runnable> keyTypedMap;


  /**
   * Set the map for key presses. The key is a KeyCode, and the values
   * are runnable function objects.
   *
   * @param kpm the map of key presses
   */
  void setKeyPressedMap(Map<Integer,Runnable> kpm) {
    keyPressedMap = kpm;
  }

  /**
   * Set the map for key releases. The key is a KeyCode, and the values
   * are runnable function objects.
   *
   * @param krm the map of key releases
   */
  void setKeyReleasedMap(Map<Integer,Runnable> krm) {
    keyReleasedMap = krm;
  }

  /**
   * Set the map for key types. The key is a KeyChar, and the values
   * are runnable function objects.
   *
   * @param ktm the map of key types
   */
  void setKeyTypedMap(Map<Character,Runnable> ktm) {
    keyTypedMap = ktm;
  }

  @Override
  public void keyTyped(KeyEvent e) {
    if (keyTypedMap.containsKey(e.getKeyChar())) {
      keyTypedMap.get(e.getKeyChar()).run();
    }
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (keyPressedMap.containsKey(e.getKeyCode())) {
      keyPressedMap.get(e.getKeyCode()).run();
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    if (keyReleasedMap.containsKey(e.getKeyCode())) {
      keyReleasedMap.get(e.getKeyCode()).run();
    }
  }

}
