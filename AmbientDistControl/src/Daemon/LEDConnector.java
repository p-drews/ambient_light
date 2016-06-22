package Daemon;

/**
 *
 * @author Atulmaharaj
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.IOException;
import java.sql.Array;
import java.util.Arrays;
import java.util.Enumeration;

public class LEDConnector implements SerialPortEventListener {

    public SerialPort serialPort;
    /**
     * The port we're normally going to use.
     */
    private static final String PORT_NAMES[] = {
        /*"/dev/tty.usbserial-A9007UX1", // Mac OS X
     "/dev/ttyUSB0", // Linux*/
        "COM3", // Windows
    };

    //Variablen zum Speichern der zuletzt gesendeten Bytes
    public byte[] lastData1;
    public byte[] lastData2;
    public int pointer = 1;

    public BufferedReader input;
    public OutputStream output;
    /**
     * Milliseconds to block while waiting for port open
     */
    public static final int TIME_OUT = 2000;
    /**
     * Default bits per second for COM port.
     */
    public static final int DATA_RATE = 9600;

    public void initialize() {
        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

//First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }
        if (portId == null) {
            System.out.println("Could not find COM port.");
            return;
        }

        try {
// open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(),
                    TIME_OUT);

// set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

// open the streams
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();
            char ch = 1;
            output.write(ch);

// add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine = input.readLine();
                System.out.println(inputLine);
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }

    }

    public synchronized void sendMessage(byte[] data) throws IOException {
        //Vergleichen der Bytes damit nicht doppelt übertragen wird.
        if (Arrays.equals(lastData1, data)) {
            return;
        } else {

            //Print Bytemessage
            System.out.println(Arrays.toString(data));

            try {
                output.write(data);
                //Speichern des zuletzt gesendeten Bytes
                lastData1 = data;

            } catch (Exception e) {
                System.out.println("could not write to port");
            }
        }
    }

}
