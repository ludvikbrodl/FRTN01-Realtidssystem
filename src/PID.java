// PID class to be written by you
public class PID {
    // Current PID parameters
    private PIDParameters p;

    // Constructor
    public PID(String name) {

    }

    // Calculates the control signal v.
    // Called from BallAndBeamRegul.
    public synchronized double calculateOutput(double y, double yref) {
        return 0;
    }

    // Updates the controller state.
    // Should use tracking-based anti-windup
    // Called from BallAndBeamRegul.
    public synchronized void updateState(double u){

    }

    // Returns the sampling interval expressed as a long.
    // Explicit type casting needed.
    public synchronized long getHMillis(){
        return 0;
    }

    // Sets the PIDParameters.
    // Called from PIDGUI.
    // Must clone newParameters.
    public synchronized void setParameters(PIDParameters newParameters){

    }
}