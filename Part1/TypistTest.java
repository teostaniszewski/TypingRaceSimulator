public class TypistTest
{
    public static void main(String[] args)
    {
        Typist t = new Typist('1', "TEST", 0.5);

        // TEST 1 - slideBack should not go below 0
        System.out.println("TEST 1 - slideBack below zero");
        t.slideBack(5);
        System.out.println("Progress (should be 0): " + t.getProgress());

        // TEST 2 - burnout countdown
        System.out.println("\nTEST 2 - burnout countdown");
        t.burnOut(2);
        System.out.println("Burnout after burnOut(2) (should be true): " + t.isBurntOut());
        System.out.println("Turns left (should be 2): " + t.getBurnoutTurnsRemaining());

        t.recoverFromBurnout();
        System.out.println("Turns left after 1 recovery (should be 1): " + t.getBurnoutTurnsRemaining());
        System.out.println("Still burnt out (should be true): " + t.isBurntOut());

        t.recoverFromBurnout();
        System.out.println("Turns left after 2 recoveries (should be 0): " + t.getBurnoutTurnsRemaining());
        System.out.println("Burnout cleared (should be false): " + t.isBurntOut());

        // TEST 3 - resetToStart
        System.out.println("\nTEST 3 - resetToStart");

        t.typeCharacter();
        t.burnOut(2);

        System.out.println("Progress before reset: " + t.getProgress());
        System.out.println("Burnout before reset: " + t.isBurntOut());
        System.out.println("Turns before reset: " + t.getBurnoutTurnsRemaining());

        t.resetToStart();

        System.out.println("Progress after reset (should be 0): " + t.getProgress());
        System.out.println("Burnout after reset (should be false): " + t.isBurntOut());
        System.out.println("Turns after reset (should be 0): " + t.getBurnoutTurnsRemaining());

        // TEST 4 - setAccuracy clamping
        System.out.println("\nTEST 4 - setAccuracy clamping");

        t.setAccuracy(-1.0);
        System.out.println("Accuracy after setting -1.0 (should be 0.0): " + t.getAccuracy());

        t.setAccuracy(2.0);
        System.out.println("Accuracy after setting 2.0 (should be 1.0): " + t.getAccuracy());

        t.setAccuracy(0.75);
        System.out.println("Accuracy after setting 0.75 (should be 0.75): " + t.getAccuracy());

        // TEST 5 - typeCharacter forward movement
        System.out.println("\nTEST 5 - typeCharacter forward movement");

        t.resetToStart();
        System.out.println("Progress before typing (should be 0): " + t.getProgress());

        t.typeCharacter();
        System.out.println("Progress after 1 type (should be 1): " + t.getProgress());

        t.typeCharacter();
        System.out.println("Progress after 2 types (should be 2): " + t.getProgress());
    }
}