;;; Library functions for Clojure/JFuge

(add-classpath "file:///Users/travers/Public/jfugue-4.0.3.jar")

(def player (new org.jfugue.Player))

;;; True utils

;;; This returns true if VAL can be found in SEQUENCE (must be a built-in way to do this)
(defn member [val sequence]
  (some (fn [elt] (= elt val)) sequence))

;;; Turn a list into an infinite list (looping it)
;;; Only seems to work some of the time?
(defn infinitize [lst]
  (lazy-cat lst (infinitize lst)))

;;; Scale a number (+++ hm, rename this for musical context!)
(defn scale [list v]
  (map (fn [e] (* v e)) list))

;;; Randomness

;;; An integer random number in range [0,n)
(defn irandom [n]
  (Math/round (Math/floor (* n (Math/random)))))

;;; range [-n..n]
(defn arandom [n]
  (- (Math/round (Math/floor (+ 1 (* 2 n (Math/random))))) n))

;;; random centered around 0 (+++ needs a better name)
(defn plusorminus [n]
  (- (irandom (* 2 (+ n 1))) n))

;;; Note manipulation

;;; convert numerics into note object
(defn jnote [pitch dur]
  (new org.jfugue.Note (byte pitch) dur))

;;; convert [note dur] pair into note object
(defn jnote1 [np]
  (new org.jfugue.Note (byte (nth np 0)) (double (nth np 1))))

;;; Turn a string like "C3" into a pitch
(defn get-pitch [string]
  (.getValue (org.jfugue.MusicStringParser/getNote string)))

;;; takes a list of note objects
(defn play-note-list [l]
  (let [pattern (new org.jfugue.Pattern)]
    (doseq [n l]
      (.addElement pattern n))
    (.play player pattern)))

(defn sequence->pattern [pitches dur]
  (let [pattern (new org.jfugue.Pattern)]
    (doseq [pitch pitches]
      (add-note pattern pitch dur))
    pattern))

;;; functional, I wanna get functional
;;; start is [pitch dur], f is a function from one to the next
(defn compose [start f]
  (iterate (fn [note]
	     (let [nnote (f [(.getValue note) (.getDecimalDuration note)])] ;argh, finding getdecimal was hard
	       (new org.jfugue.Note (byte (nth nnote 0)) (nth nnote 1))))
	   (new org.jfugue.Note (byte (nth start 0)) (nth start 1))))

(defn add-note [pattern pitch dur]
   (.addElement pattern (new org.jfugue.Note (byte pitch) dur)))

;;; Keys

(def major-key [0 2 4 5 7 9 11])
(def minor-key [0 2 3 5 7 8 10])
(def chromatic-key [0 1 2 3 4 5 6 7 8 9 10 11])
(def boring-key [0 7])

;;; Given a PITCH, a KEY (as a sequence of intervals) and a root for the key,
;;; return true if pitch is in the key.
(defn in-key? [pitch key root]
  (member (mod (- pitch root) 12)
	  key))


