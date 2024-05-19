package language.context

import language.math.Vector
import web.canvas.CanvasRenderingContext2D

typealias Painter = (context: CanvasRenderingContext2D) -> Unit

inline infix fun Painter.withState(crossinline action: CanvasRenderingContext2D.() -> Unit): Painter = { context ->
    context.save()
    context.action()
    this(context)
    context.restore()
}

fun Painter.transform(origin: Vector, corner1: Vector, corner2: Vector): Painter {
    val edge1 = corner1 - origin
    val edge2 = corner2 - origin
    return withState {
        transform(edge1.x, edge1.y, edge2.x, edge2.y, origin.x, origin.y)
    }
}

fun <T> identity(): (T) -> T = { it }

fun Painter.flipVert(): Painter = transform(
    Vector(0.0, 1.0),
    Vector(1.0, 1.0),
    Vector(0.0, 0.0)
)

fun Painter.flipHoriz(): Painter = transform(
    Vector(1.0, 0.0),
    Vector(0.0, 0.0),
    Vector(1.0, 1.0)
)

infix fun Painter.beside(other: Painter): Painter {
    val splitPoint = Vector(0.5, 0.0)
    val paintLeft = transform(Vector(0.0, 0.0), splitPoint, Vector(0.0, 1.0))
    val paintRight = other.transform(splitPoint, Vector(1.0, 0.0), Vector(0.5, 1.0))
    return { context ->
        paintLeft(context)
        paintRight(context)
    }
}

fun Painter.rotate180(): Painter = transform(
    Vector(1.0, 1.0),
    Vector(1.0, 0.0),
    Vector(0.0, 1.0)
)

infix fun Painter.below(other: Painter): Painter {
    val splitPoint = Vector(0.0, 0.5)
    val paintTop = other.transform(splitPoint, Vector(1.0, 0.5), Vector(0.0, 1.0))
    val paintBottom = transform(Vector(0.0, 0.0), Vector(1.0, 0.0), splitPoint)
    return { context ->
        paintTop(context)
        paintBottom(context)
    }
}

fun squareOfFour(
    tl: (Painter) -> Painter,
    tr: (Painter) -> Painter,
    bl: (Painter) -> Painter,
    br: (Painter) -> Painter
): (Painter) -> Painter = { painter ->
    val top = tl(painter) beside tr(painter)
    val bottom = bl(painter) beside br(painter)
    bottom below top
}

fun Painter.rightSplit(n: Int): Painter =
    if (n == 0) this
    else {
        val smaller = rightSplit(n - 1)
        this beside (smaller below smaller)
    }

fun Painter.upSplit(n: Int): Painter =
    if (n == 0) this
    else {
        val smaller = upSplit(n - 1)
        this below (smaller beside smaller)
    }

fun Painter.cornerSplit(n: Int): Painter =
    if (n == 0) this
    else {
        val up = upSplit(n - 1)
        val right = rightSplit(n - 1)
        val topLeft = up beside up
        val bottomRight = right below right
        val corner = cornerSplit(n - 1)
        (this below topLeft) beside (bottomRight below corner)
    }

fun Painter.squareLimit(n: Int): Painter {
    val combine4 = squareOfFour(
        Painter::flipHoriz,
        identity(),
        Painter::rotate180,
        Painter::flipVert
    )
    return combine4(cornerSplit(n))
}
