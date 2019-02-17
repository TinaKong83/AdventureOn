package com.example;

import java.util.ArrayList;
import java.util.Arrays;

public class Room {
    private String name;
    private String description;
    private Directions[] directions;

    //items that are available to pick up in the room
    private ArrayList<Item> items;
    private Monster monsterInRoom;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Directions[] getDirections() {
        return directions;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public Monster getMonsterInRoom() {
        return monsterInRoom;
    }

    public Item getItemObjectFromName(String inputItemName, ArrayList<Item> availableRoomItems) {
        for (int i = 0; i < availableRoomItems.size(); i++) {
            if (availableRoomItems.get(i).getName().equals(inputItemName)) {
                return availableRoomItems.get(i);
            }
        }
        return null;
    }

    /**
     * Method that determines your room given a user inputted direction.
     *
     * @param inputDirection   the direction command the user inputs (e.g. east, west).
     * @return String.
     **/
    public String roomFromDirection(String inputDirection) {
        if (inputDirection == null) {
            return null;
        }
        String newRoom;
        inputDirection = inputDirection.toLowerCase();
        for (int i = 0; i < directions.length; i++) {
            if (inputDirection.equals(directions[i].getDirectionName().toLowerCase())) {
                newRoom = directions[i].getRoom();
                return newRoom;
            }
        }
        return null;
    }

    /**
     * Method that finds all possible directions the user can move in.
     *
     * @return String.
     **/
    public String possibleDirection() {
        StringBuilder directionString = new StringBuilder();
        if (directions.length == 1) {
            return directionString.append(directions[0].getDirectionName()).toString();
        }
        for (int i = 0; i < directions.length - 1; i++) {
            if (directions.length > 1) {
                directionString.append(directions[i].getDirectionName());
                directionString.append(", ");
            }
        }
        String lastRoom = directions[directions.length - 1].getDirectionName();
        directionString.append(lastRoom);
        return directionString.toString();
    }
}
