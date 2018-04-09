/*
* Copyright © 2014 George T. Steel
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.linxiao.framework.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.Spannable;

/**
 * Provides {@link String#format} style functions that work with {@link Spanned} strings and preserve formatting.
 * 
 * @author George T. Steel
 *
 */
public class SpanFormatter {
	
	private static final Pattern FORMAT_SEQUENCE	= Pattern.compile("%([0-9]+\\$|<?)([^a-zA-z%]*)([[a-zA-Z%]&&[^tT]]|[tT][a-zA-Z])");
	
	private SpanFormatter(){}
	
	/**
     * Version of {@link String#format(String, Object...)} that works on {@link Spanned} strings to preserve rich text formatting.
	 * Both the {@code format} as well as any {@code %s args} can be Spanned and will have their formatting preserved.
	 * Due to the way {@link Spannable}s work, any argument's spans will can only be included <b>once</b> in the result.
	 * Any duplicates will appear as text only. 
     * 
	 * @param format the format string (see {@link java.util.Formatter#format})
     * @param args
     *            the list of arguments passed to the formatter. If there are
     *            more arguments than required by {@code format},
     *            additional arguments are ignored.
	 * @return the formatted string (with spans).
	 */
	public static SpannedString format(CharSequence format, Object... args) {
        return format(Locale.getDefault(), format, args);
    }
	
	/**
	 * Version of {@link String#format(Locale, String, Object...)} that works on {@link Spanned} strings to preserve rich text formatting.
	 * Both the {@code format} as well as any {@code %s args} can be Spanned and will have their formatting preserved.
	 * Due to the way {@link Spannable}s work, any argument's spans will can only be included <b>once</b> in the result.
	 * Any duplicates will appear as text only. 
     *
     * @param locale
     *            the locale to apply; {@code null} value means no localization.
     * @param format the format string (see {@link java.util.Formatter#format})
     * @param args
     *            the list of arguments passed to the formatter.
     * @return the formatted string (with spans).
     * @see String#format(Locale, String, Object...)
     */
	public static SpannedString format(Locale locale, CharSequence format, Object... args){
		SpannableStringBuilder out = new SpannableStringBuilder(format);
		
		int i = 0;
		int argAt = -1;
		
		while (i < out.length()){
			Matcher m = FORMAT_SEQUENCE.matcher(out);
			if (!m.find(i)) break;
			i=m.start();
			int exprEnd = m.end();
			
			String argTerm = m.group(1);
			String modTerm = m.group(2);
			String typeTerm = m.group(3);
			
			CharSequence cookedArg;
			
			switch (typeTerm) {
			case "%":
				cookedArg = "%";
				break;
			case "n":
				cookedArg = "\n";
				break;
			default:
				int argIdx;
				switch (argTerm) {
				case "":
					argIdx = ++argAt;
					break;
				case "<":
					argIdx = argAt;
					break;
				default:
					argIdx = Integer.parseInt(argTerm.substring(0, argTerm.length() - 1)) - 1;
					break;
				}
				
				Object argItem = args[argIdx];
				
				if (typeTerm.equals("s") && argItem instanceof Spanned) {
					cookedArg = (Spanned) argItem;
				} else {
					cookedArg = String.format(locale, "%" + modTerm + typeTerm, argItem);
				}
				break;
			}
			
			out.replace(i, exprEnd, cookedArg);
			i += cookedArg.length();
		}
		
		return new SpannedString(out);
	}
}
