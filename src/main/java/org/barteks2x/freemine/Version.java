package org.barteks2x.freemine;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.util.ResourceLoader;

public class Version {

	public static String getVersion() {
		String version = null;
		BufferedReader reader = null;
		try {
			InputStream is = ResourceLoader.getResourceAsStream("config");
			File file = new File("config");
			reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("project-version")) {
					String[] s = line.split("=");
					if (s.length >= 2) {
						version = s[1];
					}
				}
			}
		} catch (FileNotFoundException ex) {
			Logger.getLogger(FREEMine.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(FREEMine.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				reader.close();
			} catch (IOException ex) {
				Logger.getLogger(FREEMine.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return version;
	}
}
