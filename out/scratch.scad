union () {
  union () {
    hull () {
      translate ([0, 648.0, 0]) {
        resize ([60, 1, 15]){
          intersection () {
            translate ([0, 1/2, -5]) {
              cube ([10, 1, 10], center=true);
            }
            rotate (a=90.0, v=[1, 0, 0]) {
              cylinder ($fn=250, h=1, r=5, center=true);
            }
          }
        }
      }
      translate ([0, 181.83885182611846, 0]) {
        resize ([70, 1, 20]){
          intersection () {
            translate ([0, 1/2, -5]) {
              cube ([10, 1, 10], center=true);
            }
            rotate (a=90.0, v=[1, 0, 0]) {
              cylinder ($fn=250, h=1, r=5, center=true);
            }
          }
        }
      }
    }
  }
  intersection () {
    translate ([0, 0, -11]) {
      linear_extrude (height=22, center=true){
        polygon (points=[[-30, 648.0], [30, 648.0], [35, 171.83885182611846], [-35, 171.83885182611846]]);
      }
    }
    translate ([0, 221.83885182611846, -11]) {
      cube ([1000, 80, 22], center=true);
    }
  }
  hull () {
    intersection () {
      translate ([0, 282.33885182611846, -500]) {
        cube ([1000, 1, 1000], center=true);
      }
      union () {
        hull () {
          translate ([0, 648.0, 0]) {
            resize ([60, 1, 15]){
              intersection () {
                translate ([0, 1/2, -5]) {
                  cube ([10, 1, 10], center=true);
                }
                rotate (a=90.0, v=[1, 0, 0]) {
                  cylinder ($fn=250, h=1, r=5, center=true);
                }
              }
            }
          }
          translate ([0, 181.83885182611846, 0]) {
            resize ([70, 1, 20]){
              intersection () {
                translate ([0, 1/2, -5]) {
                  cube ([10, 1, 10], center=true);
                }
                rotate (a=90.0, v=[1, 0, 0]) {
                  cylinder ($fn=250, h=1, r=5, center=true);
                }
              }
            }
          }
        }
      }
    }
    intersection () {
      translate ([0, 262.33885182611846, -500]) {
        cube ([1000, 1, 1000], center=true);
      }
      intersection () {
        translate ([0, 0, -11]) {
          linear_extrude (height=22, center=true){
            polygon (points=[[-30, 648.0], [30, 648.0], [35, 171.83885182611846], [-35, 171.83885182611846]]);
          }
        }
        translate ([0, 221.83885182611846, -11]) {
          cube ([1000, 80, 22], center=true);
        }
      }
    }
  }
}
