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
        fret-last-y (last fret-spacing)
        fret-zero-x (/ (first fretboard-width) 2)
        fret-last-x (/ (last fretboard-width) 2)]
    (->> (extrude-linear {:height height}
                    (polygon [[(- fret-zero-x) fret-zero-y]
                              [fret-zero-x fret-zero-y]
                              [fret-last-x fret-last-y]
                              [fret-last-x (- fret-last-y fretboard-end-padding)]
                              [(- fret-last-x) (- fret-last-y fretboard-end-padding)]
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



(defn tuner-posts
  [{:keys [scale-length strings nut-width tuner-post-radius
           tuner-post-height nut-padding tuner-height
           nut-to-tuner-distance]}]
  (let [nut-minus-padding (- nut-width nut-padding nut-padding)
        incrx (/ nut-minus-padding (dec (count strings)))
        start (/ nut-minus-padding 2)
        mid (/ (count strings) 2)
        half-post (/ 2 tuner-post-radius)]
    (for [i (range (count strings))]
      (let [x (- start (* incrx i))
            x (if (>= i mid) (- x half-post) (+ x half-post))
            i (if (>= i mid) (- (count strings) (inc i)) i)
            y (+ scale-length  nut-to-tuner-distance (* i tuner-height))]
        (translate [x y 0]
                   (cylinder tuner-post-radius tuner-post-height))))))

(defn headstock 
  [{:keys [nut-width tuner-height tuner-width nut-to-tuner-distance strings
           nut-padding neck-material-height fretboard-height
           headstock-overcut-radius headstock-undercut-radius
           headstock-undercut-height
           headstock-thickness]}]
  (let [half-nut (/ nut-width 2)
        half-strings (Math/ceil (/ (count strings) 2))
        first-corner-x (+ (- half-nut nut-padding) (/ tuner-width 2)) 
        first-corner-y (- nut-to-tuner-distance (/ tuner-height 2))
        second-corner-y (+ first-corner-y (* half-strings tuner-height))
        second-corner-x (/ tuner-width 2)]
      (difference
        (->> (polygon [[(- half-nut) 0]
                  [(- first-corner-x) first-corner-y]
                  [(- second-corner-x) second-corner-y]
                  [second-corner-x second-corner-y]
                  [first-corner-x first-corner-y]
                  [half-nut 0]])
             (extrude-linear {:height neck-material-height})
             (translate [0 0 (- 0 (/ neck-material-height 2) fretboard-height)]))
        (translate [0 0 (- (- neck-material-height headstock-thickness))]
          (->> (cube 1000 1000 1000)
               (translate [0 500 (- 500 fretboard-height)])
               (translate [0 first-corner-y 0]))
          (->> (cylinder headstock-overcut-radius 1000)
               (with-fn 1000)
               (rotate (degrees 90) [0 1 0])
               (translate [0 first-corner-y (- headstock-overcut-radius fretboard-height)])
               ))
          (->> (cylinder headstock-overcut-radius 1000)
               (with-fn 1000)
               (rotate (degrees 90) [0 1 0])
               (translate [0 0 (- 0 headstock-undercut-height  headstock-undercut-radius)])
               )
        )))



(defn apes-strong-together
  [{:keys [fretboard-height scale-length] :as opts}]
  [(translate [0 0 (- fretboard-height)] (neck opts))
   (fretboard opts)
   (tuner-posts opts)
   (translate [0 scale-length 0]
     (headstock opts))
   ]
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
                     :neck-pocket-height 20
                     :neck-bolt-outer-radius 10
                     :neck-material-height 25
                     :truss-rod-width 7
                     :truss-rod-height 8
                     :nut-width 60
                     :nut-padding 3
                     :tuner-post-radius 3
                     :tuner-post-height 20
                     :tuner-height 30
                     :tuner-width 30
                     :nut-to-tuner-distance 50
                     :headstock-undercut-radius 50 
                     :headstock-undercut-height 22
                     :headstock-overcut-radius 50

                     :headstock-thickness 18
                     }
                    opts)
        opts (assoc opts
                    :fret-spacing 
                    (into [] (map-indexed calc-fret-spacing
                                               (repeat
                                                 (+ 1 (:frets opts))
                                                 (:scale-length opts))))) ]

    opts)))


; (render! "scratch" (bolt-holes 40 5 (config)))
;(render! "scratch" (neck (config)))
;(render! "scratch" (tuner-posts (config)))
(render! "scratch" (apes-strong-together (config)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
