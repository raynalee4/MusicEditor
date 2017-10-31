package cs3500.music.model;

import java.util.*;

import cs3500.music.util.*;


/**
 * Class for modeling the music creator. Can manipulate the notes in a piece of music,
 * and also can print out a String view of the musical composition.
 */
public final class MusicModel implements IMusicModel {

  private List<Note> notes;
  private Signature sig;
  private int tempo;
  private List<OctavePitch> pitchRange;
  private ArrayList<ArrayList<Integer>> beatXPitch;
  private Map<String, Pitch> pitchNames = new HashMap<>();
  private Map<Integer, Repeat> repeats;
  private MultiEnding end;


  /**
   * Represents a mutable @code{ IMusicModel }, where everything is changed using the
   * list of notes.
   */
  public MusicModel() {
    this.notes = new ArrayList<>();
    this.sig = new Signature(4, 4);
    this.pitchRange = new ArrayList<>();
    this.beatXPitch = new ArrayList<>();
    this.repeats = new HashMap<>();
    this.initBeatXPitch();
    for (Pitch p : Pitch.pitchOrder) {
      pitchNames.put(p.name, p);
    }
  }

  /**
   * Represents a mutable {@code IMusicModel}, where everything is changed using the
   * list of notes.
   *
   * @param notes     The notes in this piece of music
   * @param sig       This music piece's signature, or 4, 4 as a default if set to null
   */
  public MusicModel(List<Note> notes, Signature sig) {
    if (notes == null) {
      this.notes = new ArrayList<>();
    }
    else {
      this.notes = notes;
    }
    if (sig == null) {
      this.sig = new Signature(4, 4);
    }
    else {
      this.sig = sig;
    }
    this.pitchRange = new ArrayList<>();
    this.beatXPitch = new ArrayList<>();
    this.setPitchRange();
    this.initBeatXPitch();
    for (Pitch p : Pitch.pitchOrder) {
      pitchNames.put(p.name, p);
    }
  }

  @Override
  public void addNote(Note n) {
    notes.add(n);
    sortNotes();
    setPitchRange();
  }

  @Override
  public void removeNote(Note n) {
    if (notes.contains(n)) {
      notes.remove(n);
    }
    else {
      System.out.print("couldn't find note");
    }
    setPitchRange();
  }

  @Override
  public void editNote(Note n, NoteField nf, String change) {
    if (notes.contains(n)) {
      switch (nf) {
        case PITCH:
          try {
            n.setPitch(parsePitch(change));
            setPitchRange();
          }
          catch (NullPointerException npe) {
            System.out.print("Could not edit note, please try again");
            return;
          }
          break;
        case START:
          try {
            n.setStart(Integer.parseInt(change));
          }
          catch (NumberFormatException u) {
            System.out.print("Invalid note start");
          }
          break;
        case DURATION:
          try {
            n.setDuration(Integer.parseInt(change));
          }
          catch (NumberFormatException u) {
            System.out.print("Invalid note duration");
          }
          break;
        case VOLUME:
          try {
            n.setVolume(Integer.parseInt(change));
          }
          catch (NumberFormatException u) {
            System.out.print("Invalid note volume");
          }
          break;
        case INSTRUMENT:
          try {
            n.setInstrument(Integer.parseInt(change));
          }
          catch (NumberFormatException u) {
            System.out.print("Invalid note instrument");
          }
          break;
        default:
          throw new IllegalArgumentException("Not a valid part of a Note");
      }
    }
    else {
      System.out.print("Couldn't find note");
    }
  }

  /**
   * In @code{editNote}, the new desired pitch is given in a string format.
   * This method return the OctavePitch desired based on the input
   * @param in    The input to be "parsed"
   * @return      An OctavePitch specified by in
   */
  private OctavePitch parsePitch(String in) {
    try {
      if (in.charAt(1) == '#') {
        return new OctavePitch(pitchNames.get(in.substring(0, 2)),
                Integer.parseInt(in.substring(2)));
      } else {
        return new OctavePitch(pitchNames.get(in.substring(0, 0)),
                Integer.parseInt(in.substring(1)));
      }
    }
    catch (IndexOutOfBoundsException | IllegalArgumentException n) {
      System.out.print("not a valid OctavePitch");
      throw n;
    }
  }


  @Override
  public void combine(IMusicModel other, int insert) {
    if (other.getNotes().get(0).getStart() + insert >= 0) {
      for (Note n : other.getNotes()) {
        notes.add(new Note(n.getPitch(), n.getStart() + insert, n.getDuration()));
      }
      for (Repeat r : other.getRepeats().values()) {
        Repeat rep = new Repeat(r.getGoBack(), r.getMark());
        addRepeat(rep);
      }
      sortNotes();
      setPitchRange();
      initBeatXPitch();
    }
    else {
      System.out.print("cannot start the piece at a negative beat");
    }
  }

  @Override
  public void addRepeat(Repeat r) {
    if (r.getMark() <= this.length()) {
      repeats.put(r.getMark(), r);
    }
  }

  @Override
  public void setPitchRange() {
    pitchRange.clear();
    if (!notes.isEmpty()) {
      for (Note n : notes) {
        if (!pitchRange.contains(n.getPitch())) {
          pitchRange.add(n.getPitch());
        }
      }
      sortPitches();
      OctavePitch hi = pitchRange.get(pitchRange.size() - 1);
      for (int o = pitchRange.get(0).oct; o <= hi.oct; o++) {
        for (int p = 0; (o == hi.oct && p <= Pitch.pitchOrder.indexOf(hi)) || p < 12; p++) {
          if ((p >= Pitch.pitchOrder.indexOf(pitchRange.get(0).pitch) || o != pitchRange.get(0).oct)
                  && (p <= Pitch.pitchOrder.indexOf(hi.pitch) || o != hi.oct)
                  && !pitchRange.contains(new OctavePitch(Pitch.pitchOrder.get(p), o))) {
            pitchRange.add(new OctavePitch(Pitch.pitchOrder.get(p), o));
          }
        }
      }
      sortPitches();
    }
  }

