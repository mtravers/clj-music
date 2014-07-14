;;; Pattern generation

(defn take-pattern [seq n]
  (let [pattern (new org.jfugue.Pattern)]
    (doseq [[pitch dur] (take n seq)]
      (add-note pattern pitch dur))
    pattern))

;;; Abstracted for a specific purpose but more useful, so should change name
(defn pitch-time-beat [n pitches durs]
  (take-pattern (infinitize-lists pitches durs) n))

;;; (.play player (pitch-time-beat 50 '(52 66 69 71) (scale '(4 3 1) 0.03)))
;;; (.play player (pitch-time-beat 50 '(33 47 48 50 45) (scale '(2 1 3 2) 0.04)))
;;; (.play player (pitch-time-beat 50 (undiffs 54 (list (arandom 5) (arandom 5) (arandom 5))) (scale '(4 3 1) 0.03)))

(defn ascending-pattern [from to dur]
  (pitch-time-beat (- to from) (range from to) (infinitize (list dur))))

;;; infinite list of random notes
(defn random-note-list []
  (iterate (fn [i] (rand-int 128)) (rand-int 128)))

(defn random-note-pattern [n dur]
  (pitch-time-beat n (random-note-list) (infinitize (list dur))))

(defn drunk-walk [start step-range]
  (iterate (fn [prev] (mod (+ (arandom step-range) prev) 127)) start))

(defn drunk [n x dur]
  (pitch-time-beat n (drunk-walk 60 x) (list dur)))

(defn in-key-drunk [n x durs key]
  (let [pitches (filter #(in-key? % key 64)
                        (iterate #(mod (+ (arandom x) %) 127) 64))
        durs (infinitize durs)]
    (pitch-time-beat n pitches durs)))

;;; (.play player (in-key-drunk 50 5 '(0.05 0.1 0.25) major-key))

;;; 5/4 feel
;;; (.play player (in-key-drunk 100 5 '(0.1 0.2 0.1 0.1) minor-key))

;;; nice
;;; (.play player (in-key-drunk 80 5 (scale '(2 2 3 1) 0.06) minor-key))

(defn in-key-drunk-walk [step-dist key root]
  (filter (fn [n] (in-key? n key root))
 	  (drunk-walk root step-dist)))

;;; Dangerous to call outside of a thread
(defn play-infinite [player notes]
  (.play player (make-pattern (first notes)))
  (play-infinite player (rest notes)))
