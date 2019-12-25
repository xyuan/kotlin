fun foo(paramFirst: Int, paramSecond: Long) {}

fun usage(longParam: Long) {
    foo(paramFirst = 10, <caret>)
}

// LANGUAGE_VERSION: 1.4
// EXIST: longParam
// EXIST: { itemText: "paramSecond =" }