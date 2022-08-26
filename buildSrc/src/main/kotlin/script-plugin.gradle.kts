tasks.register("hello"){
    println("configure task hello")
    group = "test"
    doLast {
        println("executed task hello")
    }
}