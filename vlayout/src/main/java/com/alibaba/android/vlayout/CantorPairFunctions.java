package com.alibaba.android.vlayout;

/**
 * Cantor Pairing Function
 * <p>
 * <p>
 * see https://en.wikipedia.org/wiki/Pairing_function
 * <p>
 * <p>
 * <pre>
 * z = π(x,y) = ( x + y + 1 ) * ( x + y ) /2 + y
 * w = floor( (sqrt( 8 * z + 1) - 1) / 2)
 * t = (w^2+w)/2
 * y = z - t
 * x = w - y
 * </pre>
 *
 * @author lixi
 * @description <>
 * @date 2017/11/24
 */
public final class CantorPairFunctions {

    /**
     * process to uniquely encode two natural numbers into a single natural number
     *
     * @return cantor number
     */
    public static long process(long x, long y) {
        return (int) ((x + y) * (x + y + 1) / 2 + y);
    }


    /**
     * @param z cantor natural number {@link #process(long, long)}
     * @return the x from cantor number
     */
    public static long reverseX(long z) {
        final long w = (long) (Math.floor(Math.sqrt(8 * z + 1) - 1) / 2);
        final long t = (long) ((Math.pow(w, 2) + w) / 2);
        return (w - (z - t));
    }

    /**
     * @param z cantor natural number {@link #process(long, long)}
     * @return the y from cantor number
     */
    public static long reverseY(long z) {
        final long w = (long) (Math.floor(Math.sqrt(8 * z + 1) - 1) / 2);
        final long t = (long) ((Math.pow(w, 2) + w) / 2);
        return (z - t);
    }

}
