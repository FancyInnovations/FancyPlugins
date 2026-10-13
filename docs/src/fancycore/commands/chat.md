---
icon: dot
title: Chat
---

![](../../static/commands.png)

# Commands for the chat system

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/hytale-plugins/fancycore](https://fancyinnovations.com/docs/hytale-plugins/fancycore).
!!!

!!!warning
The command list is not complete and not all commands are properly documented yet.
Some commands might not be implemented yet.
!!!

## Messages

### Message

Description: Sends a private message to the specified player.<br/>
Syntax: ``/Message (player) (message)`` or ``/Msg (player) (message)``<br/>
Permission: ``fancycore.commands.message``

### Reply

Description: Replies to the last private message received.<br/>
Syntax: ``/Reply (message)`` or ``/R (message)``<br/>
Permission: ``fancycore.commands.reply``

### Message Spy

Description: Toggles message spying to see private messages between other players.<br/>
Syntax: ``/MessageSpy`` or ``/MsgSpy``<br/>
Permission: ``fancycore.commands.messagespy``

### ToggleMessages

Description: Toggles the ability to receive private messages.<br/>
Syntax: ``/ToggleMessages`` or ``/ToggleMsg``<br/>
Permission: ``fancycore.commands.togglemessages``

## Chat rooms

### CreateChatRoom

Description: Creates a new chat room with the specified name.<br/>
Syntax: ``/CreateChatRoom (name)`` or ``/CCR (name)``<br/>
Permission: ``fancycore.commands.createchatroom``

### JoinChatRoom

Description: Joins the specified chat room.<br/>
Syntax: ``/JoinChatRoom (name)`` or ``/JCR (name)``<br/>
Permission: ``fancycore.commands.joinchatroom``

### LeaveChatRoom

Description: Leaves the current chat room.<br/>
Syntax: ``/LeaveChatRoom`` or ``/LCR``<br/>
Permission: ``fancycore.commands.leavechatroom``

### ListChatRooms

Description: Lists all available chat rooms on the server.<br/>
Syntax: ``/ListChatRooms`` or ``/LCRs``<br/>
Permission: ``fancycore.commands.listchatrooms``

### ChatRoomInfo

Description: Displays information about the specified chat room.<br/>
Syntax: ``/ChatRoomInfo (name)`` or ``/CRInfo (name)``<br/>
Permission: ``fancycore.commands.chatroominfo``

## Chat Management  

### ClearChat

Description: Clears the chat for all players on the server.<br/>
Syntax: ``/ClearChat`` or ``/CC``<br/>
Permission: ``fancycore.commands.clearchat``

### SlowChat

Description: Sets a slow mode for chat, limiting how often players can send messages.<br/>
Syntax: ``/SlowChat (seconds)`` or ``/Slow (seconds)``<br/>
Permission: ``fancycore.commands.slowchat``

### MuteChat

Description: Mutes the chat for all players, preventing them from sending messages.<br/>
Syntax: ``/MuteChat``<br/>
Permission: ``fancycore.commands.mutechat``

### UnmuteChat

Description: Unmutes the chat, allowing players to send messages again.<br/>
Syntax: ``/UnmuteChat``<br/>
Permission: ``fancycore.commands.unmutechat``


