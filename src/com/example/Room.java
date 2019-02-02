package com.example;

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
        for (int i = 0; i < directions.length; i++) {
            if (inputDirection.equals(directions[i].getDirectionName())) {
                newRoom = directions[i].getRoom();
                return newRoom;
            }
        }
        return null;
    }

    public String possibleDirection() {
        StringBuilder directionString = new StringBuilder();
        directionString.append("From here, you can go: ");

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