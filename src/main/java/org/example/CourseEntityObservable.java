package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CourseEntityObservable extends CourseEntity {
    private final ObservableList<LectureEntity> lectures;

    public CourseEntityObservable(String name, String teacherLastName, List<LectureEntity> lecs) {
        super(name, teacherLastName);
        lectures = FXCollections.observableList(lecs);
    }

    public ObservableList<LectureEntity> getObservableList() {
        return lectures;
    }

    @Override
    public List<LectureEntity> getLectures() {
        return lectures;
    }

    @Override
    public List<LectureEntity> insertionSortLecturesByTopic() {
        lectures.sort(Comparator.comparing(LectureEntity::getStudentCount));
        return lectures;
    }

    @Override
    public List<LectureEntity> sortLecturesByWordCount() {
        Collections.sort(lectures);
        return lectures;
    }
}
