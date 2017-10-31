package cs3500.music.view;

import javax.sound.midi.*;
import java.util.Map;
import java.util.HashMap;

import cs3500.music.model.*;

/**
 * A skeleton for MIDI playback
 */
public class MidiViewImpl implements IMusicView {
  private final Sequencer seq;
  private Sequence s;
  private Track t;
  private float bpm;
  IMusicModel model;
  public Map<Repeat, Boolean> repeated;

  public MidiViewImpl(IMusicModel model) throws MidiUnavailableException, InvalidMidiDataException {
    repeated = new HashMap<>();
    for (Repeat r : model.getRepeats().values()) {
      repeated.put(r, false);
    }
    bpm = 60000000 / model.getTempo();
    this.seq = MidiSystem.getSequencer();
    initSeq();
    this.model = model;
  }

  /**
   * Sets up the sequencer to play the notes in this model
   * @throws InvalidMidiDataException
   */
  public void initSeq() throws InvalidMidiDataException {
    s = new Sequence(Sequence.PPQ, 1);
    seq.setSequence(s);
    t = s.createTrack();
  }

  public int getSeqTick() {
    return (int) this.seq.getTickPosition();
  }

  /**
   * converts an OctavePitch to its Midi pitch counterpart
   * @param op    the OctavePitch to convert
   * @return the int value of the given OctavePitch
   */
  int octavePitchToMidi(OctavePitch op) {
    return (op.getOct() * 12) + ((Pitch.pitchOrder.indexOf(op.getPitch()) % 12));
  }

  /**
   * returns the tempo in Beats per Minute
   * @return this.bpm
   */
  float getBpm() {
    return this.bpm;
  }

  /**
   * Relevant classes and methods from the javax.sound.midi library:
   * <ul>
   *  <li>{@link MidiSystem#getSynthesizer()}</li>
   *  <li>{@link Synthesizer}
   *    <ul>
   *      <li>{@link Synthesizer#open()}</li>
   *      <li>{@link Synthesizer#getReceiver()}</li>
   *      <li>{@link Synthesizer#getChannels()}</li>
   *    </ul>
   *  </li>
   *  <li>{@link Receiver}
   *    <ul>
   *      <li>{@link Receiver#send(MidiMessage, long)}</li>
   *      <li>{@link Receiver#close()}</li>
   *    </ul>
   *  </li>
   *  <li>{@link MidiMessage}</li>
   *  <li>{@link ShortMessage}</li>
   *  <li>{@link MidiChannel}
   *    <ul>
   *      <li>{@link MidiChannel#getProgram()}</li>
   *      <li>{@link MidiChannel#programChange(int)}</li>
   *    </ul>
   *  </li>
   * </ul>
   * @see <a href="https://en.wikipedia.org/wiki/General_MIDI">
   *   https://en.wikipedia.org/wiki/General_MIDI
   *   </a>
   */
  public void playNote(Note n) throws InvalidMidiDataException {
    MidiMessage start = new ShortMessage(ShortMessage.NOTE_ON,
            n.getInstrument(), octavePitchToMidi(n.getPitch()), n.getVolume());
    MidiMessage stop = new ShortMessage(ShortMessage.NOTE_OFF,
            n.getInstrument(), octavePitchToMidi(n.getPitch()), n.getVolume());
    this.t.add(new MidiEvent(start, n.getStart()));
    this.t.add(new MidiEvent(stop, n.getStart() + n.getDuration()));
  }

  public void playMultiEnding(MultiEnding m) {
    while (seq.isRunning()) {
      for (Repeat r : m.getEndings()) {
        playBuildUp(m);
        playEnding(r);
      }
    }
  }

  void playBuildUp(MultiEnding m) {
    seq.setTickPosition(m.getStart());
    seq.setTempoInBPM(bpm);
    while (seq.getTickPosition() != m.getBuildUp().getMark()) {}

  }

  void playEnding(Repeat e) {
    seq.setTickPosition(e.getGoBack());
    seq.setTempoInBPM(bpm);
    while (seq.getTickPosition() != e.getMark()) { }
  }

  @Override
  public void drawNotes() {
    try {
      seq.open();
    } catch (MidiUnavailableException e) {
      e.printStackTrace();
    }
    for (Note n : model.getNotes()) {
      try {
        playNote(n);
      } catch (InvalidMidiDataException i) {
        System.out.print("Something went wrong playing the music.");
      }
    }
    startSeq();
    while (seq.isRunning()) {
      int beat = (int) seq.getTickPosition();
      if (model.getRepeats().keySet().contains(beat) &&
              !repeated.get(model.getRepeats().get(beat))) {
        seq.setTickPosition(model.getRepeats().get(beat).getGoBack());
        repeated.put(model.getRepeats().get(beat), true);
        seq.setTempoInBPM(bpm);
      }
      if (model.getMultiEnding() != null && model.getMultiEnding().getStart() == beat) {
        playMultiEnding(model.getMultiEnding());
      }
    }
  }

  /**
   * starts the sequencer at the desired tempo
   */
  public void startSeq() {
    seq.start();
    seq.setTempoInBPM(bpm);
  }

  /**
   * Stops the sequencer
   */
  public void stopSeq() {
    seq.stop();
  }

  /**
   * Closes the sequencer to reset it
   */
  public void resetSeq() {
    if (seq.isRunning()) {
      seq.stop();
    }
    seq.close();
    try {
      initSeq();
    } catch (InvalidMidiDataException e) {
      e.printStackTrace();
    }
  }

  /**
   *
   * @param tick
   */
  public void setSeqTick(int tick) {
    if (tick < model.length()) {
      seq.setTickPosition(tick);
      seq.setTempoInBPM(bpm);
    }
  }

  public void setBpm(int b) {
    this.bpm = b;
    seq.setTempoInBPM(bpm);
  }

  @Override
  public IMusicModel getModel() {
    return this.model;
  }
}
