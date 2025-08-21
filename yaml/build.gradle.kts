plugins {
    id("night-config-lib")
}

dependencies {
	api(project(":core"))
	implementation(libs.snakeYaml)
}