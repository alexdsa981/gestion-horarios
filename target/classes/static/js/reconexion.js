let stompClient = null;
let socket = null;
let reconnectTimeout = null;
const reconnectInterval = 5000;
let wasDisconnected = false;

function connectWebSocket() {
    socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('WebSocket conectado:', frame);
        if (wasDisconnected) {
            location.reload();
        }
        wasDisconnected = false;
        if (reconnectTimeout) {
            clearTimeout(reconnectTimeout);
            reconnectTimeout = null;
        }
    }, function (error) {
        console.warn('Error en WebSocket:', error);
        wasDisconnected = true;
        if (!reconnectTimeout) {
            reconnectTimeout = setTimeout(function () {
                reconnectTimeout = null;
                console.log('Intentando reconexión WebSocket...');
                connectWebSocket();
            }, reconnectInterval);
        }
    });
}

connectWebSocket();