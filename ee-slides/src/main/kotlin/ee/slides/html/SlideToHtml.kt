package ee.slides.html

import ee.common.ext.addReturn
import ee.common.ext.ifElse
import ee.common.ext.orEmpty
import ee.common.ext.toUnderscoredLowerCase
import ee.slides.*

val textTypeToTag = mapOf("CENTERED_TITLE" to "h2", "SUBTITLE" to "blockquote")
val excludeTextTypes = setOf("SLIDE_NUMBER")

val tab = "  "
private var presentation: Presentation = Presentation.EMPTY
private var reveal: Boolean = false


fun String.toTag(b: StringBuffer, indent: String, classes: String = "", attrs: String = "",
    body: (String) -> Unit = {}) {
    b.append(indent).appendln("<$this${attrs.trim().orEmpty(" ")}${classes.trim().orEmpty(" class='", "'")}>")
    body("$indent$tab")
    b.append(indent).appendln("</$this>")
}

fun String.toTag(b: StringBuffer, indent: String, classes: String = "", attrs: String = "", text: String) {
    b.append(indent)
        .appendln("<$this${attrs.trim().orEmpty(" ")}${classes.trim().orEmpty(" class='", "'")}>$text</$this>")
}

fun section(b: StringBuffer, indent: String, classes: String = "", attrs: String = "", body: (String) -> Unit = {}) =
    "section".toTag(b, indent, classes, attrs, body)

fun aside(b: StringBuffer, indent: String, classes: String = "", attrs: String = "", body: (String) -> Unit = {}) =
    "aside".toTag(b, indent, "$classes notes", attrs, body)

fun ul(b: StringBuffer, indent: String, classes: String = "", attrs: String = "", body: (String) -> Unit = {}) =
    "ul".toTag(b, indent, classes, attrs, body)

fun ol(b: StringBuffer, indent: String, classes: String = "", attrs: String = "", body: (String) -> Unit = {}) =
    "ol".toTag(b, indent, classes, attrs, body)

fun div(b: StringBuffer, indent: String, classes: String = "", attrs: String = "", body: (String) -> Unit = {}) =
    "div".toTag(b, indent, classes, attrs, body)

fun a(b: StringBuffer, indent: String, classes: String = "", attrs: String = "", body: (String) -> Unit = {}) =
    "a".toTag(b, indent, classes, attrs, body)

fun img(b: StringBuffer, indent: String, classes: String = "", attrs: String = "") =
    "img".toTag(b, indent, classes, attrs, "")

fun p(b: StringBuffer, indent: String, classes: String = "", attrs: String = "", text: String) =
    "p".toTag(b, indent, classes, attrs, text)

fun br(b: StringBuffer, indent: String) = b.append(indent).appendln("</br>")
fun h2(b: StringBuffer, indent: String, classes: String = "", attrs: String = "", text: String) =
    "h2".toTag(b, indent, classes, attrs, text)

fun h1(b: StringBuffer, indent: String, classes: String = "", attrs: String = "", text: String) =
    "h1".toTag(b, indent, classes, attrs, text)

fun String.comment(b: StringBuffer, indent: String) {
    b.append(indent).append("<!--").append(this).appendln("-->")
}

fun Enum<*>.toCssNames(): String {
    return "${javaClass.simpleName.toUnderscoredLowerCase()}_${name.toLowerCase()}"
}

fun Paragraph.toCssNames(): String {
    return "${textAlign.toCssNames()} ${fontAlign.toCssNames()} ${textRuns.map(TextRun::toCssNames).joinToString(" ")}"
}

fun Shape.toCssNames(): String {
    if (this is TextShape && textType.trim().isNotEmpty()) {
        return "${javaClass.simpleName.toUnderscoredLowerCase()} text_type_${textType.toLowerCase()}"
    } else {
        return "${javaClass.simpleName.toUnderscoredLowerCase()}"
    }
}

fun Slide.toCssNames(): String {
    return "slide_type_${masterType.toLowerCase()}"
}

val fontNameToCss = hashMapOf<String, String>()
fun Font.toCssNames(): String {
    return fontNameToCss.getOrPut(name, {
        "${bold.ifElse(" font_bold", "")}${italic.ifElse(" font_italic", "")}${underlined.ifElse(" font_underlined",
            "")}"
    })
}

fun TextRun.toCssNames(): String {
    return "${type.orEmpty("text_run_")}${presentation.findFont(font).toCssNames()}"
}

fun TextRun.toHtml(b: StringBuffer, indent: String, tag: String = "span") {
    if (text.isNotEmpty()) tag.toTag(b, indent, toCssNames(), text = toTextCapText()) else br(b, indent)
}

