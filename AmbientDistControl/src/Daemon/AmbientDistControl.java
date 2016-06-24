package Daemon;

import Helper.Conversions;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.management.jmxremote.ConnectorBootstrap;

/**
 *
 * @author Patze
 */
public class AmbientDistControl {

    public static int LEDWidth = 17;
    public float relDist;

    public static void main(String[] args) throws Exception {
        Thread t = new Thread() {
            @Override
            public void run() {

                try {
                    //Wird gebraucht, damit die Connection aufgebaut werden kann
                    Thread.sleep(1500);
                    AmbientDistControl control = new AmbientDistControl();
                    LEDConnector connector = new LEDConnector();
                    connector.initialize();

                    System.out.println("Started");
                    Thread.sleep(1500);
                    control.simulateSimulatorInputInLoop(connector);

                    //control.updateData(connector, 'R');
                } catch (InterruptedException ex) {
                    Logger.getLogger(AmbientDistControl.class.getName(), ex.getMessage());
                } catch (IOException ex) {
                    Logger.getLogger(AmbientDistControl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        t.start();
    }

    public float distCalc(char side, float distance, float kmh) {
        float ttc = 0;

        if (side == 'l' || side == 'L') {
            // Pattern begins at a ttc = 20;
            ttc = distance / 20;

        }
        if (side == 'R' || side == 'r') {
            float safetyDist = kmh / 2;
            ttc = distance / safetyDist;
        }
        return ttc;
    }
    //Ansteuerung der LEDs nahe des Aussenspiegels

    public void updateData(LEDConnector connector, char side, float distance, float ownSpeedKmh) throws InterruptedException, IOException {
        int green = 0;
        int red = 0;
        int blue = 0;

        //Init LED Strip Data
        int maxLED = 53;
        int maxBrightness = 127;

        float relDist;

        byte pos = 0;

        //TTC = Time to Collision.. (Pattern begins at 20 seconds)
        relDist = distCalc(side, distance, ownSpeedKmh);

        if (relDist < 0) {
            return;
        }

        if (relDist > 1) {
            return;
        }

// Ausgeklammer, Quadratfunktionen
        switch (side) {
            case 'l':
                pos = 17;
                //red = (int) (Math.pow((1 - relDist),2) * maxBrightness);
                red = (int) ((1 - relDist) * maxBrightness);
                green = maxBrightness - red;
                break;
            case 'L':
                pos = (byte) ((1 - relDist) * maxLED);
                //red = (int) (Math.pow((1 - relDist),2) * maxBrightness);
                red = (int) ((1 - relDist) * maxBrightness);
                green = maxBrightness - red;
                if (pos == 0) {
                    return;
                }
                break;
            case 'r':
                pos = 17;
                //red = (int) (Math.pow((1 - relDist),2) * maxBrightness);
                red = (int) ((1 - relDist) * maxBrightness);
                green = maxBrightness - red + 1;
                break;
            case 'R':
                pos = (byte) ((relDist) * maxLED);
                red = (int) (Math.pow((relDist), 2) * maxBrightness);
                //red = (int) ((relDist) * maxBrightness);
                green = maxBrightness - red;
                if (pos == 0) {
                    return;
                }
                break;
            default:
                break;
        }

        byte[] data = new byte[]{
            (byte) ((char) blue),
            (byte) ((char) red),
            (byte) ((char) green), // color
            (byte) pos, // 
            (byte) side // side
        };

        //senden der der Daten
        connector.sendMessage(data);
    }

    private void simulateSimulatorInputInLoop(LEDConnector connector) throws IOException {

        //Simulator Data        
        float distFrontLeft, distFront, distFrontRight;
        float distRear, distRearRight, distMax; //distRearLeft
        float speedFrontLeft, speedFront, speedFrontRight;
        float speedRearLeft, speedRear, speedRearRight;
        boolean indicatorLeft, indicatorRight;
        boolean onLeftLane, onRightLane;
        float ownSpeed, ownSpeedKmh;

        // scenario 1: left rear car approaching
        // init
        ownSpeedKmh = 100;
        ownSpeed = ownSpeedKmh / 3.6f;
        speedRearLeft = 120 / 3.6f;
        speedFront = 80 / 3.6f;
        //distRearLeft = 150;
        distRearRight = 0;
        speedRearRight = 80 / 3.6f;
        distMax = 250;

        // loop, 
        // TODO: setOff() implementieren, damit nach dem Pattern das Licht ausgeht
        for (float distRearLeft = distMax; distRearLeft >= 0; distRearLeft -= (float) (0.1 * (speedRearLeft - ownSpeed))) {

            try {

                float ttcRearLeft = distRearLeft / (speedRearLeft - ownSpeed);

                try {

                    updateData(connector, 'L', ttcRearLeft, ownSpeedKmh);

                } catch (InterruptedException | IOException ex) {
                    Logger.getLogger(AmbientDistControl.class.getName()).log(Level.SEVERE, null, ex);
                }

                //distRearLeft -= 0.1 * (speedRearLeft - ownSpeed);
                if (distRearLeft <= 0) {
                    //distRearLeft = 150;
                    setOff(connector);
                }

                Thread.sleep(100); // ~10Hz update rate

            } catch (InterruptedException ex) {
                Logger.getLogger(AmbientDistControl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        /*for (distRearRight = 0; distRearRight <= distMax; distRearRight += (float) (0.1 * (speedRearLeft - ownSpeed))) {
            try {

                updateData(connector, 'R', distRearRight, ownSpeedKmh);

                //distRearRight += 0.1 * (ownSpeed - speedRearRight);

                /*if (distRearRight >= 100) {
                    //distRearRight = 0;
                    setOff(connector);
                }
                Thread.sleep(100); // ~10Hz update rate

            } catch (InterruptedException ex) {
                Logger.getLogger(AmbientDistControl.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        /*
        for (distRearRight = 0; distRearRight <= 100; distRearRight += (float) (0.1 * (speedRearLeft - ownSpeed))) {
            try {

                updateData(connector, 'R', distRearRight, ownSpeedKmh);

                //distRearRight += 0.1 * (ownSpeed - speedRearRight);

                /*if (distRearRight >= 100) {
                    //distRearRight = 0;
                    setOff(connector);
                }
                Thread.sleep(100); // ~10Hz update rate

            } catch (InterruptedException ex) {
                Logger.getLogger(AmbientDistControl.class.getName()).log(Level.SEVERE, null, ex);
            }

        }*/
    }

    @SuppressWarnings("empty-statement")
    public void setOff(LEDConnector connector) throws IOException {
        byte[] data = new byte[]{0, 0, 0, 0, 0};
        connector.sendMessage(data);
    }
}

/* Für später:
 setPattern,
 */
