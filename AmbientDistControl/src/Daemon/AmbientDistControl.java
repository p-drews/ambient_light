package Daemon;

import Helper.Conversions;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Patze
 */
public class AmbientDistControl {

  public static int LEDWidth = 17;

  /*
   public static int green;
   public static int red;
   public static int blue = 0;

   static byte[] color;
   private static char side;
   private static float speed;
   private static float distance;
   private static float ttc;
   public static int pos;
   */
  public static void main(String[] args) throws Exception {
    Thread t = new Thread() {
      @Override
      public void run() {

        try {
          AmbientDistControl control = new AmbientDistControl();
          LEDConnector connector = new LEDConnector();
          connector.initialize();
          //Wird gebraucht, damit die Connection aufgebaut werden kann
          Thread.sleep(3000);

          System.out.println("Started");
          control.simulateSimulatorInputInLoop(connector);

          //control.updateData(connector, 'R');
        } catch (InterruptedException ex) {
          Logger.getLogger(AmbientDistControl.class.getName(), ex.getMessage());
        }
      }
    };
    t.start();
  }

  //Ansteuerung der LEDs nahe des Aussenspiegels
  public void updateData(LEDConnector connector, char side, float ttc) throws InterruptedException, IOException {
    int green = 0;
    int red = 0;
    int blue = 0;

    int maxBrightness = 127;

    //int pos = LEDWidth;
    float relTTC = ttc / 20;
    if (relTTC < 0) {
      return;
    }
    if (relTTC > 1) {
      return;
    }

// Time to Collision muss noch mit rein 
    if (side == 'l') {

      green = (int) (relTTC * maxBrightness);
      red = maxBrightness - green;
    } else if (side == 'r') {
      /* Über die LEDWidth kann die zeit gesteuert werden.
       127 ist die Stärker der Farbe, somit wird die Brightness 
       von Anfang an heruntergeregelt.
       */

      red = (int) ((1 - relTTC) * maxBrightness);
      green = maxBrightness - red;
    } else if (side == 'L') {

    }

    byte[] data = new byte[]{
      (byte) 254, // 
      (byte) ((char) red), // color
      (byte) ((char) green),
      (byte) ((char) blue),
      (byte) side // side
    };

    //senden der position
    connector.sendMessage(data);
  }

  private void simulateSimulatorInputInLoop(LEDConnector connector) {
    float distFrontLeft, distFront, distFrontRight;
    float distRearLeft, distRear, distRearRight;
    float speedFrontLeft, speedFront, speedFrontRight;
    float speedRearLeft, speedRear, speedRearRight;
    boolean indicatorLeft, indicatorRight;
    boolean onLeftLane, onRightLane;
    float ownSpeed;

    // scenario 1: left rear car approaching
    // init
    ownSpeed = 100 / 3.6f;
    speedRearLeft = 120 / 3.6f;
    speedFront = 80 / 3.6f;
    distRearLeft = 150;
    
    // loop
    while (true) {
      try {
        float ttcRearLeft = distRearLeft / (speedRearLeft - ownSpeed);

        try {
          updateData(connector, 'l', ttcRearLeft);
        } catch (InterruptedException | IOException ex) {
          Logger.getLogger(AmbientDistControl.class.getName()).log(Level.SEVERE, null, ex);
        }

        distRearLeft -= 0.1 * (speedRearLeft - ownSpeed);
        if (distRearLeft <= 0) {
          distRearLeft = 150;
        }
        Thread.sleep(100); // ~10Hz update rate
        
      } catch (InterruptedException ex) {
        Logger.getLogger(AmbientDistControl.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

  }
}

/*Macht keinen Sinn.....

 TESTING:
 pos = 17; 
 for (int j = 0; j < 127 ; j++)
 {
 //Ansteuerung der LEDs nahe des Aussenspiegels rechts
 //for (int i = 0; i <= LEDWidth; i++){
 red = (int) (( 1-((float)j/127))*127);
 green = 127 - red;
 color = new byte[]{
 (byte) ((char) green),
 (byte) ((char) red),
 (byte) ((char) blue)
 };
                    
                    
 sendMessage(new byte[]{(byte) pos});
 //senden der RGB-Farben
 sendMessage(color);
 //senden der Beifahrerseite
 sendMessage(new byte[]{(byte) side});
 //}
 } 
 ----------------------------------------------------------------------



 public static void Fahrzeug(char side, float speed, float distance, char Pattern) throws IOException{
 TestController.side = side;
 TestController.speed = speed;
 TestController.distance = distance;
 TestController.ttc = distance / speed;

        
 if(side == 'l'){
 for(float i = ttc; i > 0; i--){
 if (ttc <=20 && ttc >12){
                
 sendMessage(new byte[]{(byte) side});

 red = 0; green = 127; blue = 0;

 color = new byte[]{
 (byte) ((char) green),
 (byte) ((char) red),
 (byte) ((char) blue)
 };
 sendMessage(color);

 }
 if(ttc <=12 && ttc > 9.5){
 sendMessage(new byte[]{(byte) side});

 red = 0; 
 green = 127; 
 blue = 0;

 color = new byte[]{
 (byte) ((char) green),
 (byte) ((char) red),
 (byte) ((char) blue)
 };
 sendMessage(color); 
 }
 }
 } else if(side == 'r'){

 }
        
 }*/
/* Für später:
 setPattern, Position und Klasse für die Fahrzeuge erstellen
 */
