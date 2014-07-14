(defproject clj-music "1.0.0-SNAPSHOT"
  :description "Music generation with Clojure and JFugue"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [jfugue "4.0.3"]]
  :profiles
  {
   :dev {:dependencies [[org.clojure/tools.trace "0.7.6"]
                        [com.cemerick/pomegranate "0.3.0"]]
         }
   }
  )
