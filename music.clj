;;; Pattern generation

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

(defn ascending-pattern [from to dur]
  (let [pattern (new org.jfugue.Pattern)]
	(dotimes [i (- to from)]
  	  (add-note pattern (+ from i) dur))
	pattern))

;;; Take a list of durs and apply sequentially
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

;;; (.play player (in-key-drunk-2 50 5 '(0.05 0.1 0.25) major-key))

;;; 5/4 feel
;;; (.play player (in-key-drunk-2 100 5 '(0.1 0.2 0.1 0.1) minor-key))

;;; nice
;;; (.play player (in-key-drunk-2 80 5 (scale '(2 2 3 1) 0.06) minor-key))

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

;;; (.play player (pitch-time-beat 50 '(52 66 69 71) (scale '(4 3 1) 0.03)))
;;; (.play player (pitch-time-beat 50 '(33 47 48 50 45) (scale '(2 1 3 2) 0.04)))

;;; Infinite drunk

(defn drunk-walk [step-dist]
  (iterate (fn [n] (mod (+ n (plusorminus step-dist)) 128)) 64))

(defn in-key-drunk-walk [step-dist key root]
  (filter (fn [n] (in-key? n key root))
 	  (drunk-walk step-dist)))

;;; Dangerous to call outside of a thread
(defn play-infinite [player notes]
  (.play player (make-pattern (first notes)))
  (play-infinite player (rest notes)))
