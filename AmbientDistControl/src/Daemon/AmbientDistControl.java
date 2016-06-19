package Daemon;

import static Daemon.LEDConnector.*;
import Helper.Conversions;
import java.io.IOException;


/**
 *
 * @author Patze
 */
public class AmbientDistControl {
    
    
    public static int LEDWidth = 17;
    
    public static int green;
    public static int red;
    public static int blue = 0;
    
    static byte[] color; 
    
    private static char side;
    private static float speed;
    private static float distance;
    private static float ttc;
    public static int pos;
   
    
    public static void main(String[] args) throws Exception {
        LEDConnector main = new LEDConnector();
        main.initialize();
        Thread t = new Thread() {
            @Override
            public void run() {

                try {
                    //Wird gebraucht, damit die Connection aufgebaut werden kann
                    Thread.sleep(1500);
                                       
                    updateData('l');
                } catch (InterruptedException ex) {
                    
                } catch (IOException ex) {
                    
                    
                } 
            }
        };
        t.start();
        System.out.println("Started");
    }

    //Ansteuerung der LEDs nahe des Aussenspiegels
    public static void updateData(char side) throws InterruptedException, IOException{
        // Time to Collision muss noch mit rein 
        if(side == 'l'){
            for (int j = 0; j < 127 ; j++)
            {
                /* Über die LEDWidth kann die zeit gesteuert werden.
                    127 ist die Stärker der Farbe, somit wird die Brightness 
                    von Anfang an heruntergeregelt.
                */
                    green = (int) (( 1-((float)j/127))*127);
                    red = 127 - green;
                    color = new byte[]{
                        (byte) ((char) green),
                        (byte) ((char) red),
                        (byte) ((char) blue)
                    };
                    
                    //senden der Beifahrerseite
                    sendMessage(new byte[]{(byte) side});
                    //senden der RGB-Farben
                    sendMessage(color);
                    
                    //sendMessage(new byte[]{(byte) pos});
                    Thread.sleep(80);
                }
            
            
        } else if(side == 'r'){
                      
            for (int j = 0; j < 127 ; j++)
            {
                //Ansteuerung der LEDs nahe des Aussenspiegels rechts
                for (int i = 0; i <= LEDWidth; i++){
                    red = (int) (( 1-((float)j/127))*127);
                    green = 127 - red;
                    color = new byte[]{
                        (byte) ((char) green),
                        (byte) ((char) red),
                        (byte) ((char) blue)
                    };
                    //senden der Beifahrerseite
                    sendMessage(new byte[]{(byte) side});
                    //senden der RGB-Farben
                    sendMessage(color);
                }
            } 
        }         
    }
}
    /*Macht keinen Sinn.....

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