package com.example;

import java.util.ArrayList;
import java.util.Scanner;

public class Player {
    private static Layout layout;
    private ArrayList<Item> items;
    private int health;
    private int attack;

    public ArrayList<Item> getItems() {
        return items;
    }

    public int getHealth() {
        return health;
    }

    public int getAttack() {
        return attack;
    }

    //the player keeps knocking 200 points off monster, while player health is randomly subtracted
    public static void fightMonster(Monster monster, Player player) {
        int playerHealth = player.getHealth();
        int monsterHealth = monster.getHealth();
        System.out.println("As you move in this direction, you must battle: " + monster.getName());
        System.out.println("Monster has this much health: " + monster.getHealth());
        System.out.println("Player has this much health: " + player.getHealth());

        while (monsterHealth > 0 && playerHealth > 0) {
            System.out.println("Enter 'attack' to attack the monster");
            Scanner scanner = new Scanner(System.in);
            String userAttack = scanner.nextLine().toLowerCase();
            if (userAttack.equals("attack")) {
                monsterHealth = monsterHealth - player.getAttack();
                System.out.println("You have attacked the monster. Monster now has: " + monsterHealth + " HP");
            } else {
                System.out.println("I don't understand this command. Monster is now attacking.");
            }
            //monster's attack damage is some random number btw 0 and 100
            playerHealth = playerHealth - monster.getAttack();
            System.out.println("The monster has attacked. Player now has: " + playerHealth + " HP");
            if (monsterHealth <= 0) {
                System.out.println("You have successfully defeated the monster! Go to your room.");
                break;
            } else if (playerHealth <= 0) {
                System.out.println("The monster has killed you! Try again.");
                playerHealth = playerHealth + 500;
            }
        }
    }
}