  /**
   * Sets up the grid of beats x pitch, with all values defaulted to -1.
   */
  public void initBeatXPitch() {
    beatXPitch.clear();
    for (int b = 0; b <= length(); b++) {
      beatXPitch.add(new ArrayList<>());
      for (int p = 0; p < pitchRange.size(); p++) {
        beatXPitch.get(b).add(p, -1);
      }
    }
  }

  @Override
  public void setBeatXPitch() {
    beatXPitch.clear();
    initBeatXPitch();
    for (Note n : notes) {
      beatXPitch.get(n.getStart()).set(pitchRange.indexOf(n.getPitch()), 1);
      for (int i = 1; i < n.getDuration(); i++) {
        beatXPitch.get(n.getStart() + i).set(pitchRange.indexOf(n.getPitch()), 0);
      }
    }
  }

  @Override
  public void setTempo(int tempo) {
    this.tempo = tempo;
  }

  @Override
  public void setRepeats (Map<Integer, Repeat> reps) {
    repeats.clear();
    for (Repeat r : reps.values()) {
      addRepeat(r);
    }
  }

  @Override
  public void setMultiEnding(MultiEnding m) {
    if (m.getEndings().get(m.getEndings().size() - 1).getMark() == length() + 1) {
      this.end = m;
    }
  }

  @Override
  public int length() {
    int longest = 0;
    sortNotes();
    for (Note n : notes) {
      if (n.getStart() + n.getDuration() - 1 > longest) {
        longest = n.getStart() + n.getDuration() - 1;
      }
    }
    return longest;
  }

  @Override
  public void sortNotes() {
    this.notes.sort(new ByStart());
  }

  @Override
  public void sortPitches() {
    this.pitchRange.sort(new ByPitch());
  }

  @Override
  public List<Note> getNotes() {
    List<Note> result = new ArrayList<>();
    result.addAll(notes);
    return result;
  }

  @Override
  public int getTempo() {
    int result = tempo * 1;
    return result;
  }

  @Override
  public Signature getSig() {
    return new Signature(sig.bpm, sig.beatLength);
  }

  @Override
  public ArrayList<OctavePitch> getPitchRange() {
    ArrayList<OctavePitch> result = new ArrayList<>();
    result.addAll(pitchRange);
    return result;
  }

  @Override
  public Map<Integer, Repeat> getRepeats() {
    return this.repeats;
  }

  @Override
  public MultiEnding getMultiEnding() {
    return this.end;
  }

  @Override
  public String printMusic() {
    initBeatXPitch();
    setPitchRange();
    setBeatXPitch();
    String result = "";
    for (int n = 0; n < Integer.toString(length()).length(); n++) {
      result += " ";
    }
    for (OctavePitch op : pitchRange) {
      String p = op.toString();
      int mid = (int) Math.ceil((5.0 - (double) p.length()) / 2);
      for (int g = 0; g < mid; g++) {
        p = " " + p;
      }
      while (p.length() < 5) {
        p += " ";
      }

      result += p;
    }
    result += "\n";
    for (int b = 0; b <= length(); b++) {
      String num = Integer.toString(b);
      while (num.length() < Integer.toString(length()).length()) {
        num += " ";
      }
      result += num;
      for (int p = 0; p < pitchRange.size(); p++) {
        switch (beatXPitch.get(b).get(p)) {
          case(-1): result += "     ";
          break;
          case(0): result += "  |  ";
          break;
          case(1): result += "  X  ";
          break;
          default: throw new IllegalArgumentException("invalid int value in beatXPitch");
        }
      }
      result += "\n";
    }
    if (result.length() > 0) {
      result = result.substring(0, result.length() - 1);
    }
    return result;
  }

  @Override
  public String toString() {
    return this.printMusic();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    else if (o instanceof MusicModel){
      MusicModel other = (MusicModel) o;
      return (this.notes.containsAll(other.notes)
              && other.notes.containsAll(this.notes)
              && this.sig.equals(other.sig)
              && this.tempo == other.tempo);
    }
    else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(notes, sig, tempo);
  }

  /**
   * Represents a class that builds a composition for our MusicModel.
   */
  public static final class Builder implements CompositionBuilder<IMusicModel> {
    private IMusicModel model; // The model to build

    /**
     * Constructor for building a model.
     */
    public Builder() {
      model = new MusicModel();
    }

    @Override
    public IMusicModel build() {
      return model;
    }

    @Override
    public CompositionBuilder<IMusicModel> setTempo(int tempo) {
      model.setTempo(tempo);
      return this;
    }

    @Override
    public CompositionBuilder<IMusicModel> addNote(int start, int end, int instrument,
                                                   int pitch, int volume) {
      Pitch p = Pitch.pitchOrder.get(pitch % 12);
      int oct = pitch / 12 - 1;
      Note n = new Note(new OctavePitch(p, oct), start, end - start); // Constructs note
                                                                    // using given parameters
      n.setInstrument(instrument); // Sets note's instrument
      n.setVolume(volume); // Sets note's volume
      model.addNote(n); // Adds the note to the model
      return this;
    }

    @Override
    public CompositionBuilder<IMusicModel> addRepeat(int goBack, int mark) {
      model.addRepeat(new Repeat(goBack, mark));
      return this;
    }

    @Override
    public CompositionBuilder<IMusicModel> addMultiEnding(List<Repeat> lor) {
      model.setMultiEnding(new MultiEnding(lor));
      return this;
    }
  }
}

