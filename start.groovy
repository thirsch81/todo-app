def mongoPersistorConfig = [
	address : "vertx.mongopersistor",
	host : "localhost",
	port : 27017,
	db_name : "vertx-test",
	pool_size : 10,
	fake : false
]

verticles = [
	//"CpuUtilization",
	"WebServer",
	"Deploy"
]

container.with {

	deployModule("vertx.mongo-persistor-v1.2", mongoPersistorConfig, 1) {
		println "Deployed MongoPersistor"
		deployVerticle("src/verticle/StaticData.groovy")
	}

	verticles.each { verticle ->
		deployVerticle("src/verticle/${verticle}.groovy") { println "Deployed verticle ${verticle}" }
	}
}