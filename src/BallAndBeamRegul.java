import SimEnvironment.AnalogSink;
import SimEnvironment.AnalogSource;

// BallAndBeamRegul class to be written by you
public class BallAndBeamRegul extends Thread {
    private AnalogSource analogInAngle;
    private AnalogSource analogInPosition;
    private AnalogSink analogOut;
    private AnalogSink analogRef;

    // Constructor
    public BallAndBeamRegul(ReferenceGenerator refgen, BallAndBeam bb, int priority) {

        analogInPosition = bb.getSource(0);
        analogInAngle = bb.getSource(1);
        analogOut = bb.getSink(0);
        analogRef = bb.getSink(1);
    }

    public void run() {

    }
}