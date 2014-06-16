package org.twinone.irremote.providers.lirc;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.ParseException;
import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.SignalFactory;

import android.util.Log;

public class LircParser {

	private int mOneOn;
	private int mOneOff;
	private int mZeroOn;
	private int mZeroOff;

	// Pulses are sent in the following order:
	// header <phead> <shead>
	// plead <plead>
	// pre_data <(hex)>
	// pre <ppre> <spre>

	// the actual code data in hex

	// post <ppost> <spost>
	// post_data <(hex)>
	// ptrail <ptrail>
	// foot <pfoot> <sfoot>

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

	private class PulseSpacePair {
		public int on;
		public int off;

		public PulseSpacePair(int on, int off) {
			this.on = on;
			this.off = off;
		}

		public PulseSpacePair(String on, String off) {
			this.on = Integer.parseInt(on);
			this.off = Integer.parseInt(off);
		}

	}

	private int mFrequency = 38000;

	private ArrayList<IrCode> mCodes;

	private int mPosition;

	private final String[] mFile;

	public LircParser(String[] file) {
		for (String s : file) {
			Log.d("", "file: " + s);
		}
		mCodes = new ArrayList<IrCode>();

		ArrayList<String> list = new ArrayList<String>();

		// Remove duplicate spaces, trim and remove comments
		for (int i = 0; i < file.length; i++) {
			final String s = file[i].replaceAll("\\s+", " ").trim();
			if (!s.startsWith("#") && !s.isEmpty()) {
				list.add(s);
			}
		}
		mFile = list.toArray(new String[list.size()]);
	}

	public LircListable[] parse() {
		for (mPosition = 0; mPosition < mFile.length; mPosition++) {
			expect("begin remote");
			readHeader();
			String codes = readLine();
			if (codes.equals("begin codes")) {
				readCodes();
			} else if (codes.equals("begin raw_codes")) {
				readRawCodes();
			} else {
				throw new ParseException(
						"Expected begin codes or begin raw_codes block");
			}

			// Read the end codes or end raw_codes
			readLine();
			expect("end remote");
		}
		return mCodes.toArray(new IrCode[mCodes.size()]);
	}

	// Read a line and increase position
	private String readLine() {
		Log.d("", "Reading line: " + mFile[mPosition]);
		return mFile[mPosition++];
	}

	private void readHeader() {
		String s = null;
		while (!(s = readLine()).equals("begin codes")
				&& !s.equals("begin raw_codes")) {
			final String[] ss = s.split(" ");
			final String param = ss[0];
			if (param.equals("one")) {
				mOneOn = Integer.parseInt(ss[1]);
				mOneOff = Integer.parseInt(ss[1]);
			} else if (param.equals("zero")) {
				mZeroOn = Integer.parseInt(ss[1]);
				mZeroOff = Integer.parseInt(ss[1]);
			} else if (param.equals("frequency")) {
				mFrequency = Integer.parseInt(ss[1]);
			} else if (param.equals("header")) {
				mHeader = new PulseSpacePair(ss[1], ss[2]);
			} else if (param.equals("plead")) {
				mLead = new PulseSpacePair(Integer.parseInt(ss[1]), 0);
			} else if (param.equals("pre_data")) {
				mPreDataString = ss[1];
			} else if (param.equals("post_data")) {
				mPostDataString = ss[1];
			} else if (param.equals("pre")) {
				mPre = new PulseSpacePair(ss[1], ss[2]);
			} else if (param.equals("post")) {
				mPost = new PulseSpacePair(ss[1], ss[2]);
			} else if (param.equals("ptrail")) {
				mTrail = new PulseSpacePair(Integer.parseInt(ss[1]), 0);
			} else if (param.equals("foot")) {
				mFoot = new PulseSpacePair(ss[1], ss[2]);
			} else if (param.equals("pre_data_bits")) {
				mPreDataBits = Integer.parseInt(ss[1]);
			} else if (param.equals("post_data_bits")) {
				mPostDataBits = Integer.parseInt(ss[1]);
			} else if (param.equals("bits")) {
				mBits = Integer.parseInt(ss[1]);
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
		String s = null;
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
		Signal s = new Signal();
		s.frequency = mFrequency;
		ArrayList<Integer> list = new ArrayList<Integer>();
		addToList(list, mHeader);
		addToList(list, mLead);
		addToList(list, mPreData);
		addToList(list, mPre);

		addToList(list, code);

		addToList(list, mPost);
		addToList(list, mPostData);
		addToList(list, mTrail);
		addToList(list, mFoot);

		s.pattern = toIntArray(list);
		return SignalFactory.toPronto(s);
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
			ret[i] = integers.get(i).intValue();
		}
		return ret;
	}

	private PulseSpacePair[] decodeChunk(int bits, String hex) {
		ArrayList<PulseSpacePair> result = new ArrayList<PulseSpacePair>(bits);
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
		String s = null;
		while (!(s = readLine()).equals("end raw_codes")) {
			String[] ss = s.split(" ");
			if (ss.length != 2 || !ss[0].equals("name")) {
				throw new RuntimeException(
						"Expected code name in raw_codes block instead of " + s);
			}
			final String name = ss[1];
			Signal sig = new Signal();
			sig.frequency = mFrequency;
			ArrayList<Integer> pattern = new ArrayList<Integer>();
			while (!(s = readLine()).startsWith("name")
					&& !s.equals("end raw_codes")) {
				ss = s.split(" ");
				for (String pulse : ss) {
					// pulses are in decimal
					pattern.add(Integer.parseInt(pulse));
				}
			}
			sig.pattern = toIntArray(pattern);
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

}
