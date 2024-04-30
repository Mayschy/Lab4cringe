package org.example;
import java.util.Date;
import java.util.Objects;

/**
 * Клас, який представляє лекцію із датою, темою та кількістю студентів.
 */
// Ваш клас LectureEntity повинен виглядати так:
public class LectureEntity implements Comparable<LectureEntity> {

    private Date date;
    private String topic;
    private int studentCount;

    public LectureEntity(Date date, String topic, int studentCount) {
        this.date = date;
        this.topic = topic;
        this.studentCount = studentCount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    @Override
    public String toString() {
        return "LectureEntity{" +
                "date=" + date +
                ", topic='" + topic + '\'' +
                ", studentCount=" + studentCount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LectureEntity that = (LectureEntity) o;
        return studentCount == that.studentCount &&
                Objects.equals(date, that.date) &&
                Objects.equals(topic, that.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, topic, studentCount);
    }

    @Override
    public int compareTo(LectureEntity o) {
        return Integer.compare(this.getTopic().split(" ").length, o.getTopic().split(" ").length);
    }
}

