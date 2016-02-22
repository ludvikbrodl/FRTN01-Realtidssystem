
public class Main {
	public static void main(String[] argv) {
		final int regulPriority = 8;
		
		Beam beam = new Beam();
		ReferenceGenerator refgen = new ReferenceGenerator(8);
		BeamRegul regul = new BeamRegul(refgen, beam, regulPriority);
		
		refgen.start();
		regul.start();
	}
}
