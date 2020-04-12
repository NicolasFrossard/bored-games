import {Player} from "index";
import {BaseGameState} from "./baseGameState";

export class TheMindGameState extends BaseGameState implements TheMindGameState {
    round: number;
    cardsPlayed: [];

    constructor(players: Player[]) {
        super(players);
        this.round = 0;
        this.cardsPlayed = [];
    }

    stopTheGame() : void {
        super.stopTheGame();
        this.round = 0;
    }

    moveToNextRound() : void {
        this.round++;
        this.cardsPlayed = [];

        let cardsAlreadyGiven: number[] = [];

        for (let i = 0; i < this.players.length; i++) {
            let player = this.players[i];
            let newPlayerCards: number[] = [];
            for(let i = 0; i < this.round; i++) {
                const newCard = this.randomNewCard(cardsAlreadyGiven);
                cardsAlreadyGiven.push(newCard);
                newPlayerCards.push(newCard);
            }
            player.cardsInHand = newPlayerCards;
            this.players[i] = player;
        }

        console.log(`Players for round ${this.round} will be ${JSON.stringify(this.players)}`);
    }

    randomNewCard(cardsAlreadyGiven: number[]) {
        let randomNewCard: number;
        do {
            randomNewCard = this.getRandomInt(101);
        } while(cardsAlreadyGiven.includes(randomNewCard));
        return randomNewCard;
    }

    getRandomInt(max: number) {
        return 1 + Math.floor(Math.random() * Math.floor(max+1));
    }
}
