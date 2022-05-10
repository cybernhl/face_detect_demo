package com.guadou.lib_baselib.ext

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.view.View
import android.widget.TextView
import com.guadou.lib_baselib.utils.CommUtils
import com.guadou.lib_baselib.view.span.CustomTypefaceSpan
import com.guadou.lib_baselib.view.span.MiddleIMarginImageSpan

/**
 * span相关
 * textView.sizeSpan(str, 0..2, scale = .7f) //改变scale可以控制放大或缩小，scale默认是1.5
 * textView.colorSpan(str,2..6)
 * textView.backgroundColorSpan(str,2..6)
 * textView.strikeThrougthSpan(str,2..6)
 * textView.clickSpan(str = str, range = 2..6, color = Color.BLUE, clickAction = {
 *  toast("哈哈我被点击了".toColorSpan(0..2))
 *  })
 * textView.styleSpan(str, range) //加粗，斜体等效果
 *

实际项目中append系列方法会用的更多，用法按如下
tv.text = "演示一下appendXX方法的用法"
tv.appendSizeSpan("变大变大")
.appendColorSpan("我要变色", color = Color.parseColor("#f0aafc"))
.appendBackgroundColorSpan("我是有底色的", color = Color.parseColor("#cacee0"))
.appendStrikeThrougthSpan("添加删除线哦哦哦哦")
.appendClickSpan("来点我一下试试啊", isUnderlineText = true, clickAction = {
toast("哎呀，您点到我了呢，嘿嘿")
} )
.appendStyleSpan("我是粗体的")

 */

/**
 * 将一段文字中指定range的文字改变大小
 * @param range 要改变大小的文字的范围
 * @param scale 缩放值，大于1，则比其他文字大；小于1，则比其他文字小；默认是1.5
 */
