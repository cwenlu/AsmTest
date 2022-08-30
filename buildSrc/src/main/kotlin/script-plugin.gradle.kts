tasks.register("hello") {
    println("configure task hello")
    group = "test"
    doLast {
        println("executed task hello")
    }
}

//https://docs.gradle.org/current/userguide/custom_plugins.html#sec:precompiled_plugins