package ee.slides.fx.view

import com.fasterxml.jackson.module.kotlin.readValue
import ee.common.ext.isWindows
import ee.slides.Presentation
import ee.slides.Slide
import ee.slides.Topic
import javafx.scene.control.TreeItem
import tornadofx.*
import java.nio.file.Paths

open class SlideNode(val label: String, val slidesFactory: () -> List<SlideNode> = { emptyList() })

fun Slide.toNode(prefix: String = ""): SlideNode {
    return SlideNode("$prefix$title")
}

fun Topic.toNode(): SlideNode {
    return SlideNode(name) {
        println("Build SlideNode for $name")
        val ret = arrayListOf<SlideNode>()
        topics.mapTo(ret, Topic::toNode)
        slides.mapIndexedTo(ret) { i, s -> s.toNode("${i + 1}. ") }
        ret
    }

}

class SlidesView() : View("Topics") {
    val path = Paths.get(
        if (isWindows) "G:/Ekklesiologie/Seminar" else "/Users/ee/Documents/Bibelschule/Ekklesiologie/Seminar")
    val jsonFile = path.resolve("slides.json")
    val mapper = ee.slides.mapper()
    val presentation: Presentation = mapper.readValue(jsonFile.toFile())
    val rootNode = presentation.toNode()

    override val root = treeview<SlideNode> {
        root = TreeItem(rootNode)
        root.isExpanded = true
        cellFormat { text = it.label }
        onUserSelect {
            println(it)
        }
        populate {
            it.value.slidesFactory()
        }
    }
}
