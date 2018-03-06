package org.twinone.irremote.providers.lirc;

import android.net.ParseException;
import android.util.Log;

import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.SignalFactory;

import java.util.ArrayList;
import java.util.List;

class LircParser {

    private final String[] mFile;
    private final ArrayList<IrCode> mCodes;
    private int mOneOn;
    private int mOneOff;

    // Pulses are sent in the following order:
    // header <phead> <shead>
    // plead <plead>
    // pre_data <(hex)>
    // pre <ppre> <spre>

    // the actual code data in hex
    private int mZeroOn;
    // post <ppost> <spost>
    // post_data <(hex)>
    // ptrail <ptrail>
    // foot <pfoot> <sfoot>
    private int mZeroOff;
    // number of bits per data
    private int mPreDataBits;
    private int mBits;
    private int mPostDataBits;
    private PulseSpacePair mHeader;
    private PulseSpacePair mLead;
    private String mPreDataString;
    private PulseSpacePair[] mPreData;
    private PulseSpacePair mPre;
    // Code goes here
    private PulseSpacePair mPost;
    private PulseSpacePair[] mPostData;
    private String mPostDataString;
    private PulseSpacePair mTrail;
    private PulseSpacePair mFoot;
    private int mFrequency = 38000;
    private int mPosition;

    public LircParser(String[] file) {
        for (String s : file) {
            Log.d("", "file: " + s);
        }
        mCodes = new ArrayList<>();

        ArrayList<String> list = new ArrayList<>();

        // Remove duplicate spaces, trim and remove comments
        for (String f : file) {
            final String s = f.replaceAll("\\s+", " ").trim();
            if (!s.startsWith("#") && !s.isEmpty()) {
                list.add(s);
            }
        }
        mFile = list.toArray(new String[list.size()]);
    }

    public LircListable[] parse() {
        for (mPosition = 0; mPosition < mFile.length; mPosition++) {
            expect("begin menu_main");
            readHeader();
            String codes = readLine();
            switch (codes) {
                case "begin codes":
                    readCodes();
                    break;
                case "begin raw_codes":
                    readRawCodes();
                    break;
                default:
                    throw new IllegalStateException(
                            "Expected begin codes or begin raw_codes block");
            }

            // Read the end codes or end raw_codes
            readLine();
            expect("end menu_main");
        }
        return mCodes.toArray(new IrCode[mCodes.size()]);
    }

    // Read a line and increase position
    private String readLine() {
        Log.d("", "Reading line: " + mFile[mPosition]);
        return mFile[mPosition++];
    }

    private void readHeader() {
        String s;
        while (!(s = readLine()).equals("begin codes")
                && !s.equals("begin raw_codes")) {
            final String[] ss = s.split(" ");
            final String param = ss[0];
            switch (param) {
                case "one":
                    mOneOn = Integer.parseInt(ss[1]);
                    mOneOff = Integer.parseInt(ss[1]);
                    break;
                case "zero":
                    mZeroOn = Integer.parseInt(ss[1]);
                    mZeroOff = Integer.parseInt(ss[1]);
                    break;
                case "frequency":
                    mFrequency = Integer.parseInt(ss[1]);
                    break;
                case "header":
                    mHeader = new PulseSpacePair(ss[1], ss[2]);
                    break;
                case "plead":
                    mLead = new PulseSpacePair(Integer.parseInt(ss[1]), 0);
                    break;
                case "pre_data":
                    mPreDataString = ss[1];
                    break;
                case "post_data":
                    mPostDataString = ss[1];
                    break;
                case "pre":
                    mPre = new PulseSpacePair(ss[1], ss[2]);
                    break;
                case "post":
                    mPost = new PulseSpacePair(ss[1], ss[2]);
                    break;
                case "ptrail":
                    mTrail = new PulseSpacePair(Integer.parseInt(ss[1]), 0);
                    break;
                case "foot":
                    mFoot = new PulseSpacePair(ss[1], ss[2]);
                    break;
                case "pre_data_bits":
                    mPreDataBits = Integer.parseInt(ss[1]);
                    break;
                case "post_data_bits":
                    mPostDataBits = Integer.parseInt(ss[1]);
                    break;
                case "bits":
                    mBits = Integer.parseInt(ss[1]);
                    break;
            }
        }

        if (mPreDataString != null) {
            Log.d("", "PredataString: " + mPreDataString);
            mPreData = decodeChunk(mPreDataBits, mPreDataString);
        }
        if (mPostDataString != null) {
            mPostData = decodeChunk(mPostDataBits, mPostDataString);
        }
        mPosition--;
    }

