package com.example;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
/**
 * Hello world!
 *
 */
import java.time.LocalDateTime;
import java.util.*;
import java.lang.reflect.Type;

import com.example.App.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;


public class App{
     static final String FILE_PATH = "tasks.json";
     public static ArrayList<Task> loadTasksFromJson() {
        try {
            if (!Files.exists(Paths.get(FILE_PATH))) return new ArrayList<>();
    
            Reader reader = new FileReader(FILE_PATH);
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();
    
            Type taskListType = new TypeToken<ArrayList<Task>>() {}.getType();
            ArrayList<Task> list = gson.fromJson(reader, taskListType);
    
            if (list == null) return new ArrayList<>(); // ✅ key fix here
            return list;
    
        } catch (Exception e) {
            System.out.println("Failed to load tasks.json. Creating new list.");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    

    public static void saveTasksToJson(ArrayList<Task> taskList) {
        try {
            Writer writer = new FileWriter(FILE_PATH);
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();

            gson.toJson(taskList, writer);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static int getMax(ArrayList<Task> list){
        if(list == null) return 0;
        int num = Integer.MIN_VALUE;
        for(var i : list){
            num = Math.max(i.id, num);
        }
        if(num == Integer.MIN_VALUE) return 0;
        return num;
    }
        static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return LocalDateTime.parse(json.getAsString());
        }
    }
    public static boolean checkValid(int id, ArrayList<Task> list){
        Task task = getTaskFromID(id, list);
        if(task == null) return false;
        return true;
    }
    public static Task getTaskFromID(int id, ArrayList<Task> list){
        for(var i : list){
            if(i.id == id) return i;
        }
        return null;
    }
    public static void updateTimestamp(Task task) {
    task.updatedAt = LocalDateTime.now();
}

    public static void main(String args[]){
       if(args.length < 1) {
        System.out.println("Incorrect number of command arguments");
        return;
       }
       ArrayList<Task> taskList = loadTasksFromJson();
       int lastId = getMax(taskList); // for now 0 but eventually take it from the json

       String crud = args[0];
       if(crud.equals("add")){
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < args.length; i++){
            sb.append(args[i]).append(" ");
        }
        String desc = sb.toString();
        Task newTask = new Task(desc, ++lastId);
        taskList.add(newTask);
       } else if(crud.equals("update")){
        String id = args[1];
        StringBuilder sb = new StringBuilder();
        for(int i = 2; i < args.length; i++){
            sb.append(args[i]).append(" ");
        }
        String updatedDesc = sb.toString();
        int intId = Integer.parseInt(id);
        if(!checkValid(intId, taskList)){
            System.out.println("Invalid ID!");
            return;
        }
        Task task = getTaskFromID(intId, taskList);
        task.description = updatedDesc;
        updateTimestamp(task);
       } else if(crud.equals("delete")){
        String id = args[1];
        int intId = Integer.parseInt(id);

        if(!checkValid(intId, taskList)){
            System.out.println("Invalid ID!");
            return;
        }
        Task task = getTaskFromID(intId, taskList);
        taskList.remove(task);
       } else if(crud.equals("mark-in-progress")){
        String id = args[1];
        int intId = Integer.parseInt(id);
        if(!checkValid(intId, taskList)) {
            System.out.println("Invalid ID!");
            return;
        }
        Task task = getTaskFromID(intId, taskList);
        task.status = "in progress";
        updateTimestamp(task);
       } else if(crud.equals("mark-done")){
        String id = args[1];
        int intId = Integer.parseInt(id);
        if(!checkValid(intId, taskList)) {
            System.out.println("Invalid ID!");
            return;
        }
        Task task = getTaskFromID(intId, taskList);
        task.status = "done";
        updateTimestamp(task);
       } else if(crud.equals("list")){
        String arg2 = args[1];
        for (var t : taskList) {
            if (arg2.equals("todo") && t.status.equals("todo") ||
            arg2.equals("done") && t.status.equals("done") ||
            arg2.equals("in-progress") && t.status.equals("in progress")) {
            
            System.out.println("[" + t.id + "] " + t.status + " - " + t.description);
        }
        }
    }  
    saveTasksToJson(taskList);
    }
}
