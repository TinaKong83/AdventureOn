package com.example;

import java.util.Arrays;

public class Room {
    private String name;
    private String description;
    private Directions[] directions;

    //get directions you can go from this room

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Directions[] getDirections() {
        return directions;
    }

    public String roomFromDirection(String inputDirection) {
        String newRoom;
        inputDirection = inputDirection.toLowerCase();
        for (int i = 0; i < directions.length; i++) {
            //System.out.println("my input direction is " + inputDirection);
            //System.out.println("my direction name is " + directions[i].getDirectionName().toLowerCase());
            if (inputDirection.equals(directions[i].getDirectionName().toLowerCase())) {
                newRoom = directions[i].getRoom();
                //System.out.println("This prints if I have a room for the direction");
                //System.out.println("My room name from the direction is: " + newRoom);
                return newRoom;
            }
        }
        return "Room not found";
    }

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
