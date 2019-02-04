package com.example;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class Adventure {
    private static final int STATUS_OK = 200;
    private static Layout layout;
    private static Room currentRoom;
    private static ArrayList<Room> rooms;
    private static boolean gameEnded = false;
    private static boolean commandFound = false;

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public Layout getLayout() {
        return layout;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public static void main(String[] arguments) {
        String url = "https://courses.engr.illinois.edu/cs126/adventure/siebel.json";
        // Make an HTTP request to the above URL
        try {
            makeApiRequest(url);
        } catch (UnirestException e) {
//            e.printStackTrace();
            System.out.println("Network not responding");
        } catch (MalformedURLException e) {
            System.out.println("Bad URL: " + url);
        }

        System.out.println(beginGame());
        Scanner scanner = new Scanner(System.in);
        while (!gameEnded) {
            commandFound = false;
            String userInput = scanner.nextLine().toLowerCase();
            if (userEndsGame(userInput)) {
                System.out.println("The game has ended");
                System.exit(0);
            }
            String[] possibleDirectionArray = currentRoom.possibleDirection().toLowerCase().split(", ");
            if (userInput.length() > 1 && userInput.substring(0, 2).equals("go")) {
                if (findDirectionInArray(possibleDirectionArray, userInput)) {
                    commandFound = true;
                    System.out.println(roomInformation(currentRoom));
                }
                if (!commandFound) {
                    System.out.println(printWrongDirection(userInput));
                }
            } else {
                System.out.println(printInvalidCommand(userInput));
            }
        }
    }

    private static String beginGame() {
        currentRoom = layout.roomObjectFromName(layout.getStartingRoom());
        String beginGame = "Your journey begins here";
        beginGame = beginGame + "\n" + currentRoom.getDescription();
        beginGame = beginGame + "\n" + "From here, you can go: " + currentRoom.possibleDirection();

        return beginGame;
    }

    //method to check if an array contains a specific direction as an element
    public static boolean findDirectionInArray(String[] directionsArray, String userInput) {
        if (userInput == null || directionsArray == null || userInput.length() < 1) {
            return false;
        }
        userInput = userInput.substring(3);
        for (int i = 0; i < directionsArray.length; i++) {
            if (directionsArray[i] == null) {
                continue;
            }
            if (directionsArray[i].toLowerCase().equals(userInput.toLowerCase())) {
                String currentDirection = directionsArray[i];
                String newRoomName = currentRoom.roomFromDirection(currentDirection);
                currentRoom = layout.roomObjectFromName(newRoomName);
                return true;
            }
        }
        return false;
    }

    //CHANGE TO RETURN NULL LATER???

    public static boolean userEndsGame(String userInput) {
        if (userInput == null) {
            return false;
        }
        userInput = userInput.toLowerCase();
        return userInput.equals("quit") || userInput.equals("exit");
    }

    public static String roomInformation(Room currentRoom) {
        if (currentRoom == null) {
            return null;
        }
        return currentRoom.getDescription() + "\n" + "From here, you can go: " + currentRoom.possibleDirection();
    }

    public static String printWrongDirection(String userInput) {
        if (userInput == null) {
            return "Invalid input.";
        }
        return "I can't go '" + userInput.substring(3) + "'" + "\n" + roomInformation(currentRoom);
    }

    private static String printInvalidCommand(String userInput) {
        return "I don't understand '" + userInput + "'" + "\n" + roomInformation(currentRoom);
    }

    static void makeApiRequest(String url) throws UnirestException, MalformedURLException {
        final HttpResponse<String> stringHttpResponse;

        // This will throw MalformedURLException if the url is malformed.
        new URL(url);

        stringHttpResponse = Unirest.get(url).asString();
        // Check to see if the request was successful; if so, convert the payload JSON into Java objects
        if (stringHttpResponse.getStatus() == STATUS_OK) {
            String json = stringHttpResponse.getBody();
            Gson gson = new Gson();
            layout = gson.fromJson(json, Layout.class);
            currentRoom = layout.roomObjectFromName(layout.getStartingRoom());
        }
    }
}