fun Paragraph.containsText(): Boolean {
    return textRuns.find { it.text.trim().isNotEmpty() } != null
}

fun Paragraph.toText(): String {
    return textRuns.map { it.toTextCapText() }.joinToString("")
}

fun Paragraph.toHtml(b: StringBuffer, indent: String, tag: String = "p") {
    if (containsText()) {
        tag.toTag(b, indent) {
            textRuns.forEach { it.toHtml(b, indent) }
        }
    }
}

fun Shape.toHtml(b: StringBuffer, indent: String) {
    if (this is TextShape) {
        this.toHtml(b, indent)
    } else if (this is GroupShape) {
        this.toHtml(b, indent)
    } else if (this is PictureShape) {
        this.toHtml(b, indent)
    }
}

fun TextShape.toHtml(b: StringBuffer, indent: String) {
    val notEmptyParags = paragraphs.filter(Paragraph::containsText)
    if (!excludeTextTypes.contains(textType) && notEmptyParags.isNotEmpty()) {
        val tag = textTypeToTag.getOrElse(textType, { (notEmptyParags.size == 1).ifElse("blockquote", "div") })
        tag.toTag(b, indent, toCssNames()) { newIndent ->
            val paragraphPartsByType: MutableList<Pair<ParagraphType, MutableList<Paragraph>>> = mutableListOf()
            var current: Pair<ParagraphType, MutableList<Paragraph>>? = null
            paragraphs.forEach {
                if (current == null || current!!.first != it.type) {
                    current = paragraphPartsByType.addReturn(Pair(it.type, mutableListOf()))
                }
                current!!.second.add(it)
            }
            val newIndent2 = "$newIndent$tab"
            paragraphPartsByType.forEach { parts ->
                when (parts.first) {
                    ParagraphType.DEFAULT  -> parts.second.forEach { it.toHtml(b, newIndent2) }
                    ParagraphType.NUMBERED -> ol(b, newIndent2) {
                        parts.second.forEach { it.toHtml(b, "$newIndent2$tab", "li") }
                    }
                    ParagraphType.BULLET   -> ul(b, newIndent2) {
                        parts.second.forEach { it.toHtml(b, "$newIndent2$tab", "li") }
                    }
                }
            }
        }
    }
}

fun GroupShape.toHtml(b: StringBuffer, indent: String) {
    div(b, indent, toCssNames()) { newIndent ->
        shapes.forEach { it.toHtml(b, newIndent) }
    }
}

fun PictureShape.toHtml(b: StringBuffer, indent: String) {
    if (reveal) {
        div(b, indent, toCssNames()) { newIndent ->
            a(b, newIndent, attrs = "href='#'") { newIndent ->
                img(b, newIndent, attrs = "${reveal.ifElse("data-src", "src")}='$link' alt='$name'")
            }
        }
    }
}

var titleOfLastSlide = ""
fun Slide.toHtml(b: StringBuffer, indent: String, index: Int = 0) {
    if (shapes.isNotEmpty()) {
        section(b, indent, toCssNames()) { newIndent ->

            var currentTitle = title
            if (currentTitle.isEmpty()) {
                val titleShape = shapes.find { it is TextShape && it.textType.equals("CENTERED_TITLE") }
                if (titleShape is TextShape) {
                    currentTitle = titleShape.paragraphs.map(Paragraph::toText).joinToString("<br>")
                    shapes.remove(titleShape)
                }
            }
            if (currentTitle.isNotEmpty() && (reveal || !titleOfLastSlide.equals(currentTitle, true))) {
                if (index == 0) {
                    h1(b, newIndent, text = currentTitle)
                    println(currentTitle)
                    println(b)
                } else {
                    h2(b, newIndent, text = currentTitle)
                }
                titleOfLastSlide = currentTitle
            }
            shapes.forEach { it.toHtml(b, newIndent) }
            if (notes.shapes.isNotEmpty()) {
                aside(b, newIndent) { newIndent ->
                    notes.shapes.forEach { it.toHtml(b, newIndent) }
                }
            }
        }
    }
}

fun Presentation.toCssNamesAll(to: MutableSet<String> = hashSetOf()): Set<String> {
    topics.forEach { it.toCssNamesAll(to) }
    return to
}

fun Topic.toCssNamesAll(to: MutableSet<String> = hashSetOf()): Set<String> {
    slides.forEach {
        to.addAll(it.toCssNames().split(" "))
        it.shapes.forEach {
            to.addAll(it.toCssNames().split(" "))
            if (it is TextShape) it.paragraphs.forEach { to.addAll(it.toCssNames().split(" ")) }
        }
    }
    return to
}

