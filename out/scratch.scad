intersection () {
  translate ([0, 0, -3]) {
    linear_extrude (height=6, center=true){
      polygon (points=[[-30, 648.0], [30, 648.0], [35, 171.83885182611846], [-35, 171.83885182611846]]);
    }
  }
  difference () {
    union () {
      hull () {
        translate ([0, 648.0, 0]) {
          translate ([0, 0, -254]) {
            rotate (a=90.0, v=[1, 0, 0]) {
              cylinder ($fn=1000, h=1, r=254, center=true);
            }
          }
        }
        translate ([0, 181.83885182611846, 0]) {
          translate ([0, 0, -406]) {
            rotate (a=90.0, v=[1, 0, 0]) {
              cylinder ($fn=1000, h=1, r=406, center=true);
            }
          }
        }
      }
      hull () {
        translate ([0, 181.83885182611846, 0]) {
          translate ([0, 0, -406]) {
            rotate (a=90.0, v=[1, 0, 0]) {
              cylinder ($fn=1000, h=1, r=406, center=true);
            }
          }
        }
        translate ([0, 171.83885182611846, 0]) {
          translate ([0, 0, -406]) {
            rotate (a=90.0, v=[1, 0, 0]) {
              cylinder ($fn=1000, h=1, r=406, center=true);
            }
          }
        }
      }
    }
    translate ([0, 611.6305546177374, -1/3]) {
      cube ([1000, 3/2, 6], center=true);
    }
    translate ([0, 577.3023693549399, -1/3]) {
      cube ([1000, 3/2, 6], center=true);
    }
    translate ([0, 544.900877084407, -1/3]) {
      cube ([1000, 3/2, 6], center=true);
    }
    translate ([0, 514.3179408376966, -1/3]) {
      cube ([1000, 3/2, 6], center=true);
    }
    translate ([0, 485.4514929080448, -1/3]) {
      cube ([1000, 3/2, 6], center=true);
    }
    translate ([0, 458.20519420888274, -1/3]) {
      cube ([1000, 3/2, 6], center=true);
    }
    translate ([0, 432.48811275109114, -1/3]) {
      cube ([1000, 3/2, 6], center=true);
    }
    translate ([0, 408.21442016593886, -1/3]) {
      cube ([1000, 3/2, 6], center=true);
    }
    translate ([0, 385.3031052608816, -1/3]) {
      cube ([1000, 3/2, 6], center=true);
    }
    translate ([0, 363.67770365223686, -1/3]) {
      cube ([1000, 3/2, 6], center=true);
    }
    translate ([0, 343.26604257241166, -1/3]) {
      cube ([1000, 3/2, 6], center=true);
    }
    translate ([0, 324.0, -1/3]) {
      cube ([1000, 3/2, 6], center=true);
    }
    translate ([0, 305.81527730886876, -1/3]) {
      cube ([1000, 3/2, 6], center=true);
    }
    translate ([0, 288.65118467746987, -1/3]) {
      cube ([1000, 3/2, 6], center=true);
    }
    translate ([0, 272.4504385422035, -1/3]) {
      cube ([1000, 3/2, 6], center=true);
    }
    translate ([0, 257.15897041884836, -1/3]) {
      cube ([1000, 3/2, 6], center=true);
    }
    translate ([0, 242.72574645402236, -1/3]) {
      cube ([1000, 3/2, 6], center=true);
    }
    translate ([0, 229.10259710444143, -1/3]) {
      cube ([1000, 3/2, 6], center=true);
    }
    translate ([0, 216.24405637554563, -1/3]) {
      cube ([1000, 3/2, 6], center=true);
    }
    translate ([0, 204.1072100829694, -1/3]) {
      cube ([1000, 3/2, 6], center=true);
    }
    translate ([0, 192.6515526304408, -1/3]) {
      cube ([1000, 3/2, 6], center=true);
    }
    translate ([0, 181.83885182611846, -1/3]) {
      cube ([1000, 3/2, 6], center=true);
    }
  }
}
