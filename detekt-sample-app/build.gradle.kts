dependencies {
    implementation(project(":detekt-api"))
}

detekt {

    debug = false
    buildUponDefaultConfig = true
    config = files("dconfig.yml")

    reports {
        xml.enabled = false
        html.enabled = true
        txt.enabled = false
    }
}
//./gradlew :detekt-sample-app:clean :detekt-sample-app:detekt -q --no-daemon
