package com.highvoltage.error;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		MoreCodeSquared start = new MoreCodeSquared();
		start.setCodeFileString(getCodeStringList("codeSpace/botCode.txt"));
		start.setItems(getCodeStringList("codeSpace/playerItems.txt"));
		float[][] enemyPos = convertStringToFloatArray(getCodeStringList("codeSpace/posInfo.txt")[0]);
		int enemyLevel = Integer.parseInt(getCodeStringList("codeSpace/posInfo.txt")[2]);
		String[] pos = getCodeStringList("codeSpace/posInfo.txt")[1].split(",");
		float[] bgPos = new float[] {Float.parseFloat(pos[0]), Float.parseFloat(pos[1])};
		start.initializePos(enemyPos,bgPos[0],bgPos[1], enemyLevel);
		initialize(start, config);
	}

	public String[] getCodeStringList(String path) {
		String[] codeLines;
		try {
			InputStream is = getAssets().open(path);
			codeLines = new String[is.available()];
			int fileLength = is.available();
			byte[] buffer = new byte[fileLength];
			is.read(buffer);
			String st = new String(buffer);
			codeLines = st.split("#", -2);
			for (int i = 0; i < codeLines.length; i++) {
				codeLines[i] = codeLines[i].trim();
			}
			return codeLines;
			//System.out.println("BILO: "+Arrays.toString(codeLines));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("BILO: " + e);
		}
		return null;
	}

	//[20.0, 20.0, 200.0, 200.0], [-500.0, 20.0, 200.0, 200.0]
	public static float[][] convertStringToFloatArray(String lines) {
		String ss;
		float[][] enPos = new float[0][4];
		String subset = lines.substring(1, lines.length() - 1);
		subset = subset.trim();
		int[] indeces = getIndexOfSplitCommas(subset);
		System.out.println(Arrays.toString(indeces));
		for (int i = 0; i < indeces.length + 1; i++) {
			if (i == 0) {
				ss = subset.substring(1, indeces[i] - 2);
			} else if (i < indeces.length) {
				ss = subset.substring(indeces[i - 1] + 3, indeces[i] - 2);
				System.out.println("E" + subset);
			} else { //If i is bigger than the length of indeces array
				ss = subset.substring(indeces[i - 1] + 3, subset.length() - 1);
			}
			String[] floats = ss.split(", ");
			float f1 = Float.parseFloat(floats[0]);
			float f2 = Float.parseFloat(floats[1]);
			float f3 = Float.parseFloat(floats[2]);
			float f4 = Float.parseFloat(floats[3]);
			enPos = Arrays.copyOf(enPos, enPos.length + 1);
			enPos[enPos.length - 1] = new float[]{f1, f2, f3, f4};
		}
		return enPos;
	}

	private static int[] getIndexOfSplitCommas(String line) {
		int count = 0;
		int prevCount = 0;
		int[] splitIndex = new int[0];
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == ',') {
				count++;
				System.out.println(line.charAt(i) + " " + i);
			}
			if (count % 4 == 0 && count != prevCount) {
				splitIndex = Arrays.copyOf(splitIndex, splitIndex.length + 1);
				splitIndex[splitIndex.length - 1] = i;
			}
			prevCount = count;
		}
		return splitIndex;
	}
}