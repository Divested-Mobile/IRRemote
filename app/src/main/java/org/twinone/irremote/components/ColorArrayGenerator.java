package org.twinone.irremote.components;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ColorArrayGenerator {

	private static BufferedWriter out;

	private static void writeArray(String name, String color, int[] shades)
			throws Exception {
		out.write("<string name=\"color_" + color + "\">" + color
				+ "</string>\n");
		// out.write("<!-- " + name + " -->\n");
		// out.write("<array name=\"" + name + "\">\n");
		// // for (int shade : shades) {
		// // out.write("  <item>@color/material_" + color + "_" + shade
		// // + "</item>\n");
		// // }
		// out.write("</array>\n");
	}

	public static void main(String[] args) throws Exception {

		File outFile = new File("/home/twinone/material_color_arrays.xml");

		FileWriter fw = new FileWriter(outFile);
		out = new BufferedWriter(fw);

		out.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>");

		String[] colors = new String[] { "red", "pink", "purple",
				"deep_purple", "indigo", "blue", "light_blue", "cyan", "teal",
				"green", "light_green", "lime", "yellow", "amber", "orange",
				"deep_orange", "brown", "grey", "blue_grey" };

		int[] shades = new int[] { 500, 700 };
		int[] shades_pressed = new int[] { 700, 900 };
		for (String color : colors) {
			writeArray("gradient_" + color, color, shades);
			writeArray("gradient_" + color + "_pressed", color, shades_pressed);
		}
		out.write("</resources>\n");
		out.close();
	}
}
