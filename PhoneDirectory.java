/*
 * TC: O(n) for get, O(1) for check and release
 * SC: O(n) for the released set
 */
import java.util.BitSet;

public class PhoneDirectory {

    // the maximum size of the PhoneDirectory
    private final int maxSize;

    // the highest number returned by get so far
    private int maxNum;

    // set of all released numbers
    private final BitSet released;

    public PhoneDirectory(int maxNumbers) {
        maxSize = maxNumbers;
        maxNum = 0;
        released = new BitSet();
    }

    public int get() {
        // get a released number if available
        if(!released.isEmpty()) {
            int number = released.nextSetBit(0);
            released.flip(number);
            return number;
        }
        // get the next number under maximum size
        if(maxNum < maxSize) {
            return maxNum++;
        }
        return -1;
    }

    public boolean check(int number) {
        // number should be in [maxNum, maxSize)
        // or in the released set to be available
        return number >= maxNum || released.get(number);
    }

    public void release(int number) {
        // only if it is not available can it be released
        if(!check(number)) {
            // add it to the released set
            released.set(number);
        }
    }
}
