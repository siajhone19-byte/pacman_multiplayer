# [Pacman](https://youtu.be/lB_J-VNMVpE)
- Coding Tutorial: [https://youtu.be/lB_J-VNMVpE](https://youtu.be/lB_J-VNMVpE)

In this tutorial, you will learn to create the pacman game with the built-in java awt/swing graphics library.

Throughout the tutorial, you will learn how to create the game loop, create a jframe and jpanel, draw images on the jpanel, load the game map using a tilemap, add click handlers to make the pacman move, create a simple algorithm to move each ghost at random, detect collisions between the pacman and ghosts, have pacman eat the food pellets, add a running score, and reset the game when pacman collides with a ghost. 

[How to setup Java with Visual Studio Code](https://youtu.be/BB0gZFpukJU)

![pacman-ss](https://github.com/user-attachments/assets/9f16553b-9092-4894-b740-b8903ed24fa9)

## Homework:
You can continue working on this project if you like. You can design your own map by modifying the tileMap if you want. You can add power pellets to allow pacman to eat the ghosts. In addition, there is an opening on left and right, where if pacman goes through, it would appear on the other side of the map. Currently pacman just moves off screen out of the map so a fix would be needed to make pacman appear the other side. For more of a challenge, you can modify the ghosts movement to cover areas unreachable since the ghosts only change directions when they collide against a wall, and not when theres another path available to go through.

## Wi-Fi Multiplayer (Host and Join)

This version uses the **PacManMenu** as the entry point for network play.

1. Put the project folder on both computers (or copy the ZIP to the second PC). Install JDK 17 or newer.
2. On both PCs, open PowerShell in the folder containing the `.java` files and run:
   ```powershell
   javac *.java
   java PacManMenu
   ```
3. On the host computer, choose **MULTIPLAYER → HOST GAME**. Share the IPv4 address shown in the dialog with the other player.
4. On the other computer, choose **MULTIPLAYER → JOIN GAME**, enter the host's IPv4 address, and connect.
5. Host controls Player 1 with the arrow keys. Client controls Player 2 with WASD (arrow keys also send Player 2 movement).
6. Keep the host game and server running while playing. If the client cannot connect, allow Java through Windows Firewall on the host and confirm both PCs are on the same Wi-Fi/LAN. The server uses TCP port **5000**.

The host runs the authoritative game simulation and sends state updates containing both player positions, scores, lives, remaining food, and ghost positions to the joining client. The client sends Player 2 direction inputs to the host.

Note: This is a simple LAN multiplayer implementation for one host and one joining player, not an internet matchmaking service. Both computers should use the same project/assets version.