fun CharSequence.toSizeSpan(range: IntRange, scale: Float = 1.5f): CharSequence {
    return SpannableString(this).apply {
        setSpan(
            RelativeSizeSpan(scale),
            range.start,
            range.endInclusive,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}

/**
 * 将一段文字中指定range的文字改变前景色
 * @param range 要改变前景色的文字的范围
 * @param color 要改变的颜色，默认是红色
 */
fun CharSequence.toColorSpan(range: IntRange, color: Int = Color.RED): CharSequence {
    return SpannableString(this).apply {
        setSpan(
            ForegroundColorSpan(color),
            range.start,
            range.endInclusive,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}

/**
 * 将一段文字中指定range的文字改变背景色
 * @param range 要改变背景色的文字的范围
 * @param color 要改变的颜色，默认是红色
 */
fun CharSequence.toBackgroundColorSpan(range: IntRange, color: Int = Color.RED): CharSequence {
    return SpannableString(this).apply {
        setSpan(
            BackgroundColorSpan(color),
            range.start,
            range.endInclusive,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}

/**
 * 将一段文字中指定range的文字添加删除线
 * @param range 要添加删除线的文字的范围
 */
fun CharSequence.toStrikeThrougthSpan(range: IntRange): CharSequence {
    return SpannableString(this).apply {
        setSpan(
            StrikethroughSpan(),
            range.start,
            range.endInclusive,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}

/**
 * 将一段文字中指定range的文字添加颜色和点击事件
 * @param range 目标文字的范围
 */
fun CharSequence.toClickSpan(
    range: IntRange,
    color: Int = Color.RED,
    isUnderlineText: Boolean = false,
    clickAction: () -> Unit
): CharSequence {
    return SpannableString(this).apply {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                clickAction()
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = color
                ds.isUnderlineText = isUnderlineText
            }
        }
        setSpan(clickableSpan, range.start, range.endInclusive, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}

/**
 * 将一段文字中指定range的文字添加style效果
 * @param range 要添加删除线的文字的范围
 */
fun CharSequence.toStyleSpan(style: Int = Typeface.BOLD, range: IntRange): CharSequence {
    return SpannableString(this).apply {
        setSpan(
            StyleSpan(style),
            range.start,
            range.endInclusive,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}

/**
 * 将一段文字中指定range的文字添加自定义效果
 * @param range 要添加删除线的文字的范围
 */
fun CharSequence.toCustomTypeFaceSpan(typeface: Typeface, range: IntRange): CharSequence {
    return SpannableString(this).apply {
        setSpan(
            CustomTypefaceSpan(typeface),
            range.start,
            range.endInclusive,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}


/**
 * 将一段文字中指定range的文字添加自定义效果,可以设置对齐方式，可以设置margin
 * @param range
 */
fun CharSequence.toImageSpan(
    imageRes: Int,
    range: IntRange,
    verticalAlignment: Int = 0,  //默认底部
    maginLeft: Int = 0,
    marginRight: Int = 0,
    width: Int = 0,
    height: Int = 0
): CharSequence {
    return SpannableString(this).apply {
        setSpan(
            MiddleIMarginImageSpan(
                CommUtils.getDrawable(imageRes)
                    .apply {
                        setBounds(0, 0, if (width == 0) getIntrinsicWidth() else width, if (height == 0) getIntrinsicHeight() else height)
                    },
                verticalAlignment,
                maginLeft,
                marginRight
            ),
            range.start,
            range.endInclusive,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}


/** TextView的扩展 ,本质上还是调用上面的方法**/
fun TextView.sizeSpan(str: String = "", range: IntRange, scale: Float = 1.5f): TextView {
    text = (if (str.isEmpty()) text else str).toSizeSpan(range, scale)
    return this
}

fun TextView.appendSizeSpan(str: String = "", scale: Float = 1.5f): TextView {
    append(str.toSizeSpan(0..str.length, scale))
    return this
}

fun TextView.colorSpan(str: String = "", range: IntRange, color: Int = Color.RED): TextView {
    text = (if (str.isEmpty()) text else str).toColorSpan(range, color)
    return this
}

fun TextView.appendColorSpan(str: String = "", color: Int = Color.RED): TextView {
    append(str.toColorSpan(0..str.length, color))
    return this
}

fun TextView.backgroundColorSpan(
    str: String = "",
    range: IntRange,
    color: Int = Color.RED
): TextView {
    text = (if (str.isEmpty()) text else str).toBackgroundColorSpan(range, color)
    return this
}

fun TextView.appendBackgroundColorSpan(str: String = "", color: Int = Color.RED): TextView {
    append(str.toBackgroundColorSpan(0..str.length, color))
    return this
}

fun TextView.strikeThrougthSpan(str: String = "", range: IntRange): TextView {
    text = (if (str.isEmpty()) text else str).toStrikeThrougthSpan(range)
    return this
}

fun TextView.appendStrikeThrougthSpan(str: String = ""): TextView {
    append(str.toStrikeThrougthSpan(0..str.length))
    return this
}

fun TextView.clickSpan(
    str: String = "", range: IntRange,
    color: Int = Color.RED, isUnderlineText: Boolean = false, clickAction: () -> Unit
): TextView {
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = Color.TRANSPARENT  // remove click bg color
    text =
        (if (str.isEmpty()) text else str).toClickSpan(range, color, isUnderlineText, clickAction)
    return this
}

fun TextView.appendClickSpan(
    str: String = "", color: Int = Color.RED,
    isUnderlineText: Boolean = false, clickAction: () -> Unit
): TextView {
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = Color.TRANSPARENT  // remove click bg color
    append(str.toClickSpan(0..str.length, color, isUnderlineText, clickAction))
    return this
}

fun TextView.styleSpan(str: String = "", range: IntRange, style: Int = Typeface.BOLD): TextView {
    text = (if (str.isEmpty()) text else str).toStyleSpan(style = style, range = range)
    return this
}

fun TextView.appendStyleSpan(str: String = "", style: Int = Typeface.BOLD): TextView {
    append(str.toStyleSpan(style = style, range = 0..str.length))
    return this
}

fun TextView.customTypeFaceSpan(str: String = "", range: IntRange, typeface: Typeface): TextView {
    text = (if (str.isEmpty()) text else str).toCustomTypeFaceSpan(typeface, range = range)
    return this
}

fun TextView.appendCustomTypeFaceSpan(str: String = "", typeface: Typeface): TextView {
    append(str.toCustomTypeFaceSpan(typeface, range = 0..str.length))
    return this
}

fun TextView.imagepan(
    imageRes: Int,
    verticalAlignment: Int = 0,  //默认底部
    range: IntRange,
    maginLeft: Int = 0,
    marginRight: Int = 0,
    width: Int = 0,
    height: Int = 0,
    str: String = ""
): TextView {
    text = (if (str.isEmpty()) text else str).toImageSpan(
        imageRes,
        range = range,
        verticalAlignment = verticalAlignment,
        maginLeft = maginLeft,
        marginRight = marginRight,
        width = width,
        height = height
    )
    return this
}


fun TextView.appendImageSpan(
    imageRes: Int,
    verticalAlignment: Int = 0,
    str: String = "1",
    maginLeft: Int = 0,
    marginRight: Int = 0,
    width: Int = 0,
    height: Int = 0
): TextView {
    append(
        str.toImageSpan(
            imageRes,
            range = 0..str.length,
            verticalAlignment = verticalAlignment,
            maginLeft = maginLeft,
            marginRight = marginRight,
            width = width,
            height = height
        )
    )
    return this
}