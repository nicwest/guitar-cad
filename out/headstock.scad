difference () {
  minkowski () {
    difference () {
      linear_extrude (height=15, center=true){
        polygon (points=[[-21, 0], [21, 0], [50, 150], [30, 150], [30, 170], [-30, 170], [-30, 150], [-50, 150]]);
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
    sphere ($fn=20, r=2);
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
