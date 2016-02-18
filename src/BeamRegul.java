import SimEnvironment.*;

public class BeamRegul extends Thread {
	private ReferenceGenerator referenceGenerator;
	private PI controller;

	private AnalogSource analogIn;
	private AnalogSink analogOut;
	private AnalogSink analogRef;
	
	//Define min and max control output
	private double uMin = -10.0;
	private double uMax = 10.0;

	//Constructor
	public BeamRegul(ReferenceGenerator ref, Beam beam, int pri) {
		referenceGenerator = ref;
		controller = new PI("PI");
		analogIn = beam.getSource(0);
		analogOut = beam.getSink(0);
		analogRef = beam.getSink(1);
		setPriority(pri);
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
	
	public void run() {
		long t = System.currentTimeMillis();
		while (true) {
			// Read inputs
			double y = analogIn.get();
			double ref = referenceGenerator.getRef();
			
			synchronized (controller) { // To avoid parameter changes in between
				// Compute control signal
				double u = limit(controller.calculateOutput(y, ref), uMin, uMax);
				
				// Set output
				analogOut.set(u);
				
				// Update state
				controller.updateState(u);
			}
			analogRef.set(ref); // Only for the plotter animation
			
			t = t + controller.getHMillis();
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
}
