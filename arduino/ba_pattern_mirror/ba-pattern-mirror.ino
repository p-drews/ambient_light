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
int green;
int red;
int blue = 0;

enum states{
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
      case GET_POSITION:
      {
        switch(val){
          case 17:
          {
            pos = val;
            current_state = GET_COLOR1;  
            break;
          }
          default:
          break;
        }
        break;
      }
      case GET_COLOR1:
      {
        red = val;
        current_state = GET_COLOR2;
        break;
      }
      case GET_COLOR2:
      {
        green = val;
        current_state = GET_COLOR3;
        break;
      }
      case GET_COLOR3:
      {
        blue = val;
        current_state = GET_SIDE;
        break;
      }
      case GET_SIDE:
      {
          switch(val){
          case 108:
           {
            current_state = MIRROR_LEFT;
            
            break;
           }
           case 114:
           {
            current_state = MIRROR_RIGHT;
            break;
           }
           case 76:
           {
            current_state = GROW_LEFT;
            break; 
           }
           case 82:
           {
            current_state = GROW_RIGHT;
            break;
           }
        }
        //Dieses Break verursacht 2 Farbfehler (Bei 110,17,0 bis 107,20,0) und bei
        // (17,110,0)....
        break;
      }
      case MIRROR_LEFT:
      {
        strip = &strip_left;
        
        for(int i = 0; i <= pos; i++)
        {
          
          strip->setPixelColor(i,red,green,blue);
        }
        current_state = GET_POSITION;
        break;
      }
      case MIRROR_RIGHT:
      {
        strip = &strip_right;
        
        for(int i = 0; i <= pos; i++)
        {
          
          strip->setPixelColor(i,red,green,blue);
        }
        current_state=GET_POSITION;
        break;
      }
      case GROW_LEFT:
      {
        break;
      }
      case GROW_RIGHT:
      {
        break;
      }
    }
  }
}
/*         
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

