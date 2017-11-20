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
            val name = propS()
            val height = propI()
            val width = propI()
            val x = propI()
            val y = propI()
        }

        object Color : Basic() {
            val name = propS()
            val red = propI()
            val green = propI()
            val blue = propI()
            val alpha = propI()
        }

        object Font : Basic() {
            val name = propS()
            val size = propS()
            val family = propS()
            val bold = propB()
            val italic = propB()
            val underlined = propB()
        }

        object TextCap : EnumType() {
            val None = lit()
            val Small = lit()
            val All = lit()
        }

        object TextRun : Values() {
            val text = propS()
            val cap = prop(TextCap)
            val font = propS()
            val color = propS()
            val type = propS()
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
            val name = propS()
            val type = prop(ShapeType)
            val anchor = propS()
        }

        object TextShape : Values({ superUnit(Shape) }) {
            val paragraphs = prop(n.List.GT(Paragraph))
            val textType = propS()
        }

        object PictureShape : Values({ superUnit(Shape) }) {
            val link = propB()
            val linkUri = propS()
            val fileName = propS()
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
            val title = propS()
            val masterType = propS()
            val notes = prop(Notes)
            val comments = prop(n.List)
        }

        object Topic : Entity() {
            val name = propS()
            val slides = prop(n.List.GT(Slide))
            val topics = prop(n.List.GT(Topic))
        }

        object Presentation : Entity({ superUnit(Topic) }) {
            val author = propS()
            val colors = prop(n.List.GT(Color))
            val fonts = prop(n.List.GT(Font))
            val anchors = prop(n.List.GT(Rectangle))
        }
    }
}