import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction
import org.jsonschema2pojo.DefaultGenerationConfig
import org.jsonschema2pojo.GenerationConfig
import org.jsonschema2pojo.Jsonschema2Pojo

class LabJSONSchemaToPojoCodeGen extends DefaultTask {

    FileCollection source
    String targetPackage;
    boolean includeAdditionalProperties;
    boolean generateBuilders;
    boolean useLongIntegers;
    File targetDirectory

    @TaskAction
    def generate() {
        GenerationConfig configuration = new DefaultGenerationConfig() {
            @Override
            Iterator<URL> getSource() {
                LabJSONSchemaToPojoCodeGen.this.source.collect {it.toURI()}.iterator()
            }

            @Override
            File getTargetDirectory() {
                return LabJSONSchemaToPojoCodeGen.this.targetDirectory
            }

            @Override
            String getTargetPackage() {
               LabJSONSchemaToPojoCodeGen.this.targetPackage
            }

            @Override
            boolean isIncludeAdditionalProperties() {
                LabJSONSchemaToPojoCodeGen.this.includeAdditionalProperties
            }

            @Override
            boolean isGenerateBuilders() {
                LabJSONSchemaToPojoCodeGen.this.generateBuilders
            }



            @Override
            boolean isUseLongIntegers() {
                LabJSONSchemaToPojoCodeGen.this.useLongIntegers
            }

        }
        Jsonschema2Pojo.generate(configuration)
    }

}
