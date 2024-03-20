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
package com.linxiao.framework.common

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.SpannedString
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import androidx.annotation.StringRes
import java.util.Locale
import java.util.regex.Pattern

/**
 * Provides [String.format] style functions that work with [Spanned] strings and preserve formatting.
 *
 * @author lx8421bcd
 * @since 2018-04-19
 */
object SpanUtil {
    private val FORMAT_SEQUENCE =
        Pattern.compile("%([0-9]+\\$|<?)([^a-zA-z%]*)([a-zA-Z%]&&[^tT]|[tT][a-zA-Z])")

    /**
     * 将一段文字中的指定内容替换为高亮span
     */
    fun setHighlightSpan(
        sourceSpan: SpannableStringBuilder,
        highlightText: String,
        color: Int
    ) {
        if (sourceSpan.toString().contains(highlightText)) {
            val start = sourceSpan.toString().indexOf(highlightText)
            val end = start + highlightText.length
            sourceSpan.setSpan(
                ForegroundColorSpan(color),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    /**
     * 将一段文字中的指定内容替换为高亮、可点击span
     */
    fun setClickableSpan(
        sourceSpan: SpannableStringBuilder,
        highlightText: String,
        span: ClickableSpan?
    ) {
        if (!sourceSpan.toString().contains(highlightText)) {
            return
        }
        val start = sourceSpan.toString().indexOf(highlightText)
        val end = start + highlightText.length
        sourceSpan.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    fun getText(@StringRes resId: Int, vararg args: Any?): CharSequence {
        return format(globalContext.getText(resId), *args)
    }

    /**
     * Version of [String.format] that works on [Spanned] strings to preserve rich text formatting.
     * Both the `format` as well as any `%s args` can be Spanned and will have their formatting preserved.
     * Due to the way [Spannable]s work, any argument's spans will can only be included **once** in the result.
     * Any duplicates will appear as text only.
     *
     * @param format the format string (see [java.util.Formatter.format])
     * @param args
     * the list of arguments passed to the formatter. If there are
     * more arguments than required by `format`,
     * additional arguments are ignored.
     * @return the formatted string (with spans).
     */
    fun format(format: CharSequence?, vararg args: Any?): SpannedString {
        return format(Locale.getDefault(), format, *args)
    }

    /**
     * Version of [String.format] that works on [Spanned] strings to preserve rich text formatting.
     * Both the `format` as well as any `%s args` can be Spanned and will have their formatting preserved.
     * Due to the way [Spannable]s work, any argument's spans will can only be included **once** in the result.
     * Any duplicates will appear as text only.
     *
     * @param locale
     * the locale to apply; `null` value means no localization.
     * @param format the format string (see [java.util.Formatter.format])
     * @param args
     * the list of arguments passed to the formatter.
     * @return the formatted string (with spans).
     * @see String.format
     */
    fun format(locale: Locale?, format: CharSequence?, vararg args: Any?): SpannedString {
        val out = SpannableStringBuilder(fixSpanColor(format))
        var i = 0
        var argAt = -1
        while (i < out.length) {
            val m = FORMAT_SEQUENCE.matcher(out)
            if (!m.find(i)) {
                break
            }
            i = m.start()
            val exprEnd = m.end()
            val argTerm = m.group(1)
            val modTerm = m.group(2)
            val typeTerm = m.group(3)
            var cookedArg: CharSequence
            when (typeTerm) {
                "%" -> cookedArg = "%"
                "n" -> cookedArg = "\n"
                else -> {
                    val argIdx: Int = when (argTerm) {
                        "" -> ++argAt
                        "<" -> argAt
                        else -> argTerm!!.substring(0, argTerm.length - 1).toInt() - 1
                    }
                    val argItem = args[argIdx]
                    cookedArg = if (typeTerm == "s" && argItem is Spanned) {
                        argItem
                    } else {
                        String.format(locale, "%$modTerm$typeTerm", argItem)
                    }
                }
            }
            out.replace(i, exprEnd, cookedArg)
            i += cookedArg.length
        }
        return SpannedString(out)
    }

    private fun fixSpanColor(text: CharSequence?): CharSequence? {
        return if (text is Spanned) {
            val s = SpannableString(text)
            val spans = s.getSpans(0, s.length, ForegroundColorSpan::class.java)
            for (oldSpan in spans) {
                val newSpan = ForegroundColorSpan(oldSpan.foregroundColor or -0x1000000)
                s.setSpan(
                    newSpan,
                    s.getSpanStart(oldSpan),
                    s.getSpanEnd(oldSpan),
                    s.getSpanFlags(oldSpan)
                )
                s.removeSpan(oldSpan)
            }
            s
        } else {
            text
        }
    }
}
