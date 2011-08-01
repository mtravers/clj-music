;;; Rudimentary tools for concurrency
;;; todo:
;;;  - use pools, that's probably what I want. (DONE)
;;;    - but players still only do one thing at a time, so they need a pool or something
;;;      - call-with-player 
;;;  - macro
;;;  - synchronization

(import '(java.util.concurrent Executors))

(def e (Executors/newCachedThreadPool))

(defn call-in-background [thunk]
  (.submit e thunk))

(defn call-with-player [proc]
  (call-in-background (fn [] (let [player (new org.jfugue.Player)] (proc player)))))

;;; eg (call-with-player (fn [p] (.play p "T260 I[Marimba] G3q G3q G3q Eb3i Bb3i G3q Eb3i Bb3i G3h")))
;;; 

;;; three drunks (actually sounds better if the time patterns are all the same)
(do
  (call-with-player (fn [p] (.play p (in-key-drunk-2 80 5 (scale '(2 2 3 1) 0.06) minor-key))))
  (call-with-player (fn [p] (.play p (in-key-drunk-2 80 5 (scale '(1 2 2 3) 0.06) minor-key))))
  (call-with-player (fn [p] (.play p (in-key-drunk-2 80 5 (scale '(3 1 2 2) 0.06) minor-key)))))

(call-with-player (fn [p] (play-infinite p (map (fn [pitch] (make-note pitch 0.1)) (drunk-walk 5)))))
