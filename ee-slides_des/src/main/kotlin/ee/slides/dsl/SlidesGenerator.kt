package ee.slides.dsl
import ee.design.gen.DesignKotlinGenerator
import ee.lang.integ.dPath
import ee.lang.integ.eePath

fun main(args: Array<String>) {
    val generator = DesignKotlinGenerator(Slides)
    generator.generate(dPath)
}