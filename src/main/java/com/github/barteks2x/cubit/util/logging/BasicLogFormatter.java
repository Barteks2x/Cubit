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

import com.github.barteks2x.cubit.util.ExceptionUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * Global log formatter class.
 */
class BasicLogFormatter extends SimpleFormatter {

    private static final SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String format = "[%3$s][%4$s][%1$s] %5$s%6$s%n";
    private final Date date = new Date();

    @Override
    public synchronized String format(LogRecord record) {
        date.setTime(record.getMillis());
        String source = getSource(record);
        String message = formatMessage(record);
        String throwable = getThrowMsg(record);
        return String.format(format, dateFmt.format(date), source, getLoggerName(record), record.getLevel().getLocalizedName(), message, throwable);
    }

    private String getThrowMsg(LogRecord record) {
        return ExceptionUtil.getStacktraceMessage(record.getThrown());
    }

    public String getSource(LogRecord record) {
        String source = record.getSourceMethodName();
        return source == null ? "(unknown)" : source;
    }

    private String getLoggerName(LogRecord lr) {
        String name = lr.getLoggerName();
        if (!name.contains(".")) {
            return name;
        }
        String[] s = name.split("\\.");
        return s[s.length - 1];
    }

}
