class Z

class Q {
    inline fun f(z: Z) = "OK"
}

fun box(): String {
    return Z().run(Q()::f)
}

// 0 Function1.invoke
