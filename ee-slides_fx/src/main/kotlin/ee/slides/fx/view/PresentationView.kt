package ee.slides.fx.view

import javafx.scene.layout.BorderPane
import tornadofx.*

class PresentationView : View("Slides") {
    override val root = BorderPane()
    val slides: SlidesView by inject()

    init {
        /*
        val path = Paths.get(isWindows().ifElse("G:/Ekklesiologie/Seminar",
                "/Users/eugeis/Documents/Bibelschule/Ekklesiologie/Seminar"))
        val jsonFile = path.resolve("slides.json")
        val mapper = ee.slides.mapper()
        val presentation: Presentation = mapper.readValue(jsonFile.toFile())
        slides.rootTopic = presentation
        */

        // Enable communication between the views
        //slides.master = this

        // Assign the DetailView root node to the center property of the BorderPane
        root.center = slides.root

        // Find the HeaderView and assign it to the BorderPane top (alternative approach)
        //root.top = find(HeaderView::class)
    }
}