fun Presentation.toCss(b: StringBuffer = StringBuffer(), indent: String = ""): StringBuffer {
    toCssNamesAll().toCss(b, indent)
    return b
}

fun Collection<String>.toCss(b: StringBuffer = StringBuffer(), indent: String = ""): StringBuffer {
    filter { it.trim().isNotEmpty() }.sorted().forEach { b.appendln("$indent.$it {}") }
    return b
}

fun Topic.toHtml(b: StringBuffer = StringBuffer(), indent: String = ""): StringBuffer {
    slides.forEachIndexed { i, slide -> slide.toHtml(b, indent, i) }
    topics.forEach { it.toHtml(b, indent) }
    return b
}

fun Presentation.toHtml(b: StringBuffer = StringBuffer(), indent: String = ""): StringBuffer {
    presentation = this
    reveal = false
    val newIndent = "$indent			$tab"
    b.appendln("""
<!doctype html>
<html lang="en">

	<head>
		<meta charset="utf-8">

		<title>$name</title>

		<meta elName="author" content="${presentation.author}">

		<meta elName="apple-mobile-web-app-capable" content="yes">
		<meta elName="apple-mobile-web-app-status-bar-style" content="black-translucent">

		<meta elName="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">

		<!-- Powerpoint shape types -->
        <link rel="stylesheet" href="css/powerpoint.css">

		<!--[if lt IE 9]>
		<script src="lib/js/html5shiv.js"></script>
		<![endif]-->
	</head>

	<body>

		<div class="html">
			<div class="slides">""")
    name.comment(b, newIndent)
    topics.forEach { it.toHtml(b, indent) }
    b.appendln("""
        </div>
	</body>
</html>""")
    println(b)
    return b
}

fun Presentation.toReveal(b: StringBuffer = StringBuffer(), indent: String = ""): StringBuffer {
    presentation = this
    reveal = true
    val newIndent = "$indent			$tab"
    b.appendln("""
<!doctype html>
<html lang="en">

	<head>
		<meta charset="utf-8">

		<title>$name</title>

		<meta elName="author" content="${presentation.author}">

		<meta elName="apple-mobile-web-app-capable" content="yes">
		<meta elName="apple-mobile-web-app-status-bar-style" content="black-translucent">

		<meta elName="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">

		<link rel="stylesheet" href="css/reveal.css">
        <link rel="stylesheet" href="css/theme/black.css" id="theme">

		<!-- Theme used for syntax highlighting of code -->
		<link rel="stylesheet" href="lib/css/zenburn.css">

		<!-- Powerpoint shape types -->
        <link rel="stylesheet" href="css/powerpoint.css">

		<!-- Printing and PDF exports -->
		<script>
			var link = document.createElement( 'link' );
			link.rel = 'stylesheet';
			link.type = 'text/css';
			link.href = window.location.search.match( /print-pdf/gi ) ? 'css/print/pdf.css' : 'css/print/paper.css';
			document.getElementsByTagName( 'head' )[0].appendChild( link );
		</script>

		<!--[if lt IE 9]>
		<script src="lib/js/html5shiv.js"></script>
		<![endif]-->
	</head>

	<body>

		<div class="reveal">

			<!-- Any section element inside of this container is displayed as a slide -->
			<div class="slides">""")
    name.comment(b, newIndent)
    topics.forEach { it.toHtml(b, indent) }
    b.appendln("""
        </div>
        <script src="lib/js/head.min.js"></script>
		<script src="js/reveal.js"></script>

		<script>

			// More info https://github.com/hakimel/reveal.js#configuration
			Reveal.initialize({
				controls: true,
				progress: true,
				history: true,
				center: true,

				transition: 'slide', // none/fade/slide/convex/concave/zoom

				// More info https://github.com/hakimel/reveal.js#dependencies
				dependencies: [
					{ src: 'lib/js/classList.js', condition: function() { return !document.body.classList; } },
					{ src: 'plugin/markdown/marked.js', condition: function() { return !!document.querySelector( '[data-markdown]' ); } },
					{ src: 'plugin/markdown/markdown.js', condition: function() { return !!document.querySelector( '[data-markdown]' ); } },
					{ src: 'plugin/highlight/highlight.js', async: true, callback: function() { hljs.initHighlightingOnLoad(); } },
					{ src: 'plugin/zoom-js/zoom.js', async: true },
					{ src: 'plugin/notes/notes.js', async: true }
				]
			});

		</script>

	</body>
</html>""")
    return b
}