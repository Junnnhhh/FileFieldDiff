import java.nio.file.Path;

public record FileFieldDiffConfig(
        Path sourcePath,
        Path targetPath,
        Path outputPath,
        Path whiteListPath,
        int sourceFieldIndex,
        int targetFieldIndex
) {
}
