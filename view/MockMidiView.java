package cs3500.music.view;

import cs3500.music.model.*;
import java.util.HashMap;
import java.util.List;

import javax.sound.midi.*;

/**
 * A mock MidiDevice to test our {@link MidiViewImpl} code
 */
public class MockMidiView extends MidiViewImpl {

  IMusicModel model;
  MockReceive mockReceiver;

  public MockMidiView(IMusicModel model) throws MidiUnavailableException, InvalidMidiDataException {
    super(model);
    this.model = model;
    mockReceiver = new MockReceive();
  }

  public MockReceive getReceiver() {
    return this.mockReceiver;
  }

  @Override
  public void playNote(Note n) throws InvalidMidiDataException {
    MidiMessage start = new ShortMessage(ShortMessage.NOTE_ON,
            n.getInstrument(), octavePitchToMidi(n.getPitch()), n.getVolume());
    MidiMessage stop = new ShortMessage(ShortMessage.NOTE_OFF,
            n.getInstrument(), octavePitchToMidi(n.getPitch()), n.getVolume());
    mockReceiver.send(start, n.getStart() * model.getTempo());
    mockReceiver.send(stop, (n.getStart() + n.getDuration()) * model.getTempo());

  }

  public final class MockReceive implements Receiver {

    StringBuilder result;

    public MockReceive() {
      result = new StringBuilder();
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
      if (message instanceof ShortMessage) {
        ShortMessage s = (ShortMessage) message;
        if (s.getCommand() == ShortMessage.NOTE_ON) {
          result.append(String.format("on %d %d %d %d\n",
                  s.getChannel(), s.getData1(), s.getData2(), timeStamp));
        }
        if (s.getCommand() == ShortMessage.NOTE_OFF) {
          result.append(String.format("off %d %d %d %d\n",
                  s.getChannel(), s.getData1(), s.getData2(), timeStamp));
        }
      }
    }

    @Override
    public void close() {
      System.out.print(result.toString());
    }

    @Override
    public String toString() {
      return this.result.toString();
    }
  }
}
