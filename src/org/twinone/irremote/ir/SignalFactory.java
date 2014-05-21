/*
 * Copyright 2014 Luuk Willemsen (Twinone)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.twinone.irremote.ir;

public class SignalFactory {

	public static final int FORMAT_PRONTO = 0;
	public static final int FORMAT_GLOBALCACHE = 1;

	/**
	 * Constructs a Signal object that can be sent on the IR blaster
	 */
	public static final Signal parse(String signal) {
		return parse(getFormat(signal), signal);
	}

	/**
	 * Attempt to get an appropriate format for this string, returns the best
	 * match
	 */
	public static int getFormat(String signal) {
		if (signal.startsWith("0000")) {
			return FORMAT_PRONTO;
		} else {
			return FORMAT_GLOBALCACHE;
		}
	}

	/** Parse a signal knowing it's format previously */
	public static final Signal parse(int format, String signal) {
		switch (format) {
		case FORMAT_PRONTO:
			return fromPronto(signal);
		case FORMAT_GLOBALCACHE:
			return fromGlobalCache(signal);
		default:
			throw new IllegalArgumentException("Invalid format");
		}
	}

	private static final Signal fromGlobalCache(String in) {
		// GlobalCache format is as follows:
		// Frequency,Repeat,Offset,On1,Off1, ... ,OnN,OffN
		// We ignore Repeat and Offset
		// TODO Add repeat
		final Signal out = new Signal();
		final String[] split = in.split(",");
		final long[] values = new long[split.length];
		for (int i = 0; i < split.length; i++) {
			values[i] = Long.parseLong(split[i]);
		}

		out.frequency = (int) values[0];
		final int offset = 3;
		final int[] pattern = new int[values.length - offset];
		for (int i = 0; i < pattern.length; i++) {
			pattern[i] = (int) values[offset + i];
		}

		out.pattern = pattern;
		return out;
	}

	private static final Signal fromPronto(String in) {
		final Signal out = new Signal();
		final String[] split = in.split(" ");
		final long[] pronto = new long[split.length];
		for (int i = 0; i < split.length; i++) {
			pronto[i] = Long.parseLong(split[i], 16);
		}

		if (pronto[0] != 0x0000)
			throw new IllegalArgumentException("Invalid pronto code");

		out.frequency = (int) (1000000 / (pronto[1] * 0.241246));

		final int bps1 = (int) pronto[2] * 2;
		final int bps2 = (int) pronto[3] * 2;
		final int offset = 4;

		final int[] pattern = new int[bps1 + bps2];
		for (int i = 0; i < pattern.length; i++) {
			pattern[i] = (int) pronto[offset + i];
		}

		out.pattern = pattern;
		return out;
	}
}
