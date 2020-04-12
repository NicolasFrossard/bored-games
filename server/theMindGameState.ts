import {Player} from "index";
import {BaseGameState} from "./baseGameState";

export class TheMindGameState extends BaseGameState implements TheMindGameState {
    cardsPlayed: [];

    constructor(players: Player[]) {
        super(players);
        this.cardsPlayed = [];
    }
}
