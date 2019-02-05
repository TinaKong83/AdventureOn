package com.example;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Adventure {
    private static final int STATUS_OK = 200;
    private static Layout layout;
    private static Room currentRoom;
    private static boolean gameEnded = false;

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public Layout getLayout() {
        return layout;
    }

    public static void main(String[] arguments) throws UnirestException, MalformedURLException {
        testAlternateUrl();
        System.out.println(beginGame());

        Scanner scanner = new Scanner(System.in);
        while (!gameEnded) {
            String originalInput = scanner.nextLine();
            String userInput = originalInput.toLowerCase();
            if (userEndsGame(originalInput) || reachedFinalRoom(currentRoom)) {
                System.out.println("You have reached your final destination.");
                break;
            }
            String[] userInputArray = userInput.split(" ");
            String[] possibleDirectionArray = currentRoom.possibleDirection().toLowerCase().split(", ");
            if (userInputArray.length < 2) {
                System.out.println(printInvalidCommand(originalInput, currentRoom));
            } else if (findDirectionInArray(possibleDirectionArray, userInput)) {
                System.out.println(roomInformation(currentRoom));
            } else if (userInputArray[0].equals("go")
                    && !findDirectionInArray(possibleDirectionArray, userInput)) {
                System.out.println(printWrongDirection(originalInput, currentRoom));
            } else {
                System.out.println(printInvalidCommand(originalInput, currentRoom));
            }
        }
    }

    /**
     * //TO DO: URL, MAKE METHODS THAT YOU CALL IN TO STRINGS, MAGIC NUMBERS, JAVADOC
     **/

    public static void testAlternateUrl() throws UnirestException, MalformedURLException {
        System.out.println("Please enter an alternate url.");
        Scanner scanner = new Scanner(System.in);
        String userUrl = scanner.nextLine();

        try {
            makeApiRequest(userUrl);
        } catch (UnirestException e) {
            System.out.println("Network not responding");
        } catch (MalformedURLException e) {
            System.out.println("Bad URL: " + userUrl);
            System.out.println("We will be defaulting to the original url" + "\n");
            makeApiRequest("https://courses.engr.illinois.edu/cs126/adventure/siebel.json");
        }
    }

    public static String beginGame() {
        currentRoom = layout.roomObjectFromName(layout.getStartingRoom());
        String beginGame = "Your journey begins here";
        beginGame = beginGame + "\n" + currentRoom.getDescription();
        beginGame = beginGame + "\n" + "From here, you can go: " + currentRoom.possibleDirection();
        return beginGame;
    }

    public static boolean findDirectionInArray(String[] directionsArray, String userInput) {
        if (userInput == null || directionsArray == null || userInput.length() <= 1) {
            return false;
        }
        userInput = userInput.substring(3).toLowerCase();
        for (int i = 0; i < directionsArray.length; i++) {
            if (directionsArray[i] != null && directionsArray[i].toLowerCase().equals(userInput)) {
                String currentDirection = directionsArray[i];
                String newRoomName = currentRoom.roomFromDirection(currentDirection);
                currentRoom = layout.roomObjectFromName(newRoomName);
                return true;
            }
        }
        return false;
    }

    public static boolean reachedFinalRoom(Room currentRoom) {
        if (currentRoom == null) {
            return false;
        }
        return currentRoom.getName().equals(layout.getEndingRoom());
    }

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
        return currentRoom.getDescription() + "\nFrom here, you can go: " + currentRoom.possibleDirection();
    }

    public static String printWrongDirection(String userInput, Room currentRoom) {
        if (userInput == null || currentRoom == null) {
            return null;
        }
        return "I can't go '" + userInput.substring(3) + "'\n" + roomInformation(currentRoom);
    }

    public static String printInvalidCommand(String userInput, Room currentRoom) {
        if (userInput == null || currentRoom == null) {
            return null;
        }
        return "I don't understand '" + userInput + "'\n" + roomInformation(currentRoom);
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

