;;; Library functions for Clojure/JFuge

;;; (add-classpath "file:///Users/travers/Public/jfugue-4.0.3.jar")

(def player (new org.jfugue.Player))

;;; True utils

;;; This returns true if VAL can be found in SEQUENCE (must be a built-in way to do this)
(defn member [val sequence]
  (some (fn [elt] (= elt val)) sequence))

;;; Turn a list into an infinite list (looping it)
(defn infinitize [lst]
  (lazy-cat lst (infinitize lst)))

(comment
  (take 20 (infinitize '(1 2 3)))
  (1 2 3 1 2 3 1 2 3 1 2 3 1 2 3 1 2 3 1 2)
  )

;;; Loop a number of lists, letting them beat against each other.
(defn infinitize-lists [& lsts]
  (let [ilsts (map infinitize lsts)]
    (cons (map first lsts)
          (lazy-seq
           (apply infinitize-lists (map rest lsts)))))
  )

(comment "My struggle"
;;; this won't compile, don't understand why
;;; guy with same (unanswered) problem: https://gist.github.com/pjb3/1886007
;;; OH I get why it doesn't work, the recur is off in a lazy closure or something so can't actually loop...wish there as a better error.
(defn infinitize-lists [& lsts]
  (loop [ilsts (map infinitize lsts)]
    (cons (map first ilsts)
          (lazy-seq
           (recur (map rest ilsts))))))

;;; This won't work because recur is not at tail position
(defn infinitize-lists [& lsts]
  (loop [ilsts (map infinitize lsts)]
    (cons (map first ilsts)
          (recur (map rest ilsts)))))


;;; Maybe – no, this one doesn't terminate. Fuck
(defn infinitize-lists [& lsts]
  (let [ilsts (map infinitize lsts)]
    (cons (map first ilsts)
          (lazy-seq
           (map next ilsts)))))

;;; Also doesn't terminate
(defn infinitize-lists [& lsts]
  (let [ilsts (map infinitize lsts)]
    (cons (map first ilsts)
          (map rest ilsts))))

;;; Really thought this would work, but it loops
(defn infinitize-lists [& lsts]
  (map first
       (iterate (fn [x] (map rest x))
                (map infinitize lsts))))

;;; Nope that doesn't help
(defn infinitize-lists [& lsts]
  (map first
       (lazy-seq
        (iterate (fn [x] (map next x))
                 (map infinitize lsts)))))

;;; Closer...
(defn infinitize-lists [& lsts]
  (map (comp first first)
       (iterate (fn [x] (map rest x))
                (map infinitize lsts))))

)

;;; At last!
(defn infinitize-lists [& lsts]
  (map #(map first %)
       (iterate (fn [x] (map rest x))
                (map infinitize lsts))))


(comment
  (take 20 (infinitize-lists '(a b c d) '(1 2 3)))
  ((a 1) (b 2) (c 3) (d 1) (a 2) (b 3) (c 1) (d 2) (a 3) (b 1) (c 2) (d 3) (a 1) (b 2) (c 3) (d 1) (a 2) (b 3) (c 1) (d 2))
  )

;;; Scale a number (+++ hm, rename this for musical context!)
(defn scale [list v]
  (map (fn [e] (* v e)) list))

(defn diffs [l]
  (map - (rest l) l))

(comment
  (diffs '(52 66 69 71))
  (14 3 2))

(defn undiffs [root diffs]
  (cons root (map #(+ root %) diffs)))

(comment
  (undiffs 52 '(14 3 2))

  (let [seq '(52 66 69 71)]
    (is (= (undiffs (diffs seq)) seq)))
  )


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

;;; play a single note. Dur must be a float
(defn play-note [player pitch dur]
  (let [pattern (new org.jfugue.Pattern)]
    (.addElement pattern (new org.jfugue.Note (byte pitch) dur))
    (.play player pattern)))

;;; takes a list of note objects
(defn play-note-list [l]
  (let [pattern (new org.jfugue.Pattern)]
    (doseq [n l]
      (.addElement pattern n))
    (.play player pattern)))

(defn make-note [pitch dur]
  (new org.jfugue.Note (byte pitch) dur))  

(defn add-note [pattern pitch dur]
   (.addElement pattern (make-note pitch dur)))

(defn make-pattern [note]
  (let [pattern (new org.jfugue.Pattern)]
    (.addElement pattern note)
    pattern))

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


