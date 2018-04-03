package com.abu.xbase.jsbridge;

/**
 * @author abu
 *         2017/11/29    19:22
 *         ..
 */

public class WebViewJavascriptBridgeJS {
    public static final String instance = "//notation: js file can only use this kind of comments\n" +
            "//since comments will cause error when use in webview.loadurl,\n" +
            "//comments will be remove by java use regexp\n" +
            "(function() {\n" +
            "    if (window.WebViewJavascriptBridge) {\n" +
            "        return;\n" +
            "    }\n" +
            "\n" +
            "    var messagingIframe;\n" +
            "    var sendMessageQueue = [];\n" +
            "    var receiveMessageQueue = [];\n" +
            "    var messageHandlers = {};\n" +
            "\n" +
            "    var CUSTOM_PROTOCOL_SCHEME = 'yy';\n" +
            "    var QUEUE_HAS_MESSAGE = '__QUEUE_MESSAGE__/';\n" +
            "\n" +
            "    var responseCallbacks = {};\n" +
            "    var uniqueId = 1;\n" +
            "\n" +
            "    function _createQueueReadyIframe(doc) {\n" +
            "        messagingIframe = doc.createElement('iframe');\n" +
            "        messagingIframe.style.display = 'none';\n" +
            "        doc.documentElement.appendChild(messagingIframe);\n" +
            "    }\n" +
            "\n" +
            "    //set default messageHandler\n" +
            "    function init(messageHandler) {\n" +
            "        if (WebViewJavascriptBridge._messageHandler) {\n" +
            "            throw new Error('WebViewJavascriptBridge.init called twice');\n" +
            "        }\n" +
            "        WebViewJavascriptBridge._messageHandler = messageHandler;\n" +
            "        var receivedMessages = receiveMessageQueue;\n" +
            "        receiveMessageQueue = null;\n" +
            "        for (var i = 0; i < receivedMessages.length; i++) {\n" +
            "            _dispatchMessageFromNative(receivedMessages[i]);\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    function send(data, responseCallback) {\n" +
            "        _doSend({\n" +
            "            data: data\n" +
            "        }, responseCallback);\n" +
            "    }\n" +
            "\n" +
            "    function registerHandler(handlerName, handler) {\n" +
            "        messageHandlers[handlerName] = handler;\n" +
            "    }\n" +
            "\n" +
            "    function callHandler(handlerName, data, responseCallback) {\n" +
            "        _doSend({\n" +
            "            handlerName: handlerName,\n" +
            "            data: data\n" +
            "        }, responseCallback);\n" +
            "    }\n" +
            "\n" +
            "    //sendMessage add message, 触发native处理 sendMessage\n" +
            "    function _doSend(message, responseCallback) {\n" +
            "        if (responseCallback) {\n" +
            "            var callbackId = 'cb_' + (uniqueId++) + '_' + new Date().getTime();\n" +
            "            responseCallbacks[callbackId] = responseCallback;\n" +
            "            message.callbackId = callbackId;\n" +
            "        }\n" +
            "\n" +
            "        sendMessageQueue.push(message);\n" +
            "        messagingIframe.src = CUSTOM_PROTOCOL_SCHEME + '://' + QUEUE_HAS_MESSAGE;\n" +
            "    }\n" +
            "\n" +
            "    // 提供给native调用,该函数作用:获取sendMessageQueue返回给native,由于android不能直接获取返回的内容,所以使用url shouldOverrideUrlLoading 的方式返回内容\n" +
            "    function _fetchQueue() {\n" +
            "        var messageQueueString = JSON.stringify(sendMessageQueue);\n" +
            "        sendMessageQueue = [];\n" +
            "        //android can't read directly the return data, so we can reload iframe src to communicate with java\n" +
            "        messagingIframe.src = CUSTOM_PROTOCOL_SCHEME + '://return/_fetchQueue/' + encodeURIComponent(messageQueueString);\n" +
            "    }\n" +
            "\n" +
            "    //提供给native使用,\n" +
            "    function _dispatchMessageFromNative(messageJSON) {\n" +
            "        setTimeout(function() {\n" +
            "            var message = JSON.parse(messageJSON);\n" +
            "            var responseCallback;\n" +
            "            //java call finished, now need to call js callback function\n" +
            "            if (message.responseId) {\n" +
            "                responseCallback = responseCallbacks[message.responseId];\n" +
            "                if (!responseCallback) {\n" +
            "                    return;\n" +
            "                }\n" +
            "                responseCallback(message.responseData);\n" +
            "                delete responseCallbacks[message.responseId];\n" +
            "            } else {\n" +
            "                //直接发送\n" +
            "                if (message.callbackId) {\n" +
            "                    var callbackResponseId = message.callbackId;\n" +
            "                    responseCallback = function(responseData) {\n" +
            "                        _doSend({\n" +
            "                            responseId: callbackResponseId,\n" +
            "                            responseData: responseData\n" +
            "                        });\n" +
            "                    };\n" +
            "                }\n" +
            "\n" +
            "                var handler = WebViewJavascriptBridge._messageHandler;\n" +
            "                if (message.handlerName) {\n" +
            "                    handler = messageHandlers[message.handlerName];\n" +
            "                }\n" +
            "                //查找指定handler\n" +
            "                try {\n" +
            "                    handler(message.data, responseCallback);\n" +
            "                } catch (exception) {\n" +
            "                    if (typeof console != 'undefined') {\n" +
            "                        console.log(\"WebViewJavascriptBridge: WARNING: javascript handler threw.\", message, exception);\n" +
            "                    }\n" +
            "                }\n" +
            "            }\n" +
            "        });\n" +
            "    }\n" +
            "\n" +
            "    //提供给native调用,receiveMessageQueue 在会在页面加载完后赋值为null,所以\n" +
            "    function _handleMessageFromNative(messageJSON) {\n" +
            "        console.log(messageJSON);\n" +
            "        if (receiveMessageQueue && receiveMessageQueue.length > 0) {\n" +
            "            receiveMessageQueue.push(messageJSON);\n" +
            "        } else {\n" +
            "            _dispatchMessageFromNative(messageJSON);\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    var WebViewJavascriptBridge = window.WebViewJavascriptBridge = {\n" +
            "        init: init,\n" +
            "        send: send,\n" +
            "        registerHandler: registerHandler,\n" +
            "        callHandler: callHandler,\n" +
            "        _fetchQueue: _fetchQueue,\n" +
            "        _handleMessageFromNative: _handleMessageFromNative\n" +
            "    };\n" +
            "\n" +
            "    var doc = document;\n" +
            "    _createQueueReadyIframe(doc);\n" +
            "    var readyEvent = doc.createEvent('Events');\n" +
            "    readyEvent.initEvent('WebViewJavascriptBridgeReady');\n" +
            "    readyEvent.bridge = WebViewJavascriptBridge;\n" +
            "    doc.dispatchEvent(readyEvent);\n" +
            "})();\n";
}
