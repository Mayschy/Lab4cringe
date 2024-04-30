package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class CourseEntity {

    private String name;
    private String teacherLastName;

    public void setName(String name) {
        this.name = name;
    }

    public void setTeacherLastName(String teacherLastName) {
        this.teacherLastName = teacherLastName;
    }

    public CourseEntity(String name, String teacherLastName) {
        this.name = name;
        this.teacherLastName = teacherLastName;
    }

    public String getName() {
        return name;
    }

    public String getTeacherLastName() {
        return teacherLastName;
    }

    public abstract List<LectureEntity> getLectures();

    @Override
    public String toString() {
        return "javasem2lab1task1.CourseEntity{" +
                "name='" + name + '\'' + ", teacherLastName='" + teacherLastName + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseEntity that = (CourseEntity) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(teacherLastName, that.teacherLastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, teacherLastName);
    }

    public LectureEntity[] findLecturesWithMinStudents() {
        List<LectureEntity> lectures = getLectures();
        if (lectures.isEmpty()) return null;

        int minStudentCount = lectures.get(0).getStudentCount();
        for (LectureEntity lecture : lectures) {
            if (lecture.getStudentCount() < minStudentCount) {
                minStudentCount = lecture.getStudentCount();
            }
        }

        List<LectureEntity> minLectures = new ArrayList<>();
        for (LectureEntity lecture : lectures) {
            if (lecture.getStudentCount() == minStudentCount) {
                minLectures.add(lecture);
            }
        }

        return minLectures.toArray(new LectureEntity[0]);
    }

    public LectureEntity[] findLecturesByKeyword(String keyword) {
        List<LectureEntity> lectures = getLectures();
        if (lectures.isEmpty()) return null;

        List<LectureEntity> matchingLectures = new ArrayList<>();
        for (LectureEntity lecture : lectures) {
            if (lecture.getTopic().contains(keyword)) {
                matchingLectures.add(lecture);
            }
        }

        return matchingLectures.toArray(new LectureEntity[0]);
    }

    public char[] findLastLettersInTeacherLastName() {
        String lastName = getTeacherLastName();
        if (lastName.isEmpty()) return null;

        char[] lastLetters = new char[1];
        for (int i = 0; i < lastName.length(); i++) {
            lastLetters[0] = lastName.charAt(i);
        }

        return lastLetters;
    }

    public List<LectureEntity> sortLecturesByWordCount() {
        List<LectureEntity> lectures = new ArrayList<>(getLectures());
        int n = lectures.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                int wordCount1 = lectures.get(j).getTopic().split(" ").length;
                int wordCount2 = lectures.get(j + 1).getTopic().split(" ").length;

                if (wordCount1 > wordCount2) {
                    LectureEntity temp = lectures.get(j);
                    lectures.set(j, lectures.get(j + 1));
                    lectures.set(j + 1, temp);
                }
            }
        }
        return lectures;
    }

    public List<LectureEntity> insertionSortLecturesByTopic() {
        List<LectureEntity> lectures = new ArrayList<>(getLectures());
        for (int i = 1; i < lectures.size(); i++) {
            LectureEntity key = lectures.get(i);
            int j = i - 1;
            while (j >= 0 && lectures.get(j).getTopic().compareTo(key.getTopic()) > 0) {
                lectures.set(j + 1, lectures.get(j));
                j = j - 1;
            }
            lectures.set(j + 1, key);
        }
        return lectures;
    }

    public void addLecture(LectureEntity lecture) {
        getLectures().add(lecture);
    }

    public void removeLecture(LectureEntity lecture) {
        getLectures().remove(lecture);
    }
}
