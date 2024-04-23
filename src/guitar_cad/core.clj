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
       (translate [0 1/2 (- radius)]))))

(defn cutter
  [{:keys [fretboard-width fret-spacing fretboard-height fretboard-end-padding]} height]
  (let [fret-zero-y (first fret-spacing)
        fret-last-y (- (last fret-spacing) fretboard-end-padding)
        fret-zero-x (/ (first fretboard-width) 2)
        fret-last-x (/ (last fretboard-width) 2)]
    (->> (extrude-linear {:height height}
                    (polygon [[(- fret-zero-x) fret-zero-y]
                              [fret-zero-x fret-zero-y]
                              [fret-last-x fret-last-y]
                              [(- fret-last-x) fret-last-y] ]))
         (translate [0 0 (- (/ height 2))]))))

(defn fretboard
  [{:keys [fretboard-radius fretboard-height fret-tang-height fret-tang-width
           fret-spacing fretboard-end-padding] :as opts}]
  (intersection
    (cutter opts fretboard-height)
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

(def rounded-neck-profile
  (intersection
    (->> (cube 10 1 10)
         (translate [0 1/2 -5]))
    (with-fn 250
             (->> (cylinder 5 1)
                  (rotate (degrees 90) [1 0 0])))))

(defn neck-profile
  [{:keys [neck-profiles fret-spacing]}]
  (union
        (for [[[f1 p1] [f2 p2]] (drop-last (partition 2 1 neck-profiles neck-profiles))]
          (hull 
             (translate [0 (nth fret-spacing f1) 0] p1)
             (translate [0 (nth fret-spacing f2) 0] p2)))))

(defn cross-section-at
  [y block]
  (intersection
    (->> (cube 1000 1 1000)
         (translate [0 1/2 -500])
         (rotate (degrees -40) [1 0 0])
         (translate [0 y 0 ])
         )
    block))

(defn bolt-holes
  [depth radius {:keys [neck-pocket-length neck-pocket-height fret-spacing fretboard-width]}]
  (union
    (let [last-fret (last fret-spacing)
          last-width (last fretboard-width)
          bolt (translate [0 0 (/ depth 2)] (cylinder radius (+ depth 1)))]
      [
      (translate [0 (+ last-fret 15) (- neck-pocket-height)] bolt)
      (translate [(+ (/ last-width 2) -15) (+ last-fret neck-pocket-length -15) (- neck-pocket-height)] bolt)
      (translate [(- 0 (/ last-width 2) -15) (+ last-fret neck-pocket-length -15) (- neck-pocket-height)] bolt)
      ]
    )
    
    ))

(defn neck-block
  [{:keys [neck-pocket-height fret-spacing neck-pocket-length neck-block-shoulder] :as opts}]
    (intersection 
      (cutter opts neck-pocket-height)
      (union
        (translate [0 (+ (last fret-spacing) (/ neck-pocket-length 2)) (- (/ neck-pocket-height 2))]
                   (cube 1000 neck-pocket-length neck-pocket-height))
        (translate [0 (+ (last fret-spacing) neck-pocket-length) (- (/ neck-pocket-height 2))]
                   (cylinder neck-block-shoulder neck-pocket-height))))
    )


(defn truss-rod-cutter
  [{:keys [truss-rod-width truss-rod-height]}]
  (translate [0 500 0]
             (cube truss-rod-width 1000 (* truss-rod-height 2))))

(defn neck
  [{:keys [fret-spacing neck-pocket-length  neck-block-shoulder] :as opts}]
  (let [neck-prof (neck-profile opts)
        neck-blk (neck-block opts)
        prof-cross (cross-section-at (+ (last fret-spacing) neck-pocket-length (* neck-block-shoulder 2)) neck-prof)]

    (difference
      (union
        neck-prof
        (hull neck-blk
              prof-cross))
      (truss-rod-cutter opts)
      (bolt-holes 20 5 opts)
      )
    ))

(defn apes-strong-together
  [{:keys [fretboard-height] :as opts}]
  [(translate [0 0 (- fretboard-height)] (neck opts))
   (fretboard opts)]
  )


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
                     :fretboard-width [60 70]
                     :fretboard-height 6
                     :fretboard-end-padding 10
                     :neck-profiles [[0 (resize [60 1 15] rounded-neck-profile)]
                                     [22 (resize [70 1 20] rounded-neck-profile)]]
                     :neck-block-shoulder 35
                     :neck-pocket-length 80
                     :neck-pocket-height 22
                     :neck-bolt-outer-radius 10
                     :truss-rod-width 7
                     :truss-rod-height 8
                     }
                    opts)
        opts (assoc opts
                    :fret-spacing 
                    (into [] (map-indexed calc-fret-spacing
                                               (repeat
                                                 (+ 1 (:frets opts))
                                                 (:scale-length opts)))))]
    opts)))


; (render! "scratch" (bolt-holes 40 5 (config)))
 (render! "scratch" (neck (config)))
;(render! "scratch" (apes-strong-together (config)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
