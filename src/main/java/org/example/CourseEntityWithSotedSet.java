package org.example;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class CourseEntityWithSotedSet extends CourseEntity {

    private final TreeSet<LectureEntity> lectures;

    public CourseEntityWithSotedSet(String name, String teacherLastName, List<LectureEntity> lecs) {
        super(name, teacherLastName);
        lectures = new TreeSet<>(LectureEntity::compareTo);
        lectures.addAll(lecs);
    }

    @Override
    public List<LectureEntity> insertionSortLecturesByTopic() {
        TreeSet<LectureEntity> sortedByTopic = new TreeSet<>((l1, l2) -> {
            return l1.getTopic().compareTo(l2.getTopic());
        });

        sortedByTopic.addAll(lectures);
        return new ArrayList<>(sortedByTopic);
    }

    @Override
    public List<LectureEntity> sortLecturesByWordCount() {
        return getLectures();
    }

    @Override
    public List<LectureEntity> getLectures() {
        return new ArrayList<>(lectures);
    }
}
