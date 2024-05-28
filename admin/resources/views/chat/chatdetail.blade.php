@extends('../layout')

@section('content')
<style>
    body {
        margin: 0;
        font-family: Arial, sans-serif;
    }

    .adminMessage {
        text-align: right;
        background-color: #d1e7dd;
        margin: 10px 15px;
        padding: 10px 15px;
        border-radius: 15px;
        max-width: 60%;
        align-self: flex-end;
    }

    .customerMessage {
        text-align: left;
        background-color: #f8d7da;
        margin: 10px 15px;
        padding: 10px 15px;
        border-radius: 15px;
        max-width: 60%;
        align-self: flex-start;
    }

    #messageContainer {
        height: 80vh;
        overflow-y: scroll;
        padding: 20px;
        display: flex;
        flex-direction: column;
        background-color: #f5f5f5;
        scrollbar-width: none; /* For Firefox */
    }

    #messageContainer::-webkit-scrollbar {
        display: none; /* Hide scrollbar for Chrome, Safari, and Opera */
    }

    #inputContainer {
        position: fixed;
        bottom: 0;
        left: 250px; /* Adjust this value to match the width of your left menu */
        width: calc(100% - 250px); /* Adjust this value to match the width of your left menu */
        background-color: #fff;
        padding: 10px 15px;
        box-shadow: 0px -1px 5px 0px rgba(0, 0, 0, 0.1);
        display: flex;
        align-items: center;
        transition: margin-left 0.3s ease;
    }

    #messageInput {
        flex-grow: 1;
        margin-right: 10px;
        height: 40px;
        border-radius: 20px;
        border: 1px solid #ccc;
        padding: 0 15px;
        font-size: 14px;
    }

    #sendButton {
        width: 100px;
        height: 40px;
        border-radius: 20px;
        background-color: #007bff;
        color: white;
        border: none;
        font-size: 14px;
        cursor: pointer;
        transition: background-color 0.3s;
    }

    #sendButton:hover {
        background-color: #0056b3;
    }

    .adminMessage,
    .customerMessage {
        display: inline-block;
    }

    @media (max-width: 767px) {
        #inputContainer {
            left: 0;
            width: 100%;
        }
    }
</style>
<div id="messageContainer">
    <!-- Tin nhắn sẽ được thêm vào đây bằng JavaScript -->
</div>
<div id="inputContainer">
    <input type="text" id="messageInput" placeholder="Nhập tin nhắn của bạn...">
    <button id="sendButton" onclick="sendMessage()">Gửi</button>
</div>
<script>
    // Lấy URL hiện tại
    const currentURL = window.location.href;

    // Tìm vị trí của chuỗi 'chatdetail/' trong URL
    const startIndex = currentURL.indexOf('chatdetail/') + 'chatdetail/'.length;

    // Lấy phần cuối của URL bắt đầu từ vị trí tìm được
    const roomId = currentURL.substring(startIndex);
    function displayMessage(message) {
        const messageContainer = document.getElementById('messageContainer');
        const listItem = document.createElement('div');
        listItem.innerText = message.content;

        // Phân biệt tin nhắn của admin và customer
        if (message.sender_type === 'admin') {
            listItem.classList.add('adminMessage');
        } else if (message.sender_type === 'customer') {
            listItem.classList.add('customerMessage');
        }

        messageContainer.appendChild(listItem);

        // Cuộn xuống dưới cùng
        messageContainer.scrollTo(0, messageContainer.scrollHeight);
    }

    function sendMessage() {
        const messageInput = document.getElementById('messageInput');
        const messageContent = messageInput.value.trim();

        if (messageContent !== '') {
            const currentTime = getCurrentTimeInTimezone(7); // Lấy thời gian hiện tại cộng thêm 7 giờ

            const message = {
                id: -1,
                receiver_id: roomId,
                content: messageContent,
                sender_id: localStorage.getItem('id'),
                room_id: roomId,
                sender_type: 'admin',
                updated_at: currentTime,
                created_at: currentTime
            };

            socket.emit('message', JSON.stringify(message)); // Gửi tin nhắn đến server
            messageInput.value = ''; // Xóa nội dung của ô input sau khi gửi
        }
    }

    function getCurrentTimeInTimezone(offset) {
        const currentTime = new Date();
        const timezoneTime = new Date(currentTime.getTime() + (3600000 * offset)); // Cộng thêm offset giờ vào thời gian hiện tại
        
        const isoString = timezoneTime.toISOString();
        
        // Chia tách chuỗi để tạo định dạng yêu cầu
        const date = isoString.split('.')[0]; // Lấy phần `YYYY-MM-DDTHH:mm:ss`
        const milliseconds = isoString.split('.')[1].substring(0, 6); // Lấy phần `.SSSSSS`
        
        // Tạo offset theo định dạng `+07:00`
        const timezoneOffset = (offset >= 0 ? '+' : '-') + String(Math.abs(offset)).padStart(2, '0') + ':00';
        
        // Ghép lại thành chuỗi kết quả
        return `${date}.${milliseconds}${timezoneOffset}`;
    }

    // Code của bạn để kết nối đến server và lắng nghe sự kiện "thread" và "history" đã đúng
</script>

<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
<script src="https://chat-server-tq8x.onrender.com/socket.io/socket.io.js"></script>
<script>
    const socket = io("https://chat-server-tq8x.onrender.com/");
    const room = roomId;
    socket.emit("join", room);
    console.log(`Joined room ${room}`);

    socket.on("thread", function (data) {
        const parsedMessage = JSON.parse(data);
        displayMessage(parsedMessage);
        console.log(`Received message: ${JSON.stringify(data)}`);
    });

    socket.on("history", function (data) {
        document.getElementById('messageContainer').innerHTML = "";
        const messages = JSON.parse(JSON.stringify(data));
        messages.reverse();
        messages.forEach(message => {
            displayMessage(message);
        });
        console.log(`Chat history: ${JSON.stringify(data)}`);
    });
</script>
@endsection
