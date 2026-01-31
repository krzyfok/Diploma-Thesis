public class Xoshiro256PlusPlus {
    private long x0, x1, x2, x3;

    public Xoshiro256PlusPlus(long seed) {
        SplitMix64 sm = new SplitMix64(seed);
        this.x0 = sm.next();
        this.x1 = sm.next();
        this.x2 = sm.next();
        this.x3 = sm.next();


        if (x0 == 0 && x1 == 0 && x2 == 0 && x3 == 0)
            throw new IllegalArgumentException("Error");
    }

    private long rotl(long x, int k) {
        return (x << k) | (x >>> (64 - k));
    }

    public long nextLong() {
        long result = rotl(x0 + x3, 23) + x0;
        long t = x1 << 17;
        x2 ^= x0;
        x3 ^= x1;
        x1 ^= x2;
        x0 ^= x3;
        x2 ^= t;
        x3 = rotl(x3, 45);
        return result;
    }
    public int nextInt() {
        long r = nextLong();
        return (int)(r >> 32)& 0x7FFFFFFF;
    }

    public double nextDouble() {
        long raw = nextLong();
        long shifted = raw>>>11;
        return (double)(shifted) / (1L << 53);
    }
}