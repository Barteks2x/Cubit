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
package com.github.barteks2x.cubit.util.logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public class LoggerUtil {

    private LoggerUtil() {
    }

    public static void initLoggers() throws IOException {
        Logger global = Logger.getLogger("");
        Handler[] handlers = global.getHandlers();
        for (Handler handler : handlers) {
            global.removeHandler(handler);
        }
        assert global.getHandlers().length == 0;
        Handler fileHandler = new FileHandler("OpenMine.log", 1024 * 1024, 20, false);
        fileHandler.setLevel(Level.ALL);
        fileHandler.setFormatter(new BasicLogFormatter());
        Handler outHandler = new StreamHandler(System.err, new BasicLogFormatter()) {
            @Override
            public synchronized void publish(LogRecord lr) {
                super.publish(lr);
                super.flush();
            }
        };
        outHandler.setLevel(Level.ALL);

        global.addHandler(outHandler);
        global.addHandler(fileHandler);
    }

    public static Logger getLogger(Class<? extends Object> clazz) {
        return Logger.getLogger(clazz.getName());
    }
}
