package org.twinone.irremote.ir;

import java.util.ArrayList;

public class SignalFactory {

    /**
     * Auto detect the format, and parse the signal
     */
    public static Signal parse(String signal) {
        final int format = getFormat(signal);
        if (format == Signal.FORMAT_UNKNOWN) {
            throw new RuntimeException("Could not parse signal (" + signal
                    + ")");
        }
        return parse(format, signal);
    }

    /**
     * Attempt to get an appropriate format for this string, returns the best
     * match
     */
    private static int getFormat(String signal) {
        if (signal == null || signal.isEmpty())
            return Signal.FORMAT_UNKNOWN;
        if (signal.startsWith("0000")) {
            return Signal.FORMAT_PRONTO;
        } else {
            return Signal.FORMAT_GLOBALCACHE;
        }
    }

    /**
     * Parse a signal knowing it's format previously
     */
    public static Signal parse(int format, String signal) {
        switch (format) {
            case Signal.FORMAT_AUTO:
                return parse(signal);
            case Signal.FORMAT_PRONTO:
                return fromPronto(signal);
            case Signal.FORMAT_GLOBALCACHE:
                return fromGlobalCache(signal);
            default:
                throw new IllegalArgumentException("Invalid format");
        }
    }

    public static String toGlobalCache(Signal s) {
        int[] pattern = s.getPattern();
        StringBuilder sb = new StringBuilder(pattern[0]);
        for (int i = 1; i < pattern.length; i++)
            sb.append(',').append(pattern[i]);
        return sb.toString();
    }

    private static Signal fromGlobalCache(String in) {
        // GlobalCache format is as follows:
        // Frequency[,Repeat,Offset],On1,Off1, ... ,OnN,OffN
        // We ignore Repeat and Offset
        in = in.trim();
        final String[] split = in.split(",");
        final long[] values = new long[split.length];
        for (int i = 0; i < split.length; i++) {
            values[i] = Long.parseLong(split[i]);
        }
        int freq = (int) values[0];

        // TODO 1 or 3 for offset, depending on repeat and offset being present
        final int offset = 1;
        final int[] pattern = new int[values.length - offset];
        for (int i = 0; i < pattern.length; i++) {
            pattern[i] = (int) values[offset + i];
            if (pattern[i] <= 0)
                pattern[i] = 1;
        }

        return new Signal(freq, pattern);
    }

    private static Signal fromPronto(String in) {
        in = in.trim();
        final String[] split = in.split(" ");
        final long[] pronto = new long[split.length];
        for (int i = 0; i < split.length; i++) {
            pronto[i] = Long.parseLong(split[i], 16);
        }

        if (pronto[0] != 0)
            throw new IllegalArgumentException("Invalid pronto code");

        int freq = (int) (1000000 / (pronto[1] * 0.241246));

        final int bps1 = (int) pronto[2] * 2;
        final int bps2 = (int) pronto[3] * 2;
        final int offset = 4;

        final int[] pattern = new int[bps1 + bps2];
        for (int i = 0; i < pattern.length; i++) {
            pattern[i] = (int) pronto[offset + i];
            if (pattern[i] <= 0)
                pattern[i] = 1;
        }

        return new Signal(freq, pattern);
    }

    /**
     * Converts a NOT FIXED signal to Pronto format
     */
    public static String toPronto(Signal s) {
        if (s == null)
            return null;
        // All pronto starts with 0000
        ArrayList<Long> pronto = new ArrayList<>();
        pronto.add(0L);

        int frequency = (int) (1000000 / (s.getFrequency() * 0.241246));
        pronto.add((long) frequency);

        // Add bps
        int bps = s.getPattern().length / 2;
        pronto.add((long) bps);
        // 2nd bps = 0
        pronto.add(0L);

        for (int i : s.getPattern()) {
            pronto.add((long) i);
        }

        // Make the array readable
        StringBuilder out = new StringBuilder();
        boolean append = false;
        for (long l : pronto) {
            if (append) {
                out.append(' ');
            } else
                append = true;
            String hex = Long.toHexString(l);
            // TODO should we use leading zeroes? The parser doesn't care...
            while (hex.length() < 4) {
                hex = "0" + hex;
            }
            out.append(hex);
        }

        return out.toString();
    }

}
