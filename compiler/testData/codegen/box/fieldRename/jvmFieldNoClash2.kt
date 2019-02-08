// TARGET_BACKEND: JVM
// WITH_RUNTIME

class A {
    val x = "outer"

    companion object {
        @JvmField
        val x = "companion"
    }
}

fun box(): String {
    if (A().x != "outer") return "Fail outer"
    if (A.x != "companion") return "Fail companion"

    return "OK"
}
