package zad1;
import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class Tools {

    static Options createOptionsFromYaml(String fileName) throws Exception {
        InputStream is = new FileInputStream(fileName);
        Map<String, Object> map = new Yaml().load(is);
        Map<String, List<String>> clientsMap;
        clientsMap = (Map<String, List<String>>) map.get("clientsMap");
        return new Options(
                map.get("host").toString(),
                Integer.parseInt(map.get("port").toString()),
                Boolean.parseBoolean(map.get("concurMode").toString()),
                Boolean.parseBoolean(map.get("showSendRes").toString()),
                clientsMap
        );
    }

}
