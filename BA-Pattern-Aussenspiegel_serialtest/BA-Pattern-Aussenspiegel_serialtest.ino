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
Adafruit_NeoPixel *strip = &strip_left;

//var declaration

float brightness = 127;
uint16_t LEDWidth = 17;
int pos = 0;


//position declaration

byte val;
int green;
int red;
int blue = 0;;

enum states{
 WAITING,
 PATTERN_RIGHT,
 PATTERN_LEFT,
 SECOND_COLOR,
 SECOND_COLOR_LEFT,
 MIRROR_PAT,
 MIRROR_PAT_LEFT,
 MIRROR_PAT_LEFT_1,
 WIDTH
};

int j = 0;
states current_state = WAITING;




//-------------------------------------Setup/Loop-----------------------

void setup() {
  strip_left.begin();
  strip_left.show(); //initialize left pixel

  strip_right.begin();
  strip_right.show(); //init right pixel

  Serial.begin(9600);// begin serial connection

  growing_right();


 
}

void loop () {
 strip->show(); 
}



//---------------------------------------------------------------------

/*void spiegelPat_left(red, green, blue) {
  strip = &strip_left;

  for(int i = 0; i <= LEDWidth; i++)
  {
    
    strip->setPixelColor(i,red,green,blue);
  }

}*/

//--------------------------------------------------------------------

void growing_left(){
  strip = &strip_left;
  for (int i = 0; i < strip->numPixels(); i++){
    for (int j = 0 ; j <= i ; j++){
      
      green = (int)((1-((float)(i)/53)) * 85);    
      red = 85-green;
      strip->setPixelColor(j,red, green,0 );
    }

    strip_left.show();
    delay(50);
  }

}

//--------------------------------------------------------------------

void spiegelPat_right() {
  
  strip = &strip_right;
  
  /*for (int j = 0 ; j < 127 ; j++){
    for (int i = 0; i <= LEDWidth; i++) {
      red = (int) ((1-((float)j/127))*127);
      green = 127 - red;
      strip->setPixelColor(i, red, green, 0 );
    }      
      strip->show();
      delay(20);
  };*/
}


//------------------------------------------------------------------


void growing_right(){
  
    strip = &strip_right;
    for (int i = 0; i < strip->numPixels(); i++){
      for (int j = 0 ; j <= i ; j++){
        
        red = (int)((1-((float)(i)/53)) * 85);    
        green = 85-red;
        strip->setPixelColor(j,red, green,0 );
      }
  
      strip->show();
      delay(50);
    }
}

//-----------------------------------------------------------------
void setOff() {
  for (uint16_t i=0; i< strip->numPixels(); i++){
    strip->setPixelColor(i, 0,0,0);
  }
  strip->show();
  delay(2000);
  
}

//--------------------------Serial-Event-------------------------------

void serialEvent(){ // To check if there is any data on the Serial line
  while(Serial.available()){
    byte val = Serial.read();
    
    switch(current_state){
      case WAITING:
      {
        switch(val){
          case 108:
            current_state = PATTERN_LEFT;
            break;
          case 114:
            current_state = PATTERN_RIGHT;
            break;
          case 119:
            current_state = WIDTH;
            break;
          default:
            //do nothing and wait
            break;
        }
        break;
      }
      case PATTERN_RIGHT:
      {
        red = val;
        current_state = SECOND_COLOR;
        break;
      }
      case SECOND_COLOR:
      {
        green = val;
        current_state = MIRROR_PAT;
        break;
      }
      case MIRROR_PAT:
      {
        blue = val;
         
        strip = &strip_right;

        for(int i = 0; i <= LEDWidth; i++)
        {
          
          strip->setPixelColor(i,red,green,blue);
        }
        current_state=WAITING;
        break;

      }
      
    
      

//------------------------------PATTERN_LEFT---------------------------

      case PATTERN_LEFT:
      {
        red = val;
        current_state = SECOND_COLOR_LEFT;
        break;
      }
      case SECOND_COLOR_LEFT:
      {
        green = val;
        current_state = MIRROR_PAT_LEFT;
        break;
      }
      case MIRROR_PAT_LEFT:
      {
        blue = val;
      }
      case MIRROR_PAT_LEFT_1:
      { 
        strip = &strip_left;
        pos = val;
        for(int i = 0; i <= pos; i++)
        {
          
          strip->setPixelColor(i,red,green,blue);
        }
        current_state=WAITING;
        break;
      }
      break;
    }
  }
}


/* spiegelPat(){
 *   for (int j = 0 ; j < 127 ; j++){
    for (int i = 0; i <= ledWidth; i++) {
        green = (int) ((1-((float)j/127))*127);
        red = 127 - green;
        strip->setPixelColor(i, red, green, 0 );
    }      
      strip->show();
      delay(20);
  };
 */

