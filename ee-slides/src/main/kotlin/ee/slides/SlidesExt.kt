package ee.slides

import ee.common.ext.orEmpty
import java.nio.file.Path

fun Presentation.extractPicturesTo(target: Path) {
    topics.extractPicturesTo(target)
}

fun Presentation.findColor(name: String): Color {
    val ret = colors.find { it.name.equals(name, true) }
    return ret ?: Color.EMPTY
}


fun Presentation.findFont(name: String): Font {
    val ret = fonts.find { it.name.equals(name, true) }
    return ret ?: Font.EMPTY
}

fun Presentation.findAnchor(name: String): Rectangle {
    val ret = anchors.find { it.name.equals(name, true) }
    return ret ?: Rectangle.EMPTY
}

fun List<Topic>.extractPicturesTo(target: Path) {
    forEach {
        val targetPicturesFolder = target.resolve("${it.name}/").toFile()

        it.slides.forEachIndexed { slideIndex, slide ->
            slide.shapes.filterIsInstance(PictureShape::class.java).forEachIndexed { shapeIndex, shape ->
                val picturePrefix = "${slideIndex}_${shapeIndex}_"
                targetPicturesFolder.mkdirs()
                val picturePath = targetPicturesFolder.resolve(
                        if (shape.fileName.isNotEmpty()) "$picturePrefix${shape.fileName}"
                        else "$picturePrefix${shape.name}.jpg")
                picturePath.writeBytes(shape.data!!)
                shape.data = ByteArray(0)
                if (shape.fileName.isEmpty()) {
                    shape.fileName = picturePath.name
                }
                shape.linkUri = picturePath.toRelativeString(target.toFile())
            }
        }

        it.topics.extractPicturesTo(target)
    }
}

fun TextRun.toTextCapText(prefix: String = "", suffix: String = ""): String {
    return text.orEmpty(prefix, suffix, map = {
        when (cap) {
            TextCap.NONE -> this
            TextCap.ALL -> this.toUpperCase()
            TextCap.SMALL -> this.toLowerCase()
        }
    })
}