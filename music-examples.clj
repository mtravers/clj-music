;;; Some examples translated to clojure from  http://jfugue.org/examples.html

(.play player "C D E F G A B")

;;; That was impressively easy!

(.play player "T160 I[Cello] G3q G3q G3q Eb3q Bb3i G3q Eb3q Bb3i G3h")

;; Frere Jacques
(def pattern1 (new org.jfugue.Pattern "C5q D5q E5q C5q"))
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

;;; Try file
(def entertainer (.loadMusicString 'org.jfugue.Pattern (new File "/misc/downloads/entertainer.jfugue")))
(.play player entertainer)

					; slow down
(play-note-list (take 10 (compose [50 0.05] (fn [note] [(+ 0 (nth note 0)) (* 1.2 (nth note 1))]))))
;;; speed up
(play-note-list (take 10 (compose [50 0.1] (fn [note] [(+ 0 (nth note 0)) (* 0.8 (nth note 1))]))))

;;; (play-note-list (take 20 (random-melody (new org.jfugue.Note (byte 50) 0.1) 3 0.1)))
;;; (play-note-list (take 40 (random-melody (new org.jfugue.Note (byte 50) 0.1) 8 0.05)))

;; (.play player (random-note-pattern 20 0.1))
