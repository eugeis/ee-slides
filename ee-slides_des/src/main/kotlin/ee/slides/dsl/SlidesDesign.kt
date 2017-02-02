package ee.slides.dsl

import ee.design.*
import ee.lang.*

object Slides : Comp({ artifact("ee-slides").namespace("ee.slides") }) {
    object Shared : Module() {

        object ShapeType : EnumType() {
            val Unknown = lit()
            val Text = lit()
            val Picture = lit()
            val Group = lit()
            val Table = lit()
            val Graphic = lit()
        }

        object TextAlign : EnumType() {
            val Left = lit()
            val Center = lit()
            val Right = lit()
            val Justify = lit()
        }

        object FontAlign : EnumType() {
            val Auto = lit()
            val Top = lit()
            val Center = lit()
            val Baseline = lit()
            val Bottom = lit()
        }

        object ParagraphType : EnumType() {
            val Default = lit()
            val Bullet = lit()
            val Numbered = lit()
        }

        object Rectangle : Basic() {
            val name = prop()
            val height = prop(n.Int)
            val width = prop(n.Int)
            val x = prop(n.Int)
            val y = prop(n.Int)
        }

        object Color : Basic() {
            val name = prop()
            val red = prop(n.Int)
            val green = prop(n.Int)
            val blue = prop(n.Int)
            val alpha = prop(n.Int)
        }

        object Font : Basic() {
            val name = prop()
            val size = prop()
            val family = prop()
            val bold = prop(n.Boolean)
            val italic = prop(n.Boolean)
            val underlined = prop(n.Boolean)
        }

        object TextCap : EnumType() {
            val None = lit()
            val Small = lit()
            val All = lit()
        }

        object TextRun : Values() {
            val text = prop()
            val cap = prop(TextCap)
            val font = prop()
            val color = prop()
            val type = prop()
        }

        object Paragraph : Values() {
            val textRuns = prop(n.List.GT(TextRun))
            val type = prop(ParagraphType)
            val fontAlign = prop(FontAlign)
            val textAlign = prop(TextAlign)
        }

        object SlidesBase : Values({ virtual(true) }) {
            val relations = prop(n.List.GT(SlidesBase))
        }

        object Shape : Values({ superUnit(SlidesBase).virtual(true) }) {
            val name = prop()
            val type = prop(ShapeType)
            val anchor = prop()
        }

        object TextShape : Values({ superUnit(Shape) }) {
            val paragraphs = prop(n.List.GT(Paragraph))
            val textType = prop()
        }

        object PictureShape : Values({ superUnit(Shape) }) {
            val link = prop(n.Boolean)
            val linkUri = prop()
            val fileName = prop()
            val data = prop { type(n.Blob).nullable(true) }
        }

        object GraphicShape : Values({ superUnit(Shape) }) {
            val data = prop { type(n.Blob).nullable(true) }
        }

        object TableShape : Values({ superUnit(Shape) }) {
            val data = prop { type(n.Blob).nullable(true) }
        }

        object GroupShape : Values({ superUnit(Shape) }) {
            val shapes = prop(n.List.GT(Shape))
        }

        object Sheet : Values({ superUnit(SlidesBase).virtual(true) }) {
            val shapes = prop(n.List.GT(Shape))
        }

        object Notes : Values({ superUnit(Sheet) }) {
        }

        object Slide : Values({ superUnit(Sheet) }) {
            val title = prop()
            val masterType = prop()
            val notes = prop(Notes)
            val comments = prop(n.List)
        }

        object Topic : Entity() {
            val name = prop()
            val slides = prop(n.List.GT(Slide))
            val topics = prop(n.List.GT(Topic))
        }

        object Presentation : Entity({ superUnit(Topic) }) {
            val author = prop()
            val colors = prop(n.List.GT(Color))
            val fonts = prop(n.List.GT(Font))
            val anchors = prop(n.List.GT(Rectangle))
        }
    }
}

fun model(): StructureUnitI {
    n.initObjectTree()
    return Slides.initObjectTree(true)
}