;;; Some examples translated to clojure from  http://jfugue.org/examples.html

(.play player "C D E F G A B")

;;; That was impressively easy!

(.play player "T160 I[Cello] G3q G3q G3q Eb3q Bb3i G3q Eb3q Bb3i G3h")

;; Frere Jacques
(def  pattern1 (new org.jfugue.Pattern "C5q D5q E5q C5q"))
;; "Dormez-vous?"
(def pattern2 (new org.jfugue.Pattern "E5q F5q G5h"))
;; "Sonnez les matines"
(def pattern3 (new org.jfugue.Pattern "G5i A5i G5i F5i E5q C5q"))
;; "Ding ding dong"
(def pattern4 (new org.jfugue.Pattern "C5q G4q C5h"))

;; Put it all together
(def song (new org.jfugue.Pattern))
(.add song pattern1 2)  ; // Adds 'pattern1' to 'song' twice
(.add song pattern2 2)  ; // Adds 'pattern1' to 'song' twice
(.add song pattern3 2)  ; // Adds 'pattern1' to 'song' twice
(.add song pattern4 2)  ; // Adds 'pattern1' to 'song' twice

(.play player song)

;;; that's serial, wonder how you get it to fugue?  Ah, next example
(def doubleMeasureRest (new org.jfugue.Pattern "Rw Rw"))

;;; Create the first voice
(def round1 (new org.jfugue.Pattern "V0"))
(.add round1 song)
;;; Create the second voice
(def round2  (new org.jfugue.Pattern "V1"))
(.add round2 doubleMeasureRest)
(.add round2 song)

(def round3  (new org.jfugue.Pattern "V2"))
(.add round3 doubleMeasureRest 2)
(.add round3 song)

    // Put the voices together
(def roundSong (new org.jfugue.Pattern))
(.add roundSong round1)
(.add roundSong round2)
(.add roundSong round3)

(.play player roundSong)

;;; this works, having differnet players in different threads (although they probably aren't synced up)
(def dpat (new org.jfugue.Pattern "C5q C5q Rq G4"))
(def dsong (new org.jfugue.Pattern))
(.add dsong dpat 10)
(def bplayer (new Player))
(make-thread #'(lambda () (.play bplayer dsong)) :name "player")
(.play player "A E A E B D E")

;;; Can we loop?  No, apparently not.

Hm, now I'm thinking jfugue is just a thin layer over javax.sound.midi, which could easily be called directly...

;;; Try file
(def entertainer (.loadMusicString 'org.jfugue.Pattern (new File "/misc/downloads/entertainer.jfugue")))
(.play player entertainer)


;;; Try note level stuff


(defn ascending-pattern [from to dur]
  (let [pattern (new org.jfugue.Pattern)]
    (dotimes [i (- to from)]
      (.addElement pattern (new org.jfugue.Note (byte (+ from i)) dur)))
    pattern))



(defn random-note-pattern [n dur]
  (let [pattern (new org.jfugue.Pattern)]
    (dotimes [i n]
	     (.addElement pattern (new org.jfugue.Note (byte (irandom 128)) dur)))
    pattern))

;;; infinite list of random notes
(defn random-note-list []
  (iterate (fn [i] (irandom 128)) (irandom 128)))

;;; try different way
(defn random-melody [start var dur]
  (iterate (fn [note] 
	     (new org.jfugue.Note (byte (+ (.getValue note) (arandom var))) dur))
	   start))




(defun play-np-list [l]
  (play-note-list
   (map 'jnote1 l)))



					; slow down
(play-note-list (take 10 (compose [50 0.05] (fn [note] [(+ 0 (nth note 0)) (* 1.2 (nth note 1))]))))
;;; speed up
(play-note-list (take 10 (compose [50 0.1] (fn [note] [(+ 0 (nth note 0)) (* 0.8 (nth note 1))]))))

;;; (play-note-list (take 20 (random-melody (new org.jfugue.Note (byte 50) 0.1) 3 0.1)))
;;; (play-note-list (take 40 (random-melody (new org.jfugue.Note (byte 50) 0.1) 8 0.05)))

;; (.play player (random-note-pattern 20 0.1))

;;; see if one note at a time can work
(defn play-note [player pitch dur]
  (let [pattern (new org.jfugue.Pattern)]
    (.addElement pattern (new org.jfugue.Note (byte pitch) dur))
    (.play player pattern)))

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




(defn drunk [n x dur]
  (let [pattern (new org.jfugue.Pattern)]
    (def y)
    (binding [y (irandom 128)]
      (dotimes [i n]
	(set! y (mod (+ (plusorminus x) y) 127))
	(add-note pattern y dur))
      pattern)))


;;; Learn some more tricks

(defn in-key-drunk [n x dur key]
  (let [pattern (new org.jfugue.Pattern)]
    (def y)
    (binding [y 64]
      (dotimes [i n]
	(set! y (mod (+ (plusorminus x) y) 127))
	(if (in-key? y key 64)
	  (add-note pattern y dur))))
      pattern))

(defn add-note [pattern pitch dur]
   (.addElement pattern (new org.jfugue.Note (byte pitch) dur)))

(defn ascending-pattern [from to dur]
  (let [pattern (new org.jfugue.Pattern)]
	(dotimes [i (- to from)]
  	  (add-note pattern (+ from i) dur))
	pattern))


;;; Take a list of durs and apply sequentially
;;; No pop doesn't work
(defn in-key-drunk-2 [n x durs key]
  (let [pattern (new org.jfugue.Pattern)]
    (def y)
    (def rest-durs)
    (binding [y 64 rest-durs (infinitize durs)]
      (dotimes [i n]
	(set! y (mod (+ (plusorminus x) y) 127))
	(if (in-key? y key 64)
	  (do
	    (add-note pattern y (first rest-durs))
	    (set! rest-durs (rest rest-durs))
	    ))))
    pattern))


(.play player (in-key-drunk-2 50 5 '(0.05 0.1 0.25) major-key))


;;; 5/4 feel
(.play player (in-key-drunk-2 100 5 '(0.1 0.2 0.1 0.1) minor-key))

;;; nice
(.play player (in-key-drunk-2 80 5 (scale '(2 2 3 1) 0.06) minor-key))


(defn pitch-time-beat [n pitches durs]
  (let [pattern (new org.jfugue.Pattern)]
    (def rest-pitches)
    (def rest-durs)
    (binding [rest-pitches (infinitize pitches) rest-durs (infinitize durs)]
      (dotimes [i n]
	(do
	  (add-note pattern (first rest-pitches) (first rest-durs))
	  (set! rest-pitches (rest rest-pitches))
	  (set! rest-durs (rest rest-durs))
	  )))
    pattern))

(.play player (pitch-time-beat 50 '(52 66 69 71) (scale '(4 3 1) 0.03)))
(.play player (pitch-time-beat 50 '(33 47 48 50 45) (scale '(2 1 3 2) 0.04)))

;;; Infinite drunk

(defn drunk-walk [step-dist]
  (iterate (fn [n] (mod (+ n (plusorminus step-dist)) 128)) 64))

(defn in-key-drunk-walk [step-dist key root]
  (filter (fn [n] (in-key? n key root))
	  (drunk-walk step-dist)))

