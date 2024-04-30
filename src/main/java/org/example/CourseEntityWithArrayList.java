package org.example;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CourseEntityWithArrayList extends CourseEntity {
    private final List<LectureEntity> lectures;

    public CourseEntityWithArrayList(String name, String teacherLastName, List<LectureEntity> lecs) {
        super(name, teacherLastName);
        lectures = new ArrayList<>(lecs);
    }

    @Override
    public List<LectureEntity> getLectures() {
        return lectures;
    }

    @Override
    public List<LectureEntity> insertionSortLecturesByTopic() {
        List<LectureEntity> sortedLectures = new ArrayList<>(lectures);
        sortedLectures.sort((l1, l2) -> {
            return l1.getTopic().compareTo(l2.getTopic());
        });
        return sortedLectures;
    }

    @Override
    public List<LectureEntity> sortLecturesByWordCount() {
        List<LectureEntity> sortedLectures = new ArrayList<>(lectures);
        Collections.sort(sortedLectures);
        return sortedLectures;
    }

}