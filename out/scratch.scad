difference () {
  hull () {
    rotate (a=5.0, v=[1, 0, 0]) {
      translate ([0, 0, -8]) {
        cube ([60, 1, 16], center=true);
      }
    }
    translate ([0, 10, 0]) {
      rotate (a=-90.0, v=[1, 0, 0]) {
        difference () {
          cylinder (h=1, r=25, center=true);
          translate ([0, -25, 0]) {
            cube ([60, 60, 2], center=true);
          }
        }
      }
    }
    translate ([0, 50, 0]) {
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
  translate ([0, 0, -36]) {
    rotate (a=90.0, v=[0, 1, 0]) {
      cylinder (h=60, r=20, center=true);
    }
  }
}
