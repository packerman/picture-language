package language.frame

import language.math.Segment
import language.math.Vector
import web.canvas.CanvasRenderingContext2D
import web.html.HTMLCanvasElement

fun segmentsToPainter(segments: List<Segment>): Painter<CanvasRenderingContext2D> = { context, frame ->
    fun drawLine(start: Vector, end: Vector) {
        context.moveTo(start.x, start.y)
        context.lineTo(end.x, end.y)
    }

    val m = frame.coordMap
    context.beginPath()
    for (segment in segments) {
        drawLine(m(segment.start), m(segment.end))
    }
    context.closePath()
    context.stroke()
}

fun paint(canvas: HTMLCanvasElement, action: (Frame) -> Unit) {
    val frame = Frame.fromPoints(
        Vector(0.0, canvas.height.toDouble()),
        Vector(canvas.width.toDouble(), canvas.height.toDouble()),
        Vector(0.0, 0.0)
    )
    action(frame)
}
