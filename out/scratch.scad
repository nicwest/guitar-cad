difference () {
  hull () {
    rotate (a=5.0, v=[1, 0, 0]) {
      translate ([0, 0, -10]) {
        cube ([60, 1, 20], center=true);
      }
    }
    translate ([0, 36, 0]) {
      rotate (a=-90.0, v=[1, 0, 0]) {
        difference () {
          cylinder (h=1, r=30, center=true);
          translate ([0, -30, 0]) {
            cube ([60, 60, 2], center=true);
          }
        }
      }
    }
    translate ([0, 60, 0]) {
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
  translate ([0, 0, -70]) {
    rotate (a=90.0, v=[0, 1, 0]) {
      cylinder (h=60, r=50, center=true);
    }
  }
}
