;;; Sigh, this seems to break the player
;;;(map (fn [n] (play-note n 0.1)) (take 100 (iterate inc 1)))

;;; pass player in
(let [player (new org.jfugue.Player)] (map (fn [n] (play-note player n 0.1)) (take 10 (iterate inc 1))))

;;; argh, no, the whole thing is broken.  Crap.  Running the above 2-3 times leaves it in a borked stat.  Foo, why hasn't anyone solved music yet!
java.lang.IllegalStateException: sequencer not open


;;; Argh, this doesn't work either
(def synth (. javax.sound.midi.MidiSystem (getSynthesizer)))  ; ok
(def channels (.getChannels synth))
java.lang.IllegalArgumentException: Can't call public method of non-public class: public javax.sound.midi.MidiChannel[] com.sun.media.sound.AbstractPlayer.getChannels() (NO_SOURCE_FILE:5)

Problem is that method returns an array, which fucks things.



