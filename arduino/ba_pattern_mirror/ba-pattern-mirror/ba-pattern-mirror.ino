#include <Adafruit_NeoPixel.h>
#include <Math.h>

#define PIN_left 6 //left
#define PIN_right 7 //right
#define BAUD_RATE 9600  // updates per second e.g. 115200 for sparkle,
// 7200 seems to be stable for ambicar (NeoPixel)

#define PIXEL_COUNT_left 53
#define PIXEL_COUNT_right 53

//initialize Neopixel
Adafruit_NeoPixel strip_left = Adafruit_NeoPixel(PIXEL_COUNT_left, PIN_left, NEO_GRB + NEO_KHZ800);
Adafruit_NeoPixel strip_right = Adafruit_NeoPixel(PIXEL_COUNT_right, PIN_right, NEO_GRB + NEO_KHZ800);

//set pointer on left strip
Adafruit_NeoPixel *strip = &strip_right;

//var declaration

int pos = 0;


//position declaration

byte val;
byte green;
byte red;
byte blue = 0;

enum states {
  GET_POSITION,
  GET_COLOR1,
  GET_COLOR2,
  GET_COLOR3,
  GET_SIDE,
  MIRROR_LEFT,
  MIRROR_RIGHT,
  GROW_LEFT,
  GROW_RIGHT
};


states current_state = GET_POSITION;




//-------------------------------------Setup/Loop-----------------------

void setup() {
  strip_left.begin();
  strip_left.show(); //initialize left pixel

  strip_right.begin();
  strip_right.show(); //init right pixel

  Serial.begin(9600);// begin serial connection



}

void loop () {
  strip->show();
}

//-----------------------------------------------------------------
void setOff() {
  for (uint16_t i = 0; i < strip->numPixels(); i++) {
    strip->setPixelColor(i, 0, 0, 0);
  }
  strip->show();
}

//--------------------------Serial-Event-------------------------------

void serialEvent() { // To check if there is any data on the Serial line
  while (Serial.available()) {
    byte val = Serial.read();

    switch (current_state) {
      case GET_COLOR1:
        {
          //Nicht die beste Methode, nochmal drüber nachdenken
          switch (val) {
            case 0:
              {
                blue = 0;
                current_state = GET_COLOR2;
              }
            default: break;
          }
        }
      case GET_COLOR2:
        {
          red = val;
          current_state = GET_COLOR3;
          break;
        }
      case GET_COLOR3:
        {
          green = val;
          current_state = GET_POSITION;
          break;
        }
      case GET_POSITION:
        {
          pos = val;
          current_state = GET_SIDE;
          break;
        }
      case GET_SIDE:
        {
          switch (val) {
            // char l was sent
            case 108:
              {
                current_state = MIRROR_LEFT;

                break;
              }
            // char r was sent
            case 114:
              {
                current_state = MIRROR_RIGHT;
                break;
              }
              // char L was sent
            case 76:
              {
                current_state = GROW_LEFT;
                break;
              }
            case 82:
            // char R was sent
              {
                current_state = GROW_RIGHT;
                break;
              }
            case 0:
            {
              setOff();
              current_state = GET_COLOR1;
            }
          }
          break;
        }
      case MIRROR_LEFT:
        {
          strip = &strip_left;
          
          for (int i = 0; i <= pos; i++)
          {

            strip->setPixelColor(i, red, green, blue);
          }
          current_state = GET_COLOR1;
          break;
        }
      case GROW_LEFT:
        {

          strip = &strip_left;

          for (int i = 0; i <= pos; i++)
          {
            strip->setPixelColor(i, red, green, blue);
          }
          for(int j = pos; j > strip->numPixels() ; j++){
            strip->setPixelColor(j,0,0,0);
          }
          current_state = GET_COLOR1;
          break;


        }
        case MIRROR_RIGHT:
        {

          strip = &strip_right;

          for (int i = 0; i <= pos; i++)
          {
            strip->setPixelColor(i, red, green, blue);
          }
          current_state = GET_COLOR1;
          break;


        }

      case GROW_RIGHT:
        {

          strip = &strip_right;
          for (int i = 0; i <= strip->numPixels(); i++)
          {
            if(i <= pos){
              strip->setPixelColor(i, red, green, blue);
            } else if (i > pos) {
              strip->setPixelColor(i, 0,0,0);
            }
          }          

          current_state = GET_COLOR1;
          break;
        }
    }
  }
}


