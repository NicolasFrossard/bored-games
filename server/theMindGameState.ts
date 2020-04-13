import {Player} from "index";
import {BaseGameState} from "./baseGameState";
import {Socket} from "socket.io";
import {Chance} from "chance";

export class TheMindGameState extends BaseGameState implements TheMindGameState {
    round: number;
    cardsPlayed: number[];
    lives: number;

    constructor(players: Player[]) {
        super(players);
        this.round = 0;
        this.cardsPlayed = [];
        this.lives = 0;
    }

    startTheGame() : void {
        super.startTheGame();
        this.lives = this.players.length;
    }

    stopTheGame(): void {
        super.stopTheGame();
        this.round = 0;
    }

    addNewPlayer(socket: Socket, playerName: String) : void {
        super.addNewPlayer(socket, playerName);
    }

    moveToNextRound(): void {
        this.round++;
        this.cardsPlayed = [];

        let cardsAlreadyGiven: number[] = [];

        for (let i = 0; i < this.players.length; i++) {
            let player = this.players[i];
            let newPlayerCards: number[] = [];
            for (let i = 0; i < this.round; i++) {
                const newCard = this.randomNewCard(cardsAlreadyGiven);
                cardsAlreadyGiven.push(newCard);
                newPlayerCards.push(newCard);
            }
            player.cardsInHand = newPlayerCards.sort(function (a, b) {
                return a - b
            });
            this.players[i] = player;
        }

        console.log(`Players for round ${this.round} will be ${JSON.stringify(this.players)}`);
    }

    randomNewCard(cardsAlreadyGiven: number[]) {
        let randomNewCard: number;
        do {
            randomNewCard = new Chance(Math.random).integer({min: 1, max: 100});
        } while (cardsAlreadyGiven.includes(randomNewCard));
        return randomNewCard;
    }

    playCard(card: number): boolean {
        if (this.cardsPlayed.includes(card)) {
            console.error(`The card ${card} was already played. This should not be possible`);
        }
        this.cardsPlayed.push(card);
        return true;
    }

    isCurrentRoundFinished() {
        return this.cardsPlayed.length === this.round * this.players.length;
    }
}
