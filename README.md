# MatchIt Memory Game

##Java program with Swing GUI 

The game consists of 8 pairs of cards, arranged in a 4x4 grid, that start out face down and shuffled randomly. On the face of the paired cards there are various pictures and the goal of the game is to match two cards with the same picture until all 16 cards are matched.
 
To play the game, the player clicks on a card to reveal the picture on it. The timer of the game starts immediately after the first click.  The card remains face up, till the player clicks on another card. Once the second card is clicked, there are two possibilities:

1.	The picture of this card matches the first card, in which case the cards remain open for the remaining duration of the game and are considered “paired” 

2.	The second card, does not match the first card, in which case both the cards return to their original state of being face down precisely after 1 second. As such, the player in this scenario gets 1 second to memorize the picture on the cards, along with their locations on the grid.  

In order to pair a card, the player needs to successively click on two cards with the same picture. In essence, when the player clicks on a card with a picture that had previously been revealed to him, he recalls the location of the other pair and clicks on the position of the grid where he previously saw the card in order to pair them. 

To win the game, the player must pair all 16 cards at which point all the cards will remain face up and the time that started with players first move, will now stop and record the total time taken to complete the game. In successive games, the player will endeavor to beat his previous best time.

([See the project write-up for more details.](https://github.com/rajrupabakshi/MemoryGame/blob/master/Project-writeup-Bakshi.pdf))

Created as a final project for CSCI E-10b: Introduction to Computer Science Using Java II at the Harvard Extension School. 

<br>

&copy; 2016 Rajrupa Bakshi, all rights reserved.