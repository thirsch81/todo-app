package verticle

def log = container.logger
def info = log.info
def eb = vertx.eventBus

def verticlesDir = new File("src/verticle")

deployedVerticles = [:]

eb.registerHandler("deploy.deployVerticle") { msg ->
	verticlesDir.eachFileMatch(/${msg.body.text}.groovy/) { verticleFile ->
		container.deployVerticle("${verticlesDir}/${verticleFile.name}") { deploymentID ->
			info<< "\tDeploy: deployed verticle ${deploymentID}"
		}
	}
}

eb.registerHandler("deploy.undeployVerticle") { msg ->
	verticlesDir.eachFileMatch(/${msg.body.text}.groovy/) {
		container.undeployVerticle("${verticlesDir}/${it.name}") {
			info << "\tDeploy: deployed verticle ${it}"
		}
	}
}


def getDeployedClients() {
	vertx.sharedData.getSet("deployedClients")
}

def addDeployedClient(deploymentID) {
	getDeployedClients().add(deploymentID)
}

def removeDeployedClient(deploymentID) {
	getDeployedClients().remove(deploymentID)
}

def deployClient() {
	def clientFile = new File("src/verticle/Client.groovy")
	container.deployVerticle("${clientFile}") { deploymentID ->
		addDeployedClient(deploymentID);
		info(getDeployedClients().toArray()[0])
		info("\tMaster: deployed client ${deploymentID}")
	}
}

def undeployClient() {
	def clients = getDeployedClients()
	if(clients) {
		def id = getDeployedClients().toArray()[0]
		removeDeployedClient(id)
		container.undeployVerticle(id) {
			info("\tMaster: undeployed client ${id}")
		}
	}
}