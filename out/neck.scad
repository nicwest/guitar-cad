union () {
  translate ([0, 150, 0]) {
    rotate (a=5.0, v=[1, 0, 0]) {
      translate ([0, -150, 0]) {
        translate ([0, 0, -8]) {
          difference () {
            difference () {
              linear_extrude (height=16, center=true){
                polygon (points=[[-21, 0], [21, 0], [50, 150], [-50, 150]]);
              }
              translate ([50, 150, ]) {
                resize ([40, 60, 20]){
                  cylinder (h=20, r=20, center=true);
                }
              }
              translate ([-50, 150, ]) {
                resize ([40, 60, 20]){
                  cylinder (h=20, r=20, center=true);
                }
              }
            }
            union () {
              translate ([10, 30, ]) {
                cylinder (h=20, r=5, center=true);
              }
              translate ([17, 70, ]) {
                cylinder (h=20, r=5, center=true);
              }
              translate ([24, 110, ]) {
                cylinder (h=20, r=5, center=true);
              }
            }
            mirror ([1, 0, 0]) {
              union () {
                translate ([10, 30, ]) {
                  cylinder (h=20, r=5, center=true);
                }
                translate ([17, 70, ]) {
                  cylinder (h=20, r=5, center=true);
                }
                translate ([24, 110, ]) {
                  cylinder (h=20, r=5, center=true);
                }
              }
            }
            translate ([0, 170, 0]) {
              cube ([100, 20, 40], center=true);
            }
          }
        }
      }
    }
  }
  translate ([0, 150, 0]) {
    difference () {
      hull () {
        rotate (a=5.0, v=[1, 0, 0]) {
          translate ([0, 0, -8]) {
            cube ([60, 1, 16], center=true);
          }
        }
        translate ([0, 6, 0]) {
          rotate (a=-90.0, v=[1, 0, 0]) {
            difference () {
              cylinder (h=1, r=25, center=true);
              translate ([0, -25, 0]) {
                cube ([60, 60, 2], center=true);
              }
            }
          }
        }
        translate ([0, 30, 0]) {
          resize ([60, 1, 20]){
            rotate (a=-90.0, v=[1, 0, 0]) {
              difference () {
                cylinder (h=5, r=20, center=true);
                translate ([0, -20, 0]) {
                  cube ([40, 40, 20], center=true);
                }
              }
            }
          }
        }
      }
      translate ([0, 0, -31]) {
        rotate (a=90.0, v=[0, 1, 0]) {
          cylinder (h=60, r=15, center=true);
        }
      }
    }
  }
  translate ([0, 180, 0]) {
    hull () {
      translate ([0, 0, 0]) {
        resize ([60, 1, 20]){
          rotate (a=-90.0, v=[1, 0, 0]) {
            difference () {
              cylinder (h=5, r=20, center=true);
              translate ([0, -20, 0]) {
                cube ([40, 40, 20], center=true);
              }
            }
          }
        }
      }
      translate ([0, 400, 0]) {
        resize ([70, 1, 35]){
          rotate (a=-90.0, v=[1, 0, 0]) {
            difference () {
              cylinder (h=5, r=20, center=true);
              translate ([0, -20, 0]) {
                cube ([40, 40, 20], center=true);
              }
            }
          }
        }
      }
    }
  }
}
