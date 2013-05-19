var eb = null;

$(document).ready(function() {
	openConn();
	$("#deploy-clients-btn").click(function() {
		deployClients($("#deploy-clients-input").val());
	});
	$("#target-host-btn").click(function() {
		setTargetHost($("#target-host-input").val());
	});
	var timerId = window.setInterval(updateDeployedClients, 2000);
});

function deployClients(number) {
	ebSend("master.deployClients", { "change" : number }, logReply);
}

function setTargetHost(host) {
	ebPublish("clients.setTargetHost", { "host" : host}, logReply);
}

function setArrivalRate(arrivalRate) {
	ebPublish("clients.setArrivalRate", { "arrivalRate" : arrivalRate}, logReply);
}

function updateDeployedClients() {
	ebSend("master.clientCount", { }, function(reply) {
		$("#number-deployed-clients").text(reply.number);
	});
}

function logReply(reply) {
	var debug = reply
	$messageLog = $("#message-log");
	$messageLog.val($messageLog.val() + "Received: " + reply.text + reply.number + "\n");
}

function ebPublish(address, message, handler) {
	if(eb) {
		eb.publish(address, message, handler)
	}
}

function ebSend(address, message, handler) {
	if(eb) {
		eb.send(address, message, handler)
	}
}

function openConn() {
	if (!eb) {
		eb = new vertx.EventBus("http://localhost:8080/eventbus");
		$connectionStatus = $("#connection-status");
		eb.onopen = function() {
			$connectionStatus.text("Connected");
			$connectionStatus.toggleClass("text-error text-success")
		};
		eb.onclose = function() {
			$connectionStatus.text("Disconnected");
			$connectionStatus.toggleClass("text-success text-error")
			eb = null;
		};
	}
}

function closeConn() {
	if (eb) {
		eb.close();
	}
}