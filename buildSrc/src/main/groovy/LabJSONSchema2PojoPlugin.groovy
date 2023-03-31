import org.gradle.api.Plugin
import org.gradle.api.Project

class LabJSONSchema2PojoPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.apply(plugin: LabApiDependencyResolverPlugin)

        def jsonSchemaSources = project.container(JSONSchemaSource)

        project.extensions.add('labJsonSchema2Pojo', jsonSchemaSources)

        jsonSchemaSources.all {
            def source = delegate as JSONSchemaSource
            source.codeGen = project.task("labJSONSchemaToPojoCodeGen${source.name.capitalize()}",
                    type: LabJSONSchemaToPojoCodeGen,
                    group: 'build',
                    description: "Code generator" ) as LabJSONSchemaToPojoCodeGen
            source.codeGen.targetDirectory = new File(project.buildDir, "generated-js2p-code-${source.name}")
            source.codeGen.dependsOn(project.tasks.labResolveAPIDependencies)

            project.afterEvaluate {
                project.sourceSets[source.sourceSet].java.srcDirs += [source.codeGen.targetDirectory]
                project.tasks.compileJava.dependsOn(source.codeGen)
            }
        }

    }
}