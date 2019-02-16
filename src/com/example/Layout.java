package com.example;

import java.util.ArrayList;

public class Layout {
    private String startingRoom;
    private String endingRoom;
    private ArrayList<Room> rooms;
    private Player player;

    public String getStartingRoom() {
        return startingRoom;
    }

    public String getEndingRoom() {
        return endingRoom;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * Method that returns a room object when given the name of a certain room.
     *
     * @param inputRoomName   the name of the room.
     * @return Room.
     **/
    public Room roomObjectFromName(String inputRoomName) {
        if (inputRoomName == null) {
            return null;
        }
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getName().equals(inputRoomName)) {
                return rooms.get(i);
            }
        }
        return null;
    }
}
