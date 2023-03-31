import org.gradle.api.tasks.Sync

class LabResolveApiDependencies extends Sync {

    LabResolveApiDependencies() {
        from {
            project.configurations.labApiSpecification.resolve().collect {
                project.zipTree(it)
            }
        }
        include("**/*.json")
        exclude("**/META-INF/**")
        into(project.ext.labApiSpecsDir)
        includeEmptyDirs = false
    }
}
