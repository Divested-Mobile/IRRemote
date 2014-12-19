package org.twinone.irremote.ir;

import java.util.Arrays;

public class Signal {

    public static final int FORMAT_UNKNOWN = -1;
    public static final int FORMAT_AUTO = 0;
    public static final int FORMAT_PRONTO = 1;
    public static final int FORMAT_GLOBALCACHE = 2;

    private int mFrequency;
    private int[] mPattern;
    private boolean isFixed;

    public Signal(int frequency, int[] pattern) {
        this.mFrequency = frequency;
        setPattern(pattern);
    }

    public int getFrequency() {
        return mFrequency;
    }

    public void setFrequency(int frequency) {
        mFrequency = frequency;
    }

    public int[] getPattern() {
        return mPattern;
    }

    void setPattern(int[] pattern) {
        mPattern = absPattern(Arrays.copyOf(pattern, pattern.length));
    }

    private int[] absPattern(int[] pattern) {
        for (int i = 0; i < pattern.length; i++) {
            final int s = pattern[i];
            if (s < 0) {
                pattern[i] = -s;
            } else if (s == 0) {
                pattern[i] = 1;
            }
        }

        return pattern;
    }

    @Override
    public Signal clone() {
        Signal s = new Signal(mFrequency, mPattern);
        s.isFixed = isFixed;
        return s;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Fixed:" + isFixed + " Signal@"
                + mFrequency + "/");
        boolean a = false;
        for (int i : mPattern) {
            if (a)
                sb.append(',');
            a = true;
            sb.append(i);
        }
        return sb.toString();
    }

    public Signal fix(SignalCorrector sc) {
        if (isFixed)
            return this;
        setPattern(sc.fix(mFrequency, mPattern));
        // this.mPattern = sc.fix(mFrequency, mPattern);
        isFixed = true;
        return this;
    }

}
