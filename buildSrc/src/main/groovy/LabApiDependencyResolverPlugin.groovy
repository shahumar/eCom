import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency


class LabApiDependencyResolverPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.ext.labApiSpecsDir = "${project.buildDir}/lab-api-specs"

        def c = project.configurations.create('labApiSpecification')

        project.configurations.implementation.extendsFrom(c)

        def resolveTask = project.task("labResolveAPIDependencies",
                type: LabResolveApiDependencies,
                group: 'build setup',
                description: "fetch API dependencies"
        )
        project.afterEvaluate {
            project.configurations.labApiSpecification.allDependencies.each {
                if (it instanceof DefaultProjectDependency) {
                    def buildTask = it.dependencyProject.tasks.getByPath("build")
                    resolveTask.dependsOn(buildTask)
                }
            }
        }

    }
}
