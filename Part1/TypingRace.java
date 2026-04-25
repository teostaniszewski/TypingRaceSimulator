import java.util.concurrent.TimeUnit;
import java.lang.Math;

/**
 * A typing race simulation. Three typists race to complete a passage of text,
 * advancing character by character — or sliding backwards when they mistype.
 *
 * Originally written by Ty Posaurus, who left this project to "focus on his
 * two-finger technique". He assured us the code was "basically done".
 * We have found evidence to the contrary.
 *
 * @author TyPosaurus
 * @author Teo Staniszewski (modifications and bug fixes)
 * @version 1
 */
public class TypingRace
{
    private int passageLength;   // Total characters in the passage to type
    private Typist seat1Typist;
    private Typist seat2Typist;
    private Typist seat3Typist;
    
    private boolean seat1JustMistyped;
    private boolean seat2JustMistyped;
    private boolean seat3JustMistyped;

    // Accuracy thresholds for mistype and burnout events
    private static final double MISTYPE_BASE_CHANCE = 0.3;
    private static final int    SLIDE_BACK_AMOUNT   = 2;
    private static final int    BURNOUT_DURATION     = 3;

    /**
     * Constructor for objects of class TypingRace.
     * Sets up the race with a passage of the given length.
     * Initially there are no typists seated.
     *
     * @param passageLength the number of characters in the passage to type
     */
    public TypingRace(int passageLength)
    {
        this.passageLength = passageLength;
        seat1Typist = null;
        seat2Typist = null;
        seat3Typist = null;
        seat1JustMistyped = false;
        seat2JustMistyped = false;
        seat3JustMistyped = false;
    }

    /**
     * Seats a typist at the given seat number (1, 2, or 3).
     *
     * @param theTypist  the typist to seat
     * @param seatNumber the seat to place them in (1–3)
     */
    public void addTypist(Typist theTypist, int seatNumber)
    {
        if (seatNumber == 1)
        {
            seat1Typist = theTypist;
        }
        else if (seatNumber == 2)
        {
            seat2Typist = theTypist;
        }
        else if (seatNumber == 3)
        {
            seat3Typist = theTypist;
        }
        else
        {
            System.out.println("Cannot seat typist at seat " + seatNumber + " — there is no such seat.");
        }
    }

    /**
     * Starts the typing race.
     * All typists are reset to the beginning, then the simulation runs
     * turn by turn until one typist completes the full passage.
     */
    public void startRace()
    {
        boolean finished = false;
        Typist winner = null;

        // Reset all typists to the start of the passage
        if (seat1Typist != null)
        {
            seat1Typist.resetToStart();
        }
        if (seat2Typist != null)
        {
            seat2Typist.resetToStart();
        }
        if (seat3Typist != null)
        {
            seat3Typist.resetToStart();
        }

        while (!finished)
        {
            seat1JustMistyped = false;
            seat2JustMistyped = false;
            seat3JustMistyped = false;

            // Advance each typist by one turn
            advanceTypist(seat1Typist, 1);
            advanceTypist(seat2Typist, 2);
            advanceTypist(seat3Typist, 3);

            // Print the current state of the race
            printRace();

            // Check if any typist has finished the passage
            if (raceFinishedBy(seat1Typist))
            {
                winner = seat1Typist;
                finished = true;
            }
            else if (raceFinishedBy(seat2Typist))
            {
                winner = seat2Typist;
                finished = true;
            }
            else if (raceFinishedBy(seat3Typist))
            {
                winner = seat3Typist;
                finished = true;
            }

            // Wait 200ms between turns so the animation is visible
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (Exception e) {}
        }

        if (winner != null)
        {
            double oldAccuracy = winner.getAccuracy();
            winner.setAccuracy(oldAccuracy + 0.02);

            System.out.println();
            System.out.println("And the winner is... " + winner.getName() + "!");
            System.out.println("Final accuracy: " + formatAccuracy(winner.getAccuracy())
                + " (improved from " + formatAccuracy(oldAccuracy) + ")");
        }
    }

    /**
     * Simulates one turn for a typist.
     *
     * If the typist is burnt out, they recover one turn's worth and skip typing.
     * Otherwise:
     *   - They may type a character (advancing progress) based on their accuracy.
     *   - They may mistype (sliding back) — the chance of a mistype should decrease
     *     for more accurate typists.
     *   - They may burn out — more likely for very high-accuracy typists
     *     who are pushing themselves too hard.
     *
     * @param theTypist the typist to advance
     * @param seatNumber the seat number (1–3) of the typist
     */
    private void advanceTypist(Typist theTypist, int seatNumber)
    {
        if (theTypist == null)
        {
            return;
        }

        if (theTypist.isBurntOut())
        {
            // Recovering from burnout — skip this turn
            theTypist.recoverFromBurnout();
            return;
        }

        // Attempt to type a character
        if (Math.random() < theTypist.getAccuracy())
        {
            theTypist.typeCharacter();
        }

        // Mistype check — the probability should reflect the typist's accuracy
        if (Math.random() < (1.0 - theTypist.getAccuracy()) * MISTYPE_BASE_CHANCE)
        {
            theTypist.slideBack(SLIDE_BACK_AMOUNT);
            markMistyped(seatNumber);
        }

        // Burnout check — pushing too hard increases burnout risk
        // (probability scales with accuracy squared, capped at ~0.05)
        if (Math.random() < 0.05 * theTypist.getAccuracy() * theTypist.getAccuracy())
        {
            theTypist.burnOut(BURNOUT_DURATION);
            theTypist.setAccuracy(theTypist.getAccuracy() - 0.01);
        }
    }

