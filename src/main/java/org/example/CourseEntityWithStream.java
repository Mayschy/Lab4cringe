package org.example;
import java.util.List;
import java.util.stream.Collectors;

public class CourseEntityWithStream extends CourseEntityWithArrayList {

    public CourseEntityWithStream(String name, String teacherLastName, List<LectureEntity> lecs) {
        super(name, teacherLastName, lecs);
    }

    @Override
    public List<LectureEntity> insertionSortLecturesByTopic() {
        return getLectures().stream()
                .sorted((l1, l2) -> l1.getTopic().compareTo(l2.getTopic()))
                .collect(Collectors.toList());
    }

    @Override
    public List<LectureEntity> sortLecturesByWordCount() {
        return getLectures().stream()
                .sorted((l1, l2) -> Integer.compare(l1.getTopic().split(" ").length, l2.getTopic().split(" ").length))
                .collect(Collectors.toList());
    }
}
