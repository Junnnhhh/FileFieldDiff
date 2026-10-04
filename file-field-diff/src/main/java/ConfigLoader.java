import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigLoader {

    public static FileFieldDiffConfig load() throws Exception {

        Path baseDir = getJarDirectory();

        Path configPath = baseDir.resolve("config.properties");
        validationCheckFile(configPath, "config.properties");

        Properties properties = new Properties();

        try (BufferedReader reader = Files.newBufferedReader(configPath)) {
            properties.load(reader);
        }

        Path sourcePath = Paths.get(getRequiredProperty(properties, "source.path"));
        validationCheckFile(sourcePath, "source.path");

        Path targetPath = Paths.get(getRequiredProperty(properties, "target.path"));
        validationCheckFile(targetPath, "target.path");

        Path outputPath = Paths.get(getRequiredProperty(properties, "output.path"));

        int sourceFieldIndex;
        int targetFieldIndex;

        try {
            sourceFieldIndex = Integer.parseInt(getRequiredProperty(properties,"source.field.index")) - 1;
            targetFieldIndex = Integer.parseInt(getRequiredProperty(properties,"target.field.index")) - 1;
        } catch(NumberFormatException e) {
            throw new NumberFormatException("config.properties에서 source.field.index 또는 target.field.index의 값이 숫자인지 확인해주세요.");
        }

        return new FileFieldDiffConfig(
                sourcePath,
                targetPath,
                outputPath,
                null,
                sourceFieldIndex,
                targetFieldIndex
        );
    }

    private static String getRequiredProperty(Properties properties, String key) {

        String value = properties.getProperty(key);

        if(value == null || value.isBlank()) {
            throw new IllegalArgumentException("config.properties 필수 설정이 없습니다: " + key);
        }

        return value.trim();
    }

    private static Path getJarDirectory() throws Exception {
        return Paths.get(
                FileFieldDiff.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI()
        ).getParent();
    }

    private static void validationCheckFile(Path path, String key) {
        if(!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("대상 파일이 존재하지 않습니다. [ key : " +  key +  " , fileName : " + path.getFileName()  + " ]" );
        }
    }

}