    /**
     * Returns true if the given typist has completed the full passage.
     *
     * @param theTypist the typist to check
     * @return true if their progress has reached or passed the passage length
     */
    private boolean raceFinishedBy(Typist theTypist)
    {
        if (theTypist == null)
        {
            return false;
        }

        return theTypist.getProgress() >= passageLength;
    }

    /**
     * Marks that a typist in a given seat has just mistyped during this turn.
     * This is used to display the [<] indicator in the race output.
     *
     * @param seatNumber the seat number of the typist (1–3)
     */
    private void markMistyped(int seatNumber)
    {
        if (seatNumber == 1)
        {
            seat1JustMistyped = true;
        }
        else if (seatNumber == 2)
        {
            seat2JustMistyped = true;
        }
        else if (seatNumber == 3)
        {
            seat3JustMistyped = true;
        }
    }

    /**
     * Prints the current state of the race to the terminal.
     * Shows each typist's position along the passage, burnout state,
     * and a WPM estimate based on current progress.
     */
    private void printRace()
    {
        System.out.print('\u000C'); // Clear terminal

        System.out.println("  TYPING RACE - passage length: " + passageLength + " chars");
        multiplePrint('=', passageLength + 3);
        System.out.println();

        printSeat(seat1Typist, seat1JustMistyped);
        System.out.println();

        printSeat(seat2Typist, seat2JustMistyped);
        System.out.println();

        printSeat(seat3Typist, seat3JustMistyped);
        System.out.println();

        multiplePrint('=', passageLength + 3);
        System.out.println();
        System.out.println("  [~] = burnt out    [<] = just mistyped");
    }

    /**
     * Prints a single typist's lane.
     *
     * Examples:
     *   |          1           | TURBOFINGERS (Accuracy: 0.85)
     *   |    2~              | HUNT_N_PECK  (Accuracy: 0.40) BURNT OUT (2 turns)
     *
     * @param theTypist the typist whose lane to print
     * @param justMistyped whether to show the [<] indicator for a recent mistype
     */
    private void printSeat(Typist theTypist, boolean justMistyped)
    {
        if (theTypist == null)
        {
            return;
        }
        int spacesBefore = theTypist.getProgress();
        int spacesAfter  = passageLength - theTypist.getProgress();

        System.out.print('|');
        multiplePrint(' ', spacesBefore);

        // Always show the typist's symbol so they can be identified on screen.
        // Append ~ when burnt out so the state is visible without hiding identity.
        System.out.print(theTypist.getSymbol());
        if (theTypist.isBurntOut())
        {
            System.out.print('~');
            spacesAfter--; // symbol + ~ together take two characters
        }

        if (justMistyped)
        {
            System.out.print(" [<]");
            spacesAfter = spacesAfter - 4;
        }

        if (spacesAfter < 0)
        {
            spacesAfter = 0;
        }

        multiplePrint(' ', spacesAfter);
        System.out.print('|');
        System.out.print(' ');

        // Print name and accuracy
        if (theTypist.isBurntOut())
        {
            System.out.print(theTypist.getName()
                + " (Accuracy: " + formatAccuracy(theTypist.getAccuracy()) + ")"
                + " BURNT OUT (" + theTypist.getBurnoutTurnsRemaining() + " turns)");
        }
        else
        {
            System.out.print(theTypist.getName()
                + " (Accuracy: " + formatAccuracy(theTypist.getAccuracy()) + ")");
        }

        if (justMistyped)
        {
            System.out.print(" <- just mistyped");
        }
    }

    /**
     * Formats an accuracy value to two decimal places for clearer output.
     *
     * @param accuracyValue the accuracy value to format
     * @return the formatted accuracy value as a String
     */
    private String formatAccuracy(double accuracyValue)
    {
        return String.format("%.2f", accuracyValue);
    }

    /**
     * Prints a character a given number of times.
     *
     * @param aChar the character to print
     * @param times how many times to print it
     */
    private void multiplePrint(char aChar, int times)
    {
        int i = 0;
        while (i < times)
        {
            System.out.print(aChar);
            i = i + 1;
        }
    }

}