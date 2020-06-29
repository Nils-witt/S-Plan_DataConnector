/*
 * Copyright (c) 2020. Nils Witt
 */

package de.nils_witt.splan;

import com.google.gson.Gson;
import de.nils_witt.splan.dataModels.Course;
import de.nils_witt.splan.dataModels.Lesson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class StundenplanUntis {
    private final Logger logger;
    private final Api api;
    private final Gson gson = new Gson();

    public StundenplanUntis(Logger logger, Api api) {
        this.logger = logger;
        this.api = api;
    }

    public ArrayList<Lesson> readDocument(String document) {
        BufferedReader reader;
        ArrayList<Lesson> lessons = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(document));
            String line = reader.readLine();
            while (line != null) {
                Lesson lesson = new Lesson();
                //System.out.println(line);

                String[] lessonParts = line.split(";");
                int id = Integer.parseInt(lessonParts[0]);
                String className = lessonParts[1].replaceAll("\"", "");
                String teacher = lessonParts[2].replaceAll("\"", "");
                String group = lessonParts[3].replaceAll("\"", "");
                String room = lessonParts[4].replaceAll("\"", "");
                int day = Integer.parseInt(lessonParts[5]);
                int lessonNumber = Integer.parseInt(lessonParts[6]);

                lesson.setDay(day);
                lesson.setLessonNumber(lessonNumber);
                lesson.setRoom(room);
                lesson.setTeacher(teacher);

                lesson.setCourse(new Course());
                lesson.getCourse().setGrade(className);
                String[] groupParts = group.split("-");
                if (groupParts.length == 2) {
                    lesson.getCourse().setSubject(groupParts[0]);

                    lesson.getCourse().setGroup(groupParts[1].substring(groupParts[1].length() - 1));
                    if (groupParts[1].contains("LK")) {
                        lesson.getCourse().setGroup("L" + lesson.getCourse().getGroup());
                    }
                }

                if (!className.equals("")) {
                    lessons.add(lesson);
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        api.addLessons(lessons);
        return lessons;
    }
}
