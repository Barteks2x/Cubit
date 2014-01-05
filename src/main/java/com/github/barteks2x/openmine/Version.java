package com.github.barteks2x.openmine;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Version {

	public static String getVersion() {
		String version = null;
		BufferedReader reader = null;
		try {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(
					"config");
			reader = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("describe")) {
					String[] s = line.split("=");
					if (s.length >= 2) {
						version = s[1];
					}
				}
			}
		} catch (FileNotFoundException ex) {
			Logger.getLogger(OpenMine.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(OpenMine.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ex) {
				Logger.getLogger(OpenMine.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return version;
	}
}
