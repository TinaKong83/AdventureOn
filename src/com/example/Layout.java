package com.example;

import java.util.ArrayList;

public class Layout {
    private String startingRoom;
    private String endingRoom;
    private ArrayList<Room> rooms;

    public String getStartingRoom() {
        return startingRoom;
    }

    public String getEndingRoom() {
        return endingRoom;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public Room roomObjectFromName(String inputRoomName) {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getName().equals(inputRoomName)) {
                return rooms.get(i);
            }
        }
        return null;
    }
}
