package cc.xfl12345.mybigdata.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.IntStream;

public class StudyJavaList {
    // JSON TOOL
    private ObjectMapper objectMapper = new ObjectMapper();

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @BeforeTestClass
    public void beforeTestClass() {
        setObjectMapper(new ObjectMapper());
    }

    @AfterTestClass
    public void afterTestClass() {
        setObjectMapper(null);
    }

    @Test
    public void testMemorySet() {
        Integer[] array = new Integer[100];
        Arrays.fill(array, 0);

        ArrayList<Integer> source = new ArrayList<>(array.length);
        Collections.addAll(source, array);

        for (int i = 0; i < 100; i++) {
            int item = 100 - 1 - i;
            source.set(item, i);
        }

        // source.forEach(System.out::println);
        System.out.println(objectMapper.valueToTree(source).toPrettyString());
    }

    @Test
    public void studySortedMap() {
        List<Integer> integerList = new ArrayList<>(IntStream.range(0, 200).boxed().toList());
        System.out.println("Before shuffle: " + objectMapper.valueToTree(integerList).toPrettyString());
        Collections.shuffle(integerList);
        System.out.println("After shuffle: " + objectMapper.valueToTree(integerList).toPrettyString());
        ConcurrentSkipListMap<Integer, Integer> integerConcurrentSkipListMap = new ConcurrentSkipListMap<>();
        integerList.parallelStream().forEach(item -> integerConcurrentSkipListMap.put(item, item));
        System.out.println("Values: " + objectMapper.valueToTree(integerConcurrentSkipListMap.values()).toPrettyString());
        System.out.println("Keys: " + objectMapper.valueToTree(integerConcurrentSkipListMap.keySet()).toPrettyString());
    }
}
