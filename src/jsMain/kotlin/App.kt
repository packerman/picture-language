import language.frame.*
import language.math.Segment
import language.math.Vector
import web.canvas.CanvasRenderingContext2D
import web.dom.document
import web.html.HTMLCanvasElement

fun arrow(length: Double, width: Double): Painter<CanvasRenderingContext2D> {
    val start = (1.0 - length) / 2.0
    val end = start + length
    return segmentsToPainter(
        listOf(
            Segment(Vector(start, start), Vector(end, end)),
            Segment(Vector(end, end), Vector(end - width, end)),
            Segment(Vector(end, end), Vector(end, end - width))
        )
    )
}

infix fun <C : CanvasRenderingContext2D> Painter<C>.withState(action: C.() -> Unit): Painter<C> = { context, frame ->
    context.save()
    context.action()
    this(context, frame)
    context.restore()
}

fun main() {
    val canvas = document.getElementById("render") as HTMLCanvasElement
    val context = requireNotNull(canvas.getContext(CanvasRenderingContext2D.ID)) {
        "Cannot initialize Canvas 2D context"
    }

//    val painter = arrow(0.85, 0.15).squareLimit(0)
    val painter = arrow(0.85, 0.15).printing().squareLimit(5)
    println("Canvas size ${canvas.width} ${canvas.height}")

    val frame = Frame.fromPoints(
        Vector(0.0, canvas.height - 1.0),
        Vector(canvas.width - 1.0, canvas.height - 1.0),
        Vector(0.0, 0.0)
    )

    painter(context, frame)
}
