package ee.slides.dsl
import ee.design.gen.kt.DesignKotlinGenerator
import ee.lang.integ.dPath

fun main(args: Array<String>) {
    val generator = DesignKotlinGenerator(Slides)
    generator.generate(dPath)
}