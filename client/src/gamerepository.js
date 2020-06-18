import React from 'react';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
const WEB_SOCKET_ENDPOINT = "/message";
const TURN_ENDPOINT = "/turn";

const webSocketChannels = [{
    route: '/topic',
    callback: () => { }
}];

const socket = SockJS(WEB_SOCKET_ENDPOINT);
const stompClient = Stomp.over(socket);

export class GameRepository {

    constructor() {
        this.maxReconnect = 5;
        this.timeStamp = Date.now();
    }

    setupWebSocket = () => {
        const webSoc = stompClient;
        webSoc.connect({}, this.connect);
        webSoc.message = (body) => this.timeStamp= Date.now();
        webSoc.error = (err) => {
            if (this.maxReconnect > 0) {
                this.maxReconnect = this.maxReconnect - 1;
            }
        };
    }

    connect = () => {
        const channels = webSocketChannels;
        const webSoc = stompClient;
        channels.forEach((channel) => {
            webSoc.subscribe(channel.route, channel.callback);
        });
    }

    sendGameState = (gameState) => {
        const webSoc = stompClient;
        webSoc.send(TURN_ENDPOINT, {timeStamp: this.timeStamp.toString()}, gameState);
    }

    getGame = (gameId, callback) => {
        let myRequest = new Request('/games/' + gameId);
        fetch(myRequest, {
            method: 'GET',
            credentials: "include"
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            }).then((data) => {
                callback(data);
        })
            .catch((error) => {
                console.log(error)
        });
    }
}
