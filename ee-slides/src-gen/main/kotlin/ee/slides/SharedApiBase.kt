package ee.slides

enum class ShapeType() {
    UNKNOWN(), TEXT(), PICTURE(), GROUP(), TABLE(), GRAPHIC();


    fun isUnknown(): Boolean = this == UNKNOWN
    fun isText(): Boolean = this == TEXT
    fun isPicture(): Boolean = this == PICTURE
    fun isGroup(): Boolean = this == GROUP
    fun isTable(): Boolean = this == TABLE
    fun isGraphic(): Boolean = this == GRAPHIC
}

fun String?.toShapeType(): ShapeType {
    return if (this != null) {
        ShapeType.valueOf(this)
    } else {
        ShapeType.UNKNOWN
    }
}

enum class TextAlign() {
    LEFT(), CENTER(), RIGHT(), JUSTIFY();


    fun isLeft(): Boolean = this == LEFT
    fun isCenter(): Boolean = this == CENTER
    fun isRight(): Boolean = this == RIGHT
    fun isJustify(): Boolean = this == JUSTIFY
}

fun String?.toTextAlign(): TextAlign {
    return if (this != null) {
        TextAlign.valueOf(this)
    } else {
        TextAlign.LEFT
    }
}

enum class FontAlign() {
    AUTO(), TOP(), CENTER(), BASELINE(), BOTTOM();


    fun isAuto(): Boolean = this == AUTO
    fun isTop(): Boolean = this == TOP
    fun isCenter(): Boolean = this == CENTER
    fun isBaseline(): Boolean = this == BASELINE
    fun isBottom(): Boolean = this == BOTTOM
}

fun String?.toFontAlign(): FontAlign {
    return if (this != null) {
        FontAlign.valueOf(this)
    } else {
        FontAlign.AUTO
    }
}

enum class ParagraphType() {
    DEFAULT(), BULLET(), NUMBERED();


    fun isDefault(): Boolean = this == DEFAULT
    fun isBullet(): Boolean = this == BULLET
    fun isNumbered(): Boolean = this == NUMBERED
}

fun String?.toParagraphType(): ParagraphType {
    return if (this != null) {
        ParagraphType.valueOf(this)
    } else {
        ParagraphType.DEFAULT
    }
}

enum class TextCap() {
    NONE(), SMALL(), ALL();


    fun isNone(): Boolean = this == NONE
    fun isSmall(): Boolean = this == SMALL
    fun isAll(): Boolean = this == ALL
}

fun String?.toTextCap(): TextCap {
    return if (this != null) {
        TextCap.valueOf(this)
    } else {
        TextCap.NONE
    }
}

data class Rectangle(var name: String = "", var height: Int = 0, var width: Int = 0, var x: Int = 0, var y: Int = 0) {
    companion object {
        val EMPTY = Rectangle()
    }


}

fun Rectangle?.orEmpty(): Rectangle {
    return if (this != null) this else Rectangle.EMPTY
}

data class Color(var name: String = "", var red: Int = 0, var green: Int = 0, var blue: Int = 0, var alpha: Int = 0) {
    companion object {
        val EMPTY = Color()
    }


}

fun Color?.orEmpty(): Color {
    return if (this != null) this else Color.EMPTY
}

data class Font(var name: String = "", var size: String = "", var family: String = "", var bold: Boolean = false,
    var italic: Boolean = false, var underlined: Boolean = false) {
    companion object {
        val EMPTY = Font()
    }


}

fun Font?.orEmpty(): Font {
    return if (this != null) this else Font.EMPTY
}

open class TextRun {
    companion object {
        val EMPTY = TextRun()
    }

    var text: String = ""
    var cap: TextCap = TextCap.NONE
    var font: String = ""
    var color: String = ""
    var type: String = ""

    constructor(text: String = "", cap: TextCap = TextCap.NONE, font: String = "", color: String = "",
        type: String = "") {
        this.text = text
        this.cap = cap
        this.font = font
        this.color = color
        this.type = type
    }


}

fun TextRun?.orEmpty(): TextRun {
    return if (this != null) this else TextRun.EMPTY
}

open class Paragraph {
    companion object {
        val EMPTY = Paragraph()
    }

    var textRuns: MutableList<TextRun> = arrayListOf()
    var type: ParagraphType = ParagraphType.DEFAULT
    var fontAlign: FontAlign = FontAlign.AUTO
    var textAlign: TextAlign = TextAlign.LEFT

    constructor(textRuns: MutableList<TextRun> = arrayListOf(), type: ParagraphType = ParagraphType.DEFAULT,
        fontAlign: FontAlign = FontAlign.AUTO, textAlign: TextAlign = TextAlign.LEFT) {
        this.textRuns = textRuns
        this.type = type
        this.fontAlign = fontAlign
        this.textAlign = textAlign
    }


}

fun Paragraph?.orEmpty(): Paragraph {
    return if (this != null) this else Paragraph.EMPTY
}

abstract class SlidesBase {

    var relations: MutableList<SlidesBase> = arrayListOf()

    constructor(relations: MutableList<SlidesBase> = arrayListOf()) {
        this.relations = relations
    }


}


abstract class Shape : SlidesBase {

    var name: String = ""
    var type: ShapeType = ShapeType.UNKNOWN
    var anchor: String = ""

    constructor(relations: MutableList<SlidesBase> = arrayListOf(), name: String = "",
        type: ShapeType = ShapeType.UNKNOWN, anchor: String = "") : super(relations) {
        this.name = name
        this.type = type
        this.anchor = anchor
    }


}


open class TextShape : Shape {
    companion object {
        val EMPTY = TextShape()
    }

