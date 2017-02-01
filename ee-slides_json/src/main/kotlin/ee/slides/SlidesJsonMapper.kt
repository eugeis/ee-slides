package ee.slides

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.deser.SettableBeanProperty
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.KotlinModule
import ee.slides.ShapeType.*

class ShapeDeserializer : StdDeserializer<Shape>(Shape::class.java) {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Shape {
        var ret: Shape = TextShape.EMPTY
        if (p != null) {
            val mapper = p.codec
            val root: ObjectNode = mapper.readTree(p)
            val typeAsString = root.findValue("type").asText()
            if (typeAsString != null) {
                when (ShapeType.valueOf(typeAsString)) {
                    TEXT -> ret = mapper?.treeToValue(root, TextShape::class.java) as Shape
                    GROUP -> ret = mapper?.treeToValue(root, GroupShape::class.java) as Shape
                    PICTURE -> ret = mapper?.treeToValue(root, PictureShape::class.java) as Shape
                    GRAPHIC -> ret = mapper?.treeToValue(root, GraphicShape::class.java) as Shape
                    TABLE -> ret = mapper?.treeToValue(root, TableShape::class.java) as Shape
                    else -> ret = TextShape.EMPTY
                }
            }
        }
        return ret
    }

    override fun findBackReference(refName: String?): SettableBeanProperty {
        return super.findBackReference(refName)
    }
}

fun mapper(): ObjectMapper {
    val mapper = ObjectMapper().registerModule(KotlinModule())
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    val module = SimpleModule()
    module.addDeserializer(Shape::class.java, ShapeDeserializer())
    mapper.registerModule(module)
    return mapper
}