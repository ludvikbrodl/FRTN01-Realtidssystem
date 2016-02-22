public class MainBB {
    public static void main(String[] argv) {
        final int regulPriority = 8;

        BallAndBeam bb = new BallAndBeam();

        ReferenceGenerator refgen = new ReferenceGenerator(8);
        BallAndBeamRegul regul = new BallAndBeamRegul(refgen, bb, regulPriority);

        refgen.start();
        regul.start();
    }
}