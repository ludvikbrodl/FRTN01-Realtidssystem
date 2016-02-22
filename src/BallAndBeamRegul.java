import SimEnvironment.AnalogSink;
import SimEnvironment.AnalogSource;

// BallAndBeamRegul class to be written by you
public class BallAndBeamRegul extends Thread {
    private final ReferenceGenerator referenceGenerator;
    private final PI innerController;
    private final PID outerController;
    private AnalogSource analogInAngle;
    private AnalogSource analogInPosition;
    private AnalogSink analogOut;
    private AnalogSink analogRef;

    //Define min and max control output
    private double uMin = -10.0;
    private double uMax = 10.0;

    // Constructor
    public BallAndBeamRegul(ReferenceGenerator refgen, BallAndBeam bb, int priority) {
        referenceGenerator = refgen;
        innerController = new PI("PI");
        PIParameters PIparams = new PIParameters();
        outerController = new PID("PID");
        analogInPosition = bb.getSource(0);
        analogInAngle = bb.getSource(1);
        analogOut = bb.getSink(0);
        analogRef = bb.getSink(1);
        setPriority(priority);
    }

    public void run() {
        long t = System.currentTimeMillis();
        double angleRef;
        while (true) {
            // Read inputs
            double pos = analogInPosition.get();
            double angle = analogInAngle.get();
            double posRef = referenceGenerator.getRef();
            synchronized (outerController) {
                angleRef = outerController.calculateOutput(pos, posRef);
                synchronized (innerController) {
                    double u = limit(innerController.calculateOutput(angle, angleRef), uMin, uMax);
                    analogOut.set(u);
                    innerController.updateState(u);
                }
                outerController.updateState(angleRef);
            }
            analogRef.set(posRef); // Only for the plotter animation

            t = t + innerController.getHMillis();
            long duration = t - System.currentTimeMillis();
            if (duration > 0) {
                try {
                    sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Saturate output at limits
    private double limit(double u, double umin, double umax) {
        if (u < umin) {
            u = umin;
        } else if (u > umax) {
            u = umax;
        }
        return u;
    }
}