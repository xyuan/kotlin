// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_PARAMETER -UNUSED_VARIABLE

@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

import kotlin.internal.OnlyInputTypes

interface Bound
class First : Bound
class Second : Bound
class Inv<I >(val v: I)
class InvB<I : Bound>(val v: I)
class In<in C : Bound>(v: C)
class Out<out O : Bound>(val v: O)

fun <@OnlyInputTypes M> strictId(arg: M): M = arg
fun <@OnlyInputTypes S> strictSelect(arg1: S, arg2: S): S = arg1

fun testOK(first: First, bound: Bound, second: Second, x: InvB<First>, y: In<First>, z: Out<First>) {
    strictId(Inv(first))
    strictId(In(first))
    strictId(Out(first))
    strictId(Inv(15))
    strictId(Inv("foo"))
    strictSelect(Inv(first), Inv(first))
    strictSelect<Out<Bound>>(Out(first), Out(second))
    strictSelect(Out(first), Out(bound))
    strictSelect(In(first), In(bound))
    val out: Out<Bound> = strictSelect(Out(first), Out(second))
}

fun testFail(first: First, bound: Bound, second: Second, x: InvB<First>, y: In<First>, z: Out<First>) {
    <!TYPE_INFERENCE_ONLY_INPUT_TYPES!>strictSelect<!>(Inv(15), Inv(1.5))
    <!TYPE_INFERENCE_ONLY_INPUT_TYPES!>strictSelect<!>(InvB(first), InvB(bound))
    <!TYPE_INFERENCE_ONLY_INPUT_TYPES!>strictSelect<!>(Out(first), Out(second))
    <!TYPE_INFERENCE_ONLY_INPUT_TYPES!>strictSelect<!>(In(first), In(second))
}