    var paragraphs: MutableList<Paragraph> = arrayListOf()
    var textType: String = ""

    constructor(relations: MutableList<SlidesBase> = arrayListOf(), name: String = "",
        type: ShapeType = ShapeType.UNKNOWN, anchor: String = "", paragraphs: MutableList<Paragraph> = arrayListOf(),
        textType: String = "") : super(relations, name, type, anchor) {
        this.paragraphs = paragraphs
        this.textType = textType
    }


}

fun TextShape?.orEmpty(): TextShape {
    return if (this != null) this else TextShape.EMPTY
}

open class PictureShape : Shape {
    companion object {
        val EMPTY = PictureShape()
    }

    var link: Boolean = false
    var linkUri: String = ""
    var fileName: String = ""
    var data: ByteArray? = null

    constructor(relations: MutableList<SlidesBase> = arrayListOf(), name: String = "",
        type: ShapeType = ShapeType.UNKNOWN, anchor: String = "", link: Boolean = false, linkUri: String = "",
        fileName: String = "", data: ByteArray? = null) : super(relations, name, type, anchor) {
        this.link = link
        this.linkUri = linkUri
        this.fileName = fileName
        this.data = data
    }


}

fun PictureShape?.orEmpty(): PictureShape {
    return if (this != null) this else PictureShape.EMPTY
}

open class GraphicShape : Shape {
    companion object {
        val EMPTY = GraphicShape()
    }

    var data: ByteArray? = null

    constructor(relations: MutableList<SlidesBase> = arrayListOf(), name: String = "",
        type: ShapeType = ShapeType.UNKNOWN, anchor: String = "", data: ByteArray? = null) : super(relations, name,
        type, anchor) {
        this.data = data
    }


}

fun GraphicShape?.orEmpty(): GraphicShape {
    return if (this != null) this else GraphicShape.EMPTY
}

open class TableShape : Shape {
    companion object {
        val EMPTY = TableShape()
    }

    var data: ByteArray? = null

    constructor(relations: MutableList<SlidesBase> = arrayListOf(), name: String = "",
        type: ShapeType = ShapeType.UNKNOWN, anchor: String = "", data: ByteArray? = null) : super(relations, name,
        type, anchor) {
        this.data = data
    }


}

fun TableShape?.orEmpty(): TableShape {
    return if (this != null) this else TableShape.EMPTY
}

open class GroupShape : Shape {
    companion object {
        val EMPTY = GroupShape()
    }

    var shapes: MutableList<Shape> = arrayListOf()

    constructor(relations: MutableList<SlidesBase> = arrayListOf(), name: String = "",
        type: ShapeType = ShapeType.UNKNOWN, anchor: String = "", shapes: MutableList<Shape> = arrayListOf()) : super(
        relations, name, type, anchor) {
        this.shapes = shapes
    }


}

fun GroupShape?.orEmpty(): GroupShape {
    return if (this != null) this else GroupShape.EMPTY
}

abstract class Sheet : SlidesBase {

    var shapes: MutableList<Shape> = arrayListOf()

    constructor(relations: MutableList<SlidesBase> = arrayListOf(), shapes: MutableList<Shape> = arrayListOf()) : super(
        relations) {
        this.shapes = shapes
    }


}


open class Notes : Sheet {
    companion object {
        val EMPTY = Notes()
    }


    constructor(relations: MutableList<SlidesBase> = arrayListOf(), shapes: MutableList<Shape> = arrayListOf()) : super(
        relations, shapes) {

    }


}

fun Notes?.orEmpty(): Notes {
    return if (this != null) this else Notes.EMPTY
}

open class Slide : Sheet {
    companion object {
        val EMPTY = Slide()
    }

    var title: String = ""
    var masterType: String = ""
    var notes: Notes = Notes.EMPTY
    var comments: MutableList<String> = arrayListOf()

    constructor(relations: MutableList<SlidesBase> = arrayListOf(), shapes: MutableList<Shape> = arrayListOf(),
        title: String = "", masterType: String = "", notes: Notes = Notes.EMPTY,
        comments: MutableList<String> = arrayListOf()) : super(relations, shapes) {
        this.title = title
        this.masterType = masterType
        this.notes = notes
        this.comments = comments
    }


}

fun Slide?.orEmpty(): Slide {
    return if (this != null) this else Slide.EMPTY
}

open class Topic {
    companion object {
        val EMPTY = Topic()
    }

    var name: String = ""
    var slides: MutableList<Slide> = arrayListOf()
    var topics: MutableList<Topic> = arrayListOf()

    constructor(name: String = "", slides: MutableList<Slide> = arrayListOf(),
        topics: MutableList<Topic> = arrayListOf()) {
        this.name = name
        this.slides = slides
        this.topics = topics
    }


}

fun Topic?.orEmpty(): Topic {
    return if (this != null) this else Topic.EMPTY
}

open class Presentation : Topic {
    companion object {
        val EMPTY = Presentation()
    }

    var author: String = ""
    var colors: MutableList<Color> = arrayListOf()
    var fonts: MutableList<Font> = arrayListOf()
    var anchors: MutableList<Rectangle> = arrayListOf()

    constructor(name: String = "", slides: MutableList<Slide> = arrayListOf(),
        topics: MutableList<Topic> = arrayListOf(), author: String = "", colors: MutableList<Color> = arrayListOf(),
        fonts: MutableList<Font> = arrayListOf(), anchors: MutableList<Rectangle> = arrayListOf()) : super(name, slides,
        topics) {
        this.author = author
        this.colors = colors
        this.fonts = fonts
        this.anchors = anchors
    }


}

fun Presentation?.orEmpty(): Presentation {
    return if (this != null) this else Presentation.EMPTY
}


