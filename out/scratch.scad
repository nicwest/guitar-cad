hull () {
  translate ([0, 0, 0]) {
    resize ([40, 1, 10]){
      rotate (a=-90.0, v=[1, 0, 0]) {
        difference () {
          cylinder ($fn=100, h=5, r=20, center=true);
          translate ([0, -20, 0]) {
            cube ([40, 40, 20], center=true);
          }
        }
      }
    }
  }
  translate ([0, 200, 0]) {
    resize ([50, 1, 15]){
      rotate (a=-90.0, v=[1, 0, 0]) {
        difference () {
          cylinder ($fn=100, h=5, r=20, center=true);
          translate ([0, -20, 0]) {
            cube ([40, 40, 20], center=true);
          }
        }
      }
    }
  }
}
