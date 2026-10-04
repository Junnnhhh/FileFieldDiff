import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileFieldDiff {

    private final FileFieldDiffConfig config;

    public FileFieldDiff(FileFieldDiffConfig config) {
        this.config = config;
    }

    public void run() throws IOException {

        List<String> targetFieldValueList = new ArrayList<>();
        List<String> notMatchedList = new ArrayList<>();

        try(BufferedReader reader = Files.newBufferedReader(config.targetPath())) {
            String line;

            while(( line = reader.readLine()) != null) {
                String[] fields = line.split("\\|", -1);

                if(fields.length <= config.targetFieldIndex()) {
                    throw new IllegalArgumentException("target 필드 인덱스가 유효하지 않습니다.");
                }

                targetFieldValueList.add(fields[config.targetFieldIndex()]);
            }
        }

        try(BufferedReader reader = Files.newBufferedReader(config.sourcePath())) {

            String line;

            while(( line = reader.readLine()) != null) {
                String[] fields = line.split("\\|", -1);

                if(fields.length <= config.sourceFieldIndex()) {
                    throw new IllegalArgumentException("source 필드 인덱스가 유효하지 않습니다.");
                }

                String sourceFieldValue = fields[config.sourceFieldIndex()];

                if(!this.checkContainsFieldValues(sourceFieldValue, targetFieldValueList)) {
                    notMatchedList.add(line);
                }
            }
        }

        Files.createDirectories(config.outputPath().getParent());
        Files.write(config.outputPath(), notMatchedList);
    }

    private static boolean checkContainsFieldValues(String sourceValue, List<String> targetList) {

        for(String targetRow : targetList) {
            if(targetRow.contains(sourceValue)) {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) throws Exception {

        FileFieldDiffConfig config = ConfigLoader.load();

        FileFieldDiff fileFieldDiff = new FileFieldDiff(config);
        fileFieldDiff.run();
    }
}
