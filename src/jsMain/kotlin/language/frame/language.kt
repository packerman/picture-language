package language.frame

import language.math.Vector

//fun <C> wave(): Painter<C> = TODO()
//
//val rogers: Painter = TODO()
//
//fun <C> wave4(): Painter<C> = flippedPairs(wave())

typealias Unary<T> = (T) -> T

typealias Binary<T> = (T, T) -> T

data class Frame(val origin: Vector, val edge1: Vector, val edge2: Vector) {
    val coordMap: Unary<Vector>
        get() = { v ->
            origin + edge1 * v.x + edge2 * v.y
        }

    companion object {
        fun fromPoints(origin: Vector, point1: Vector, point2: Vector) = Frame(origin, point1 - origin, point2 - origin)
    }
}

typealias Painter<C> = (context: C, frame: Frame) -> Unit

fun <T> identity(): Unary<T> = { it }

fun <C> Painter<C>.transform(origin: Vector, corner1: Vector, corner2: Vector): Painter<C> =
    { context, frame ->
        val m = frame.coordMap
        val newOrigin = m(origin)
        this(context, Frame(newOrigin, m(corner1) - newOrigin, m(corner2) - newOrigin))
    }

fun <C> Painter<C>.flipVert(): Painter<C> = transform(
    Vector(0.0, 1.0),
    Vector(1.0, 1.0),
    Vector(0.0, 0.0)
)

fun <C> Painter<C>.flipHoriz(): Painter<C> = transform(
    Vector(1.0, 0.0),
    Vector(0.0, 0.0),
    Vector(1.0, 1.0)
)

infix fun <C> Painter<C>.beside(other: Painter<C>): Painter<C> {
    val splitPoint = Vector(0.5, 0.0)
    val paintLeft = transform(Vector(0.0, 0.0), splitPoint, Vector(0.0, 1.0))
    val paintRight = other.transform(splitPoint, Vector(1.0, 0.0), Vector(0.5, 1.0))
    return { context, frame ->
        paintLeft(context, frame)
        paintRight(context, frame)
    }
}

fun <C> Painter<C>.rotate180(): Painter<C> = transform(
    Vector(1.0, 1.0),
    Vector(1.0, 0.0),
    Vector(0.0, 1.0)
)

infix fun <C> Painter<C>.below(other: Painter<C>): Painter<C> {
    val splitPoint = Vector(0.0, 0.5)
    val paintTop = other.transform(splitPoint, Vector(1.0, 0.5), Vector(0.0, 1.0))
    val paintBottom = transform(Vector(0.0, 0.0), Vector(1.0, 0.0), splitPoint)
    return { context, frame ->
        paintTop(context, frame)
        paintBottom(context, frame)
    }
}

fun <C> squareOfFour(
    tl: Unary<Painter<C>>,
    tr: Unary<Painter<C>>,
    bl: Unary<Painter<C>>,
    br: Unary<Painter<C>>
): Unary<Painter<C>> = { painter ->
    val top = tl(painter) beside tr(painter)
    val bottom = bl(painter) beside br(painter)
    bottom below top
}

fun <C> flippedPairs(painter: Painter<C>): Painter<C> {
    val painter2 = painter beside painter.flipVert()
    return painter2 below painter2
}

fun <C> Painter<C>.rightSplit(n: Int): Painter<C> =
    if (n == 0) this
    else {
        val smaller = rightSplit(n - 1)
        this beside (smaller below smaller)
    }

fun <C> Painter<C>.upSplit(n: Int): Painter<C> =
    if (n == 0) this
    else {
        val smaller = upSplit(n - 1)
        this below  (smaller beside  smaller)
    }

fun <C> Painter<C>.cornerSplit(n: Int): Painter<C> =
    if (n == 0) this
    else {
        val up = upSplit(n - 1)
        val right = rightSplit(n - 1)
        val topLeft = up beside up
        val bottomRight = right below right
        val corner = cornerSplit(n - 1)
        (this below topLeft) beside (bottomRight below corner)
    }

fun <C> Painter<C>.squareLimit(n: Int): Painter<C> {
    val combine4 = squareOfFour(
        Painter<C>::flipHoriz,
        identity(),
        Painter<C>::rotate180,
        Painter<C>::flipVert
    )
    return combine4(cornerSplit(n))
}

fun <C> Painter<C>.printing(): Painter<C> = { context, frame ->
    println("Frame $frame")
    this(context, frame)
}

