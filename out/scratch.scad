union () {
  cube ([60, 6, 6], center=true);
  translate ([-35/2, 0, 0]) {
    rotate (a=90.0, v=[1, 0, 0]) {
      cylinder (h=12, r=0.254, center=true);
    }
  }
  translate ([-21/2, 0, 0]) {
    rotate (a=90.0, v=[1, 0, 0]) {
      cylinder (h=12, r=0.3302, center=true);
    }
  }
  translate ([-7/2, 0, 0]) {
    rotate (a=90.0, v=[1, 0, 0]) {
      cylinder (h=12, r=0.4318, center=true);
    }
  }
  translate ([7/2, 0, 0]) {
    rotate (a=90.0, v=[1, 0, 0]) {
      cylinder (h=12, r=0.7619999999999999, center=true);
    }
  }
  translate ([21/2, 0, 0]) {
    rotate (a=90.0, v=[1, 0, 0]) {
      cylinder (h=12, r=1.0668, center=true);
    }
  }
  translate ([35/2, 0, 0]) {
    rotate (a=90.0, v=[1, 0, 0]) {
      cylinder (h=12, r=1.3208, center=true);
    }
  }
}
