(ns guitar-cad.core
  (:require [clojure.java.io :refer [make-parents]]
            [scad-clj.scad :refer [write-scad]]
            [scad-clj.model :refer [polygon extrude-linear cylinder difference union
                                    translate mirror resize minkowski sphere
                                    with-fn cube square hull rotate intersection]])
  (:gen-class))

(defn degrees [n] (* n (/ Math/PI 180)))


(defn gauge->mm
  [n]
  (* n 25.4))

(defn render!
  [file-name part]
  (let [file-path (str "out/" file-name ".scad")]
    (make-parents file-path)
    (spit file-path
          (write-scad part))))


(defn- calc-fret-spacing
  [fret scale-length]
  (/ scale-length (Math/pow 2 (/ fret 12))))

(defn- fretboard-profile
  [radius]
  (with-fn 1000
  (->> (cylinder radius 1)
       (rotate (degrees 90) [1 0 0])
       (translate [0 0 (- radius)]))))

(defn fretboard-cutter
  [{:keys [fretboard-width fret-spacing fretboard-height fretboard-end-padding]}]
  (let [fret-zero-y (first fret-spacing)
        fret-last-y (- (last fret-spacing) fretboard-end-padding)
        fret-zero-x (/ (last (first fretboard-width)) 2)
        fret-last-x (/ (last (last fretboard-width)) 2)]
    (->> (extrude-linear {:height fretboard-height}
                    (polygon [[(- fret-zero-x) fret-zero-y]
                              [fret-zero-x fret-zero-y]
                              [fret-last-x fret-last-y]
                              [(- fret-last-x) fret-last-y] ]))
         (translate [0 0 (- (/ fretboard-height 2))]))))

(defn fretboard
  [{:keys [fretboard-radius fretboard-height fret-tang-height fret-tang-width
           fret-spacing fretboard-end-padding] :as opts}]
  (intersection
    (fretboard-cutter opts)
    (difference
      (union
        (for [[[f1 r1] [f2 r2]] (drop-last (partition 2 1 fretboard-radius fretboard-radius))]
          (hull 
            (->> (fretboard-profile r1)
                 (translate [0 (nth fret-spacing f1) 0]))
            (->> (fretboard-profile r2)
                 (translate [0 (nth fret-spacing f2) 0]))))

        ; add a little padding to the end of the fretboard
        (let [r (last (last fretboard-radius))
              y (last fret-spacing)]
          (hull
            (->> (fretboard-profile r)
                 (translate [0 y 0]))
            (->> (fretboard-profile r)
                 (translate [0 (- y fretboard-end-padding) 0])))))

      (for [fret (drop 1 fret-spacing)]
        (->> (cube 1000 fret-tang-width fret-tang-height)
             (translate [0 fret (- (/ 2 fret-tang-height))]))))))


(defn config
  ([] (config {}))
  ([opts]
  (let [opts (merge {:pretty? false
                     :strings [0.010 0.013 0.017 0.030 0.042 0.052]
                     :frets 22
                     :fret-tang-height 6
                     :fret-tang-width 3/2
                     :scale-length 648
                     :fretboard-radius [[0 254] [22 406]]
                     :fretboard-width [[0 60] [22 70]]
                     :fretboard-height 6
                     :fretboard-end-padding 10
                     :neck-radius [[0 60] [22 70]]
                     }
                    opts)
        opts (assoc opts
                    :fret-spacing 
                    (into [] (map-indexed calc-fret-spacing
                                               (repeat
                                                 (+ 1 (:frets opts))
                                                 (:scale-length opts)))))]
    opts)))


(render! "scratch" (fretboard (config)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
