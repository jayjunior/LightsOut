public class BitOps {
    public static long set(long bitIndex,long bitSet){
        if(bitIndex < 0 ) throw new IllegalArgumentException("Just don't do That !");
        long mask = 1L << bitIndex;
        return bitSet | mask;
    }
    public static boolean isSet(long bitIndex , long bitSet){
        if(bitIndex < 0) throw new IllegalArgumentException("Just don't do That !");

        long mask = 1L << bitIndex;
        return (bitSet & mask) == mask;
    }
    public static long clear(long bitIndex , long bitSet){
        if(bitIndex < 0 ) throw new IllegalArgumentException("Just don't do That !");

        long mask = 1L << bitIndex;
        return bitSet & ~mask;
    }
    public static long flip(long bitIndex , long bitSet){
        if(bitIndex < 0 ) throw new IllegalArgumentException("Just don't do That !");

        long mask = 1L << bitIndex;
        return bitSet ^ mask;
    }
}