    private void readCodes() {
        String s;
        while (!(s = readLine()).equals("end codes")) {
            final String[] ss = s.split(" ");
            final String codeName = ss[0];
            final String code = ss[1];
            IrCode c = new IrCode(codeName, buildProntoCode(decodeChunk(mBits,
                    code)));
            mCodes.add(c);
        }
        mPosition--;
    }

    private String buildProntoCode(PulseSpacePair[] code) {
        ArrayList<Integer> list = new ArrayList<>();
        addToList(list, mHeader);
        addToList(list, mLead);
        addToList(list, mPreData);
        addToList(list, mPre);

        addToList(list, code);

        addToList(list, mPost);
        addToList(list, mPostData);
        addToList(list, mTrail);
        addToList(list, mFoot);

        return SignalFactory.toPronto(new Signal(mFrequency, toIntArray(list)));
    }

    private void addToList(ArrayList<Integer> list, PulseSpacePair[] pairs) {
        if (pairs == null)
            return;
        for (PulseSpacePair pair : pairs) {
            addToList(list, pair);
        }
    }

    private void addToList(ArrayList<Integer> list, PulseSpacePair pair) {
        if (pair == null)
            return;
        list.add(pair.on);
        list.add(pair.off);
    }

    private int[] toIntArray(List<Integer> integers) {
        int[] ret = new int[integers.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = integers.get(i);
        }
        return ret;
    }

    private PulseSpacePair[] decodeChunk(int bits, String hex) {
        ArrayList<PulseSpacePair> result = new ArrayList<>(bits);
        // parse a hex string without "0x"
        int num = Integer.parseInt(hex.substring(2), 16);
        String bitString = Integer.toBinaryString(num);
        // only keep bits we need
        if (bits < bitString.length()) {
            bitString = bitString.substring(bitString.length() - bits);
        } else {
            while (bits > bitString.length()) {
                bitString = "0" + bitString;
            }
        }
        Log.d("", "bits: " + bits + " string: " + bitString);
        for (int i = 0; i < bitString.length(); i++) {
            char c = bitString.charAt(i);
            if (c == '0') {
                result.add(new PulseSpacePair(mZeroOn, mZeroOff));
            } else {
                result.add(new PulseSpacePair(mOneOn, mOneOff));
            }
        }

        return result.toArray(new PulseSpacePair[result.size()]);
    }

    private void readRawCodes() {
        String s;
        while (!(s = readLine()).equals("end raw_codes")) {
            String[] ss = s.split(" ");
            if (ss.length != 2 || !ss[0].equals("name")) {
                throw new RuntimeException(
                        "Expected code name in raw_codes block instead of " + s);
            }
            final String name = ss[1];
            ArrayList<Integer> pattern = new ArrayList<>();
            while (!(s = readLine()).startsWith("name")
                    && !s.equals("end raw_codes")) {
                ss = s.split(" ");
                for (String pulse : ss) {
                    // pulses are in decimal
                    pattern.add(Integer.parseInt(pulse));
                }
            }
            Signal sig = new Signal(mFrequency, toIntArray(pattern));
            mCodes.add(new IrCode(name, SignalFactory.toPronto(sig)));
            mPosition--;
        }
        mPosition--;
    }

    private void expect(String what) {
        final String s = readLine();
        if (!s.equals(what)) {
            throw new RuntimeException("Expected '" + what + "' instead of '"
                    + s + "'");
        }
    }

    private class PulseSpacePair {
        public final int on;
        public final int off;

        public PulseSpacePair(int on, int off) {
            this.on = on;
            this.off = off;
        }

        public PulseSpacePair(String on, String off) {
            this.on = Integer.parseInt(on);
            this.off = Integer.parseInt(off);
        }

    }

}
