/* 
 * The MIT License
 *
 * Copyright 2014 Bartosz Skrzypczak.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.barteks2x.cubit.util;

import com.github.barteks2x.cubit.util.logging.LoggerUtil;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Version {

    private static final Logger logger = LoggerUtil.getLogger(Version.class);

    public static String getVersion() {
        String version = null;
        BufferedReader reader = null;
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().
                    getResourceAsStream("config");
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
            logger.log(Level.SEVERE, "Config file not found", ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Couldn't open config file!", ex);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE,  "Exception when closing input stream!", ex);
            }
        }
        return version;
    }
}
