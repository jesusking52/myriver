package com.riverauction.riverauction.feature;

public interface MyLessonActiveStudentLoadable {
    void firstLoad(Integer userId);
    void loadMore(Integer lessonId, Integer nextToken);
}
