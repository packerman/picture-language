package language.context

import language.math.Segment
import language.math.Vector
import web.canvas.CanvasRenderingContext2D
import web.html.HTMLCanvasElement

fun segmentsToPainter(segments: List<Segment>): Painter = { context ->
    fun drawLine(start: Vector, end: Vector) {
        context.moveTo(start.x, start.y)
        context.lineTo(end.x, end.y)
    }

    context.beginPath()
    for (segment in segments) {
        drawLine(segment.start, segment.end)
    }
    context.closePath()
    context.stroke()
}

fun paint(canvas: HTMLCanvasElement, painter: Painter) {
    painter.transform(Vector(0.0, canvas.height - 1.0),
        Vector(canvas.width - 1.0, canvas.height - 1.0),
        Vector(0.0, 0.0))
}
