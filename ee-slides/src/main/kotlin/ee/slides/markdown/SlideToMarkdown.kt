package ee.slides.markdown

import ee.common.ext.addReturn
import ee.common.ext.orEmpty
import ee.slides.*

private var presentation: Presentation = Presentation.EMPTY

val tab = "  "

fun br(b: Appendable) = b.appendln().appendln()
fun h2(b: Appendable, classes: String = "", attrs: String = "", text: String) = b.appendln("\n\n## $text")
fun h1(b: Appendable, classes: String = "", attrs: String = "", text: String) = b.appendln("\n\n# $text")

val textTypeToMarkdownTag = mapOf<String, (String) -> String>("CENTERED_TITLE" to { it -> it.orEmpty("\n# ") },
    "SUBTITLE" to { it -> it.orEmpty("> ") })

val excludeTextTypes = setOf("SLIDE_NUMBER")

fun String.comment(b: Appendable) {
    b.append("[//]: # (").append(this).appendln(")")
}

fun TextRun.toMarkdown(): String {
    val fontObj = presentation.findFont(font)
    if (fontObj.italic && fontObj.bold) {
        return toTextCapText(" *__", "__* ")
    } else if (fontObj.italic) {
        return toTextCapText(" *", "* ")
    } else if (fontObj.bold) {
        return toTextCapText(" __", "__ ")
    } else {
        return toTextCapText()
    }
}

fun Paragraph.toMarkdown(b: Appendable, tag: String = "", textTypeMapper: (String) -> String) {
    b.appendln(textTypeMapper("$tag${textRuns.map { it.toMarkdown() }.joinToString("")}"))
}

fun Shape.toMarkdown(b: Appendable) {
    if (this is TextShape) {
        this.toMarkdown(b)
    } else if (this is GroupShape) {
        this.toMarkdown(b)
    } else if (this is PictureShape) {
        this.toMarkdown(b)
    }
}

fun TextShape.toMarkdown(b: Appendable) {
    if (!excludeTextTypes.contains(textType)) {
        b.appendln()
        val markdownTextType = textTypeToMarkdownTag.getOrElse(textType, { { it -> it } })

        if (paragraphs.isNotEmpty()) {
            val paragraphPartsByType: MutableList<Pair<ParagraphType, MutableList<Paragraph>>> = mutableListOf()
            var current: Pair<ParagraphType, MutableList<Paragraph>>? = null
            paragraphs.forEach {
                if (current == null || current!!.first != it.type) {
                    current = paragraphPartsByType.addReturn(Pair(it.type, mutableListOf()))
                }
                current!!.second.add(it)
            }

            paragraphPartsByType.forEach { parts ->
                when (parts.first) {
                    ParagraphType.DEFAULT  -> parts.second.forEach {
                        it.toMarkdown(b, textTypeMapper = markdownTextType)
                    }
                    ParagraphType.NUMBERED -> parts.second.forEachIndexed { i, p ->
                        p.toMarkdown(b, "${i + 1}. ", markdownTextType)
                    }
                    ParagraphType.BULLET   -> parts.second.forEach { it.toMarkdown(b, "* ", markdownTextType) }
                }
            }
        }
    }
}

fun GroupShape.toMarkdown(b: Appendable) {
    b.appendln()
    shapes.forEach { it.toMarkdown(b) }
}

fun PictureShape.toMarkdown(b: Appendable) {
    b.appendln()
    b.appendln("![alt text]($link '$name')")
}

var titleOfLastSlide = ""
fun Slide.toMarkdown(b: Appendable) {
    if (title.isNotEmpty() && !titleOfLastSlide.equals(title, true)) {
        h2(b, text = title)
        titleOfLastSlide = title
    }
    shapes.forEach { it.toMarkdown(b) }
    b.appendln()
    notes.shapes.forEach { it.toMarkdown(b) }
}

fun Topic.toMarkdown(b: Appendable) {
    slides.forEach { it.toMarkdown(b) }
    topics.forEach { it.toMarkdown(b) }
}

class AppendableWrapper(val delegate: Appendable) : Appendable {

    override fun append(csq: CharSequence?): Appendable {
        delegate.append(csq)
        return this
    }

    override fun append(csq: CharSequence?, start: Int, end: Int): Appendable {
        delegate.append(csq, start, end)
        return this
    }

    override fun append(c: Char): Appendable {
        delegate.append(c)
        return this
    }

    override fun toString(): String {
        return delegate.toString()
    }
}

fun Presentation.toMarkdown(b: Appendable = AppendableWrapper(StringBuffer())): Appendable {
    presentation = this
    name.comment(b)
    topics.forEach { it.toMarkdown(b) }
    return b
}